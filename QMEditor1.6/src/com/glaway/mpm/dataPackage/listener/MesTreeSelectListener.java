package com.glaway.mpm.dataPackage.listener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.model.tree.XWTreeNode;

public class MesTreeSelectListener implements TreeSelectionListener {

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreePath treePath = e.getPath();
		if (treePath != null) {
			System.out.println("-----------treeSelected-----" + e.getPath());
		}
	}

}
