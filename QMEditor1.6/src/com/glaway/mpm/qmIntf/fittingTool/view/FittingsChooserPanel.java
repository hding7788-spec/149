/*
 * @author wanghaoyu
 * @date 2013-4-17
 * 版权属 南京国睿信维软件有限公司 所有
 */

package com.glaway.mpm.qmIntf.fittingTool.view;

import com.glaway.mpm.qmIntf.participatePart.ParticipatePartAddDialog;
import com.glaway.mpm.task.CmTaskExecutor;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.visual.view.pview.VaPViewFactory;
import com.glaway.mpm.visual.view.pview.VaPViewImpl;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.ui.VaEBomTreePanel;
import com.glaway.mpm.visual.view.ui.VaPVNotInstalledPanel;
import com.glaway.mpm.visual.view.ui.VaPViewSearchPanel;
import wt.method.RemoteMethodServer;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class FittingsChooserPanel extends JPanel implements CmTaskExecutor {
	private static final long			serialVersionUID	= -1742766009549726405L;
	// for CmTaskExecutor -> start
	private volatile boolean			executorActive;
	// for CmTaskExecutor -> end
	// private static final VaLogger log =
	// VaLogger.getLogger(FittingsChooserPanel.class);
	// private FittingsChooserPanel frame = null;
	private static VaActionProgressBar	animFrame			= null;
	public static final int				TEXTFIELD_SIZE		= 15;
	public static final int				TEXTFIELD_SIZE2		= 30;
	private static VaTree						ebomTree;
	private String						curPartNumber;
	private VaEBomTreePanel				jScrollPane1;
	public VaEBomTreePanel getjScrollPane1() {
		return jScrollPane1;
	}

	public void setjScrollPane1(VaEBomTreePanel jScrollPane1) {
		this.jScrollPane1 = jScrollPane1;
	}

	private JSplitPane					jSplitPane1;
	private Window						mainOwner;

	public String getCurPartNumber() {
		return curPartNumber;
	}

	public void setCurPartNumber(String curPartNumber) {
		this.curPartNumber = curPartNumber;
	}

	public static void showDialog(Map<String, String> map1) {

		// final Map<String, String> map = map1;
		Thread runThread = new Thread() {
			public void run() {

				// VaTreeNode root =
				// FittingsChooserPanel.getInstance().jScrollPane1.getTree().getRoot();
				//
				// if (map != null && map.get("xmlPath") != null) {
				// String filePath = map.get("xmlPath");
				// FittingsChooserPanel.getInstance().setUsedNodeTree(root,
				// FittingsChooserPanel.getInstance().getPPartFromXML(filePath));
				// }
				// FittingsChooserPanel.getInstance().setVisible(true);
				// FittingsChooserPanel.getInstance().jScrollPane1.getTree().repaint();
				// animFrame.finish();
				// animFrame.dispose();
			}
		};

		animFrame = new VaActionProgressBar(null, "加载装配工具", "加载装配工具", "正在加载装配工具，请稍候...");
		runThread.start();
		animFrame.setVisible(true);
		// progressBar.setVisible(false);
	}

	/**
	 * Creates new form VaMainframe
	 */
	public FittingsChooserPanel(HashMap<String, String> m, Window mainOwner) {
		this.mainOwner = mainOwner;
		this.setEnabled(false);
		initComponents();
		if (m != null && m.get("xmlPath") != null) {
			// String filePath = m.get("xmlPath").toString();
			// setUsedNodeTree(this.getPBomTree().getRoot(),
			// getPPartFromXML(filePath));
		}
		this.setEnabled(true);
	}

	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		this.setLayout(new BorderLayout());

		jSplitPane1 = new javax.swing.JSplitPane();

		jScrollPane1 = new VaEBomTreePanel(mainOwner);
		ebomTree = jScrollPane1.getEbomTree();
		jScrollPane1.getTree().setRootVisible(false);
		jScrollPane1.getTree().setLinkedEBomTree(ebomTree);
		// jPanel2 = new //javax.swing.JPanel();
		/** init pview **/

		if (VaPViewImpl.isPviewInitialized()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					jSplitPane1.setRightComponent(new VaPViewSearchPanel(VaPViewFactory.PV_NAME_MBOM));
				}
			});
		} else
			jSplitPane1.setRightComponent(new VaPVNotInstalledPanel());

		jSplitPane1.setLeftComponent(jScrollPane1);
		jSplitPane1.setDividerLocation(1000);
		this.add(jSplitPane1, BorderLayout.CENTER);
		initListener();
		validate();
	}// </editor-fold>

	// private void setUsedNodeTree(VaTreeNode node, Vector usedList) {
	//
	// if (usedList != null && usedList.contains(node.getOccId())) {
	// node.setUsed(true);
	// } else {
	// node.setUsed(false);
	// }
	// int count = node.getChildCount();
	// for (int i = 0; i < count; i++) {
	// VaTreeNode child = (VaTreeNode) node.getChildAt(i);
	// setUsedNodeTree(child, usedList);
	// }
	// }

	// private Vector getPPartFromXML(String filePath) {
	// try {
	// Vector v = new Vector();
	// SAXReader reader = new SAXReader();
	// InputStream is = new FileInputStream(filePath);
	// org.dom4j.Document document = reader.read(is);
	//
	// // 获得节点的命名空间
	// String uri = document.getRootElement().getNamespaceURI();
	// // 将uri存入map中
	// HashMap map = new HashMap();
	// map.put("xx", uri);
	//
	// org.dom4j.XPath xpath = DocumentHelper
	// .createXPath("/xx:technics/xx:QMFawTechnicsInfo/xx:steps/xx:QMProcedureInfo/xx:paces/xx:QMProcedureInfo/xx:parts/xx:QMPartInfo");
	// xpath.setNamespaceURIs(map);
	//
	// List list = xpath.selectNodes(document);
	// for (int i = 0; i < list.size(); i++) {
	// // Node node = (Node)list.get(i);//转型为Node
	// Element e = (Element) list.get(i);// 转型为Element
	// log.debug(e.attributeValue("occId"));
	// if (!e.attributeValue("occId").trim().equals("")) {
	// String[] occIds = e.attributeValue("occId").split(",");
	// for (int j = 0; j < occIds.length; j++) {
	// v.add(occIds[j]);
	// }
	// }
	//
	// }
	// log.debug(v);
	// return v;
	// } catch (Exception e) {
	// log.error(e);
	// return null;
	// }
	// }

	// public void searchButtonPerformed(ActionEvent e) {
	// log.debug("searchButtonPerformed");
	// VaTreeNode root = jScrollPane1.getTree().getRoot();
	// if (e.getSource().equals(jButton1)) {
	// log.debug("jButton1 search");
	// String x1s = getX1();
	// String x2s = getX2();
	// String y1s = getY1();
	// String y2s = getY2();
	// String z1s = getZ1();
	// String z2s = getZ2();
	//
	// if (isSpace(x1s) && isSpace(x2s) && isSpace(y1s) && isSpace(y2s) &&
	// isSpace(z1s) && isSpace(z2s)) {
	// log.debug("空间范围坐标值全部为空，不进行检索操作！");
	// return;
	// }
	//
	// Point3d lower = new Point3d();
	// Point3d upper = new Point3d();
	// lower.setX(Double.valueOf(isSpace(x1s) ? "0.0" : x1s) / 1000.0);
	// lower.setY(Double.valueOf(isSpace(y1s) ? "0.0" : y1s) / 1000.0);
	// lower.setZ(Double.valueOf(isSpace(z1s) ? "0.0" : z1s) / 1000.0);
	// upper.setX(Double.valueOf(isSpace(x2s) ? "0.0" : x2s) / 1000.0);
	// upper.setY(Double.valueOf(isSpace(y2s) ? "0.0" : y2s) / 1000.0);
	// upper.setZ(Double.valueOf(isSpace(z2s) ? "0.0" : z2s) / 1000.0);
	//
	// BoundingBox bbox = new BoundingBox();
	// bbox.setLower(lower);
	// bbox.setUpper(upper);
	//
	// List<BoundingBox> bboxs = new ArrayList<BoundingBox>();
	// bboxs.add(bbox);
	// check(root, bboxs);
	//
	// } else if (e.getSource().equals(jButton2)) {
	// log.debug("jButton2 search");
	// String referenceStr = getReferenceString();
	// String instanceStr = getInstanceString();
	// String distanceStr = getDistanceString();
	//
	// List<BoundingBox> bboxs = new ArrayList<BoundingBox>();
	// getBondingBoxes(root, referenceStr, instanceStr, bboxs);
	// double distance = 0.0;
	// boolean flag = false;
	// if (distanceStr != null && distanceStr.trim().length() > 0) {
	// distance = Double.valueOf(distanceStr) / 1000.0;
	// flag = true;
	// }
	// if (flag) {
	// for (BoundingBox box : bboxs) {
	// Point3d lower = new Point3d();
	// Point3d upper = new Point3d();
	// box.getLower(lower);
	// box.getUpper(upper);
	//
	// lower.setX(lower.getX() - distance);
	// lower.setY(lower.getY() - distance);
	// lower.setZ(lower.getZ() - distance);
	//
	// upper.setX(upper.getX() + distance);
	// upper.setY(upper.getY() + distance);
	// upper.setZ(upper.getZ() + distance);
	//
	// box.setLower(lower);
	// box.setUpper(upper);
	// }
	// }
	// check(root, bboxs);
	// } else if (e.getSource().equals(jButton3)) {
	// log.debug("jButton3 search");
	// String partNum = this.jTextField11.getText().trim();
	// String partName = this.jTextField12.getText().trim();
	// String partVer = this.jTextField13.getText().trim();
	// log.debug("input " + partNum + "," + partName + "," + partVer);
	// if (partNum.equals("") && partName.equals("") && partVer.equals("")) {
	// JOptionPane.showMessageDialog(null, "请输入查询条件!");
	// return;
	// }
	//
	// checkAttr(root, partNum, partName, partVer);
	// }
	// jScrollPane1.getTree().repaint();
	// }

	// private void getBondingBoxes(VaTreeNode node, String referenceStr, String
	// instanceStr, List<BoundingBox> bboxs) {
	// boolean isInstanceStrNull = true;
	// if (instanceStr != null && instanceStr.trim().length() > 0) {
	// isInstanceStrNull = false;
	// }
	// if (node.getPart().getNumber().equals(referenceStr)) {
	// Vector vec = node.getBboxes();
	// if (!isInstanceStrNull) {
	// if (node.getOccId().equals(instanceStr)) {
	// if (vec != null && vec.size() >= 1) {
	// BoundingBox box = (BoundingBox) vec.get(0);
	// if (box != null) {
	// Point3d lower = new Point3d();
	// Point3d upper = new Point3d();
	// box.getLower(lower);
	// box.getUpper(upper);
	// BoundingBox boxtoAdd = new BoundingBox(lower, upper);
	// bboxs.add(boxtoAdd);
	// }
	// }
	// }
	// } else {
	// if (vec != null && vec.size() >= 1) {
	// BoundingBox box = (BoundingBox) vec.get(0);
	// if (box != null) {
	// Point3d lower = new Point3d();
	// Point3d upper = new Point3d();
	// box.getLower(lower);
	// box.getUpper(upper);
	// BoundingBox boxtoAdd = new BoundingBox(lower, upper);
	// bboxs.add(boxtoAdd);
	// }
	// }
	// }
	// }
	// int count = node.getChildCount();
	// for (int i = 0; i < count; i++) {
	// VaTreeNode temp = (VaTreeNode) node.getChildAt(i);
	// getBondingBoxes(temp, referenceStr, instanceStr, bboxs);
	// }
	// }

	// private void check(VaTreeNode node, List<BoundingBox> bboxs) {
	// Vector vec = node.getBboxes();
	// node.setSelected(false);
	// if (vec != null) {
	// BoundingBox tocheckbbox = (BoundingBox) vec.get(0);
	// for (BoundingBox box : bboxs) {
	// // if (tocheckbbox.intersect(box)) {
	// if (box.intersect(tocheckbbox)) {
	// node.setSelected(true);
	// }
	// }
	// }
	// int count = node.getChildCount();
	// for (int i = 0; i < count; i++) {
	// VaTreeNode child = (VaTreeNode) node.getChildAt(i);
	// check(child, bboxs);
	// }
	// }

	// private void checkAttr(VaTreeNode node, String pNumber, String pName,
	// String pVersion) {
	//
	// node.setSelected(false);
	// log.debug("node:" + node.getPart().getName() + "," + pNumber + ","
	// + pName + "," + pVersion);
	// log.debug("node info:" + node.getPart().getName() + ","
	// + node.getPart().getNumber() + ","
	// + node.getPart().getVersion());
	//
	// boolean flag1 = node.getPart().getNumber().indexOf(pNumber) > -1 ? true
	// : false;
	// boolean flag2 = node.getPart().getName().indexOf(pName) > -1 ? true
	// : false;
	// boolean flag3 = node.getPart().getVersion().indexOf(pVersion) > -1 ? true
	// : false;
	//
	// node.setSelected(flag1 && flag2 && flag3);
	//
	// int count = node.getChildCount();
	// if (!(flag1 && flag2 && flag3)) {
	// for (int i = 0; i < count; i++) {
	// VaTreeNode child = (VaTreeNode) node.getChildAt(i);
	// checkAttr(child, pNumber, pName, pVersion);
	// }
	// }
	// }

	// private boolean isSpace(String str) {
	// if (str == null || str.trim().length() == 0) {
	// return true;
	// }
	// return false;
	// }

	@Override
	public boolean isExecutorActive() {
		// TODO Auto-generated method stub
		return executorActive;
	}

	@Override
	public void setExecutorActive(boolean executorActive) {
		// TODO Auto-generated method stub
		this.executorActive = executorActive;
	}

	private void initListener() {
		// if (owner.isEdit()) {
		// initDragListener();
		// }
		// initTreeSelectionListener();
		// initMouseListener();
	}

	/**
	 * 配置VaTree的选择监听,包含联动控制
	 */
	// private void initTreeSelectionListener() {
	// ebomTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	// jScrollPane1.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	//
	// // Comparator comtor = new VaTreeNodeComparator();
	// Comparator comtor2 = new VaTreeNodeModelComparator();
	// VaTreeLinkage linkage_etree = new
	// VaDefaultTreeLinkage(comtor2).addLinkageTree(jScrollPane1.getTree());
	// VaTreeLinkage linkage_mtree = new
	// VaDefaultTreeLinkage(comtor2).addLinkageTree(ebomTree);
	//
	// VaTreeLinkage linkage_model = new
	// VaTreeModelLinkage(comtor2).addLinkageTree(ebomTree);
	//
	// TreeSelectionListener treeSelectListenre_etree = new
	// VaTreeSelectionListener(linkage_etree);
	// TreeSelectionListener treeSelectListenre_mtree = new
	// VaTreeSelectionListener(linkage_mtree);
	// TreeModelListener treeModelListener_mtree = new
	// VaTreeModelListener(linkage_model);
	//
	// ebomTree.addTreeSelectionListener(treeSelectListenre_etree);
	// jScrollPane1.getTree().addTreeSelectionListener(treeSelectListenre_mtree);
	// jScrollPane1.getTree().getModel().addTreeModelListener(treeModelListener_mtree);
	// }

	public VaTree getPBomTree() {
		return this.jScrollPane1.getTree();
	}

	public static VaTree getEBomTree() {
		return ebomTree;
	}

	// /**
	// *配置VaTree的鼠标监听,包含m视图新节点创建控制
	// */
	// private void initMouseListener() {
	// CmMenuItemFactory itemEFactory = new
	// CmEBOMenuItemFactory(owner.isEdit());
	// MouseListener eAdapter = new CmTreeMouseAdapter(owner, itemEFactory);
	// getEBomTree().addMouseListener(eAdapter);
	//
	// CmMenuItemFactory itemMFactory = new
	// CmMBomMMenuItemFactory(owner.isEdit());
	// MouseListener mAdapter = new CmTreeMouseAdapter(owner, itemMFactory);
	// getMBomTree().addMouseListener(mAdapter);
	// // getMBomTree().addMouseListener(new
	// // CmMBomTreeMouseAdapter(panelMBomTree,owner.isEdit()));
	// }

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		/*
		 * Set the Nimbus look and feel
		 */
		// <editor-fold defaultstate="collapsed"
		// desc=" Look and feel setting code (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase
		 * /tutorial/uiswing/lookandfeel/plaf.html
		 */
		// try {
		// for (javax.swing.UIManager.LookAndFeelInfo info :
		// javax.swing.UIManager
		// .getInstalledLookAndFeels()) {
		// if ("Nimbus".equals(info.getName())) {
		// javax.swing.UIManager.setLookAndFeel(info.getClassName());
		// break;
		// }
		// }
		// } catch (ClassNotFoundException ex) {
		// java.util.logging.Logger.getLogger(VaMainframe.class.getName())
		// .log(java.util.logging.Level.SEVERE, null, ex);
		// } catch (InstantiationException ex) {
		// java.util.logging.Logger.getLogger(VaMainframe.class.getName())
		// .log(java.util.logging.Level.SEVERE, null, ex);
		// } catch (IllegalAccessException ex) {
		// java.util.logging.Logger.getLogger(VaMainframe.class.getName())
		// .log(java.util.logging.Level.SEVERE, null, ex);
		// } catch (javax.swing.UnsupportedLookAndFeelException ex) {
		// java.util.logging.Logger.getLogger(VaMainframe.class.getName())
		// .log(java.util.logging.Level.SEVERE, null, ex);
		// }
		// </editor-fold>

		/*
		 * Create and display the form
		 */
		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("wcadmin");
		// ParticipatePartAddDialog.showDialog(null, null);
		// ParticipatePartAddDialog.getInstance().setVisible(true);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("oid", "584762");
		map.put("partNumber", "AL2_907_1460");
		// map.put("oid", "584916");
		// map.put("partNumber", "AL2_850_760");
		map.put("xmlPath", "C:\\Users\\Administrator\\Desktop\\AL2_850_760`波束选择板装配工艺`装配工艺`AL2_850_760`多基地面雷达.xml");
		ParticipatePartAddDialog.showDialog(map, null);
	}
}
