package com.glaway.mpm.pbombuilder.panel;

import com.glaway.mpm.pbombuilder.action.*;
import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.data.*;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.gui.CmTheme;
import com.glaway.mpm.pbombuilder.jws.CmContext;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.*;
import com.glaway.mpm.pbombuilder.tree.action.*;
import com.glaway.mpm.pbombuilder.util.*;
import wt.doc.WTDocument;
import wt.part.WTPart;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;

/**
 * <br>
 * Created on 2012-10-16
 *
 * @author chenyunlong
 */
public class CmMBomTreePanel extends CmAbstractPanel {
	private static final long serialVersionUID = 7452042908382287580L;
	 private static final CmLogger log = CmLogger.getLogger(CmMBomTreePanel.class);

	private static CmMBomTreePanelImpl impl = CmMBomTreePanelImpl.newCmMBomTreePanelImpl();

	private CmScrollPaneTree scrollTreePane;
	private JToolBar toolBar;
	private CmAction actShowInProductView;
	private JButton btnShowInProductView;
	private CmMBomMainFrame mainOwner;
	private EbomTreeExpandAction ebomTreeExpandAction;
	private JButton expandButton;
	private EbomTreeCollapseAction ebomTreeCollapseAction;
	private JButton collapseButton;
	private EbomTreePackageAction ebomTreePackageAction;
	private JButton packageButton;
	private EbomTreeUnPackageAction ebomTreeUnPackageAction;
	private JButton unPackageButton;
	private EbomTreeCancelAction ebomTreeCancelAction;
	private JButton cancelButton;
	private BomTreeReportAction bomTreeReportAction;
	private JButton reportButton;
	private PbomTreeEditReportAction pbomTreeEditReportAction;
	private JButton pbomReportButton;
	private PbomTreeSearchAction pbomTreeSearchAction;
	private JButton pbomSearchButton;
	public static JTextField pbomSearchText;
	private CmTree ebomtree;
	private PbomTreeUpdateAction pbomTreeUpdateAction;
	private JButton pbomUpdateButton;

	public CmMBomTreePanel(){

	}

	public CmMBomTreePanel(CmMBomMainFrame owner,CmTree ebomtree) {
		super();
		//System.out.println("加载CmMBomTreePanel begin");
		this.mainOwner = owner;
		this.ebomtree = ebomtree;
		this.setLayout(new BorderLayout());
		try {
			initUI();
		} catch (CmTaskException e) {
			// log.error(e);
		}
		if (mainOwner.getViewObject() != null) {
			loadViewDate(mainOwner.getViewObject());
		}
		//System.out.println("加载CmMBomTreePanel end");
	}


	@Override
	protected void initDimension() {
		this.setMinimumSize(new Dimension(180, 360));
	}

	@Override
	protected void initLayout() {
		add(toolBar, BorderLayout.NORTH);
		add(scrollTreePane, BorderLayout.CENTER);
	}

	@Override
	protected void loadInitDatas() {
	}

	@Override
	protected void initActions() {
	}

