package com.glaway.mpm.qmIntf.common.model;

import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

public class CommonTree extends JTree {

	public CommonTree() {
		super();
		// putClientProperty("JTree.lineStyle", "None");
		setRootVisible(false);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}

}
