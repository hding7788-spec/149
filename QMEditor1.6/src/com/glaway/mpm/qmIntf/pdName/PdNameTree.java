package com.glaway.mpm.qmIntf.pdName;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.glaway.mpm.model.KtType;
import com.glaway.mpm.model.PdNameType;
import com.glaway.mpm.qmIntf.common.model.CommonTree;
import com.glaway.mpm.qmIntf.resourceTree.PdNameInfoPanel;
import com.glaway.mpm.qmIntf.resourceTree.RsTreeUtil;
import com.glaway.mpm.qmIntf.resourceTree.model.KtTreeNode;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class PdNameTree extends CommonTree {
	private static final long serialVersionUID = 1L;
	private WorkShopNode root = null;
	private PdNameMouseAdapter mouseAdapter = null;
	private static boolean isExpanded = false;
	private PdNameTree tree = null;
	private static NewTechnicsPart window;

	public PdNameTree() {
		this(null,null,null);
	}

	public PdNameTree(WorkShopNode dictn,final PdNameInfoPanel pdNameInfoPanel,NewTechnicsPart frame) {
		super();
		if (dictn != null)
			root = dictn;
		((DefaultTreeModel) getModel()).setRoot(root);
		setCellRenderer(new PdNameTreeRenderer());
		mouseAdapter = new PdNameMouseAdapter(this);
		addMouseListener(mouseAdapter);
		tree = this;
		this.window = frame;

		addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = e.getPath();
				if (path != null) {
					Object object = path.getLastPathComponent();
					if (object instanceof PdNameNode) {
						PdNameNode node = (PdNameNode) object;
						pdNameInfoPanel.initDatas(node.getPdName());
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
					if (value instanceof WorkShopNode) {
						final WorkShopNode node = (WorkShopNode)value;
						String name = node.getName();
						if("工序名称".equals(name) && !isExpanded) {
							final VaActionProgressBar progressBar = new VaActionProgressBar(
									window, "工序名称树", "正在加载工序名称资源,请等待...", "加载工序名称资源中");
							Thread thread = new Thread() {
								public void run() {
									isExpanded = true;
									node.removeAllChildren();
									PdNameType pdNameType = ResourceIntf.get812AllPdNameType();
									if (pdNameType != null) {
										PdNameTreeXmlUtil.parseRootNode(node,pdNameType);
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
	}

	public WorkShopNode getRoot() {
		return root;
	}

	public void setRoot(WorkShopNode root) {
		this.root = root;
		((DefaultTreeModel) this.getModel()).setRoot(root);
	}

	public PdNameMouseAdapter getMouseAdapter() {
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
