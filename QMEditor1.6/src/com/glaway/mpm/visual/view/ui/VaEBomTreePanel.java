/**
 * <br>Created on 2010-10-23
 * @author Dennis Huang - ���ٽ�
 */
package com.glaway.mpm.visual.view.ui;

import com.glaway.mpm.task.CmTaskExecutorCallback;
import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.task.util.CmTaskHelper;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.VaTheme;
import com.glaway.mpm.visual.view.action.VaAction;
import com.glaway.mpm.visual.view.action.VaPviewAction;
import com.glaway.mpm.visual.view.pview.VaEPVSelectionObserver;
import com.glaway.mpm.visual.view.pview.VaEPViewPVSGenerator;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeMouseAdapter;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.visual.view.tree.VaTreeSelectionListener;
import com.glaway.mpm.visual.view.tree.menu.VaEBOMenuItemFactory;
import com.glaway.mpm.visual.view.tree.menu.VaMenuItemFactory;
import com.ptc.pview.pvkapp.Instance;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author wanghaoyu
 *
 */
public class VaEBomTreePanel extends VaAbstractPanel {
	private static final long		serialVersionUID	= 7452042908382287580L;
	private static final VaLogger	log					= VaLogger.getLogger(VaEBomTreePanel.class);

	// for CmTaskExecutor -> start
	private volatile boolean		executorActive;
	// for CmTaskExecutor -> end

	public static VaScrollPaneTree		scrollTreePane;
	public static VaScrollPaneTree getScrollTreePane() {
		return scrollTreePane;
	}

	public void setScrollTreePane(VaScrollPaneTree scrollTreePane) {
		this.scrollTreePane = scrollTreePane;
	}

	public static  VaScrollPaneTree getScrollTreePane2() {
		return scrollTreePane2;
	}

	public void setScrollTreePane2(VaScrollPaneTree scrollTreePane2) {
		this.scrollTreePane2 = scrollTreePane2;
	}

	public static  VaScrollPaneTree        scrollTreePane2;
	private JSplitPane              splitTreePanel;
	private JPanel                  treePanel;
	private JToolBar				toolBar;
	// actions -> start
	private VaAction				actSearch;
	// private CmEBomEffAction actValidity;
	private VaAction				actShowInProductView;
	// actions -> end

	// UI components -> start
	// private JButton btnSearch;
	// private JButton btnValidity;
	private JButton					btnShowInProductView;
	private JButton					btnCopy;
	private JButton					btnCancel;

	private JButton					btnChkUsed;
	private JButton					btnChkUnUsed;
	private JButton					btnClear;

	private JButton					btnClearFittings;

	private JTextField				txtSearch;
	private JButton					btnSearch;

	private List<VaTreeNode>		nodeList			= new ArrayList<VaTreeNode>();
	private List<VaTreeNode>		nodeFromPrvList		= new ArrayList<VaTreeNode>();
	private Window					mainOwner;

	private VaTree					ebomTree;

	public VaTree getEbomTree() {
		return ebomTree;
	}

	public void setEbomTree(VaTree ebomTree) {
		this.ebomTree = ebomTree;
	}

	public List<VaTreeNode> getSelectedCopyNodeList(Object sender, Object params, CmTaskExecutorCallback callback) {
		return nodeList;
	}

	@SuppressWarnings("unchecked")
	public void setSelectedCopyNodeList(Object sender, Object params, CmTaskExecutorCallback callback) {
		this.nodeList = (List<VaTreeNode>) params;
	}

	public List<VaTreeNode> getSelectedPViewNodeList(Object sender, Object params, CmTaskExecutorCallback callback) {
		return nodeFromPrvList;
	}

	@SuppressWarnings("unchecked")
	public void setSelectedPViewNodeList(Object sender, Object params, CmTaskExecutorCallback callback) {
		this.nodeFromPrvList = (List<VaTreeNode>) params;
	}

