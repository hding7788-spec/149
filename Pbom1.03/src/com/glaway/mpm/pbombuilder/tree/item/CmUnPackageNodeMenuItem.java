package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeCancelAction;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

public class CmUnPackageNodeMenuItem extends CmMenuItem {
	private static final long serialVersionUID = -8396429397442896329L;
	private CmTree tree;
	private CmTreeNode currNode;

	public CmUnPackageNodeMenuItem(CmTree tree, CmTreeNode node) {
		this.tree = tree;
		this.currNode = node;
		setText("拆包");
		setIconStr("unpackage.png");
		setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode node) {
		TreePath[] paths =  CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if (paths.length > 1) {
			return false;
		}
		else if(CmCommonStringUtil.isPackage(node)){
			return true;
		}
		else{
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean isHasUnPackageNode(CmTreeNode node) {
		boolean flag = false;
		Enumeration children = node.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if (null != child.getListNode() && child.getListNode().size() > 0) {
				flag = true;
				break;
			}
			isHasUnPackageNode(child);
		}
		return flag;
	}

	/**
	 * @param evt
	 */
	@Override
	protected void actionPerformed(ActionEvent evt) {
//		EbomTreeCancelAction.addPbomTreePackage(this.currNode,"unpackages",false);
		CmTree cmtree = null;
		if("PBOM".equals(tree.getRoot().toString())){
			cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
		}
		else{
			cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();
		}
		CmCommonPackageAction action = new CmCommonPackageAction();
		action.unpackageOneNode(tree.getRoot(), currNode);
		action.unpackageOneNode(cmtree.getRoot(), currNode);
		CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
		CmCommonStringUtil.sortTheTreeNode(cmtree.getRoot());
		tree.updateUI();
		cmtree.updateUI();
	}
	
	
}
