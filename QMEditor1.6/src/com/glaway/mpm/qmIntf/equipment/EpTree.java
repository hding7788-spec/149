package com.glaway.mpm.qmIntf.equipment;

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

import com.glaway.mpm.model.EpType;
import com.glaway.mpm.model.Equipment;
import com.glaway.mpm.model.KtType;
import com.glaway.mpm.qmIntf.common.model.CommonTree;
import com.glaway.mpm.qmIntf.resourceTree.RsTreeUtil;
import com.glaway.mpm.qmIntf.resourceTree.model.KtTreeNode;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class EpTree extends CommonTree {
	private static final long serialVersionUID = 1L;
	private EpTreeNode root = null;
	// private Logger logger = LogUtil.getLogger(this.getClass());
	private EpTreeMouseAdapter mouseAdapter;
	private ToolTipManager ttm;
	private Map<String, byte[]> imageCache = new HashMap<String, byte[]>();
	public EpTreePopupMenu popupMenu;
	private static boolean isExpanded = false;
	private EpTree tree = null;
	private static NewTechnicsPart window;

	@Override
	public void expandPath(TreePath path) {
		// Object object = path.getLastPathComponent();
		// if (!(object instanceof EpNode)) {
		super.expandPath(path);
		// }
	}

	@Override
	public void collapsePath(TreePath path) {
		// Object object = path.getLastPathComponent();
		// if (!(object instanceof EpNode)) {
		super.collapsePath(path);
		// }
	}

	public EpTree(EpTreeNode dictn, final EpInfoPanel epInfoPanel, boolean flag,NewTechnicsPart frame) {
		super();
		if (dictn != null)
			root = dictn;
		((DefaultTreeModel) getModel()).setRoot(root);
		setCellRenderer(new EpTreeRenderer());
		mouseAdapter = new EpTreeMouseAdapter(this);
		addMouseListener(mouseAdapter);
		tree = this;
		this.window = frame;

		popupMenu = new EpTreePopupMenu(this, mouseAdapter.observable);
		if (flag) {
			addTreeSelectionListener(new TreeSelectionListener() {

				@Override
				public void valueChanged(TreeSelectionEvent e) {
					TreePath path = e.getPath();
					if (path != null) {
						Object object = path.getLastPathComponent();
						if (object instanceof EpNode) {
							EpNode node = (EpNode) object;
							Equipment equipment = node.getEquipment();
							epInfoPanel.getjLabel4().setText(equipment.getNumber());
							epInfoPanel.getjLabel5().setText(equipment.getName());
							epInfoPanel.getjLabel6().setText(equipment.getEquipmentType());
							epInfoPanel.getjLabe20().setText(equipment.getMindex());
							epInfoPanel.getjLabe21().setText(equipment.getCsize());
//							String oid = equipment.getOid();
//							byte[] bytes = imageCache.get(oid);
//							if (bytes == null) {
//								bytes = ResourceIntf.getResourceImage(oid);
//								imageCache.put(oid, bytes);
//							}
//							SwingUtil.setIcon(epInfoPanel.getjLabel7(), bytes);
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
					if (value instanceof EpTreeNode) {
						final EpTreeNode node = (EpTreeNode)value;
						String name = node.getName();
						if("设备".equals(name) && !isExpanded) {
							final VaActionProgressBar progressBar = new VaActionProgressBar(
									window, "设备树", "正在加载设备资源,请等待...", "加载设备资源中");
							Thread thread = new Thread() {
								public void run() {
									isExpanded = true;
									node.removeAllChildren();
									EpType epType = ResourceIntf.getAllEquipments();
									if (epType != null) {
										EpTreeXmlUtil.parseNode(node, epType.getEpTypes());
										if(epType.getEquipments()!=null)
										for (Equipment equipment : epType.getEquipments()) {
											EpNode node1 = new EpNode(equipment, true);
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
		if (object instanceof EpNode) {
			EpNode node = (EpNode) object;
			return node.getEquipment().getName();
		}
		return null;
	}

	public EpTreeNode getRoot() {
		return root;
	}

	public void setRoot(EpTreeNode root) {
		this.root = root;
		((DefaultTreeModel) this.getModel()).setRoot(root);
	}

	public EpTreeMouseAdapter getMouseAdapter() {
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
