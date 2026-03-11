package com.glaway.mpm.visual.view.pview;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.media.j3d.BoundingBox;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import wt.fc.ObjectIdentifier;
import wt.part.WTPart;

import com.glaway.mpm.qmIntf.decoratePView.showPanel.view.DPViewStructureGenerator;
import com.glaway.mpm.task.CmTaskExecutor;
import com.glaway.mpm.task.CmTaskExecutorCallback;
import com.glaway.mpm.task.CmTaskInfo;
import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.task.util.CmTaskHelper;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.visual.control.VaMathUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.tree.VaDefaultTreeLinkage;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.visual.view.tree.VaTreeNodeModelComparator;
import com.glaway.mpm.visual.view.ui.VaPViewScenePanel;
import com.glaway.mpm.visual.view.ui.VaPViewScenesPanel;
import com.glaway.mpm.wcIntf.ImageIntf;
import com.ptc.pview.dg.DPoint3D;
import com.ptc.pview.dg.FMat33;
import com.ptc.pview.dg.Location;
import com.ptc.pview.pvkapp.ComponentInstance;
import com.ptc.pview.pvkapp.ComponentNode;
import com.ptc.pview.pvkapp.Instance;
import com.ptc.pview.pvkapp.ShapeInstance;
import com.ptc.pview.pvkapp.ShapeInstance_holder;
import com.ptc.pview.pvkapp.ShapeScene;
import com.ptc.pview.pvkapp.ShapeSource;
import com.ptc.pview.pvkapp.Structure;
import com.ptc.pview.pvkapp.Tree;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.ConnectionLostException;
import com.ptc.pview.utils.dom.InvalidActorException;
import com.ptc.pview.utils.dom.MessageProtocolException;

public class VaEPViewPVSGenerator implements VaPViewGenerator,CmTaskExecutor {
	private static final VaLogger			log					= VaLogger.getLogger(VaEPViewPVSGenerator.class);

	// for CmTaskExecutor -> start
	private volatile boolean				executorActive;
	// for CmTaskExecutor -> end
	private VaTree							tree;
	private VaTree							stree;
	private static volatile boolean			isInterrupted		= false;
	private List<ObjectIdentifier>			treeNodeOidKeyList	= new ArrayList<ObjectIdentifier>();
//	private static HashMap<Long, URL>		id2url				= new HashMap<Long, URL>(32);

	private Structure						structure;
	private ShapeScene						shapeScene;
	private VaPViewImpl						pviewImpl;
	private String							pvsPath;
//	public static HashMap<String, Instance>	insMap				= new HashMap<String, Instance>();

	public static JTree mirrorTreeForCad;


	public VaEPViewPVSGenerator(VaTree tree, VaTree sourceTree) throws Exception, ActorShutdownException, IOException,
			InvalidActorException {
		this.tree = tree;
		this.stree = sourceTree;
		pviewImpl = VaPViewFactory.getPViewImpl4MBOM();
		// 清除旧的PV内容,为新的结构准备空间
		VaTreeNode root = tree.getRoot();
		resetChildNodePvWorld(root);
		log.debug("清除旧的EBOM的PView完毕,准备空间新的结构");

		VaEPViewPVSGenerator.isInterrupted = false;

		registerTaskExecutor();
		if (!tree.getRoot().isLeaf()) {
//			initOidKeyList(tree.getRoot()); // 获取树上所有的被选中的叶节点
//			log.debug("获取EBOM树上所有的被选中的叶节点");
//			try {
//				id2url.putAll(VaPartStructureUtil.getPViewURLHashMap(treeNodeOidKeyList, ".ol"));
//			} catch (RemoteException e) {
//				log.error(e);
//			} catch (InvocationTargetException e) {
//				log.error(e.getMessage());
//				log.error(e.getCause());
//			}
		}
	}

