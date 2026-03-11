package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.AddAssistModelDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
/**
 * 添加工艺辅件
 * @author chenyunlong
 *
 */
public class CmAddAssistModelNodeMenuItem extends CmMenuItem{
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;
	
	public CmAddAssistModelNodeMenuItem(CmTree tree, CmTreeNode currNode,Window owner) {
		 this.tree = tree;
	     this.currNode = currNode;
	     this.owner = owner;
	     setText("添加工艺辅件");
	     setIconStr("assist.gif");
//	     setEnabled(canEdit?displayValidate(this.currNode):canEdit);
	     setEnabled(displayValidate(this.currNode));
	}
	
	private boolean displayValidate(CmTreeNode node) {
		// 工艺辅件没有添加工艺辅件功能
		// PBOM节点不可添加
		TreePath[] paths =  CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if(paths.length>1){
			return false;
		}
		else if (null==node.getParent() || node.getPart().getPartType().equals("assistant")) {
			return false;
		}
		else {
			//已归档的零件不可添加工艺辅件
			return !CmCommonStringUtil.isHasFilingOfObj(node);
		}
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if(paths.length==1){
			CmTreeNode node = (CmTreeNode) paths[0].getLastPathComponent();
			AddAssistModelDialog d = new AddAssistModelDialog(node,tree);
			d.showDialog();
		}
	}
}