	@Override
	protected void initComponents() {
		scrollTreePane = new CmScrollPaneTree(new CmTreeNode("PBOM"));
		// 显示可视化
		actShowInProductView = new CmPivewAction(mainOwner, getTree(), CmMPViewStructureGenerator.class,
				CmMPVSelectionObserver.class);
		btnShowInProductView = new JButton(actShowInProductView);
		btnShowInProductView.setBackground(CmTheme.CM_TURQUOISE);
		btnShowInProductView.setForeground(Color.WHITE);
		// 展开
		ebomTreeExpandAction = new EbomTreeExpandAction(getTree());
		expandButton = new JButton(ebomTreeExpandAction);
		expandButton.setForeground(Color.WHITE);
		expandButton.putClientProperty("hideActionText", true);
		// 收起
		ebomTreeCollapseAction = new EbomTreeCollapseAction(getTree());
		collapseButton = new JButton(ebomTreeCollapseAction);
		collapseButton.setForeground(Color.WHITE);
		collapseButton.putClientProperty("hideActionText", true);
		// 打包
		ebomTreePackageAction = new EbomTreePackageAction(getTree());
		packageButton = new JButton(ebomTreePackageAction);
		packageButton.setForeground(Color.WHITE);
		packageButton.putClientProperty("hideActionText", true);
		// 拆包
		ebomTreeUnPackageAction = new EbomTreeUnPackageAction(getTree());
		unPackageButton = new JButton(ebomTreeUnPackageAction);
		unPackageButton.setForeground(Color.WHITE);
		unPackageButton.putClientProperty("hideActionText", true);
		//撤销
		ebomTreeCancelAction = new EbomTreeCancelAction(getTree(),ebomtree);
		cancelButton = new JButton(ebomTreeCancelAction);
		cancelButton.setForeground(Color.WHITE);
		cancelButton.putClientProperty("hideActionText", true);
		ebomTreeCancelAction.initButton(cancelButton);
		//EBOM比较PBOM的报表
		bomTreeReportAction = new BomTreeReportAction();
		reportButton = new JButton(bomTreeReportAction);
		reportButton.setForeground(Color.WHITE);
		reportButton.putClientProperty("hideActionText", true);
		//PBOM编辑报表
		pbomTreeEditReportAction = new PbomTreeEditReportAction();
		pbomReportButton = new JButton(pbomTreeEditReportAction);
		pbomReportButton.setForeground(Color.WHITE);
		pbomReportButton.putClientProperty("hideActionText", true);
		//同步EBOM
		pbomTreeUpdateAction = new PbomTreeUpdateAction(ebomtree,getTree());
		pbomUpdateButton = new JButton(pbomTreeUpdateAction);
		pbomUpdateButton.setForeground(Color.WHITE);
		pbomUpdateButton.putClientProperty("hideActionText", true);
		//快速查询
		pbomTreeSearchAction =new PbomTreeSearchAction(getTree());
		pbomSearchButton = new JButton(pbomTreeSearchAction);
		pbomSearchButton.setForeground(Color.WHITE);
		pbomSearchButton.putClientProperty("hideActionText", true);
		pbomSearchText = new JTextField();
		pbomSearchText.setSize(new Dimension(10, 20));

		pbomSearchText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					CmCommonSearchAction.bomTreeSearchAction(pbomSearchText.getText(), getTree(), "PBOM");
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

		toolBar = buildToolBar();
	}

	private JToolBar buildToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setBackground(CmTheme.CM_TURQUOISE);
		toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.WHITE));
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		toolBar.add(expandButton);
		toolBar.add(collapseButton);
		toolBar.add(unPackageButton);
		toolBar.add(packageButton);
//		toolBar.add(btnShowInProductView);
		toolBar.add(reportButton);
//		toolBar.add(pbomReportButton);
		toolBar.add(pbomUpdateButton);
