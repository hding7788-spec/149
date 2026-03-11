package com.glaway.mpm.parameter.listener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.model.tree.XWTreeNode;

public class QualityTreeSelectListener implements TreeSelectionListener {

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreePath treePath = e.getPath();
		if (treePath != null) {
			XWTreeNode treeNode = (XWTreeNode) treePath.getLastPathComponent();
			MPMParameterProcessor.showInfomation(treeNode);
		}
	}

}
