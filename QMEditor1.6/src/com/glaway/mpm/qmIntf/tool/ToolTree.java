package com.glaway.mpm.qmIntf.tool;

import javax.swing.tree.DefaultTreeModel;

import com.glaway.mpm.qmIntf.common.model.CommonTree;

public class ToolTree extends CommonTree {
	private static final long serialVersionUID = 1L;
	private ToolTreeNode root = null;
	private ToolTreeMouseAdapter mouseAdapter = null;

	public ToolTree() {
		this(null);
	}

	public ToolTree(ToolTreeNode dictn) {
		super();
		if (dictn != null)
			root = dictn;
		((DefaultTreeModel) getModel()).setRoot(root);
		setCellRenderer(new ToolTreeRenderer());
		mouseAdapter = new ToolTreeMouseAdapter(this);
		addMouseListener(mouseAdapter);
	}

	public ToolTreeNode getRoot() {
		return root;
	}

	public void setRoot(ToolTreeNode root) {
		this.root = root;
		((DefaultTreeModel) this.getModel()).setRoot(root);
	}

	public ToolTreeMouseAdapter getMouseAdapter() {
		return mouseAdapter;
	}

}