//		toolBar.add(cancelButton);
		toolBar.add(pbomSearchText);
		toolBar.add(pbomSearchButton);
		return toolBar;
	}

	@Override
	protected void registerTaskExecutor() throws CmTaskException {
		PviewTask.registerTaskExecutor("CmMBomTreePanel.mergerAO2Tree", this);
		PviewTask.registerTaskExecutor("CmMBomTreePanel.mergerNode2Tree", this);
	}

	@Override
	protected void unregisterTaskExecutor() throws CmTaskException {
		PviewTask.unregisterTaskExecutor(this);
	}

	private void loadViewDate(CmLightPart part) {
		this.mergerAO2Tree(part);
	}

	public CmTree getTree() {
		return scrollTreePane.getTree();
	}

	public void pasteNodesToMBom(CmTree tree, CmTreeNode parentNode, List<CmTreeNode> selectedNodeList) {
		String errorMsg = impl.pasteNodesToMBom(tree, parentNode, selectedNodeList);
		if (errorMsg.length() > 0) {
			JOptionPane.showMessageDialog(CmContext.getMainFrame(), errorMsg);
		}
	}

	@SuppressWarnings("unchecked")
	public void mergerNode2Tree(Object sender, Object params, CmTaskExecutorCallback callback) throws RemoteException,
			InvocationTargetException {
		// log.debug("come here params type=", params.getClass().getName());
		Object[] objs = (Object[]) params;
		List<CmTreeNode> nodes = (List<CmTreeNode>) objs[0];
		CmTreeNode parentNode = (CmTreeNode) objs[1];

		pasteNodesToMBom(getTree(), parentNode, nodes);
	}

	public void mergerAO2Tree(Object sender, Object params, CmTaskExecutorCallback callback) throws RemoteException,
			InvocationTargetException {
		if (params instanceof CmLightPart) {
			CmLightPart aoPart = (CmLightPart) params;

			CmTreeNode aoNode = getAOStructure(aoPart);

			CmTree tree = getTree();
			CmTreeNode root = tree.getRoot();
			if (root.getChildCount() == 0) {
				if (aoNode != null) {
					root.add(aoNode);
					tree.expandPath(new TreePath(aoNode.getPath()));
				}
			} else {
				CmTreeNode oldNode = (CmTreeNode) root.getChildAt(0);
				if (JOptionPane.showConfirmDialog(null, "��������µ�AO:" + aoNode.getPart().getPartNumber()
						+ "��MBOM����,�Ƿ�Ҫ���浱ǰ���ڱ༭��AO���:" + oldNode.getPart().getPartNumber() + " ?", "ȷ��",
						JOptionPane.OK_CANCEL_OPTION) == 0) {
					try {
						PviewTask.sendTask(CmTaskInfo.newCmTaskInfo("mainframe.saveBeforeLoad", this, null), null);
					} catch (CmTaskException e) {
						e.printStackTrace();
					}
				}
				root.removeAllChildren();

				root.add(aoNode);
				tree.expandPath(new TreePath(aoNode.getPath()));

			}
			tree.updateUI();
		}
	}

	public void mergerAO2Tree(CmLightPart aoPart) {

		CmTreeNode aoNode = getAOStructure(aoPart);
		CmTree tree = getTree();
		CmTreeNode root = tree.getRoot();
		if (aoNode != null) {
			root.add(aoNode);
			tree.expandPath(new TreePath(aoNode.getPath()));
			tree.updateUI();
		}
	}

	// ��ϵͳ�л�ȡ�ṹ-Alex.Huang 20110622
	private CmTreeNode getStructureFromDb(CmLightPart lightPart) {
		 log.debug("��ϵͳ��ȡ�ṹ���");
		CmPartWithOcc nodeChild = null;
		try {
			nodeChild = buildStructure(lightPart.getOid());
		} catch (Exception e) {
			e.printStackTrace();
		}
		CmTreeNode treeNodeChild = CmBizObjUtil.buildTree(nodeChild);
		return treeNodeChild;
	}

	private CmPartWithOcc buildStructure(long parentOid) throws RemoteException, InvocationTargetException {
		WTPart parent = null;
		try {
			parent = (WTPart) CmSearchHelper.search(WTPart.class, parentOid);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return CmPartStructureUtil.buildStructure(parent);
	}

	// --------------------------------------------
	private CmTreeNode getAOStructure(CmLightPart lightPart) {
		String state = lightPart.getState();
		CmSettings settings = CmSettings.getSection(CmSettings.SECTION_MBOM);
		String[] partState = settings.get("mbom.part.state.displayName", new String[0]);

		/*
		 * for(String s:partState){ if(s.equals(state)){
		 * log.debug("������ǰ״̬��"+state); return getStructureFromDb(lightPart);
		 * } }
		 */

		// log.debug("��xml��ȡ�ṹ���");
		CmXmlDataProxy proxy = CmXmlDataProxy.getCmXmlDataProxy();

		CmTreeNode ret = null;
		CmXMLDataHelper helper = new CmXMLDataHelper();
		try {
			PviewTask.postTask("mainframe.setStatus", "���ڼ���AO���......");
			WTPart part = CmBizObjUtil.getWTPartFromLightPart(lightPart);

			WTDocument doc = CmBizRelationHelper.getSavedDoc4Part(CmMBomMainFrame.NODE, CmConstants.TI_DOC_DATA_MBOM,
					part, false);

			CmXML xml = helper.findContentXMLForDoc(doc);

			CmXML strucutreXml = xml.loc("." + CmTreeNodeStructureProxy.XMLTITLE);

			if (proxy.getProxy(CmXmlDataProxy.MBOM_STRUCTURE_PROXY) == null) {
				proxy.registerStructureProxy(CmXmlDataProxy.MBOM_STRUCTURE_PROXY);
			}

			if (proxy.getProxy(CmXmlDataProxy.MBOM_ATTRIBUTE_PROXY) == null) {
				proxy.registerAttributProxy(CmXmlDataProxy.MBOM_ATTRIBUTE_PROXY);
			}
			CmTreeNodeStructureProxy structure = proxy
					.buildStructure(CmXmlDataProxy.MBOM_STRUCTURE_PROXY, strucutreXml);

			ret = structure.getTreeNode();

			if (ret == null) {
				ret = new CmTreeNode(new CmNode(lightPart));
				structure.setTreeNode(ret);
			} else {
				CmXML attributXml = xml.loc("." + CmTreeNodeAttributProxy.XMLTITLE);
				proxy.buildAttribut(CmXmlDataProxy.MBOM_ATTRIBUTE_PROXY, ret, attributXml);

				// �Ѹ����ڵ�part���ɵ�ǰpart
				ret.setUserObject(new CmNode(lightPart));
			}
			PviewTask.postTask("mainframe.setStatus", "AO��ݼ������");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}