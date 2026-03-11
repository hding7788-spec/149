package com.glaway.mpm.pbombuilder.action;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import org.apache.commons.collections.CollectionUtils;

import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.pview.CmPViewFactory;
import com.glaway.mpm.pbombuilder.pview.CmPViewGenerator;
import com.glaway.mpm.pbombuilder.pview.CmPViewImpl;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeCancelAction;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmFileUtil;
import com.glaway.mpm.pbombuilder.util.CmMathUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;
import com.ptc.pview.dg.DPoint3D;
import com.ptc.pview.dg.FMat33;
import com.ptc.pview.dg.Location;
import com.ptc.pview.pvkapp.AsyncEventCB;
import com.ptc.pview.pvkapp.ComponentInstance;
import com.ptc.pview.pvkapp.ComponentNode;
import com.ptc.pview.pvkapp.Instance;
import com.ptc.pview.pvkapp.ShapeInstance;
import com.ptc.pview.pvkapp.ShapeScene;
import com.ptc.pview.pvkapp.ShapeView;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.ConnectionLostException;
import com.ptc.pview.utils.dom.InvalidActorException;
import com.ptc.pview.utils.dom.MessageProtocolException;

public class CmPPViewStructureGenerator implements CmPViewGenerator {
	public static final String		PVIEW_TEMP_PATH	= new File(System.getProperty("java.io.tmpdir") + "\\"
															+ CmConnectFrame.partOid + "\\").getAbsolutePath();
	private static final CmLogger	log				= CmLogger.getLogger(CmEPViewPVSGenerator.class);
	private static boolean			interrupted		= false;
	private CmPViewImpl				pviewImpl;
	private CmTreeNode				rootNode;
	private Set<String>				olFileList;
	private SavePBomPViewDialog		dialog;
	private Window					dialogParent;

	public CmPPViewStructureGenerator(CmTreeNode rootNode, Window dialogParent) {
		pviewImpl = CmPViewFactory.getPviewImpl4PBOM();
		this.rootNode = rootNode;
		this.dialogParent = dialogParent;
		this.displayDialog();
	}

