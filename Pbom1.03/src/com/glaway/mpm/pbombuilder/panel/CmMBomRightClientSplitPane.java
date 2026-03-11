package com.glaway.mpm.pbombuilder.panel;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseListener;
import java.util.Comparator;

import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.data.CmDefaultTreeLinkage;
import com.glaway.mpm.pbombuilder.data.CmMenuItemFactory;
import com.glaway.mpm.pbombuilder.data.CmTreeLinkage;
import com.glaway.mpm.pbombuilder.data.CmTreeNodeComparator;
import com.glaway.mpm.pbombuilder.jws.CmContext;
import com.glaway.mpm.pbombuilder.tree.CMUpdateAttributeListener;
import com.glaway.mpm.pbombuilder.tree.CmDefaultDragNodeMerger;
import com.glaway.mpm.pbombuilder.tree.CmMBomDragNodeDetect;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeDragListeners;
import com.glaway.mpm.pbombuilder.tree.CmTreeSelectionListener;
import com.glaway.mpm.pbombuilder.tree.item.CmEBOMenuItemFactory;
import com.glaway.mpm.pbombuilder.tree.item.CmMBomMMenuItemFactory;
import com.glaway.mpm.pbombuilder.util.CmTreeMouseAdapter;

/**
 * Created on 2012-10-16
 *
 * @author chenyunlong
 *
 */
public class CmMBomRightClientSplitPane extends JSplitPane {
	private static final long serialVersionUID = -6214093173263211488L;

	private CmEBomTreePanel panelEBomTree;
	private CmMBomTreePanel panelMBomTree;
	private CmMBomMainFrame owner;

	public CmMBomRightClientSplitPane(CmMBomMainFrame owner) {
		CmConnectFrame.startAnimFrame.setHeaderMessage("加载右面板");
		this.owner = owner;
		// 搜索、有效性过滤、显示可视化
		panelEBomTree = new CmEBomTreePanel(owner);
		// 查找并添加、新建AO组件、显示可视化
		panelMBomTree = new CmMBomTreePanel(owner,getEBomTree());

		this.setLeftComponent(panelEBomTree);
		this.setRightComponent(panelMBomTree);
		this.setOneTouchExpandable(true);
		this.setContinuousLayout(true);
		this.setDividerSize(10);
		//this.initDragListener();

		initListener();
		CmCommonPackageAction commonPackageAction=new CmCommonPackageAction();
		commonPackageAction.packageAllNode(getEBomTree());
		commonPackageAction.packageAllNode(getMBomTree());
		getEBomTree().expandAllLevels(getEBomTree().getRoot());
		getMBomTree().expandAllLevels(getMBomTree().getRoot());

//      this.setVisible(!CmConnectFrame.isPlanningView);
      if(CmConnectFrame.isPlanningView){
//    	  getEBomTree().getRoot().removeAllChildren();
    	  getEBomTree().setVisible(false);
    	  getEBomTree().updateUI();
      }
      CmConnectFrame.startAnimFrame.setHeaderMessage("完成加载右面板");
	}

	public CmTree getEBomTree() {
		return panelEBomTree.getTree();
	}

	public CmTree getMBomTree() {
		return panelMBomTree.getTree();
	}

	/**
	 *配置CmTree的拖动监听
	 */
	private void initDragListener() {// load drag listener
		CmTree mtree = getMBomTree();
		CmTree etree = getEBomTree();
		etree.setDragEnabled(true);
		CmMBomDragNodeDetect detect = new CmMBomDragNodeDetect();
		CmDefaultDragNodeMerger merger = new CmDefaultDragNodeMerger(mtree, mtree.getRoot(), CmContext.getMainFrame());
		// 定义源树监听
		CmTreeDragListeners gestureListener = new CmTreeDragListeners(etree,mtree, detect, merger);
		// 设置目标树TransferHandler
		mtree.setTransferHandler(gestureListener);

		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(etree, DnDConstants.ACTION_COPY_OR_MOVE, gestureListener);
		new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, gestureListener);
		dragSource.createDefaultDragGestureRecognizer(mtree, DnDConstants.ACTION_COPY_OR_MOVE, gestureListener);
		new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, gestureListener);
	}

	public CmEBomTreePanel getPanelEBomTree() {
		return panelEBomTree;
	}

	public void setPanelEBomTree(CmEBomTreePanel panelEBomTree) {
		this.panelEBomTree = panelEBomTree;
	}

	private void initListener() {
		if (owner.isEdit()) {
			//initDragListener();
		}
		initTreeSelectionListener();
		initMouseListener();
	}

	/**
	 *配置CmTree的选择监听,包含联动控制
	 */
	@SuppressWarnings("unchecked")
	private void initTreeSelectionListener() {
		CmConnectFrame.startAnimFrame.setHeaderMessage("加载EBOM EMBOM 联动监听器");

		CmTree etree = getEBomTree();
		CmTree mtree = getMBomTree();

		etree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		mtree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		Comparator comtor = new CmTreeNodeComparator();
		CmTreeLinkage linkage_etree = new CmDefaultTreeLinkage(comtor).addLinkageTree(mtree);
		CmTreeLinkage linkage_mtree = new CmDefaultTreeLinkage(comtor).addLinkageTree(etree);

		TreeSelectionListener treeSelectListenre_etree = new CmTreeSelectionListener(linkage_etree);
		TreeSelectionListener treeSelectListenre_mtree = new CmTreeSelectionListener(linkage_mtree);

		etree.addTreeSelectionListener(treeSelectListenre_etree);
		mtree.addTreeSelectionListener(treeSelectListenre_mtree);
		//mtree.addTreeSelectionListener(new CMUpdateAttributeListener());

		CmConnectFrame.startAnimFrame.setHeaderMessage("完成加载EBOM EMBOM 联动监听器");
	}

	/**
	 *配置CmTree的鼠标监听,包含m视图新节点创建控制
	 */
	private void initMouseListener() {
		CmMenuItemFactory itemEFactory = new CmEBOMenuItemFactory(owner.isEdit());
		MouseListener eAdapter = new CmTreeMouseAdapter(owner, itemEFactory);
		getEBomTree().addMouseListener(eAdapter);

		CmMenuItemFactory itemMFactory = new CmMBomMMenuItemFactory(owner.isEdit());
		MouseListener mAdapter = new CmTreeMouseAdapter(owner, itemMFactory);
		getMBomTree().addMouseListener(mAdapter);
		// getMBomTree().addMouseListener(new
		// CmMBomTreeMouseAdapter(panelMBomTree,owner.isEdit()));
	}
}