	/**
	 * 清除上一次可视化记录在树节点上的内容
	 *
	 * @param node
	 */
	private void resetChildNodePvWorld(VaTreeNode node) {
		node.set_pviewComponentNode(null);
		node.set_pviewComponentInstance(null);
		node.set_pviewShapeInstance(null);
		Enumeration<VaTreeNode> childs = node.children();
		while (childs.hasMoreElements()) {
			VaTreeNode child = childs.nextElement();
			resetChildNodePvWorld(child);
			// if (VaCommonStringUtil.isPackage(child)) {
			// List<VaTreeNode> list = child.getListNode();
			// for (VaTreeNode brother : list) {
			// resetChildNodePvWorld(brother);
			// }
			// }
		}
	}

//	private void initOidKeyList(VaTreeNode treeNode) {
//		getOidKeysFromTreeNode(treeNode, treeNodeOidKeyList, id2url.keySet());
//	}

	@SuppressWarnings("unchecked")
	private void getOidKeysFromTreeNode(VaTreeNode treeNode, Collection<ObjectIdentifier> oidKeys,
			Collection<Long> excepts) {
		if (!treeNode.children().hasMoreElements() && treeNode.getPart() != null) {
			long oid = treeNode.getPart().getOid();
			if (oid > 0 && !excepts.contains(oid))
				oidKeys.add(new ObjectIdentifier(WTPart.class, treeNode.getPart().getOid()));
		} else {
			Enumeration<VaTreeNode> children = (Enumeration<VaTreeNode>) treeNode.children();
			while (children.hasMoreElements()) {
				VaTreeNode child = children.nextElement();
				if (child.isSelected()) {
					getOidKeysFromTreeNode(child, oidKeys, excepts);
				}
				// if (VaCommonStringUtil.isPackage(child)) {
				// for (VaTreeNode brother : child.getListNode()) {
				// if (brother.isSelected()) {
				// getOidKeysFromTreeNode(brother, oidKeys, excepts);
				// }
				// }
				// }
			}
		}
	}

