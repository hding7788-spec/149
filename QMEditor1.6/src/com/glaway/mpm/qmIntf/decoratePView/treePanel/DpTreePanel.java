package com.glaway.mpm.qmIntf.decoratePView.treePanel;

import com.glaway.mpm.qmIntf.decoratePView.showPanel.view.DPViewStructureGenerator;
import com.glaway.mpm.qmIntf.decoratePView.showPanel.view.DpEPVSelectionObserver;
import com.glaway.mpm.qmIntf.decoratePView.showPanel.view.DpPviewAction;
import com.glaway.mpm.task.CmTaskExecutorCallback;
import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.task.util.CmTaskHelper;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.VaTheme;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.visual.view.tree.menu.VaDpMenuItemFactory;
import com.glaway.mpm.visual.view.tree.menu.VaMenuItemFactory;
import com.glaway.mpm.visual.view.ui.VaAbstractPanel;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

public class DpTreePanel extends VaAbstractPanel {
	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());
	private JScrollPane jScrollPane;
	public  static DpTree dpTree;
	public String filePath;
	private String technicsPath;
	private String paceNumber;
	private String stepNumber;
	private String procedureContent;
	public static JCheckBox isHeritCheck = new JCheckBox();
	private JButton sureButton;
	private JButton cancelButton;
	private VaTree ebomTree;
	private DpPviewAction actShowInProductView;
	private List<VaTreeNode> nodeList = new ArrayList<VaTreeNode>();
	private List<VaTreeNode> nodeFromPrvList = new ArrayList<VaTreeNode>();
	private Window						mainOwner;

	public DpTreePanel(String filePath, String technicsPath, String stepNumber,
			String paceNumber, String procedureContent,VaTree tree, Window mainOwner) {
		this.mainOwner = mainOwner;
		this.technicsPath = technicsPath;
		this.filePath = filePath;
		this.stepNumber = stepNumber;
		this.paceNumber = paceNumber;
		this.procedureContent = procedureContent;
		this.ebomTree = tree;
		init();
	}

	private void init() {

		try {
			initLookAndFeel();
			initDimension();
			initComponents();
			initLayout();
			initActions();
			loadInitDatas();
			initMouseListener();
			registerTaskExecutor();
		} catch (CmTaskException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void initLookAndFeel() {

	}

	protected void initDimension() {

	}

	public void initComponents() {
		sureButton = new JButton("确定");
		cancelButton = new JButton("取消");

//		JDialog dialog = new JDialog();
//		JScrollPane  spane= new JScrollPane();
//		spane.setViewportView(ebomTree);
//		dialog.getContentPane().setLayout(new BorderLayout());
//		dialog.add(spane,BorderLayout.CENTER);
//		dialog.setSize(500,1000);
//		dialog.setVisible(true);

		jScrollPane = new JScrollPane(){
			public void paintComponent(Graphics g) {
			      super.paintComponent(g);
			      setBackground(Color.WHITE);

//			      Image image = null;
//			      if (image != null) {
//			         int height = image.getHeight(this);
//			         int width = image.getWidth(this);
//
//			         if (height != -1 && height > getHeight())
//			            height = getHeight();
//
//			         if (width != -1 && width > getWidth())
//			            width = getWidth();
//
//			         int x = (int) (((double) (getWidth() - width)) / 2.0);
//			         int y = (int) (((double) (getHeight() - height)) / 2.0);
//
//			         g.drawImage(image, x, y, width, height, this);
//			      }
			   }
		};

		jScrollPane.setOpaque(true);
		jScrollPane.getViewport().setOpaque(false);
		dpTree = DpTreeXmlUtil.xmlToCoTree2(filePath, stepNumber, paceNumber,
				procedureContent);
		SwingUtil.expandAll(dpTree);

		VaTreeNode root = (VaTreeNode)dpTree.getModel().getRoot();
		Enumeration<VaTreeNode> nodeEnum = root.children();
		while(nodeEnum.hasMoreElements()){
			reverseTreeNodeChildren(nodeEnum.nextElement());
		}
		reverseTreeNodeChildren(root);

		((DefaultTreeModel)dpTree.getModel()).reload();
		jScrollPane.setViewportView(dpTree);
	}

	private static void reverseTreeNodeChildren(VaTreeNode treeNode){

		Enumeration<VaTreeNode> nodeEnum = treeNode.children();

		List<VaTreeNode> nodeList = Collections.list(nodeEnum);

		Collections.reverse(nodeList);
		treeNode.removeAllChildren();

		Iterator iterator = nodeList.iterator();

		while(iterator.hasNext()){
			treeNode.add((VaTreeNode)iterator.next());
		}
	}

	protected void initLayout() {

		this.setLayout(new BorderLayout());
		JToolBar toolBar = buildToolBar();
		this.add(toolBar, BorderLayout.NORTH);
		this.add(jScrollPane, BorderLayout.CENTER);
	}

	private JToolBar buildToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setBackground(VaTheme.VA_TURQUOISE);
		toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0,
				Color.WHITE));
		toolBar.setFloatable(false);
		toolBar.setRollover(true);

		actShowInProductView = new DpPviewAction(mainOwner, this.ebomTree,
				DPViewStructureGenerator.class, DpEPVSelectionObserver.class);
		JButton btnShowInProductView = new JButton(actShowInProductView);
		Icon icon = new ImageIcon(DpTreePanel.class.getResource("/image/productView.gif"));
		btnShowInProductView.setIcon(icon);
		btnShowInProductView.setBackground(VaTheme.VA_TURQUOISE);
		btnShowInProductView.setForeground(Color.WHITE);
		toolBar.add(btnShowInProductView);

		isHeritCheck.setText("继承性选择");
		toolBar.add(isHeritCheck);
