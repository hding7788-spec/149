package com.glaway.mpm.qmIntf.commonString;

import javax.swing.tree.DefaultTreeModel;

import com.glaway.mpm.qmIntf.common.model.CommonTree;

public class CsTree extends CommonTree {
	private static final long serialVersionUID = 1L;
	private CsTreeNode root = null;
	private CsTreeMouseAdapter mouseAdapter;

	/**
	 * Description:
	 * 
	 * flag : MouseAdapter
	 * 
	 */
	public CsTree(CsTreeNode dictn, boolean flag) {
		super();
		if (dictn != null)
			root = dictn;
		((DefaultTreeModel) getModel()).setRoot(root);

		if (flag) {
			mouseAdapter = new CsTreeMouseAdapter(this);
			addMouseListener(mouseAdapter);
		}
		setCellRenderer(new CsTreeRenderer());
	}

	public CsTreeNode getRoot() {
		return root;
	}

	public void setRoot(CsTreeNode root) {
		this.root = root;
		((DefaultTreeModel) this.getModel()).setRoot(root);
	}

	public CsTreeMouseAdapter getMouseAdapter() {
		return mouseAdapter;
	}

}
