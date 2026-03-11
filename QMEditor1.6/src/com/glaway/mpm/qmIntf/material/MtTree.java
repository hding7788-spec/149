package com.glaway.mpm.qmIntf.material;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.glaway.mpm.model.KtType;
import com.glaway.mpm.model.MtType;
import com.glaway.mpm.qmIntf.common.model.CommonTree;
import com.glaway.mpm.qmIntf.resourceTree.RsTreeUtil;
import com.glaway.mpm.qmIntf.resourceTree.model.KtTreeNode;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class MtTree extends CommonTree {
	private static final long serialVersionUID = 1L;
	private MtTreeNode root = null;
	private MtTreeMouseAdapter mouseAdapter = null;
	private static boolean isExpanded = false;
	private MtTree tree = null;
	private static NewTechnicsPart window;

	// private ToolTipManager ttm;

	public MtTree() {
		this(null, null,null);
	}

	public MtTree(MtTreeNode dictn, final MtInfoPanel mtInfoPanel,NewTechnicsPart frame) {
		super();
		if (dictn != null)
			root = dictn;
		((DefaultTreeModel) getModel()).setRoot(root);
		setCellRenderer(new MtTreeRenderer());
		mouseAdapter = new MtTreeMouseAdapter(this);
		addMouseListener(mouseAdapter);
		tree = this;
		this.window = frame;

		addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = e.getPath();
				if (path != null) {
					Object object = path.getLastPathComponent();
					if (object instanceof MtNode) {
						MtNode node = (MtNode) object;
						mtInfoPanel.initDatas(node.getMaterial());
					}
				}
			}
		});

		addTreeExpansionListener(new TreeExpansionListener() {

			@Override
			public void treeExpanded(TreeExpansionEvent e) {
				TreePath path = e.getPath();
				if (path != null) {
					Object value = path.getLastPathComponent();
					if (value instanceof MtTreeNode) {
						final MtTreeNode node = (MtTreeNode)value;
						String name = node.getName();
						if("辅料".equals(name) && !isExpanded) {
							final VaActionProgressBar progressBar = new VaActionProgressBar(
									window, "工艺辅料树", "正在加载工艺辅料资源,请等待...", "加载工艺辅料资源中");
							Thread thread = new Thread() {
								public void run() {
									isExpanded = true;
									node.removeAllChildren();
									MtType mtType = ResourceIntf.getMaterialTypes();
									if (mtType != null) {
										List<MtType> types = mtType.getMtTypes();
										if (types != null && types.size() != 0) {
											MtTreeXmlUtil.parseNode(node, types);
											if (types == null || types.size() == 0) {
												MtTreeXmlUtil.parseMaterial(node, mtType.getMaterials());
											}
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

		// ttm = ToolTipManager.sharedInstance();
		// ttm.setDismissDelay(10000);// pref
		// ttm.setInitialDelay(100);// pref
		// ttm.setReshowDelay(50);
		// ttm.setDismissDelay(2000);
		// ttm.registerComponent(this);
	}

	// @Override
	// public String getToolTipText(MouseEvent evt) {
	// if (getRowForLocation(evt.getX(), evt.getY()) == -1)
	// return null;
	// TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
	// Object object = curPath.getLastPathComponent();
	// if (object instanceof MtNode) {
	// MtNode node = (MtNode) object;
	// return node.getMaterial().;
	// }
	// return null;
	// }

	public MtTreeNode getRoot() {
		return root;
	}

	public void setRoot(MtTreeNode root) {
		this.root = root;
		((DefaultTreeModel) this.getModel()).setRoot(root);
	}

	public MtTreeMouseAdapter getMouseAdapter() {
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
