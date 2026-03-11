package com.glaway.mpm.qmIntf.measure;

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
import com.glaway.mpm.model.Tool;
import com.glaway.mpm.model.ToolType;
import com.glaway.mpm.qmIntf.common.model.CommonTree;
import com.glaway.mpm.qmIntf.resourceTree.RsTreeUtil;
import com.glaway.mpm.qmIntf.resourceTree.model.ToolNode;
import com.glaway.mpm.qmIntf.resourceTree.model.ToolTreeNode;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.ResourceIntf;


public class MeasureTree extends CommonTree {
	private static final long serialVersionUID = 1L;
	private MeasureTreeNode root = null;
	private MeasureTreeMouseAdapter mouseAdapter = null;
	private ToolTipManager ttm;
	private Map<String, byte[]> imageCache = new HashMap<String, byte[]>();
	public MeasureTreePopupMenu popupMenu;
	private static boolean isExpanded = false;
	private MeasureTree tree = null;
	private static NewTechnicsPart window;

	public MeasureTree(MeasureTreeNode dictn, final MeasureInfoPanel rsInfoPanel,
			NewTechnicsPart frame) {
		super();
		popupMenu = new MeasureTreePopupMenu(frame, this);
		if (dictn != null)
			root = dictn;
		((DefaultTreeModel) getModel()).setRoot(root);
		setCellRenderer(new MeasureTreeRenderer());
		mouseAdapter = new MeasureTreeMouseAdapter(this);
		addMouseListener(mouseAdapter);
		tree = this;
		this.window = frame;

		addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = e.getPath();
				if (path != null) {
					Object value = path.getLastPathComponent();
					if (value instanceof ToolNode) {
						ToolNode node = (ToolNode) value;
						Tool tool = node.getTool();
						String type = tool.getType();
						rsInfoPanel.getjLabel1().setVisible(true);
						rsInfoPanel.getjLabel2().setVisible(true);
						rsInfoPanel.getjLabel3().setVisible(true);
						rsInfoPanel.getjLabel4().setVisible(true);
						rsInfoPanel.getjLabel5().setVisible(true);
						rsInfoPanel.getjLabel6().setVisible(true);
						rsInfoPanel.getjLabel11().setVisible(true);
						rsInfoPanel.getjLabel12().setVisible(true);
						rsInfoPanel.getjLabel13().setVisible(false);
						rsInfoPanel.getjLabel14().setVisible(false);
						rsInfoPanel.getjLabel15().setVisible(false);
						rsInfoPanel.getjLabel16().setVisible(false);
						rsInfoPanel.getjLabel21().setVisible(false);
						rsInfoPanel.getjLabel22().setVisible(false);
						rsInfoPanel.getjLabel23().setVisible(false);
						rsInfoPanel.getjLabel24().setVisible(false);
						rsInfoPanel.getjLabel25().setVisible(false);
						rsInfoPanel.getjLabel26().setVisible(false);
						rsInfoPanel.getjLabel31().setVisible(false);
						rsInfoPanel.getjLabel32().setVisible(false);
						rsInfoPanel.getjLabel33().setVisible(false);
						rsInfoPanel.getjLabel34().setVisible(false);
						rsInfoPanel.getjLabel35().setVisible(false);
						rsInfoPanel.getjLabel36().setVisible(false);

						if(type.contains(LoadConfig.getInstance().getMeasure())){
							rsInfoPanel.getjLabel1().setText("编号");
							rsInfoPanel.getjLabel2().setText(tool.getToolNum());
							rsInfoPanel.getjLabel3().setText("名称");
							rsInfoPanel.getjLabel4().setText(tool.getToolName());
							rsInfoPanel.getjLabel5().setText("型号");
							rsInfoPanel.getjLabel6().setText(tool.getMindex());
							rsInfoPanel.getjLabel11().setText("规格");
							rsInfoPanel.getjLabel12().setText(tool.getCsize());
						}
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
					if (value instanceof ToolTreeNode) {
						final ToolTreeNode node = (ToolTreeNode)value;
						String name = node.getName();
						if("量具".equals(name) && !isExpanded) {
							final VaActionProgressBar progressBar = new VaActionProgressBar(
									window, "量具树", "正在加载量具资源,请等待...", "加载量具资源中");
							Thread thread = new Thread() {
								public void run() {
									isExpanded = true;
									node.removeAllChildren();
									ToolType measureType = ResourceIntf.getAllMeasures();
									if (measureType != null) {
										RsTreeUtil.generateToolNode(measureType, node);
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

	public MeasureTreeNode getRoot() {
		return root;
	}

	public void setRoot(MeasureTreeNode root) {
		this.root = root;
		((DefaultTreeModel) this.getModel()).setRoot(root);
	}

	public MeasureTreeMouseAdapter getMouseAdapter() {
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