	@SuppressWarnings("unchecked")
	public void setSelectedForInstance(Object sender, Object params, CmTaskExecutorCallback callback) {
		List<Instance> insList = (List<Instance>) params;
		List<VaTreeNode> nodeList = new ArrayList<VaTreeNode>();
		if (insList != null && insList.size() > 0) {
			for (int i = 0; i < insList.size(); i++) {
				if (null != this.ebomTree.getNodeFromInstance(insList.get(i))) {
					nodeList.add(this.ebomTree.getNodeFromInstance(insList.get(i)));
				}
			}
		}
		this.nodeFromPrvList = nodeList;

		setEbomSelected(ebomTree.getRoot(), nodeList);
		ebomTree.repaint();

		setPbomSelected(ebomTree.getRoot());
		scrollTreePane.getTree().repaint();

		// Comparator<VaTreeNode> comtor = new VaTreeNodeModelComparator();
		// VaTreeLinkage linkage = new
		// VaTreeModelLinkage(comtor).addLinkageTree(this.ebomTree);
		// linkage.nodeChange(nodeList);
	}

	@SuppressWarnings("unchecked")
	private void setEbomSelected(VaTreeNode node, List<VaTreeNode> nodeList) {
		Enumeration<VaTreeNode> children = node.children();
		while (children.hasMoreElements()) {
			VaTreeNode child = children.nextElement();
			boolean flag = false;
			for (VaTreeNode tempNode : nodeList) {
				if (null != tempNode) {
					if (child.getPart().getNumber().equals(tempNode.getPart().getNumber())
							&& child.getOccId().equals(tempNode.getOccId())) {
						flag = true;
						break;
					}
				}
			}
			if (!flag && !"-1".equals(child.getOccId())) {
				child.setSelected(false);
			} else {
				child.setSelected(true);
			}
			setEbomSelected(child, nodeList);
		}
	}

	@SuppressWarnings("unchecked")
	private void setPbomSelected(VaTreeNode node) {
		Enumeration<VaTreeNode> children = node.children();
		while (children.hasMoreElements()) {
			VaTreeNode child = children.nextElement();

			VaTreeNode pbomTreeNode = (VaTreeNode) scrollTreePane.getTree().getRoot().children().nextElement();

			Enumeration<VaTreeNode> pbomNodeChildren = pbomTreeNode.children();
			while (pbomNodeChildren.hasMoreElements()) {
				VaTreeNode pbomChild = pbomNodeChildren.nextElement();
				if (child.getOccId().equals(pbomChild.getOccId())) {
					pbomChild.setSelected(child.isSelected());
				}
			}

			setPbomSelected(child);
		}
	}

	// UI components -> end

	public VaEBomTreePanel(Window owner) {
		super();
		this.mainOwner = owner;
		try {
			initUI();
			initListener();

		} catch (Exception e) {
			log.error(e);
		}
	}

	public VaTree getTree() {
		return scrollTreePane.getTree();
	}

