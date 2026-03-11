package com.glaway.mpm.qmIntf.dashboard;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.glaway.mpm.model.KtType;
import com.glaway.mpm.model.UnSDashboard;
import com.glaway.mpm.model.UnSDashboardType;
import com.glaway.mpm.qmIntf.common.model.CommonTree;
import com.glaway.mpm.qmIntf.resourceTree.RsTreeUtil;
import com.glaway.mpm.qmIntf.resourceTree.model.KtTreeNode;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class UnSDashboardTree extends CommonTree {

	private UnSDashboardTreeNode root = null;
	// private Logger logger = LogUtil.getLogger(this.getClass());
	private UnSDashboardTreeMouseAdapter mouseAdapter;
	private ToolTipManager ttm;
	private Map<String, byte[]> imageCache = new HashMap<String, byte[]>();
	public UnSDashboardTreePopupMenu popupMenu;
	private static boolean isExpanded = false;
	private UnSDashboardTree tree = null;
	private static NewTechnicsPart window;

	@Override
	public void expandPath(TreePath path) {
		super.expandPath(path);
	}

	@Override
	public void collapsePath(TreePath path) {
		super.collapsePath(path);
	}

	public UnSDashboardTree(UnSDashboardTreeNode dictn, final UnSDashboardInfoPanel dashboardInfoPanel, boolean flag,NewTechnicsPart frame) {
		super();
		if (dictn != null)
			root = dictn;
		((DefaultTreeModel) getModel()).setRoot(root);
		setCellRenderer(new UnSDashboardTreeRenderer());
		mouseAdapter = new UnSDashboardTreeMouseAdapter(this);
		addMouseListener(mouseAdapter);
		popupMenu = new UnSDashboardTreePopupMenu(this, mouseAdapter.observable);
		tree = this;
		this.window = frame;

		if (flag) {
			addTreeSelectionListener(new TreeSelectionListener() {

				@Override
				public void valueChanged(TreeSelectionEvent e) {
					TreePath path = e.getPath();
					if (path != null) {
						Object object = path.getLastPathComponent();
						if (object instanceof UnSDashboardNode) {
							UnSDashboardNode node = (UnSDashboardNode) object;
							UnSDashboard dashboard = node.getUnSDashboard();
							dashboardInfoPanel.getjLabel4().setText(dashboard.getNumber());
							dashboardInfoPanel.getjLabel5().setText(dashboard.getName());
							dashboardInfoPanel.getjLabel6().setText(dashboard.getEquipmentType());
							dashboardInfoPanel.getjLabe20().setText(dashboard.getMindex());
							dashboardInfoPanel.getjLabe21().setText(dashboard.getCsize());
						}
					}
				}
			});
		}

		addTreeExpansionListener(new TreeExpansionListener() {

			@Override
			public void treeExpanded(TreeExpansionEvent e) {
				TreePath path = e.getPath();
				if (path != null) {
					Object value = path.getLastPathComponent();
					if (value instanceof UnSDashboardTreeNode) {
						final UnSDashboardTreeNode node = (UnSDashboardTreeNode)value;
						String name = node.getName();
						if("非标准仪器仪表".equals(name) && !isExpanded) {
							final VaActionProgressBar progressBar = new VaActionProgressBar(
									window, "非标准仪器仪表树", "正在加载非标准仪器仪表资源,请等待...", "加载非标准仪器仪表资源中");
							Thread thread = new Thread() {
								public void run() {
									isExpanded = true;
									node.removeAllChildren();
									UnSDashboardType dashboardType = ResourceIntf.getAllUnSDashboards();
									if (dashboardType != null) {
										UnSDashboardTreeXmlUtil.parseNode(node, dashboardType.getDashboardTypes());
										if(dashboardType.getDashboards()!=null)
										for (UnSDashboard dashboard : dashboardType.getDashboards()) {
											UnSDashboardNode node1 = new UnSDashboardNode(dashboard, true);
											node.add(node1);
										}
									}
									reFlash();

									progressBar.finish();
									progressBar.setVisible(false);
								}
							};

							thread.start();
							progressBar.setVisible(true);
						}
					}
				}
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				// TODO Auto-generated method stub

			}
		});

		ttm = ToolTipManager.sharedInstance();
		ttm.setDismissDelay(10000);// pref
		ttm.setInitialDelay(100);// pref
		ttm.setReshowDelay(50);
		ttm.setDismissDelay(2000);
		ttm.registerComponent(this);
	}

	@Override
	public String getToolTipText(MouseEvent evt) {
		if (getRowForLocation(evt.getX(), evt.getY()) == -1)
			return null;
		TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
		Object object = curPath.getLastPathComponent();
		if (object instanceof UnSDashboardNode) {
			UnSDashboardNode node = (UnSDashboardNode) object;
			return node.getUnSDashboard().getName();
		}
		return null;
	}

	public UnSDashboardTreeNode getRoot() {
		return root;
	}

	public void setRoot(UnSDashboardTreeNode root) {
		this.root = root;
		((DefaultTreeModel) this.getModel()).setRoot(root);
	}

	public UnSDashboardTreeMouseAdapter getMouseAdapter() {
		return mouseAdapter;
	}

	public void reFlash() {
		//this.updateUI();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tree.updateUI();
			}
		});
	}
}
