/**
 * <br>Created on 2010-10-23
 * @author Dennis Huang - ���ٽ�
 */
package com.glaway.mpm.qmIntf.decoratePView.showPanel.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.action.VaAction;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeMouseAdapter;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.visual.view.tree.menu.VaEBOMenuItemFactory;
import com.glaway.mpm.visual.view.tree.menu.VaMenuItemFactory;
import com.glaway.mpm.visual.view.ui.VaAbstractPanel;
import com.glaway.mpm.visual.view.ui.VaScrollPaneTree;


/**
 * <br>
 * Created on 2010-10-23
 *
 * @author Dennis Huang - ���ٽ�
 */
public class DpEBomTreePanel extends VaAbstractPanel {
	private static final long serialVersionUID = 7452042908382287580L;
	private static final VaLogger log = VaLogger
			.getLogger(DpEBomTreePanel.class);

	// for CmTaskExecutor -> start
	private volatile boolean executorActive;
	// for CmTaskExecutor -> end

	private VaScrollPaneTree scrollTreePane;
//	private JToolBar toolBar;

	// actions -> start
	private VaAction actSearch;
	// private CmEBomEffAction actValidity;
//	private VaAction actShowInProductView;
	// actions -> end

	// UI components -> start
	// private JButton btnSearch;
	// private JButton btnValidity;
//	private JButton btnShowInProductView;
	private JButton sureButton;
	private JButton cancelButton;

	private List<VaTreeNode> nodeList = new ArrayList<VaTreeNode>();
	private List<VaTreeNode> nodeFromPrvList = new ArrayList<VaTreeNode>();
	private JPanel mainOwner;

	public static boolean flag;

	public List<VaTreeNode> getSelectedCopyNodeList(Object sender, Object params) {
		return nodeList;
	}

	@SuppressWarnings("unchecked")
	public void setSelectedCopyNodeList(Object sender, Object params) {
		this.nodeList = (List<VaTreeNode>) params;
	}

	public List<VaTreeNode> getSelectedPViewNodeList(Object sender,
			Object params) {
		return nodeFromPrvList;
	}

	@SuppressWarnings("unchecked")
	public void setSelectedPViewNodeList(Object sender, Object params) {
		this.nodeFromPrvList = (List<VaTreeNode>) params;
	}

	// UI components -> end

	public DpEBomTreePanel(JPanel owner) {
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

//	private JToolBar buildToolBar() {
//		JToolBar toolBar = new JToolBar();
//		toolBar.setBackground(VaTheme.VA_TURQUOISE);
//		toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0,
//				Color.WHITE));
//		toolBar.setFloatable(false);
//		toolBar.setRollover(true);
		// toolBar.set
		// toolBar.add(btnSearch);
		// toolBar.add(btnValidity);
//		toolBar.add(btnShowInProductView);
//		toolBar.add(sureButton);
//		toolBar.add(cancelButton);
//		return toolBar;
//	}

	@Override
	protected void initActions() {

	}

	@Override
	protected void initComponents() {

		scrollTreePane = new VaScrollPaneTree(null);
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

//		actShowInProductView = new VaPivewAction(mainOwner, getTree(),
//				VaEPViewStructureGenerator.class, VaEPVSelectionObserver.class);
//		btnShowInProductView = new JButton(actShowInProductView);

//		sureButton = new JButton("确定");
//		sureButton.setToolTipText("确定");
//		cancelButton = new JButton("取消");
//		cancelButton.setToolTipText("取消");
//
//		sureButton.setBackground(VaTheme.VA_TURQUOISE);
//		sureButton.setForeground(Color.WHITE);
//		cancelButton.setBackground(VaTheme.VA_TURQUOISE);
//		cancelButton.setForeground(Color.WHITE);

//		sureButton.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				SubPartSearchDialog.dialog.dispose();
//				flag = true;
//			}
//		});
//
//		cancelButton.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				SubPartSearchDialog.dialog.dispose();
//				flag = false;
//			}
//		});

		// btnShowInProductView.setText("显示");

//		Icon icon = new ImageIcon(
//				VaEBomTreePanel.class.getResource("/images/productView.gif"));
//		btnShowInProductView.setIcon(icon);
//		btnShowInProductView.setBackground(VaTheme.VA_TURQUOISE);
//		btnShowInProductView.setForeground(Color.WHITE);

//		toolBar = buildToolBar();

	}

	@Override
	protected void initDimension() {
		this.setMinimumSize(new Dimension(180, 360));
	}

	@Override
	protected void initLayout() {
		this.setLayout(new BorderLayout());
//		this.add(toolBar, BorderLayout.NORTH);
		this.add(scrollTreePane, BorderLayout.CENTER);
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

	private void initListener() {

		initDragListener();

		initTreeSelectionListener();
		initMouseListener();
	}

	/**
	 * 配置CmTree的拖动监听
	 */
	private void initDragListener() {// load drag listener

		VaTree etree = getTree();
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

		etree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		TreeSelectionListener treeSelectListenre_etree = new DpTreeSelectionListener(
				null);

		etree.addTreeSelectionListener(treeSelectListenre_etree);

	}

	/**
	 * 配置CmTree的鼠标监听,包含m视图新节点创建控制
	 */
	private void initMouseListener() {
		VaMenuItemFactory itemEFactory = new VaEBOMenuItemFactory(true);
		MouseListener eAdapter = new VaTreeMouseAdapter(
				VaContext.getMainFrame(), itemEFactory);
		getTree().addMouseListener(eAdapter);
	}

	@Override
	protected void registerTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void unregisterTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub

	}
}