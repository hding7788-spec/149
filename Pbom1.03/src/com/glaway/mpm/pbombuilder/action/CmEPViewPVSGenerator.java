package com.glaway.mpm.pbombuilder.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import wt.fc.ObjectIdentifier;
import wt.part.WTPart;

import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.pview.CmPViewFactory;
import com.glaway.mpm.pbombuilder.pview.CmPViewGenerator;
import com.glaway.mpm.pbombuilder.pview.CmPViewImpl;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmMathUtil;
import com.glaway.mpm.pbombuilder.util.CmPartStructureUtil;
import com.glaway.mpm.pbombuilder.util.PviewTask;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;
import com.ptc.pview.dg.DPoint3D;
import com.ptc.pview.dg.FMat33;
import com.ptc.pview.dg.Location;
import com.ptc.pview.pvkapp.ComponentInstance;
import com.ptc.pview.pvkapp.ComponentNode;
import com.ptc.pview.pvkapp.Instance;
import com.ptc.pview.pvkapp.ShapeInstance;
import com.ptc.pview.pvkapp.ShapeInstance_holder;
import com.ptc.pview.pvkapp.ShapeScene;
import com.ptc.pview.pvkapp.Structure;
import com.ptc.pview.pvkapp.Tree;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.ConnectionLostException;
import com.ptc.pview.utils.dom.InvalidActorException;
import com.ptc.pview.utils.dom.MessageProtocolException;

public class CmEPViewPVSGenerator implements CmPViewGenerator {
	private static final CmLogger log = CmLogger
			.getLogger(CmEPViewPVSGenerator.class);

	private CmTree tree;
	private static volatile boolean isInterrupted = false;
	private List<ObjectIdentifier> treeNodeOidKeyList = new ArrayList<ObjectIdentifier>();
	private static HashMap<Long, URL> id2url = new HashMap<Long, URL>(32);

	private Structure structure;
	private ShapeScene shapeScene;
	private CmPViewImpl pviewImpl;

	public CmEPViewPVSGenerator(CmTree tree) throws Exception,
			ActorShutdownException, IOException, InvalidActorException {
		this.tree = tree;
		pviewImpl = CmPViewFactory.getPViewImpl4MBOM();
		// 清除旧的PV内容,为新的结构准备空间
		CmTreeNode root = tree.getRoot();
		resetChildNodePvWorld(root);
		log.debug("清除旧的EBOM的PView完毕,准备空间新的结构");

		CmEPViewPVSGenerator.isInterrupted = false;
		if (!tree.getRoot().isLeaf()) {
			initOidKeyList(tree.getRoot()); // 获取树上所有的被选中的叶节点
			log.debug("获取EBOM树上所有的被选中的叶节点");
			try {
				id2url.putAll(CmPartStructureUtil.getPViewURLHashMap(
						treeNodeOidKeyList, ".ol"));
			} catch (RemoteException e) {
				log.error(e);
			} catch (InvocationTargetException e) {
				log.error(e.getMessage());
				log.error(e.getCause());
			}
		}
	}

	/**
	 * 清除上一次可视化记录在树节点上的内容
	 *
	 * @param node
	 */
	private void resetChildNodePvWorld(CmTreeNode node) {
		node.set_pviewComponentNode(null);
		node.set_pviewComponentInstance(null);
		node.set_pviewShapeInstance(null);
		Enumeration<CmTreeNode> childs = node.children();
		while (childs.hasMoreElements()) {
			CmTreeNode child = childs.nextElement();
			resetChildNodePvWorld(child);
			if (CmCommonStringUtil.isPackage(child)) {
				List<CmTreeNode> list = child.getListNode();
				for (CmTreeNode brother : list) {
					resetChildNodePvWorld(brother);
				}
			}
		}
	}

	private void initOidKeyList(CmTreeNode treeNode) {
		getOidKeysFromTreeNode(treeNode, treeNodeOidKeyList, id2url.keySet());
	}

	@SuppressWarnings("unchecked")
	private void getOidKeysFromTreeNode(CmTreeNode treeNode,
			Collection<ObjectIdentifier> oidKeys, Collection<Long> excepts) {
		if (!treeNode.children().hasMoreElements()
				&& treeNode.getPart() != null) {
			long oid = treeNode.getPart().getOid();
			if (oid > 0 && !excepts.contains(oid))
				oidKeys.add(new ObjectIdentifier(WTPart.class, treeNode
						.getPart().getOid()));
		} else {
			Enumeration<CmTreeNode> children = (Enumeration<CmTreeNode>) treeNode
					.children();
			while (children.hasMoreElements()) {
				CmTreeNode child = children.nextElement();
				if (child.isSelected()) {
					getOidKeysFromTreeNode(child, oidKeys, excepts);
				}
				if (CmCommonStringUtil.isPackage(child)) {
					for (CmTreeNode brother : child.getListNode()) {
						if (brother.isSelected()) {
							getOidKeysFromTreeNode(brother, oidKeys, excepts);
						}
					}
				}
			}
		}
	}

