package com.glaway.mpm.qmIntf.frock;

import javax.swing.tree.DefaultTreeModel;

import com.glaway.mpm.qmIntf.common.model.CommonTree;

public class FkTree extends CommonTree {
	private static final long serialVersionUID = 1L;
	private FkTreeNode root = null;
	private FkTreeMouseAdapter mouseAdapter;

	public FkTree() {
		this(null);
	}

	public FkTree(FkTreeNode dictn) {
		super();
		if (dictn != null)
			root = dictn;
		((DefaultTreeModel) getModel()).setRoot(root);
		setCellRenderer(new FkTreeRenderer());
		mouseAdapter = new FkTreeMouseAdapter(this);
		addMouseListener(mouseAdapter);
	}

	public FkTreeNode getRoot() {
		return root;
	}

	public void setRoot(FkTreeNode root) {
		this.root = root;
		((DefaultTreeModel) this.getModel()).setRoot(root);
	}

	public FkTreeMouseAdapter getMouseAdapter() {
		return mouseAdapter;
	}

}
