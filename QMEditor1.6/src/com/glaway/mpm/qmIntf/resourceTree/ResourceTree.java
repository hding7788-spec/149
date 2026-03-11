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

import com.glaway.mpm.model.FkType;
import com.glaway.mpm.model.Frock;
import com.glaway.mpm.model.KnifeTool;
import com.glaway.mpm.model.Tool;
import com.glaway.mpm.qmIntf.common.model.CommonTree;
import com.glaway.mpm.qmIntf.resourceTree.model.FkNode;
import com.glaway.mpm.qmIntf.resourceTree.model.FkTreeNode;
import com.glaway.mpm.qmIntf.resourceTree.model.KtNode;
import com.glaway.mpm.qmIntf.resourceTree.model.ToolNode;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.ResourceIntf;


public class ResourceTree extends CommonTree {
	private static final long serialVersionUID = 1L;
	private ResourceTreeNode root = null;
	private ResourceTreeMouseAdapter mouseAdapter = null;
	private ToolTipManager ttm;
	private Map<String, byte[]> imageCache = new HashMap<String, byte[]>();
	public RsTreePopupMenu popupMenu;
	private static boolean isExpanded = false;
	private ResourceTree tree = null;
	private static NewTechnicsPart window;

	public ResourceTree(ResourceTreeNode dictn, final RsInfoPanel rsInfoPanel,
			NewTechnicsPart frame) {
		super();
		popupMenu = new RsTreePopupMenu(frame, this);
		if (dictn != null)
			root = dictn;
		((DefaultTreeModel) getModel()).setRoot(root);
		setCellRenderer(new ResourceTreeRenderer());
		mouseAdapter = new ResourceTreeMouseAdapter(this);
		addMouseListener(mouseAdapter);
		tree = this;
		this.window = frame;

		addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = e.getPath();
				if (path != null) {
					Object value = path.getLastPathComponent();
					if (value instanceof FkNode) {
						FkNode node = (FkNode) value;
						Frock frock = node.getFrock();

						rsInfoPanel.getjLabel1().setVisible(true);
						rsInfoPanel.getjLabel2().setVisible(true);
						rsInfoPanel.getjLabel3().setVisible(true);
						rsInfoPanel.getjLabel4().setVisible(true);
						rsInfoPanel.getjLabel5().setVisible(true);
						rsInfoPanel.getjLabel6().setVisible(true);
						rsInfoPanel.getjLabel11().setVisible(false);
						rsInfoPanel.getjLabel12().setVisible(false);
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

						rsInfoPanel.getjLabel1().setText("编号");
						rsInfoPanel.getjLabel2().setText(frock.getFrockNum());
						rsInfoPanel.getjLabel3().setText("名称");
						rsInfoPanel.getjLabel4().setText(frock.getFrockName());
						rsInfoPanel.getjLabel5().setText("类别");
						rsInfoPanel.getjLabel6().setText(frock.getFrockType());

					} else if (value instanceof ToolNode) {
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

						if(type.contains(LoadConfig.getInstance().getTool())){
							rsInfoPanel.getjLabel1().setText("工具编号");
							rsInfoPanel.getjLabel2().setText(tool.getToolNum());
							rsInfoPanel.getjLabel3().setText("工具名称");
							rsInfoPanel.getjLabel4().setText(tool.getToolName());
							rsInfoPanel.getjLabel5().setText("型号");
							rsInfoPanel.getjLabel6().setText(tool.getMindex());
							rsInfoPanel.getjLabel11().setText("规格");
							rsInfoPanel.getjLabel12().setText(tool.getCsize());
						}else if(type.contains(LoadConfig.getInstance().getMeasure())){
							rsInfoPanel.getjLabel1().setText("量具编号");
							rsInfoPanel.getjLabel2().setText(tool.getToolNum());
							rsInfoPanel.getjLabel3().setText("量具名称");
							rsInfoPanel.getjLabel4().setText(tool.getToolName());
							rsInfoPanel.getjLabel5().setText("型号");
							rsInfoPanel.getjLabel6().setText(tool.getMindex());
							rsInfoPanel.getjLabel11().setText("规格");
							rsInfoPanel.getjLabel12().setText(tool.getCsize());
						}


//						rsInfoPanel.getjLabel3().setText("工量具规格");
//						rsInfoPanel.getjLabel6().setText(tool.getToolSpec());
//						String oid = tool.getOid();
//						byte[] bytes = imageCache.get(oid);
//						if (bytes == null) {
//							bytes = ResourceIntf.getResourceImage(oid);
//							imageCache.put(oid, bytes);
//						}

					} else if (value instanceof KtNode) {
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

						KtNode node = (KtNode) value;
						KnifeTool knifeTool = node.getKnifeTool();
						rsInfoPanel.getjLabel1().setText("刀具编号");
						rsInfoPanel.getjLabel2().setText(
								knifeTool.getKnifeToolNum());
						rsInfoPanel.getjLabel3().setText("刀具名称");
						rsInfoPanel.getjLabel4().setText(knifeTool.getKnifeToolName());

						rsInfoPanel.getjLabel5().setText("类别");
						rsInfoPanel.getjLabel6().setText(knifeTool.getKnifetype());
						rsInfoPanel.getjLabel11().setText("规格");
						rsInfoPanel.getjLabel12().setText(knifeTool.getCsize());

//						rsInfoPanel.getjLabel5().setText(
//								knifeTool.getKnifeToolName());
//						rsInfoPanel.getjLabel3().setText("刀具规格");
//						rsInfoPanel.getjLabel6().setText(
//								knifeTool.getKnifeToolSpec());
//						String oid = knifeTool.getOid();
//						byte[] bytes = imageCache.get(oid);
//						if (bytes == null) {
//							bytes = ResourceIntf.getResourceImage(oid);
//							imageCache.put(oid, bytes);
//						}

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
					if (value instanceof FkTreeNode) {
						final FkTreeNode node = (FkTreeNode)value;
						String name = node.getName();
						if("工装".equals(name) && !isExpanded) {
							final VaActionProgressBar progressBar = new VaActionProgressBar(
									window, "工装树", "正在加载工装资源,请等待...", "加载工装资源中");
							Thread thread = new Thread() {
								public void run() {
									isExpanded = true;
									node.removeAllChildren();
									FkType fkType = ResourceIntf.getAllFrocks();
									if (fkType != null) {
										RsTreeUtil.getData(FkType.class, fkType, node);
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

	// @Override
	// public String getToolTipText(MouseEvent evt) {
	// if (getRowForLocation(evt.getX(), evt.getY()) == -1)
	// return null;
	// TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
	//
	// Object object = curPath.getLastPathComponent();
	// if (object instanceof FkNode) {
	// FkNode node = (FkNode) object;
	// Frock frock = node.getFrock();
	// return frock.getFrockNum() + ":" + frock.getFrockName();
	// } else if (curPath.getLastPathComponent() instanceof ToolNode) {
	// ToolNode node = (ToolNode) object;
	// Tool tool = node.getTool();
	// return tool.getToolNum() + ":" + tool.getToolName();
	// } else if (curPath.getLastPathComponent() instanceof KtNode) {
	// KtNode node = (KtNode) object;
	// KnifeTool tool = node.getKnifeTool();
	// return tool.getKnifeToolNum() + ":" + tool.getKnifeToolName();
	// } else if (curPath.getLastPathComponent() instanceof MstNode) {
	// MstNode node = (MstNode) object;
	// MeasureTool tool = node.getMeasureTool();
	// return tool.getMeasureToolNum() + ":" + tool.getMeasureToolName();
	// }
	// return null;
	// }

	public ResourceTreeNode getRoot() {
		return root;
	}

	public void setRoot(ResourceTreeNode root) {
		this.root = root;
		((DefaultTreeModel) this.getModel()).setRoot(root);
	}

	public ResourceTreeMouseAdapter getMouseAdapter() {
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
