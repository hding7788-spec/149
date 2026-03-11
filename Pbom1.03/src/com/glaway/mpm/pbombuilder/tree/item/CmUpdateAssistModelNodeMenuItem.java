package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.UpdateAssistModelDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;

public class CmUpdateAssistModelNodeMenuItem  extends CmMenuItem{
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;

	public CmUpdateAssistModelNodeMenuItem(CmTree tree, CmTreeNode currNode,Window owner) {
		 this.tree = tree;
	     this.currNode = currNode;
	     this.owner = owner;
	     setText("修改工艺辅件数量");
	     setIconStr("assist.gif");
	     //setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode node) {
		// 修改工艺辅件的数量
		TreePath[] paths =  CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if(paths.length>1){
			return false;
		}else if ("assistant".equals(node.getPart().getPartType()) && "拟制".equals(node.getPart().getLifecycle())) {
			return true;
		}
		else {
			return false;
		}

	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if(paths.length==1){
			CmTreeNode node = (CmTreeNode) paths[0].getLastPathComponent();
			UpdateAssistModelDialog d = new UpdateAssistModelDialog(node,tree);
			d.showDialog();
		}
	}
}
