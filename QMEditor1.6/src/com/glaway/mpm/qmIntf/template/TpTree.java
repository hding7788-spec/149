package com.glaway.mpm.qmIntf.template;

import java.awt.Color;

import javax.swing.JToolTip;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeModel;

import com.glaway.mpm.qmIntf.common.model.CommonTree;

public class TpTree extends CommonTree {
	private static final long serialVersionUID = 1L;
	private TpTreeNode root = null;
	private boolean showTooltip = false;
	private ToolTipManager ttm;

	public TpTree(TpTreeNode dictn) {
		super();
		if (dictn != null)
			root = dictn;
		((DefaultTreeModel) getModel()).setRoot(root);

		setCellRenderer(new TpTreeRenderer());
		// addMouseListener(new CsTreeMouseAdapter(this));
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

	public TpTreeNode getRoot() {
		return root;
	}

	public void setRoot(TpTreeNode root) {
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
