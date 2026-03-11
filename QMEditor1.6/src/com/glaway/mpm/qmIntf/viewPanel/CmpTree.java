package com.glaway.mpm.qmIntf.viewPanel;

import java.awt.Color;

import javax.swing.JToolTip;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.glaway.mpm.qmIntf.common.model.CommonTree;

public class CmpTree extends CommonTree {
	private static final long serialVersionUID = 1L;
	private CmpTreeNode root = null;
	private boolean showTooltip = false;
	private ToolTipManager ttm;

	public CmpTree() {
		this(null);
	}

	public CmpTree(CmpTreeNode dictn) {
		super();
		if (dictn != null)
			root = dictn;
		((DefaultTreeModel) getModel()).setRoot(root);

		setCellRenderer(new CmpTreeRenderer());
		addMouseListener(new CmpTreeNodeMouseAdapter(this));
		getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		ttm = ToolTipManager.sharedInstance();
	}

	@Override
	public JToolTip createToolTip() {
		if (showTooltip) {
			JToolTip ttp = super.createToolTip();
			ttp.setBackground(new Color(255, 255, 200));
			ttp.setForeground(Color.BLACK);
			return ttp;
		}
		return null;
	}

	public CmpTreeNode getRoot() {
		return root;
	}

	public void setRoot(CmpTreeNode root) {
		this.root = root;
		((DefaultTreeModel) this.getModel()).setRoot(root);
	}

	public ToolTipManager getTtm() {
		return ttm;
	}

	public void setTtm(ToolTipManager ttm) {
		this.ttm = ttm;
	}

	public boolean isShowTooltip() {
		return showTooltip;
	}

	public void setShowTooltip(boolean showTooltip) {
		this.showTooltip = showTooltip;
	}

}
