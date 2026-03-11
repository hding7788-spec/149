package com.glaway.mpm.qmIntf.resourceTree;

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

import com.glaway.mpm.model.KnifeTool;
import com.glaway.mpm.model.KtType;
import com.glaway.mpm.qmIntf.common.model.CommonTree;
import com.glaway.mpm.qmIntf.resourceTree.model.KtNode;
import com.glaway.mpm.qmIntf.resourceTree.model.KtTreeNode;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class KtTree extends CommonTree {

	private static final long serialVersionUID = 1L;
	private KtTreeNode root = null;
	private KtTreeMouseAdapter mouseAdapter = null;
	private ToolTipManager ttm;
	private Map<String, byte[]> imageCache = new HashMap<String, byte[]>();
	private static boolean isExpanded = false;
	private KtTree tree = null;
	private static NewTechnicsPart window;

	public KtTree(KtTreeNode dictn, final RsInfoPanel rsInfoPanel, NewTechnicsPart frame) {
		super();
		if (dictn != null)
			root = dictn;
		((DefaultTreeModel) getModel()).setRoot(root);
		setCellRenderer(new KtTreeRenderer());
		mouseAdapter = new KtTreeMouseAdapter(this);
		addMouseListener(mouseAdapter);
		tree = this;
		this.window = frame;

		addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = e.getPath();
				if (path != null) {
					Object value = path.getLastPathComponent();
					if (value instanceof KtNode) {
						rsInfoPanel.getjLabel1().setVisible(true);
						rsInfoPanel.getjLabel2().setVisible(true);

						rsInfoPanel.getjLabel3().setVisible(true);
						rsInfoPanel.getjLabel4().setVisible(true);

						rsInfoPanel.getjLabel5().setVisible(true);
						rsInfoPanel.getjLabel6().setVisible(true);

						rsInfoPanel.getjLabel11().setVisible(true);
						rsInfoPanel.getjLabel12().setVisible(true);

						rsInfoPanel.getjLabel13().setVisible(true);
						rsInfoPanel.getjLabel14().setVisible(true);

						rsInfoPanel.getjLabel15().setVisible(true);
						rsInfoPanel.getjLabel16().setVisible(true);

						rsInfoPanel.getjLabel21().setVisible(true);
						rsInfoPanel.getjLabel22().setVisible(true);

						rsInfoPanel.getjLabel23().setVisible(true);
						rsInfoPanel.getjLabel24().setVisible(true);

						rsInfoPanel.getjLabel25().setVisible(true);
						rsInfoPanel.getjLabel26().setVisible(true);

						rsInfoPanel.getjLabel31().setVisible(true);
						rsInfoPanel.getjLabel32().setVisible(true);

						rsInfoPanel.getjLabel33().setVisible(true);
						rsInfoPanel.getjLabel34().setVisible(true);

						rsInfoPanel.getjLabel35().setVisible(true);
						rsInfoPanel.getjLabel36().setVisible(true);

						rsInfoPanel.getjLabel37().setVisible(true);
						rsInfoPanel.getjLabel38().setVisible(true);
						rsInfoPanel.getjLabel39().setVisible(true);
						rsInfoPanel.getjLabel40().setVisible(true);
						rsInfoPanel.getjLabel41().setVisible(true);
						rsInfoPanel.getjLabel42().setVisible(true);
						rsInfoPanel.getjLabel43().setVisible(true);
						rsInfoPanel.getjLabel44().setVisible(true);
						rsInfoPanel.getjLabel45().setVisible(true);
						rsInfoPanel.getjLabel46().setVisible(true);
						rsInfoPanel.getjLabel47().setVisible(true);
						rsInfoPanel.getjLabel48().setVisible(true);

						KtNode node = (KtNode) value;
						KnifeTool knifeTool = node.getKnifeTool();
						rsInfoPanel.getjLabel1().setText("编号");
						rsInfoPanel.getjLabel2().setText(knifeTool.getKnifeToolNum());
						rsInfoPanel.getjLabel3().setText("名称");
						rsInfoPanel.getjLabel4().setText(knifeTool.getKnifeToolName());

						rsInfoPanel.getjLabel5().setText("材料");
						rsInfoPanel.getjLabel6().setText(knifeTool.getCmat());
						rsInfoPanel.getjLabel11().setText("刃口直径");
						rsInfoPanel.getjLabel12().setText(knifeTool.getRkzj());
						rsInfoPanel.getjLabel13().setText("夹持直径");
						rsInfoPanel.getjLabel14().setText(knifeTool.getJczj());
						rsInfoPanel.getjLabel15().setText("刃口长度");
						rsInfoPanel.getjLabel16().setText(knifeTool.getRkcd());
						rsInfoPanel.getjLabel21().setText("总长度");
						rsInfoPanel.getjLabel22().setText(knifeTool.getZcd());
						rsInfoPanel.getjLabel23().setText("公差");
						rsInfoPanel.getjLabel24().setText(knifeTool.getGc());
						rsInfoPanel.getjLabel25().setText("最小加工尺寸");
						rsInfoPanel.getjLabel26().setText(knifeTool.getZxjgcc());
						rsInfoPanel.getjLabel31().setText("最大加工尺寸");
						rsInfoPanel.getjLabel32().setText(knifeTool.getZdjgcc());
						rsInfoPanel.getjLabel33().setText("结构形式");
						rsInfoPanel.getjLabel34().setText(knifeTool.getJgxs());
						rsInfoPanel.getjLabel35().setText("接口类型");
						rsInfoPanel.getjLabel36().setText(knifeTool.getJklx());
						rsInfoPanel.getjLabel37().setText("技术备注");
						rsInfoPanel.getjLabel38().setText(knifeTool.getJsbz());
						rsInfoPanel.getjLabel39().setText("刃口圆角半径");
						rsInfoPanel.getjLabel40().setText(knifeTool.getRkyjbj());
						rsInfoPanel.getjLabel41().setText("类别");
						rsInfoPanel.getjLabel42().setText(knifeTool.getKnifetype());
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
					if (value instanceof KtTreeNode) {
						final KtTreeNode node = (KtTreeNode)value;
						String name = node.getName();
						if("刀具".equals(name) && !isExpanded) {
							final VaActionProgressBar progressBar = new VaActionProgressBar(
									window, "刀具树", "正在加载刀具资源,请等待...", "加载刀具资源中");
							Thread thread = new Thread() {
								public void run() {
									isExpanded = true;
									node.removeAllChildren();
									KtType ktType = ResourceIntf.getAllKnifeTools();
									if (ktType != null) {
										RsTreeUtil.getData(KtType.class, ktType, node);
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

	public KtTreeNode getRoot() {
		return root;
	}

	public void setRoot(KtTreeNode root) {
		this.root = root;
		((DefaultTreeModel) this.getModel()).setRoot(root);
	}

	public KtTreeMouseAdapter getMouseAdapter() {
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