//		toolBar.add(sureButton);
//		toolBar.add(cancelButton);
		return toolBar;
	}

	public static DpTree getDpTree() {
		return dpTree;
	}

	public static void setDpTree(DpTree dpTree) {
		DpTreePanel.dpTree = dpTree;
	}

	protected void initActions() {
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				DpTecnicsNode root = (DpTecnicsNode)dpTree.getRoot();

				Map<String, List<String>> returnMap = new HashMap<String, List<String>>();
				Enumeration<Object> element = root.children();
				while (element.hasMoreElements()) {
					DpStepNode stepNode = (DpStepNode) element.nextElement();
					if (stepNode.isSelected()) {
						List<String> list = new ArrayList<String>();
						Enumeration<Object> element1 = stepNode.children();
						while (element1.hasMoreElements()) {
							DpPaceNode paceNode = (DpPaceNode) element1
									.nextElement();
							if (paceNode.isSelected()) {
								list.add(paceNode.getPace().getName());
							}
						}
						returnMap.put(stepNode.getStep().getOid(), list);
					}
				}
				logger.debug(returnMap);
//				dialog.dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
//				dialog.dispose();
			}
		});
	}

	protected void loadInitDatas() {

	}
	/**
	 *配置CmTree的鼠标监听,包含m视图新节点创建控制
	 */
	private void initMouseListener() {
		VaMenuItemFactory itemEFactory = new VaDpMenuItemFactory(true);
		MouseListener eAdapter = new DpTreeMouseAdapter(VaContext.getMainFrame(), itemEFactory);
		getTree().addMouseListener(eAdapter);

	}

	public VaTree getTree() {
		return this.dpTree;
	}

	public void pviewAction(){
		if(actShowInProductView != null){
			actShowInProductView.actionPerformed(null);
		}
	}


	public List<VaTreeNode> getSelectedPViewNodeList(Object sender,
			Object params,CmTaskExecutorCallback callback) {
		return nodeFromPrvList;
	}

	@SuppressWarnings("unchecked")
	public void setSelectedPViewNodeList(Object sender, Object params,CmTaskExecutorCallback callback) {
		this.nodeFromPrvList = (List<VaTreeNode>) params;
	}

	public List<VaTreeNode> getSelectedCopyNodeList(Object sender, Object params,CmTaskExecutorCallback callback) {
		return nodeList;
	}

	@SuppressWarnings("unchecked")
	public void setSelectedCopyNodeList(Object sender, Object params,CmTaskExecutorCallback callback) {
		this.nodeList = (List<VaTreeNode>) params;
	}

	@Override
	protected void registerTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub
		 CmTaskHelper.registerTaskExecutor("DpTreePanel.getSelectedPViewNodeList", this, true);
	      CmTaskHelper.registerTaskExecutor("DpTreePanel.setSelectedPViewNodeList", this, true);
	      CmTaskHelper.registerTaskExecutor("DpTreePanel.getSelectedCopyNodeList", this, true);
	      CmTaskHelper.registerTaskExecutor("DpTreePanel.setSelectedCopyNodeList", this, true);
	}

	@Override
	protected void unregisterTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub

	}
}