	private JToolBar buildToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setBackground(VaTheme.VA_TURQUOISE);
		toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.WHITE));
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		// toolBar.set
		// toolBar.add(btnSearch);
		// toolBar.add(btnValidity);
		toolBar.add(btnShowInProductView);
		// toolBar.add(btnCopy);
		// toolBar.add(btnCancel);
		toolBar.add(btnChkUsed);
		toolBar.add(btnChkUnUsed);
		toolBar.add(btnClear);
		// toolBar.add(btnClearFittings);
		toolBar.addSeparator();
		toolBar.add(txtSearch);
		toolBar.add(btnSearch);
		return toolBar;
	}

	@Override
	protected void initActions() {

	}

	@Override
	protected void initComponents() {
		// 参装工具上面的PBOM树
		scrollTreePane = new VaScrollPaneTree("ZPBOM");
		// 与上面相对应的背后树
		ebomTree = new VaTree(new VaTreeNode("EBOM"));
		ebomTree.initTree(VaContext.getCurrentPartNumber(), "", "EBOM");
//		if(NewTechnicsPart.flag){
//			ebomTree.expandAllLevels(ebomTree.getRoot());
//			JDialog dia = new JDialog();
//			dia.setTitle("VaEBomTreePanel");
//			JScrollPane diaJsp = new JScrollPane(ebomTree);
//			dia.add(diaJsp);
//			dia.setSize(400,800);
//			dia.setVisible(true);
//		}

		// ����
		// actSearch = new CmCISearchAction(getTree(), getTree().getRoot(),
		// CmContext.getMainFrame());
		// btnSearch = new JButton("加");// (actSearch);
		// btnSearch.setForeground(Color.WHITE);
		// btnSearch.putClientProperty("hideActionText", true);

		// ��Ч��
		// actValidity = new CmEBomEffAction(CmContext.getMainFrame(),
		// getTree());
		// btnValidity = new JButton("验证");// (actValidity);
		// btnValidity.setForeground(Color.WHITE);
		// btnValidity.putClientProperty("hideActionText", true);

		actShowInProductView = new VaPviewAction(mainOwner, ebomTree, scrollTreePane.getTree(),
				VaEPViewPVSGenerator.class, VaEPVSelectionObserver.class);
//		actShowInProductView = new VaPviewAction(mainOwner, ebomTree, scrollTreePane.getTree(),
//				VaEPViewStructureGenerator.class, VaEPVSelectionObserver.class);

		btnShowInProductView = new JButton(actShowInProductView);
		// btnShowInProductView.setText("显示");
		// btnCopy = new JButton("复制");
		// btnCancel = new JButton("取消");

		btnChkUsed = new JButton();
		btnChkUsed.setIcon(new ImageIcon(this.getClass().getResource("/images/fittings/check_fit.gif")));
		btnChkUsed.setToolTipText("选中已装");
		btnChkUsed.setBackground(VaTheme.VA_TURQUOISE);
		btnChkUsed.setForeground(Color.WHITE);
		btnChkUnUsed = new JButton();
		btnChkUnUsed.setIcon(new ImageIcon(this.getClass().getResource("/images/fittings/check_unfit.gif")));
		btnChkUnUsed.setToolTipText("选中未装");

		btnChkUnUsed.setBackground(VaTheme.VA_TURQUOISE);
		btnChkUnUsed.setForeground(Color.WHITE);
		btnClear = new JButton();
		btnClear.setIcon(new ImageIcon(this.getClass().getResource("/images/fittings/clear.gif")));
		btnClear.setToolTipText("清除选择");
		btnClearFittings = new JButton("清除全部参装");
		btnClear.setBackground(VaTheme.VA_TURQUOISE);
		btnClear.setForeground(Color.WHITE);
		txtSearch = new JTextField();
		txtSearch.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					VaTreeNode root = scrollTreePane.getTree().getRoot();
					String searchContent = txtSearch.getText().trim();
					log.debug("searchContent : " + searchContent);
					Enumeration children = root.breadthFirstEnumeration();
					while (children.hasMoreElements()) {
						VaTreeNode child = (VaTreeNode) children.nextElement();
						String lowerCase = searchContent.toLowerCase();
						if (child.toString().toLowerCase().indexOf(lowerCase) > -1) {
							child.setSelected(true);
						} else {
							child.setSelected(false);
						}
						if (child.isLeaf()) {
							DefaultTreeModel treeModel = (DefaultTreeModel) (VaEBomTreePanel.this.scrollTreePane
									.getTree().getModel());
							log.debug("搜索： treeModel--nodeChanged");
							treeModel.nodeChanged(child);
						}
					}
					scrollTreePane.getTree().repaint();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

		btnSearch = new JButton();
		btnSearch.setIcon(new ImageIcon(this.getClass().getResource("/images/fittings/search.gif")));
		btnSearch.setToolTipText("搜索");
		btnSearch.setBackground(VaTheme.VA_TURQUOISE);
		btnSearch.setForeground(Color.WHITE);
		btnSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				VaTreeNode root = scrollTreePane.getTree().getRoot();
				String searchContent = txtSearch.getText().trim();
				log.debug("searchContent : " + searchContent);
				Enumeration children = root.breadthFirstEnumeration();
				while (children.hasMoreElements()) {
					VaTreeNode child = (VaTreeNode) children.nextElement();
					String lowerCase = searchContent.toLowerCase();
					if (child.toString().toLowerCase().indexOf(lowerCase) > -1) {
						child.setSelected(true);
					} else {
						child.setSelected(false);
					}
					if (child.isLeaf()) {
						DefaultTreeModel treeModel = (DefaultTreeModel) (VaEBomTreePanel.this.scrollTreePane.getTree()
								.getModel());
						log.debug("搜索： treeModel--nodeChanged");
						treeModel.nodeChanged(child);
					}
				}
				scrollTreePane.getTree().repaint();
			}
		});

		// btnCopy.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// // TODO Auto-generated method stub
		// Vector<VaTreeNode> v = new Vector<VaTreeNode>();
		// getSelectedNodes(scrollTreePane.getTree().getRoot(),v);
		// if(usedFlag){
		// int ret = JOptionPane.showConfirmDialog(null, "自动过滤已使用参装件并复制", null,
		// JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
		// if(ret == 2)
		// {
		// return;
		// }
		// }
		//
		//
		// VaClipboard.clipboard.clear();
		//
		// boolean isSamePart = false;
		//
		// List<VaTreeNode> nodeList = new ArrayList<VaTreeNode>();
		// for (VaTreeNode node : v) {
		// Map<String, String> partMap = new HashMap();
		// isSamePart = false;
		//
		// for (Map<String, String> map : VaClipboard.clipboard) {
		// String key = "oid";
		// String value = String.valueOf(node.getPart().getOid());
		//
		// if (map.containsKey(key) && map.containsValue(value)) {
		// map.put("occId", map.get("occId") + "," + node.getOccId());
		// map.put("useCount", String.valueOf(Integer.parseInt(map
		// .get("useCount")) + 1));
		//
		// isSamePart = true;
		// break;
		// }
		// }
		// // partMap.containsKey(key)
		// if (!isSamePart) {
		// partMap.put("partNumber", node.getPart().getNumber());
		// partMap.put("oid", String.valueOf(node.getPart().getOid()));
		// partMap.put("occId", node.getOccId());
		// partMap.put("partName", node.getPart().getName());
		// partMap.put("material", "");
		// partMap.put("dutu", "");
		// partMap.put("remark", "");
		// partMap.put("useCount", "1");
		// nodeList.add(node);
		// VaClipboard.clipboard.add(partMap);
		// }
		//
		// }
		// // ParticipatePartAddDialog.getInstance().setVisible(false);
		// log.debug(VaClipboard.clipboard);
		//
		// }
		// });
		//

		btnChkUsed.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				checkUsed(scrollTreePane.getTree().getRoot(),true);
				scrollTreePane.getTree().repaint();
			}
		});

		btnChkUnUsed.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				checkUsed(scrollTreePane.getTree().getRoot(), false);
				scrollTreePane.getTree().repaint();
			}
		});

		btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				scrollTreePane.getTree().getRoot().setSelected(false);
				VaTreeNode partRoot = (VaTreeNode) scrollTreePane.getTree().getRoot().children().nextElement();

				DefaultTreeModel treeModel = (DefaultTreeModel) (VaEBomTreePanel.this.scrollTreePane.getTree()
						.getModel());
				log.debug("搜索： treeModel--nodeChanged");
				treeModel.nodeChanged(partRoot);

				scrollTreePane.getTree().repaint();
			}
		});

		btnClearFittings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				VaTree tree = scrollTreePane.getTree();
				VaTreeNode node = tree.getRoot();
				Vector vFittings = new Vector();
				// getFittings(node,vFittings);
				try {
					// com.glaway.mpm.controller.CancelPartHandler.clearFittings(vFittings,
					// true);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		Icon icon = new ImageIcon(VaPviewAction.class.getResource("/image/productView.gif"));
		btnShowInProductView.setIcon(icon);
		btnShowInProductView.setBackground(VaTheme.VA_TURQUOISE);
		btnShowInProductView.setForeground(Color.WHITE);

		toolBar = buildToolBar();

	}

	private void getFittings(VaTreeNode node, Vector fittingList) {

		if (node.isUsed()) {
			fittingList.add(node.getOccId());
		}

		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			VaTreeNode child = (VaTreeNode) node.getChildAt(i);
			getFittings(child, fittingList);
		}
	}

	boolean	usedFlag	= false;

	private void getSelectedNodes(VaTreeNode node, Vector selectedList) {

		if (node.isSelected()) {
			if (node.isUsed()) {
				usedFlag = true;
			} else {
				selectedList.add(node);
			}
		}

		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			VaTreeNode child = (VaTreeNode) node.getChildAt(i);
			getSelectedNodes(child, selectedList);
		}
	}

	@Override
	protected void initDimension() {
		this.setMinimumSize(new Dimension(180, 360));
	}

	@Override
	protected void initLayout() {
		splitTreePanel=new JSplitPane();
		scrollTreePane2=new VaScrollPaneTree("FPBOM");
		treePanel=new JPanel();
		splitTreePanel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitTreePanel.setLeftComponent(scrollTreePane);
		splitTreePanel.setRightComponent(scrollTreePane2);
		splitTreePanel.setDividerLocation(500);
		treePanel.setLayout(new BorderLayout());
		treePanel.add(splitTreePanel, BorderLayout.CENTER);

		this.setLayout(new BorderLayout());
		this.add(toolBar, BorderLayout.NORTH);
		this.add(treePanel, BorderLayout.CENTER);


	}

	@Override
	protected void loadInitDatas() {
	}

	public boolean isExecutorActive() {
		return executorActive;
	}

	public void setExecutorActive(boolean executorActive) {
		this.executorActive = executorActive;
	}

	private void checkUsed(VaTreeNode node, boolean useFlag) {

		node.setSelected(false);

		if (useFlag) {
			node.setSelected(node.isUsed());
		} else {
			node.setSelected(!node.isUsed());
		}
		int count = node.getChildCount();

		for (int i = 0; i < count; i++) {
			VaTreeNode child = (VaTreeNode) node.getChildAt(i);
			checkUsed(child, useFlag);
			if (child.isLeaf()) {
				DefaultTreeModel treeModel = (DefaultTreeModel) (this.scrollTreePane.getTree().getModel());
				treeModel.nodeChanged(child);
			}
		}

	}

	private void initListener() {

		// initDragListener();

		// initTreeSelectionListener();
		initMouseListener();
	}

	/**
	 * 配置CmTree的拖动监听
	 */
	private void initDragListener() {// load drag listener

		// VaTree etree = getTree();
		// etree.setDragEnabled(true);

		// // 定义源树监听
		// CmTreeDragListeners gestureListener = new CmTreeDragListeners(etree,
		// detect, merger);
		//
		// // 设置目标树TransferHandler
		// mtree.setTransferHandler(gestureListener);
		//
		// DragSource dragSource = DragSource.getDefaultDragSource();
		// dragSource.createDefaultDragGestureRecognizer(etree,
		// DnDConstants.ACTION_COPY_OR_MOVE, gestureListener);
		// new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE,
		// gestureListener);
	}

	/**
	 * 配置CmTree的选择监听,包含联动控制
	 */
	private void initTreeSelectionListener() {
		VaTree etree = getTree();

		etree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		TreeSelectionListener treeSelectListenre_etree = new VaTreeSelectionListener(null);

		etree.addTreeSelectionListener(treeSelectListenre_etree);

	}

	/**
	 * 配置CmTree的鼠标监听,包含m视图新节点创建控制
	 */
	private void initMouseListener() {
		VaMenuItemFactory itemEFactory = new VaEBOMenuItemFactory(true);
		MouseListener eAdapter = new VaTreeMouseAdapter(VaContext.getMainFrame(), itemEFactory);
//		getTree().addMouseListener(eAdapter);
		scrollTreePane2.getTree().addMouseListener(eAdapter);
		scrollTreePane.getTree().addMouseListener(eAdapter);


	}

	@Override
	protected void registerTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub
		CmTaskHelper.registerTaskExecutor("VaEBomTreePanel.getSelectedPViewNodeList", this, true);
		CmTaskHelper.registerTaskExecutor("VaEBomTreePanel.setSelectedPViewNodeList", this, true);
		CmTaskHelper.registerTaskExecutor("VaEBomTreePanel.getSelectedCopyNodeList", this, true);
		CmTaskHelper.registerTaskExecutor("VaEBomTreePanel.setSelectedCopyNodeList", this, true);
		CmTaskHelper.registerTaskExecutor("VaEBomTreePanel.setSelectedForInstance", this, true);
	}

	@Override
	protected void unregisterTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub

	}
}
