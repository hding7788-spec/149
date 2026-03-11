package com.glaway.mpm.qmIntf.template;

import javax.swing.tree.DefaultTreeModel;

import com.glaway.mpm.qmIntf.common.model.CommonTree;

public class StepTree extends CommonTree {
	private static final long serialVersionUID = 1L;
	private TpTreeNode root = null;

	public StepTree(TpTreeNode dictn) {
		super();
		if (dictn != null)
			root = dictn;
		((DefaultTreeModel) getModel()).setRoot(root);

		setCellRenderer(new StTreeRenderer());
	}

	public TpTreeNode getRoot() {
		return root;
	}

	public void setRoot(TpTreeNode root) {
		this.root = root;
		((DefaultTreeModel) this.getModel()).setRoot(root);
	}

}