	private String getPVSPath(String oid) {
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("oid", oid);
			map.put("viewName", "Design");
//			if(CmConnectFrame.isPlanningView){
//				map.put("viewName", LoadConfig.getInstance().getPbomView());
//			}else{
//				map.put("viewName", "Design");
//			}
			String path = System.getProperty("java.io.tmpdir") + "\\" + oid
					+ "\\";
			File fileDir = new File(path);
			if (fileDir.exists()) {
				for (int i = 0; i < fileDir.list().length; i++) {
					if (fileDir.list()[i].endsWith(".pvs")) {
						return path + fileDir.list()[i];
					}
				}
			}

			Map<String, byte[]> ret = PBOMEditorToWCIntf
					.getPVSAndMarkupRMI(map);
			if (ret.size() == 0) {
				return "";
			}

			for (Entry<String, byte[]> map1 : ret.entrySet()) {
				String olName = (String) map1.getKey();
				byte[] olBytes = (byte[]) map1.getValue();
				if (path != null) {

					if (!fileDir.exists()) {
						fileDir.mkdirs();
					}
					if (olBytes == null) {

						return "";
					}

					writeBytes(path + olName, olBytes);

				}
			}
			for (int i = 0; i < fileDir.list().length; i++) {
				if (fileDir.list()[i].endsWith(".pvs")) {
					return path + fileDir.list()[i];
				}
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	private boolean writeBytes(String filePath, byte[] bytes) {
		if (bytes == null) {
			System.out.println("bytes = null");
			return false;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			int size = bytes.length / 1024;
			for (int i = 0; i < size; i++) {
				fos.write(bytes, i * 1024, 1024);
			}
			fos.write(bytes, size * 1024, bytes.length % 1024);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public void generatePVStructure(String finishFrame) {

		String pvsPath = getPVSPath(CmConnectFrame.partOid);// "D:\\pvs\\911.pvs";//
															// downloadPVS();

		if (pvsPath == null || pvsPath.equals("")) {
			JOptionPane.showMessageDialog(null, "下载PVS图形失败");
			CmPivewAction.closeProcessBaer();
			return;
		}

		pviewImpl = CmPViewFactory.getPViewImpl4MBOM();
		try {
			pviewImpl.resetPvWorld();
		} catch (ConnectionLostException e1) {
			e1.printStackTrace();
		} catch (ActorShutdownException e1) {
			e1.printStackTrace();
		} catch (InvalidActorException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		pviewImpl.waitforClientIntialized();

		pviewImpl.openFile(pvsPath, finishFrame);

	}

	private ComponentNode getComponentNode(CmTreeNode node) throws Exception {
		ComponentNode ret = buildComponentNode(node.getOccId());
		if (node.getPart() != null && node.getPart().getOid() > 0) {
			URL url = id2url.get(node.getPart().getOid());
			if (url != null)
				ret.SetShapeSource(url.toExternalForm(), 0, 0, 0, 1, 1, 1);
		}

		return ret;
	}

	private ComponentNode buildComponentNode(String id) throws Exception {
		ComponentNode ret = structure.CreateComponentNode(id, (byte) 'a');
		return ret;
	}

	private void processTreeRoot(CmTreeNode parent) {
		if (CmEPViewPVSGenerator.isInterrupted)
			return;

		CmTreeNode child = tree.getRoot();

		// 子节点被选中，或者子节点本身的可视化信息已经创建好
		PviewTask.postTask("mainframe.setStatus", "正在处理节点" + child);
		log.debug("正在处理节点" + child);
		try {
			if (child.isSelected()) {
				if (child.children().hasMoreElements()) {
					// Treat an assembly
					ComponentNode cnParent = parent.get_pviewComponentNode();
					ComponentNode cnChild;

					cnChild = getComponentNode(child);

					ComponentInstance ciChild = getComponentInstance(cnParent,
							cnChild, child.getOccId());
					if (ciChild != null) {
						Instance instChild = ciChild.GetInstance();
						ShapeInstance siChild = shapeScene
								.CreateShapeInstance(instChild);

						if (siChild != null) {
							tree.addPVMapping(siChild.GetInstance(), child);
							siChild.SetVisibility(child.isSelected());
							child.set_pviewComponentNode(cnChild);
							child.set_pviewComponentInstance(ciChild);
							child.set_pviewShapeInstance(siChild);
						}
					}
				}
				// Launch recurse
				processChildren(child);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processChildren(CmTreeNode parent) {
		Enumeration<?> children = parent.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) (children.nextElement());

			// // 子节点被选中，或者子节点本身的可视化信息已经创建好
			PviewTask.postTask("mainframe.setStatus", "正在处理节点" + child);
			log.debug("正在处理节点" + child);
			if (child.isSelected()) {
				processChildPview(child, parent);
			}
			if (CmCommonStringUtil.isPackage(child)) {
				for (CmTreeNode brother : child.getListNode()) {
					if (child.children().hasMoreElements()) {
						processChildPview(brother, parent);
					} else {
						processChildrenPiview(brother, parent);
					}
				}
			}
		}
	}

	public void processChildPview(CmTreeNode child, CmTreeNode parent) {
		log.debug("正在处理选中节点" + child);
		try {
			if (child.children().hasMoreElements()) { // Treat an assembly
				ComponentNode cnParent = parent.get_pviewComponentNode();
				ComponentNode cnChild = child.get_pviewComponentNode();
				if (cnChild == null) {
					cnChild = getComponentNode(child);
					child.set_pviewComponentNode(cnChild);
				}

				ComponentInstance ciChild = child.get_pviewComponentInstance();
				if (ciChild == null) {
					ciChild = getComponentInstance(cnParent, cnChild,
							child.getOccId());
					child.set_pviewComponentInstance(ciChild);
				}
				if (ciChild != null) {
					ShapeInstance siChild = shapeScene
							.CreateShapeInstance(ciChild.GetInstance());
					if (siChild != null) {
						tree.addPVMapping(siChild.GetInstance(), child);
						siChild.SetVisibility(true);
						child.set_pviewShapeInstance(siChild);
					}
				}
			} else { // Treat a leaf
				processChildrenPiview(child, parent);
				// 打包后的兄弟节点
				if (CmCommonStringUtil.isPackage(child)) {
					List<CmTreeNode> list = child.getListNode();
					for (CmTreeNode brother : list) {
						processChildrenPiview(brother, parent);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ActorShutdownException e) {
			e.printStackTrace();
		} catch (InvalidActorException e) {
			e.printStackTrace();
		} catch (ConnectionLostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		processChildren(child);
	}

	public void processChildrenPiview(CmTreeNode child, CmTreeNode parent) {
		if (child.isSelected()) {
			try {
				if (child.children().hasMoreElements()) { // Treat an assembly
					ComponentNode cnParent = parent.get_pviewComponentNode();
					ComponentNode cnChild = child.get_pviewComponentNode();
					if (cnChild == null) {
						cnChild = getComponentNode(child);
						child.set_pviewComponentNode(cnChild);
					}

					ComponentInstance ciChild = child
							.get_pviewComponentInstance();
					if (ciChild == null) {
						ciChild = getComponentInstance(cnParent, cnChild,
								child.getOccId());
						child.set_pviewComponentInstance(ciChild);
					}
					if (ciChild != null) {
						ShapeInstance siChild = shapeScene
								.CreateShapeInstance(ciChild.GetInstance());
						if (siChild != null) {
							tree.addPVMapping(siChild.GetInstance(), child);
							siChild.SetVisibility(true);
							child.set_pviewShapeInstance(siChild);
						}
					}
				} else { // Treat a leaf
					ComponentNode cnParent = parent.get_pviewComponentNode();
					Location location = getPviewLocation(child.getMatrix());
					ComponentNode cnChild = child.get_pviewComponentNode();
					if (cnChild == null) {
						cnChild = (ComponentNode) getComponentNode(child);
						child.set_pviewComponentNode(cnChild);
					}
					if (cnChild != null) {
						ComponentInstance ciChild = child
								.get_pviewComponentInstance();
						if (ciChild == null) {
							ciChild = getComponentInstance(cnParent, cnChild,
									child.getOccId());
							child.set_pviewComponentInstance(ciChild);
						}
						if (ciChild != null) {
							ciChild.SetLocation(location);
							ShapeInstance siChild = getShapeInstance(
									shapeScene, ciChild);
							if (siChild != null) {
								siChild.SetLocation(location);
								tree.addPVMapping(siChild.GetInstance(), child);
								siChild.SetVisibility(true);
								child.set_pviewShapeInstance(siChild);
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ActorShutdownException e) {
				e.printStackTrace();
			} catch (InvalidActorException e) {
				e.printStackTrace();
			} catch (ConnectionLostException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private ComponentInstance getComponentInstance(ComponentNode aParentCN,
			ComponentNode aChildCN, String instanceId) throws Exception {
		// log.debug("instanceId - " + instanceId);
		ComponentInstance ret = aParentCN
				.AddComponentNode(aChildCN, instanceId);
		if (null != ret) {
			ret.SetName(instanceId);
		}
		return ret;
	}

	private ShapeInstance getShapeInstance(ShapeScene shapeScene,
			ComponentInstance aComponentInstance) throws Exception {
		ShapeInstance theShapeInstance = shapeScene
				.CreateShapeInstance(aComponentInstance.GetInstance());
		return theShapeInstance;
	}

	private static Location getPviewLocation(Matrix4d matrix) throws Exception {
		Vector3d v = CmMathUtil.matrice4ToTrans(matrix);
		Matrix3d d;

		// ML start Symmetry Handling
		if (!(matrix.determinant() > 0.0d)) {
			// if the part is right symmetric or the matrix4d determinant is
			// non-positive
			log.debug("Part is right symmetric or its matrix has a non positive determinant.");
			Vector3d angles = new Vector3d();
			angles.x = Math.atan2(matrix.m21, matrix.m22);
			angles.y = -Math.asin(matrix.m20);
			angles.z = Math.atan2(-matrix.m10, matrix.m00);
			d = CmMathUtil.anglesToMatrice(angles);

			Matrix3d PIrotation = new Matrix3d();
			PIrotation.rotY(Math.PI);
			d.mul(PIrotation); // Rotates of PI
			d.mul(-1D); // mirroring
		} else {
			d = CmMathUtil.matrix4ToMatrix3(matrix);
		}

		// ML start fixed pview issues for volvo

		float[] f = new float[9];
		f = CmMathUtil.getOrientationFromMatrix4d(matrix);
		FMat33 theFMat33;
		if (f == null) {
			float m00 = Double.valueOf(d.m00).floatValue();
			float m01 = Double.valueOf(d.m01).floatValue();
			float m02 = Double.valueOf(d.m02).floatValue();
			float m10 = Double.valueOf(d.m10).floatValue();
			float m11 = Double.valueOf(d.m11).floatValue();
			float m12 = Double.valueOf(d.m12).floatValue();
			float m20 = Double.valueOf(d.m20).floatValue();
			float m21 = Double.valueOf(d.m21).floatValue();
			float m22 = Double.valueOf(d.m22).floatValue();
			theFMat33 = new FMat33(m00, m01, m02, m10, m11, m12, m20, m21, m22);
		} else {
			theFMat33 = new FMat33(f[0], f[1], f[2], f[3], f[4], f[5], f[6],
					f[7], f[8]);
		}

		DPoint3D thePoint3D = new DPoint3D(v.x, v.y, v.z);
		Location theLocation = new Location();
		theLocation.Set(theFMat33, thePoint3D);
		return theLocation;
	}

	public static void desactivate(ShapeInstance shapeInstance) {
		if (shapeInstance != null) {
			try {
				shapeInstance.SetVisibility(false);
			} catch (MessageProtocolException e) {
				e.printStackTrace();
			} catch (ActorShutdownException e) {
				e.printStackTrace();
			} catch (InvalidActorException e) {
				e.printStackTrace();
			} catch (ConnectionLostException e) {
				e.printStackTrace();
			}
		}
	}

	public static void activate(ShapeInstance shapeInstance)
			throws MessageProtocolException {
		if (shapeInstance != null) {
			try {
				shapeInstance.SetVisibility(true);
			} catch (ActorShutdownException e) {
				e.printStackTrace();
			} catch (InvalidActorException e) {
				e.printStackTrace();
			} catch (ConnectionLostException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return
	 */
	public CmPViewImpl getPviewImpl() {
		return pviewImpl;
	}

	void linkPviewToTree() {
		try {
			Tree pvTree = CmPViewFactory.getPViewImpl4MBOM().getWorld().GetTree();
			CmTree cmtree = this.tree;
			uppackageAllNode(cmtree);
			HashMap<String,Instance> insMap = new HashMap<String, Instance>();
			printTree(pvTree.GetRoot(),0,insMap);
			CmTreeNode root = cmtree.getRoot();
			Enumeration<CmTreeNode> child = root.breadthFirstEnumeration();
			while (child.hasMoreElements()) {
				CmTreeNode cmNode = child.nextElement();
//				System.out.println("occPath : " + cmNode.getOccpath());

				if(cmNode.getOccpath() == null){
					continue;
				}
				else{
					String idPath = cmNode.getOccpath().replaceFirst("-1", "");
					if (idPath.equals("")) {
						idPath = "/";
					}
					setPviewNode(idPath, cmNode, insMap);
				}
//				processPackage(idPath, cmNode, pvTree);

				// String partNumber =
				// nIns.GetName().substring(0,nIns.GetName().indexOf('.'));
				// if(cmNode.getPart().getPartNumber().equals(partNumber)){
				// cmNode.setOccpath(ins.GetIDPath());
				// cmNode.set_pviewComponentInstance(ci);
				// cmNode.set_pviewComponentNode(cn);
				// ShapeInstance_holder sh = new ShapeInstance_holder();
				// CmPViewFactory.getPViewImpl4MBOM().getWorld().GetFirstShapeScene().GetShapeInstance(ins,
				// sh);
				// cmNode.set_pviewShapeInstance(sh.value);
				// break;
				// }

			}
			cmtree.getRoot().setSelected(true);
			// Instance ins = tree.GetRoot();
			cmtree.repaint();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void printTree(Instance ins , int i,HashMap<String,Instance> map){
    	try{
    		int count = i+1;
    		System.out.println("instance "+ins.GetIDPath() + " : "+ins.GetName()+":::"+count+"::::");
    		map.put(ins.GetIDPath(), ins);
    		Instance nIns = ins.GetFirstChild();

    		if(nIns != null){
    			Instance eIns = nIns;
    			while(eIns != null)
    			{
    				printTree(eIns,count,map);
    				eIns = eIns.GetNextSibling();
    			}
    		}
    	}catch(Throwable e){
    		e.printStackTrace();
    	}

    }

	public boolean uppackageAllNode(CmTree tree) {
		boolean flag = uppackageTheNode(tree.getRoot(),false);
		CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
		tree.updateUI();
		return flag;
	}

	@SuppressWarnings("unchecked")
	public boolean uppackageTheNode(CmTreeNode node,boolean flag) {
		if(CmCommonStringUtil.isPackage(node)){
			flag = true;
			CmTreeNode parent = (CmTreeNode) node.getParent();
			List<CmTreeNode> list = node.getListNode();
			for (CmTreeNode brotherNode : list) {
				parent.add(brotherNode);
			}
			node.setListNode(null);
		}
		Enumeration children = node.children();
		while (children.hasMoreElements()) {
			CmTreeNode cmnode = (CmTreeNode) children.nextElement();
			flag = uppackageTheNode(cmnode,flag);
		}
		return flag;
	}

	private void processPackage(String idPath, CmTreeNode node, Tree t) throws Exception {

//		setPviewNode(idPath, node, t);
		if (CmCommonStringUtil.isPackage(node)) {

			List<CmTreeNode> nodeList = node.getListNode();
			for (int i = 0; i < nodeList.size(); i++) {
//				String idPathC = nodeList.get(i).getOccpath() == null ? ""
//						: nodeList.get(i).getOccpath().replaceFirst("0", "");
				Enumeration<CmTreeNode> temp = nodeList.get(i).children();
				while (temp.hasMoreElements()) {
					CmTreeNode cmTreeNode = (CmTreeNode) temp.nextElement();
					String idPathTemp = cmTreeNode.getOccpath() == null ? ""
							: cmTreeNode.getOccpath().replaceFirst("0", "");
					processPackage(idPathTemp, cmTreeNode, t);
				}

			}
		}


	}

	private void setPviewNode(String idPath, CmTreeNode cmNode, Map<String,Instance> m)
			throws Exception {

		idPath = idPath.replace('+', '/');
//		System.out.println("idPath : " + idPath);
//		Instance ins = tree.FindInstance(idPath);
		Instance ins = m.get(idPath);
		if (ins == null) {
			return;
		}
		cmNode.set_pviewComponentInstance(ins.GetComponentInstance());
		cmNode.set_pviewComponentNode(ins.GetComponentNode());
		ShapeInstance_holder sh = new ShapeInstance_holder();
		CmPViewFactory.getPViewImpl4MBOM().getWorld().GetFirstShapeScene()
				.GetShapeInstance(ins, sh);
		cmNode.set_pviewShapeInstance(sh.value);

		tree.addPVMapping(ins, cmNode);

	}

}