	private String getPVSPath(String oid) {
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("oid", oid);
			map.put("viewName", LoadConfig.getInstance().getPbomView());

			pvsPath = System.getProperty("java.io.tmpdir") + "\\" + oid + "\\";
			File fileDir = new File(pvsPath);
			if (fileDir.exists()) {
				for (int i = 0; i < fileDir.list().length; i++) {
					if (fileDir.list()[i].endsWith(".pvs")) {
						return pvsPath + fileDir.list()[i];
					}
				}
			}

			Map<String, byte[]> ret = ImageIntf.getPVSAndMarkupRMI(map);
			if (ret.size() == 0) {
				return "";
			}

			for (Entry<String, byte[]> map1 : ret.entrySet()) {
				String olName = (String) map1.getKey();
				byte[] olBytes = (byte[]) map1.getValue();
				if (pvsPath != null) {

					if (!fileDir.exists()) {
						fileDir.mkdirs();
					}
					if (olBytes == null) {

						return "";
					}

					if (olName.endsWith(".etb")) {
						writeETBFileBytes(pvsPath + olName, olBytes);
					} else {
						writeBytes(pvsPath + olName, olBytes);
					}

				}
			}
			for (int i = 0; i < fileDir.list().length; i++) {
				if (fileDir.list()[i].endsWith(".pvs")) {
					return pvsPath + fileDir.list()[i];
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
			log.debug("bytes = null");
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

	/**
	 * 以UTF-8的格式生成注释集的ETB文件(前提是来源的bytes的编码方式是GBK) 风险：来源格式不确定,可能有很多种格式
	 *
	 * @author sun_youfei
	 * @date 2013-11-6
	 * @param filePath
	 * @param bytes
	 * @return
	 *
	 */
	private boolean writeETBFileBytes(String filePath, byte[] bytes) {
		if (bytes == null) {
			System.out.println("bytes = null");
			return false;
		}
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			fos = new FileOutputStream(filePath);
			osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(new String(bytes, "GBK"));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return true;
	}

	public void generatePVStructure(String finishFrame) {

		String pvsPath = getPVSPath(VaContext.getCurrentPartOid());// "D:\\pvs\\911.pvs";//
		System.out.println("pvsPath:"+pvsPath);
		// downloadPVS();
		if (pvsPath == null || pvsPath.equals("")) {
			JOptionPane.showMessageDialog(null, "下载PVS图形失败,未找到该组件的可视化文件!");

			try {
				CmTaskHelper.sendTask(CmTaskInfo.newCmTaskInfo("VaPviewAction.finishAnimFrame", this, null), null);
			} catch (CmTaskException e) {
				e.printStackTrace();
			}
			return;
		}

		pviewImpl = VaPViewFactory.getPViewImpl4MBOM();
		try {
			pviewImpl.resetPvWorld(this);
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

	private ComponentNode getComponentNode(VaTreeNode node) throws Exception {
		ComponentNode ret = buildComponentNode(node.getOccId());
		if (node.getPart() != null && node.getPart().getOid() > 0) {
//			URL url = id2url.get(node.getPart().getOid());
//			if (url != null)
//				ret.SetShapeSource(url.toExternalForm(), 0, 0, 0, 1, 1, 1);
		}

		return ret;
	}

	private ComponentNode buildComponentNode(String id) throws Exception {
		ComponentNode ret = structure.CreateComponentNode(id, (byte) 'a');
		return ret;
	}

	private void processTreeRoot(VaTreeNode parent) {
		if (VaEPViewPVSGenerator.isInterrupted)
			return;

		VaTreeNode child = tree.getRoot();

		// 子节点被选中，或者子节点本身的可视化信息已经创建好
		// PviewTask.postTask("mainframe.setStatus", "正在处理节点" + child);
		log.debug("正在处理节点" + child);
		try {
			if (child.isSelected()) {
				if (child.children().hasMoreElements()) {
					// Treat an assembly
					ComponentNode cnParent = parent.get_pviewComponentNode();
					ComponentNode cnChild;

					cnChild = getComponentNode(child);

					ComponentInstance ciChild = getComponentInstance(cnParent, cnChild, child.getOccId());
					if (ciChild != null) {
						Instance instChild = ciChild.GetInstance();
						ShapeInstance siChild = shapeScene.CreateShapeInstance(instChild);

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

	private void processChildren(VaTreeNode parent) {
		Enumeration<?> children = parent.children();
		while (children.hasMoreElements()) {
			VaTreeNode child = (VaTreeNode) (children.nextElement());

			// // 子节点被选中，或者子节点本身的可视化信息已经创建好
			// PviewTask.postTask("mainframe.setStatus", "正在处理节点" + child);
			log.debug("正在处理节点" + child);
			if (child.isSelected()) {
				processChildPview(child, parent);
			}
			// if (VaCommonStringUtil.isPackage(child)) {
			// for (VaTreeNode brother : child.getListNode()) {
			// if (child.children().hasMoreElements()) {
			// processChildPview(brother, parent);
			// } else {
			// processChildrenPiview(brother, parent);
			// }
			// }
			// }
		}
	}

	public void processChildPview(VaTreeNode child, VaTreeNode parent) {
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
					ciChild = getComponentInstance(cnParent, cnChild, child.getOccId());
					child.set_pviewComponentInstance(ciChild);
				}
				if (ciChild != null) {
					ShapeInstance siChild = shapeScene.CreateShapeInstance(ciChild.GetInstance());
					if (siChild != null) {
						tree.addPVMapping(siChild.GetInstance(), child);
						siChild.SetVisibility(true);
						child.set_pviewShapeInstance(siChild);
					}
				}
			} else { // Treat a leaf
				processChildrenPiview(child, parent);
				// 打包后的兄弟节点
				// if (VaCommonStringUtil.isPackage(child)) {
				// List<VaTreeNode> list = child.getListNode();
				// for (VaTreeNode brother : list) {
				// processChildrenPiview(brother, parent);
				// }
				// }
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

	public void processChildrenPiview(VaTreeNode child, VaTreeNode parent) {
		if (child.isSelected()) {
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
						ciChild = getComponentInstance(cnParent, cnChild, child.getOccId());
						child.set_pviewComponentInstance(ciChild);
					}
					if (ciChild != null) {
						ShapeInstance siChild = shapeScene.CreateShapeInstance(ciChild.GetInstance());
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
						ComponentInstance ciChild = child.get_pviewComponentInstance();
						if (ciChild == null) {
							ciChild = getComponentInstance(cnParent, cnChild, child.getOccId());
							child.set_pviewComponentInstance(ciChild);
						}
						if (ciChild != null) {
							ciChild.SetLocation(location);
							ShapeInstance siChild = getShapeInstance(shapeScene, ciChild);
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

	private ComponentInstance getComponentInstance(ComponentNode aParentCN, ComponentNode aChildCN, String instanceId)
			throws Exception {
		// log.debug("instanceId - " + instanceId);
		ComponentInstance ret = aParentCN.AddComponentNode(aChildCN, instanceId);
		if (null != ret) {
			ret.SetName(instanceId);
		}
		return ret;
	}

	private ShapeInstance getShapeInstance(ShapeScene shapeScene, ComponentInstance aComponentInstance)
			throws Exception {
		ShapeInstance theShapeInstance = shapeScene.CreateShapeInstance(aComponentInstance.GetInstance());
		return theShapeInstance;
	}

	private static Location getPviewLocation(Matrix4d matrix) throws Exception {
		Vector3d v = VaMathUtil.matrice4ToTrans(matrix);
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
			d = VaMathUtil.anglesToMatrice(angles);

			Matrix3d PIrotation = new Matrix3d();
			PIrotation.rotY(Math.PI);
			d.mul(PIrotation); // Rotates of PI
			d.mul(-1D); // mirroring
		} else {
			d = VaMathUtil.matrix4ToMatrix3(matrix);
		}

		// ML start fixed pview issues for volvo

		float[] f = new float[9];
		f = VaMathUtil.getOrientationFromMatrix4d(matrix);
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
			theFMat33 = new FMat33(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8]);
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

	public static void activate(ShapeInstance shapeInstance) throws MessageProtocolException {
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
	public VaPViewImpl getPviewImpl() {
		return pviewImpl;
	}

	public void linkPviewToTree() {
		try {
			Tree pvTree = VaPViewFactory.getPViewImpl4MBOM().getWorld().GetTree();
			VaTree cmtree = this.tree;
			mirrorTreeForCad = new JTree();
//			mirrorTreeForCad.removeAll();
			DefaultMutableTreeNode dftMtbTNRoot = new DefaultMutableTreeNode("Mirror");
			DefaultTreeModel treeModel = new DefaultTreeModel(dftMtbTNRoot);
			mirrorTreeForCad.setModel(treeModel);
			// log.debug(pvTree.GetRoot().GetName());
			// uppackageAllNode(cmtree);
//			insMap.clear();
			printTree(pvTree.GetRoot(), dftMtbTNRoot,0);
//			 JDialog dia = new JDialog();
//			 dia.setTitle("mirror");
//			 JScrollPane diaJsp = new JScrollPane(mirrorTreeForCad);
//			 dia.add(diaJsp);
//			 dia.setSize(400,800);
//			 dia.setVisible(true);
			VaTreeNode root = cmtree.getRoot();

			Enumeration<VaTreeNode> child = root.children();
			while (child.hasMoreElements()) {
				VaTreeNode cmNode = child.nextElement();
				log.debug("occPath : " + cmNode.getOccpath());

				if (cmNode.getOccpath() == null) {
					continue;
				} else {
					String idPath = cmNode.getOccpath().replaceFirst("-1", "");
					log.debug("idPath : " + idPath);
					if (idPath.equals("") || idPath.indexOf("+") < 0) {
						idPath = "/";
					}
					setPviewNode(idPath, cmNode);
				}

				Enumeration<VaTreeNode> child2 = cmNode.depthFirstEnumeration();
				while (child2.hasMoreElements()) {
					VaTreeNode childNode = child2.nextElement();
					log.debug("occPath : " + childNode.getOccpath());
					String idPath = "";
					if ("false".equals(VaContext.getPbomSaved())) {
						// String pbomOccId =
						// VaTree.getPbomOccId(childNode.getPart().getOid());
						//String tempOccPath = childNode.getOccpath().replace(cmNode.getOccpath(), "");
						String tempOccPath = childNode.getOccpath();
						// childNode.setOccId(childNode.getOccpath());

						if (tempOccPath.startsWith("-1")) {
							idPath = tempOccPath.replaceFirst("-1", "");
						} else {
							idPath = tempOccPath;
						}
						if (idPath.equals("")) {
							idPath = "/";
						}
						setPviewNode(idPath, childNode);
					} else {
						setPviewNodeForSaved(childNode.getOccpath(), childNode);
						// setPviewNode(idPath, childNode, insMap);
					}
					// Enumeration<VaTreeNode> child3 =
					// childNode.depthFirstEnumeration();
					// while (child3.hasMoreElements()) {
					// VaTreeNode children = child3.nextElement();
					// log.debug("occPath : " + children.getOccpath());
					//
					// String idPath2="";
					// if(children.getOccpath().startsWith("0+0"))
					// {
					// idPath2 = children.getOccpath().replace("0+0", idPath);
					// }
					// if (children.getOccpath().startsWith("0")) {
					// idPath2 = children.getOccpath().replaceFirst("0", "");
					// }
					// else{
					// idPath2 = children.getOccpath();
					// }
					// if (idPath2.equals("")) {
					// idPath2 = "/";
					// }
					// setPviewNode(idPath2, children, insMap);
					// }

				}
				// processPackage(idPath, cmNode, pvTree);

				// String partNumber =
				// nIns.GetName().substring(0,nIns.GetName().indexOf('.'));
				// if(cmNode.getPart().getPartNumber().equals(partNumber)){
				// cmNode.setOccpath(ins.GetIDPath());
				// cmNode.set_pviewComponentInstance(ci);
				// cmNode.set_pviewComponentNode(cn);
				// ShapeInstance_holder sh = new ShapeInstance_holder();
				// VaPViewFactory.getPViewImpl4MBOM().getWorld().GetFirstShapeScene().GetShapeInstance(ins,
				// sh);
				// cmNode.set_pviewShapeInstance(sh.value);
				// break;
				// }

			}
			this.tree.getRoot().setSelected(true);
			this.stree.getRoot().setSelected(true);
			// Instance ins = tree.GetRoot();
			cmtree.repaint();
			this.stree.repaint();

			VaPViewScenePanel panel = pviewImpl.getPanelContext();
			VaPViewScenesPanel sPanel = null;
			Container jc = panel.getParent();
			while (jc != null) {
				if (jc instanceof VaPViewScenesPanel) {
					sPanel = (VaPViewScenesPanel) jc;
					break;
				}
				jc = jc.getParent();
			}
			if (sPanel != null) {
				sPanel.add(pviewImpl.initMenuBar(), BorderLayout.SOUTH);
				sPanel.revalidate();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void printTree(Instance ins,DefaultMutableTreeNode node, int i) {
		try {
			int count = i + 1;
			//log.debug("instance " + ins.GetIDPath() + " : " + ins.GetName() + ":::" + count + "::::");
//			map.put(ins.GetIDPath(), ins);

			VaPviewInstance instance = new VaPviewInstance();
			instance.setIdPath(ins.GetIDPath());
			instance.setName(ins.GetName());
			instance.setLocation(ins.GetComponentInstance().GetLocation());
			instance.setInstance(ins);
			String fileSource = "";
			if(ins.GetComponentNode() != null){
				ShapeSource ss = ins.GetComponentNode().GetShapeSource();
				if(ss != null){
					fileSource = ss.GetFileSource();
				}
			}
			instance.setOlFileSource(fileSource);
			node.setUserObject(instance);
			Instance nIns = ins.GetFirstChild();

			if (nIns != null) {
				Instance eIns = nIns;

				while (eIns != null) {
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
					node.add(childNode);
					printTree(eIns, childNode,count);
					eIns = eIns.GetNextSibling();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	// public boolean uppackageAllNode(VaTree tree) {
	// boolean flag = uppackageTheNode(tree.getRoot(),false);
	// VaCommonStringUtil.sortTheTreeNode(tree.getRoot());
	// tree.updateUI();
	// return flag;
	// }

	// @SuppressWarnings("unchecked")
	// public boolean uppackageTheNode(VaTreeNode node,boolean flag) {
	// if(VaCommonStringUtil.isPackage(node)){
	// flag = true;
	// VaTreeNode parent = (VaTreeNode) node.getParent();
	// List<VaTreeNode> list = node.getListNode();
	// for (VaTreeNode brotherNode : list) {
	// parent.add(brotherNode);
	// }
	// node.setListNode(null);
	// }
	// Enumeration children = node.children();
	// while (children.hasMoreElements()) {
	// VaTreeNode cmnode = (VaTreeNode) children.nextElement();
	// flag = uppackageTheNode(cmnode,flag);
	// }
	// return flag;
	// }

	// private void processPackage(String idPath, VaTreeNode node, Tree t)
	// throws Exception {
	//
	// // setPviewNode(idPath, node, t);
	// if (VaCommonStringUtil.isPackage(node)) {
	//
	// List<VaTreeNode> nodeList = node.getListNode();
	// for (int i = 0; i < nodeList.size(); i++) {
	// // String idPathC = nodeList.get(i).getOccpath() == null ? ""
	// // : nodeList.get(i).getOccpath().replaceFirst("0", "");
	// Enumeration<VaTreeNode> temp = nodeList.get(i).children();
	// while (temp.hasMoreElements()) {
	// VaTreeNode cmTreeNode = (VaTreeNode) temp.nextElement();
	// String idPathTemp = cmTreeNode.getOccpath() == null ? ""
	// : cmTreeNode.getOccpath().replaceFirst("0", "");
	// processPackage(idPathTemp, cmTreeNode, t);
	// }
	//
	// }
	// }
	//
	//
	// }
	private void setPviewNodeForSaved(String idPath, VaTreeNode cmNode) throws Exception {

		// idPath = idPath.replace('+', '/');
		log.debug("idPath : " + idPath + "; name:" + cmNode.getPart().getName());
		// Instance ins = tree.FindInstance(idPath);
		Instance ins = null;
		DefaultMutableTreeNode mrTreeRoot = (DefaultMutableTreeNode)mirrorTreeForCad.getModel().getRoot();
		Enumeration<DefaultMutableTreeNode> children = mrTreeRoot.breadthFirstEnumeration();

		while(children.hasMoreElements()){
			VaPviewInstance child = (VaPviewInstance)(children.nextElement()).getUserObject();
			String nodeIdpath = child.getIdPath();
			log.debug("nodeIdpath : " + nodeIdpath);
			if(nodeIdpath.endsWith(idPath)){
				ins = child.getInstance();
				break;
			}
		}
		log.debug("ins : " + ins);
		if (ins == null) {
			return;
		}
		cmNode.set_pviewComponentInstance(ins.GetComponentInstance());
		cmNode.set_pviewComponentNode(ins.GetComponentNode());
		ShapeInstance_holder sh = new ShapeInstance_holder();
		VaPViewFactory.getPViewImpl4MBOM().getWorld().GetFirstShapeScene().GetShapeInstance(ins, sh);
		cmNode.set_pviewShapeInstance(sh.value);

		Collection<String[]> properties = new ArrayList<String[]>();
		VaPropertiesVisitorImpl propertiesVisitor = VaPViewFactory.getPViewImpl4MBOM().getPropertiesVisitor(properties);

		ins.Visit(propertiesVisitor);
		cmNode.set_pviewInstanceProperties(properties);

		if (sh.value != null) {
			log.debug("URL : " + pvsPath + ins.GetComponentNode().GetShapeSource().GetFileSource());
			// URL url = new URL("file://" + pvsPath +
			// ins.GetComponentNode().GetShapeSource().GetFileSource());
			DPViewStructureGenerator.id2url.put(cmNode.getPart().getOid(), pvsPath
					+ ins.GetComponentNode().GetShapeSource().GetFileSource());

			VaPViewFactory.getPViewImpl4MBOM().getBoundingBox(idPath);
			// FBox fbox = ins.GetComponentNode().GetShapeSource().GetBBox();
			//
			// if (fbox != null) {
			// Point3d p1 = new Point3d(fbox.GetMin(0), fbox.GetMin(1),
			// fbox.GetMin(2));
			// Point3d p2 = new Point3d(fbox.GetMax(0), fbox.GetMax(1),
			// fbox.GetMax(2));
			//
			// log.debug("Xmin : " + fbox.GetMin(0) + ", Ymin : " +
			// fbox.GetMin(1) + ", " + "Zmin : " + fbox.GetMin(2));
			// log.debug("Xmax : " + fbox.GetMax(0) + ", Ymax : " +
			// fbox.GetMax(1) + ", " + "Zmax : " + fbox.GetMax(2));
			// BoundingBox bbox = new BoundingBox(p1, p2);
			// Vector<BoundingBox> bBox = new Vector<BoundingBox>();
			// bBox.add(bbox);
			// cmNode.setBboxes(bBox);
			// }
		}
		tree.addPVMapping(ins, cmNode);
		// setLinkEbomTreeNode(tree, cmNode);
	}

	private void setPviewNode(String idPath, VaTreeNode cmNode) throws Exception {
		log.debug("idPath : " + idPath + "; name:" + cmNode.getPart().getName());
		idPath = idPath.replace('+', '/');
		log.debug("idPath : " + idPath + "; name:" + cmNode.getPart().getName());
		// Instance ins = tree.FindInstance(idPath);
		Instance ins = null;
		DefaultMutableTreeNode mrTreeRoot = (DefaultMutableTreeNode)mirrorTreeForCad.getModel().getRoot();
		Enumeration<DefaultMutableTreeNode> children = mrTreeRoot.breadthFirstEnumeration();

		while(children.hasMoreElements()){
			VaPviewInstance child = (VaPviewInstance)(children.nextElement()).getUserObject();
			String nodeIdpath = child.getIdPath();
			//log.debug("nodeIdpath : " + nodeIdpath);
			if(nodeIdpath.equals(idPath)){
				ins = child.getInstance();
				break;
			}
		}
		log.debug("ins : " + ins);
		if (ins == null) {
			return;
		}
		cmNode.set_pviewComponentInstance(ins.GetComponentInstance());
		cmNode.set_pviewComponentNode(ins.GetComponentNode());
		ShapeInstance_holder sh = new ShapeInstance_holder();
		VaPViewFactory.getPViewImpl4MBOM().getWorld().GetFirstShapeScene().GetShapeInstance(ins, sh);
		log.debug(cmNode+"  sh.value : " + sh.value);
		cmNode.set_pviewShapeInstance(sh.value);
		log.debug(cmNode+"  cmNode.get_pviewShapeInstance() : " + cmNode.get_pviewShapeInstance());

		Collection<String[]> properties = new ArrayList<String[]>();
		VaPropertiesVisitorImpl propertiesVisitor = VaPViewFactory.getPViewImpl4MBOM().getPropertiesVisitor(properties);

		ins.Visit(propertiesVisitor);
		cmNode.set_pviewInstanceProperties(properties);

		if (sh.value != null) {
			log.debug("URL : " + pvsPath + ins.GetComponentNode().GetShapeSource().GetFileSource());
			// URL url = new URL("file://" + pvsPath +
			// ins.GetComponentNode().GetShapeSource().GetFileSource());
			DPViewStructureGenerator.id2url.put(cmNode.getPart().getOid(), pvsPath
					+ ins.GetComponentNode().GetShapeSource().GetFileSource());

			BoundingBox bbox = VaPViewFactory.getPViewImpl4MBOM().getBoundingBox(idPath);
			Vector<BoundingBox> bBox = new Vector<BoundingBox>();
			bBox.add(bbox);
			cmNode.setBboxes(bBox);

			// FBox fbox = ins.GetComponentNode().GetShapeSource().GetBBox();
			//
			// if (fbox != null) {
			// Point3d p1 = new Point3d(fbox.GetMin(0), fbox.GetMin(1),
			// fbox.GetMin(2));
			// Point3d p2 = new Point3d(fbox.GetMax(0), fbox.GetMax(1),
			// fbox.GetMax(2));
			//
			// log.debug("Xmin : " + fbox.GetMin(0) + ", Ymin : " +
			// fbox.GetMin(1) + ", " + "Zmin : " + fbox.GetMin(2));
			// log.debug("Xmax : " + fbox.GetMax(0) + ", Ymax : " +
			// fbox.GetMax(1) + ", " + "Zmax : " + fbox.GetMax(2));
			// BoundingBox bbox = new BoundingBox(p1, p2);
			// Vector<BoundingBox> bBox = new Vector<BoundingBox>();
			// bBox.add(bbox);
			// cmNode.setBboxes(bBox);
			// }
		}
		tree.addPVMapping(ins, cmNode);
		// setLinkEbomTreeNode(tree, cmNode);
	}

	private void setLinkEbomTreeNode(VaTree ebomTree, VaTreeNode ebomNode) {
		Comparator comtor = new VaTreeNodeModelComparator();
		VaDefaultTreeLinkage link = new VaDefaultTreeLinkage(comtor);

		VaTreeNode processNode = link.findLinkNode(ebomNode, ebomTree.getLinkedProcessEBomTree());

		if (processNode != null) {
			processNode.set_pviewComponentInstance(ebomNode.get_pviewComponentInstance());
			processNode.set_pviewComponentNode(ebomNode.get_pviewComponentNode());
			processNode.set_pviewShapeInstance(ebomNode.get_pviewShapeInstance());
			processNode.set_pviewInstanceProperties(ebomNode.get_pviewInstanceProperties());
		}

	}

	@SuppressWarnings("unchecked")
	public void setSelectInstance(Object sender,Object params,CmTaskExecutorCallback callback) {
		try{
			String idpath = (String)((ArrayList)params).get(0);
			if("true".equals(VaContext.getPbomSaved())){

				DefaultMutableTreeNode mrTreeRoot = (DefaultMutableTreeNode)mirrorTreeForCad.getModel().getRoot();
				Enumeration<DefaultMutableTreeNode> children = mrTreeRoot.breadthFirstEnumeration();

				while(children.hasMoreElements()){
					VaPviewInstance child = (VaPviewInstance)(children.nextElement()).getUserObject();
					String nodeIdpath = child.getIdPath();
					if(nodeIdpath.endsWith(idpath)){
						idpath = nodeIdpath;
						break;
					}
				}

			}else{
				idpath = idpath.replaceFirst("-1", "");
				idpath = idpath.replace("+", "/");
			}

			if(idpath.equals("/")){
				log.debug(" the idpath is : / ,cant deal with it!");
				return;
			}

			boolean selectionMode = (Boolean)((ArrayList)params).get(1);
			setSelectInstance(idpath, selectionMode);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setSelectInstance(String path,boolean selection){
			try {

				DefaultMutableTreeNode mrTreeRoot = (DefaultMutableTreeNode)mirrorTreeForCad.getModel().getRoot();
				Enumeration<DefaultMutableTreeNode> children = mrTreeRoot.breadthFirstEnumeration();

				while(children.hasMoreElements()){
					VaPviewInstance child = (VaPviewInstance)(children.nextElement()).getUserObject();
					String nodeIdpath = child.getIdPath();
					if(nodeIdpath.startsWith(path)){
						Instance inst = child.getInstance();
						ShapeInstance_holder sh = new ShapeInstance_holder();
						pviewImpl.getPanelContext().getShapeScene().GetShapeInstance(inst, sh);
						if(sh.value != null){
							sh.value.SetHighlight(selection);
						}
					}
				}
			} catch (MessageProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ActorShutdownException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidActorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConnectionLostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	public boolean isExecutorActive(){
		return executorActive;
	}
	public void setExecutorActive(boolean executorActive){
		this.executorActive = executorActive;
	}

	protected void registerTaskExecutor() throws CmTaskException {
		CmTaskHelper.registerTaskExecutor("VaEPViewPVSGenerator.setSelectInstance", this,true);
	}

	protected void unregisterTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub

	}

	// private VaTreeNode clonePViewProperties(VaTreeNode source) throws
	// Exception {
	// VaTreeNode target;
	// if(source.get_pviewComponentNode() != null &&
	// source.get_pviewComponentNode().GetShapeSource() != null)
	// target = new VaTreeNode(pvsPath + File.separator +
	// source.get_pviewComponentNode().GetShapeSource().GetFileSource());
	// else
	// target = new VaTreeNode(source.getUserObject());
	// target.setMatrix(source.getMatrix());
	// target.setOccId(source.getOccId());
	// target.setOccpath(source.getOccpath());
	//
	// copyInstanceProperties(source,target);
	//
	// return target;
	// }

	// private void getInstanceProperties(VaTreeNode node,Instance ins) {
	// Collection<String[]> properties = new ArrayList<String[]>();
	// VaPropertiesVisitorImpl propertiesVisitor =
	// VaPViewFactory.getPViewImpl(VaPViewFactory.PV_NAME_MBOM).getPropertiesVisitor(properties);
	// try {
	// if(source.get_pviewComponentInstance().GetInstance() != null)
	// source.get_pviewComponentInstance().GetInstance().Visit(propertiesVisitor);
	// else
	// source.get_pviewShapeInstance().GetInstance().Visit(propertiesVisitor);
	// } catch(Exception e) {
	// e.printStackTrace();
	// }
	//
	// target.set_pviewInstanceProperties(properties);
	// }
}
