package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmTaskInfo;
import com.glaway.mpm.pbombuilder.util.PviewTask;

/**
 * 
 * Created on 2012-10-23
 * 
 * @author chenyunlong
 */
public class CmAddChildModelNodeItem extends CmMenuItem {
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;

	public CmAddChildModelNodeItem(CmTree tree, CmTreeNode currNode, Window owner) {
		this.tree = tree;
		this.currNode = currNode;
		this.owner = owner;
		setText("剪切");
		setIconStr("cut.gif");
		// setEnabled(canEdit?displayValidate(this.currNode):canEdit);
		setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode node) {
		if ("PBOM".equals(node.toString())) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		TreePath[] paths = tree.getSelectionPaths();
		List<CmTreeNode> nodeList = new ArrayList<CmTreeNode>();
		for (int i = 0; i < paths.length; i++) {
			CmTreeNode node = (CmTreeNode) paths[i].getLastPathComponent();
			if (isChildNode(node, paths)) {

				nodeList.add(node);
				node.removeFromParent();
			}
		}
		CmTaskInfo taskInfo = CmTaskInfo.newCmTaskInfo("CmEBomTreePanel.setSelectedCopyNodeList", evt.getSource(),
				nodeList);
		try {
			PviewTask.postTask(taskInfo, null);
		} catch (CmTaskException e1) {
		}
		tree.updateUI();
	}

	public boolean isChildNode(CmTreeNode node, TreePath[] paths) {
		boolean flag = true;
		CmTreeNode parent = (CmTreeNode) node.getParent();
		for (int i = 0; i < paths.length; i++) {
			CmTreeNode child = (CmTreeNode) paths[i].getLastPathComponent();
			if (parent.equals(child)) {
				flag = false;
			}
		}
		return flag;
	}
}