	@Override
	public void generatePVStructure(String finishFrame) {
		olFileList = new HashSet<String>();
		try {
			pviewImpl.resetPvWorld();
		} catch (ConnectionLostException e) {
			e.printStackTrace();
		} catch (ActorShutdownException e) {
			e.printStackTrace();
		} catch (InvalidActorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		pviewImpl.waitforClientIntialized();

		ComponentNode theRootComponentNode;
		try {
			theRootComponentNode = buildComponentNode(rootNode.getPart().getPartNumber());
			if (CollectionUtils.isNotEmpty(rootNode.get_pviewInstanceProperties())) {
				for (String[] property : rootNode.get_pviewInstanceProperties())
					theRootComponentNode.AddProperty(property[0], property[1], property[2]);
			}

			pviewImpl.getStructure().SetRoot(theRootComponentNode);
			rootNode.set_pviewComponentNode(theRootComponentNode);

//			if (CmConnectFrame.bigassemble) {// 大装配
//				processChildrenForBigAssemble(rootNode);
//			} else {// 普通装配
				processChildren(rootNode);
//			}

			ShapeView shapeView = pviewImpl.getPanelContext().getShapeView();
			AsyncEventCB zoomAllAsyncEvent = pviewImpl.getAsyncEvent(finishFrame);

			shapeView.ZoomAll(zoomAllAsyncEvent.GetAsyncEventIf());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 普通装配构建新的PVS结构
	 *
	 * @param parentNode
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void processChildren(CmTreeNode parentNode) throws Exception {
		if (interrupted)
			return;

		Enumeration<CmTreeNode> children = parentNode.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = children.nextElement();
			// PviewTask.postTask("mainframe.setStatus","正在处理节点" + child);
			processChildrenPiview(child, parentNode);

			// 打包后的兄弟节点
			if (CmCommonStringUtil.isPackage(child)) {
				List<CmTreeNode> list = child.getListNode();
				for (CmTreeNode brother : list) {
					processChildrenPiview(brother, parentNode);
				}
			}
		}
	}

	// private void processPackageParent(CmTreeNode leafNode){
	// if(leafNode.isLeaf()){
	// CmTreeNode parent = (CmTreeNode)child.getParent();
	// if(parent.toString().equals(rootNode)){
	// continue;
	// }else if(CmCommonStringUtil.isPackage(parent)){
	// List<CmTreeNode> list = parent.getListNode();
	// for (CmTreeNode brother : list) {
	// processChildrenPiview(brother, parentNode);
	// }
	// }
	// }
	// }

	/**
	 * 大装配构建新的PVS结构
	 *
	 * @param parentNode
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void processChildrenForBigAssemble(CmTreeNode parentNode) throws Exception {
		if (interrupted)
			return;

		Enumeration<CmTreeNode> children = parentNode.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = children.nextElement();
			processChildrenPiviewForBigAssemble(child, parentNode);
			// 打包后的兄弟节点
			if (CmCommonStringUtil.isPackage(child)) {
				List<CmTreeNode> list = child.getListNode();
				for (CmTreeNode brother : list) {
					processChildrenPiviewForBigAssemble(brother, parentNode);
				}
			}
		}
	}

	private void processChildrenPiviewForBigAssemble(CmTreeNode child, CmTreeNode parentNode) throws Exception {
		if (child.children().hasMoreElements()) {// 存在树节点的组件
			// Location location = getPviewLocation(child.getMatrix());
			Location location = child.get_pviewComponentInstance().GetLocation();
			ComponentNode componentNode = buildComponentNode(child.getPart().getPartNumber());
			ComponentInstance componentInstance = getComponentInstance(parentNode.get_pviewComponentNode(),
					componentNode, child.getOccpath(), child.getPart().getPartNumber());
			componentInstance.SetLocation(location);
			if (CollectionUtils.isNotEmpty(child.get_pviewInstanceProperties())) {
				for (String[] property : child.get_pviewInstanceProperties())
					componentInstance.AddProperty(property[0], property[1], property[2]);
			}
			ShapeInstance shapeInstance = getShapeInstance(pviewImpl.getPanelContext().getShapeScene(),
					componentInstance);
			if (shapeInstance != null)
				shapeInstance.SetVisibility(true);

			child.set_pviewComponentNode(componentNode);
			child.set_pviewComponentInstance(componentInstance);
			child.set_pviewShapeInstance(shapeInstance);
			processChildrenForBigAssemble(child);
		} else {// 不存在树节点的组件以及零件
			if (null != child.get_pviewComponentInstance()) {
				// Location location = getPviewLocation(child.getMatrix());
				// Location location =
				// getPviewLocationForBigAssemble(child.get_pviewComponentInstance().GetInstance());
				Location location = child.get_pviewComponentInstance().GetLocation();
				Instance ins = child.get_pviewComponentInstance().GetInstance();

				if (ins == null && null != child.get_pviewShapeInstance().GetInstance()) {
					ins = child.get_pviewShapeInstance().GetInstance();
				}

				if (ins != null) {
					ComponentNode componentNode = getComponentNodeForBigAssemble(ins);

					ComponentInstance componentInstance = getComponentInstance(parentNode.get_pviewComponentNode(),
							componentNode, ins.GetIDPath(), ins.GetName());
					componentInstance.SetLocation(location);
					if (CollectionUtils.isNotEmpty(child.get_pviewInstanceProperties())) {
						for (String[] property : child.get_pviewInstanceProperties())
							componentInstance.AddProperty(property[0], property[1], property[2]);
					}
					ShapeInstance shapeInstance = getShapeInstance(pviewImpl.getPanelContext().getShapeScene(),
							componentInstance);
					if (shapeInstance != null) {
						shapeInstance.SetVisibility(true);
					}
					child.set_pviewComponentNode(componentNode);
					child.set_pviewComponentInstance(componentInstance);
					child.set_pviewShapeInstance(shapeInstance);
					processChildrenInstance(ins, child.get_pviewComponentNode());
				}

				processChildrenForBigAssemble(child);
			}
		}
	}

	/**
	 * 根据图形的structure处理未加载的子节点
	 *
	 * @param ins
	 * @param parentNode
	 */
	private void processChildrenInstance(Instance ins, ComponentNode parentNode) {
		try {
			Instance i = ins.GetFirstChild();
			while (i != null) {
				Location location = getPviewLocationForBigAssemble(i);

				ComponentNode componentNode = getComponentNodeForBigAssemble(i);

				ComponentInstance componentInstance = getComponentInstance(parentNode, componentNode, i.GetIDPath(),
						i.GetName());
				componentInstance.SetLocation(location);
				if (CollectionUtils.isNotEmpty(getInstanceProperties(i))) {
					for (String[] property : getInstanceProperties(i))
						componentInstance.AddProperty(property[0], property[1], property[2]);
				}
				ShapeInstance shapeInstance = getShapeInstance(pviewImpl.getPanelContext().getShapeScene(),
						componentInstance);
				if (shapeInstance != null) {
					shapeInstance.SetVisibility(true);
				}

				processChildrenInstance(i, componentNode);

				i = i.GetNextSibling();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 遍历图形上instance 获取instance的Properties
	 *
	 * @param ins
	 * @return
	 */
	private Collection<String[]> getInstanceProperties(Instance ins) {
		Collection<String[]> properties = new ArrayList<String[]>();
		CmPropertiesVisitorImpl propertiesVisitor = CmPViewFactory.getPViewImpl(CmPViewFactory.PV_NAME_MBOM)
				.getPropertiesVisitor(properties);
		try {
			ins.Visit(propertiesVisitor);
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
		return properties;
	}

	/**
	 * 大装配构建component node
	 *
	 * @param tempIns
	 * @return
	 * @throws Exception
	 */
	private ComponentNode getComponentNodeForBigAssemble(Instance tempIns) throws Exception {
		ComponentNode ret = buildComponentNode(tempIns.GetIDPath());
		String olFile = "";
		if (tempIns.GetComponentNode() != null && tempIns.GetComponentNode().GetShapeSource() != null) {
			olFile = getOLPathForBigAssemble(tempIns.GetComponentNode().GetShapeSource().GetFileSource());
		}
		olFileList.add(olFile);
		ret.SetShapeSource(olFile, 0, 0, 0, 0, 0, 0);
		return ret;
	}

	/**
	 * 保存可视化时图形的地址
	 *
	 * @param fileName
	 * @return
	 */
	private String getOLPathForBigAssemble(String fileName) {
		String olPath = CmPPViewStructureGenerator.PVIEW_TEMP_PATH + File.separator + fileName;
		log.debug("getOLPathForBigAssemble olPath----" + olPath);
		return olPath;
	}

	/**
	 * 获取位置
	 *
	 * @param ins
	 * @return
	 */
	private Location getPviewLocationForBigAssemble(Instance ins) {
		try {
			return ins.GetComponentInstance().GetLocation();
			// ShapeInstance_holder shapeInstanceHolder = new
			// ShapeInstance_holder();
			// boolean flag =
			// CmPViewFactory.getPViewImpl4MBOM().getWorld().GetFirstShapeScene().GetShapeInstance(ins,
			// shapeInstanceHolder);
			// if (flag) {
			// Location_holder Location_holder = new Location_holder();
			// shapeInstanceHolder.value.GetLocation(Location_holder);
			// return Location_holder.value;
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Location();
	}

	public void processChildrenPiview(CmTreeNode child, CmTreeNode parentNode) throws Exception {
		if (!child.children().hasMoreElements()) {// 底层零件
			Location location = getPviewLocation(child.getMatrix());
			ComponentNode componentNode = getComponentNode(child);

			ComponentInstance componentInstance = getComponentInstance(parentNode.get_pviewComponentNode(),
					componentNode, child.getOccpath(), child.getPart().getPartNumber());
			// ComponentInstance componentInstance =
			// getComponentInstance(parentNode.get_pviewComponentNode(),componentNode,child.getOccId(),child.getPart().getPartNumber());
			componentInstance.SetLocation(location);
			if (CollectionUtils.isNotEmpty(child.get_pviewInstanceProperties())) {
				for (String[] property : child.get_pviewInstanceProperties()){
					componentInstance.AddProperty(property[0], property[1], property[2]);
					//log.debug("零件   property[0] = " + property[0] + " :: property[1] = " + property[1] + " :: property[2] = " + property[2]);
				}
			}
			ShapeInstance shapeInstance = getShapeInstance(pviewImpl.getPanelContext().getShapeScene(),
					componentInstance);
			if (shapeInstance != null) {
				shapeInstance.SetLocation(location);
				shapeInstance.SetVisibility(true);
			}

			child.set_pviewComponentInstance(componentInstance);
			child.set_pviewComponentNode(componentNode);
			child.set_pviewShapeInstance(shapeInstance);
		} else {// 组件
			ComponentNode componentNode = buildComponentNode(child.getPart().getPartNumber());
			ComponentInstance componentInstance = getComponentInstance(parentNode.get_pviewComponentNode(),
					componentNode, child.getOccpath(), child.getPart().getPartNumber());
			// ComponentInstance componentInstance =
			// getComponentInstance(parentNode.get_pviewComponentNode(),componentNode,child.getOccId(),child.getPart().getPartNumber());
			if (CollectionUtils.isNotEmpty(child.get_pviewInstanceProperties())) {
				for (String[] property : child.get_pviewInstanceProperties()){
					componentInstance.AddProperty(property[0], property[1], property[2]);
					//log.debug("组件   property[0] = " + property[0] + " :: property[1] = " + property[1] + " :: property[2] = " + property[2]);
				}
			}
			ShapeInstance shapeInstance = getShapeInstance(pviewImpl.getPanelContext().getShapeScene(),
					componentInstance);
			if (shapeInstance != null)
				shapeInstance.SetVisibility(true);

			child.set_pviewComponentNode(componentNode);
			child.set_pviewComponentInstance(componentInstance);
			child.set_pviewShapeInstance(shapeInstance);
			processChildren(child);
		}
	}

	public static Location getPviewLocation(Matrix4d matrix) throws Exception {
		Vector3d v = CmMathUtil.matrice4ToTrans(matrix);
		Matrix3d d;
		if (!(matrix.determinant() > 0.0d)) {
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
			theFMat33 = new FMat33(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8]);
		}
		// ML end fixed pview issues for volvo
		DPoint3D thePoint3D = new DPoint3D(v.x, v.y, v.z);
		Location theLocation = new Location();
		theLocation.Set(theFMat33, thePoint3D);
		return theLocation;
	}

	private ComponentNode getComponentNode(CmTreeNode node) throws Exception {
		ComponentNode ret = buildComponentNode(node.getPart().getPartNumber());
		String olFile = node.getUserObject().toString();
		olFileList.add(olFile);
		ret.SetShapeSource(olFile, 0, 0, 0, 0, 0, 0);
		return ret;
	}

	private ComponentInstance getComponentInstance(ComponentNode aParentCN, ComponentNode aChildCN, String instanceId,
			String instanceName) throws Exception {
		ComponentInstance ret = aParentCN.AddComponentNode(aChildCN, instanceId);
		if (ret != null)
			ret.SetName(instanceName);

		return ret;
	}

	private ShapeInstance getShapeInstance(ShapeScene shapeScene, ComponentInstance aComponentInstance)
			throws Exception {
		ShapeInstance theShapeInstance = shapeScene.CreateShapeInstance(aComponentInstance.GetInstance());
		return theShapeInstance;
	}

	private ComponentNode buildComponentNode(String id) throws Exception {
		ComponentNode ret = pviewImpl.getStructure().CreateComponentNode(id, (byte) 'a');
		return ret;
	}

	@Override
	public CmPViewImpl getPviewImpl() {
		return pviewImpl;
	}

	private void displayDialog() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dialog = new SavePBomPViewDialog(CmPPViewStructureGenerator.this.dialogParent);
				dialog.setVisible(true);
			}
		});
	}

	private void saveOLZip(File zipFile, String pvs) throws IOException, InterruptedException {
		ZipOutputStream zos = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(zipFile), new Adler32()));
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			bos = new BufferedOutputStream(zos);
			for (String olFilePath : olFileList) {
				if (!CmCommonStringUtil.isEmpty(olFilePath)) {
					File olFile = new File(olFilePath);
					zos.putNextEntry(new ZipEntry(olFile.getName()));
					bis = new BufferedInputStream(new FileInputStream(olFile));
					int data;
					while ((data = bis.read()) != -1)
						bos.write(data);
					bis.close();
					bos.flush();
				}
			}
			File pvsFile = new File(pvs);
			zos.putNextEntry(new ZipEntry(pvsFile.getName()));
			Thread.sleep(1000);
			bis = new BufferedInputStream(new FileInputStream(pvsFile));
			int data;
			while ((data = bis.read()) != -1)
				bos.write(data);
			bos.flush();
		} finally {
			if (zos != null)
				zos.close();
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}
	}

	private void saveRepresentation(File zipFile) throws IOException {
		long partOid = rootNode.getPart().getOid();
		FileInputStream fis = null;
		FileChannel fc = null;
		try {
			fis = new FileInputStream(zipFile);
			fc = fis.getChannel();
			ByteBuffer buffer = fc.map(MapMode.READ_ONLY, 0, fc.size());
			byte[] data = new byte[(int) fc.size()];
			buffer.get(data);
			PBOMEditorToWCIntf.savePBomRepresentation(Long.toString(partOid), data, zipFile.getName());
		} finally {
			if (fc != null)
				fc.close();
			if (fis != null)
				fis.close();
		}
		savePViewStructure(this.rootNode);
		// this.rootNode.getPart().setStructureSaved(true);
	}

	private class SavePBomPViewDialog extends JDialog {
		private static final long	serialVersionUID	= 1L;

		SavePBomPViewDialog(Window parent) {
			super(parent);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			initComponents();
			setModal(true);
			addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					CmPViewFactory.shutdown(CmPViewFactory.PV_NAME_PBOM);
					super.windowClosing(e);
				}
			});
			CmCommonStringUtil.setMiddleOnScreenWithDialog(this);
		}

		private void initComponents() {
			JPanel jPanel2 = new JPanel();
			JButton saveBtn = new JButton();
			JLabel label = new JLabel("    注意：保存后之前操作不能撤销！");
			label.setForeground(Color.RED);

			setResizable(false);

			GroupLayout jPanel1Layout = new GroupLayout(pviewImpl.getPanelContext());
			pviewImpl.getPanelContext().setLayout(jPanel1Layout);
			jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0,
					0, Short.MAX_VALUE));
			jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0,
					547, Short.MAX_VALUE));

			jPanel2.setLayout(new GridBagLayout());
			saveBtn.setText("保存");
			saveBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						File pvsFolder = new File(PVIEW_TEMP_PATH + File.separator + "savedPVS");
						System.out.println("pvsFolder :: " + pvsFolder);
						if (!pvsFolder.exists() || !pvsFolder.isDirectory())
							pvsFolder.mkdir();
						// 保存注释集
						// saveAnno();
						String pvsPath = pvsFolder.getAbsolutePath() + File.separator
								+ rootNode.getPart().getPartNumber() + ".pvs";
						pviewImpl.savePVS(pvsPath);
						File zipFile = new File(pvsFolder + File.separator + rootNode.getPart().getPartNumber() + "_"
								+ System.currentTimeMillis() + ".zip");
						saveOLZip(zipFile, pvsPath);

						saveRepresentation(zipFile);
						savePViewStructure(CmPPViewStructureGenerator.this.rootNode);
						// CmPPViewStructureGenerator.this.rootNode.getPart().setStructureSaved(true);
						EbomTreeCancelAction.clearCancel();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					CmPViewFactory.shutdown(CmPViewFactory.PV_NAME_PBOM);
					dialog.setVisible(false);
					dialog.dispose();
				}
			});
			jPanel2.add(saveBtn, new GridBagConstraints());
			jPanel2.add(label);

			GroupLayout layout = new GroupLayout(getContentPane());
			getContentPane().setLayout(layout);
			layout.setHorizontalGroup(layout
					.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(pviewImpl.getPanelContext(), GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
							Short.MAX_VALUE).addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE));
			layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
					layout.createSequentialGroup()
							.addComponent(pviewImpl.getPanelContext(), GroupLayout.PREFERRED_SIZE,
									GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)));

			pack();
		}
	}

	/**
	 * 注释集保存
	 */
	private void saveAnno() {
		List<File> annoFileList = new ArrayList<File>();

		File pviewTempFile = new File(PVIEW_TEMP_PATH);
		FilenameFilter filter = new FileFlter("pvs");
		File[] pvsFile = pviewTempFile.listFiles(filter);
		File etbFile = null;

		if (null != pvsFile && pvsFile.length > 0) {
			String etbFileName = pvsFile[0].getName().substring(0, pvsFile[0].getName().lastIndexOf("."));
			etbFile = new File(PVIEW_TEMP_PATH + File.separator + etbFileName + ".etb");
			if (etbFile.exists()) {
				annoFileList.add(etbFile);
			}

			filter = new FileFlter("ast");
			File[] astFiles = pviewTempFile.listFiles(filter);
			if (null != astFiles && astFiles.length > 0) {
				for (File astFile : astFiles) {
					annoFileList.add(astFile);
					olFileList.add(astFile.getAbsolutePath());
					File gifFile = new File(PVIEW_TEMP_PATH + File.separator
							+ astFile.getName().substring(0, astFile.getName().lastIndexOf(".")) + ".gif");
					if (gifFile.exists()) {
						annoFileList.add(gifFile);
						olFileList.add(PVIEW_TEMP_PATH + File.separator
								+ astFile.getName().substring(0, astFile.getName().lastIndexOf(".")) + ".gif");
					}
				}
			}
		}

		FileInputStream fin = null;
		try {
			for (File file : annoFileList) {
				fin = new FileInputStream(file);
				CmFileUtil.writeInputStreamToFile(fin, PVIEW_TEMP_PATH + File.separator + "savedPVS" + File.separator
						+ file.getName());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} finally {
			if (null != fin) {
				try {
					fin.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (null != etbFile) {
			File savedPvsEtbFile = new File(PVIEW_TEMP_PATH + File.separator + "savedPVS" + File.separator
					+ etbFile.getName());
			savedPvsEtbFile.renameTo(new File(PVIEW_TEMP_PATH + File.separator + "savedPVS" + File.separator
					+ rootNode.getPart().getPartNumber() + ".etb"));
			olFileList.add(PVIEW_TEMP_PATH + File.separator + "savedPVS" + File.separator
					+ rootNode.getPart().getPartNumber() + ".etb");
		}
	}

	class FileFlter implements FilenameFilter {
		String	filterStr;

		public FileFlter(String fileterStr) {
			this.filterStr = fileterStr;
		}

		@Override
		public boolean accept(File dir, String name) {
			String fileFormat = name.substring(name.lastIndexOf(".") + 1);
			if (filterStr.equalsIgnoreCase(fileFormat)) {
				return true;
			}
			return false;
		}
	}

	/**
	 * 相同编号的零件设置相同的保存状态
	 *
	 * @author chenyunlong
	 * @date 2013-11-8
	 * @param obj
	 *
	 */
	public void savePViewStructure(CmTreeNode obj) {
		List<CmTreeNode> list = new ArrayList<CmTreeNode>();
		CmTree mtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();
		CmCommonStringUtil.getAllChildNodesFromParent(mtree.getRoot(), list);
		for (CmTreeNode pbom : list) {
			if (CmCommonNodeUtil.checkNodeIsCommon(pbom, obj)) {
				pbom.getPart().setStructureSaved(true);
				CmCommonNodeUtil.getAllChildrenOccpath(pbom);
			}
		}
	}
}