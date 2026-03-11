package com.glaway.mpm.parameter.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.glaway.mpm.parameter.model.tree.XWParameterTypeTreeObject;
import com.glaway.mpm.parameter.model.tree.XWTechnicsTypeTreeObject;
import com.glaway.mpm.parameter.model.tree.XWTreeNode;
import com.glaway.mpm.parameter.model.tree.XWTreeObject;
import com.glaway.mpm.parameter.ui.LeftTreePanel;

public class QualityMouseListener extends MouseAdapter {

	private JPanel panel;

	public QualityMouseListener(JPanel panel) {
		this.panel = panel;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			if (panel instanceof LeftTreePanel) {
				LeftTreePanel treePanel = (LeftTreePanel)panel;
				JTree tree = treePanel.getQualityTree();
				TreePath treePath = tree.getPathForLocation(e.getX(), e.getY());
	            if(treePath != null) {
	            	tree.setSelectionPath(treePath);
	            	XWTreeNode node = (XWTreeNode)treePath.getLastPathComponent();
	            	XWTreeObject treeObject = node.getTreeObject();
	            	if (treeObject instanceof XWParameterTypeTreeObject) {
	            		treePanel.getParamPopupMenu().setStatus();
		            	treePanel.getParamPopupMenu().show(tree, e.getX(), e.getY());
	            	} else if (treeObject instanceof XWTechnicsTypeTreeObject) {
	            		treePanel.getRecordPopupMenu().setStatus();
		            	treePanel.getRecordPopupMenu().show(tree, e.getX(), e.getY());
	            	}
	            }
			}
		}
	}

}
