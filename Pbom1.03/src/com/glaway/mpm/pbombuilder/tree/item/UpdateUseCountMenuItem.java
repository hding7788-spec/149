package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.UpdateUseCountDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

/**
 * 添加工艺中间件
 *
 * @author chenyunlong
 *
 */
public class UpdateUseCountMenuItem extends CmMenuItem {
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;

	public UpdateUseCountMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
		this.tree = tree;
		this.currNode = currNode;
		this.owner = owner;
		setText("修改设计使用数量");
		setIconStr("edit.gif");
		// setEnabled(canEdit?displayValidate(this.currNode):canEdit);
		setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode node) {
		//工艺中间件、工艺组合件及毛坯件可以修改数量
		if("zuhe".equals(node.getPart().getPartType())
				||"middle".equals(node.getPart().getPartType())
				||"mp".equals(node.getPart().getPartType())
				||"middle2".equals(node.getPart().getPartType())
				||"NS".equals(node.getPart().getPartType())){
			if(!CmCommonStringUtil.isHasFilingOfObj(node)){
				return true;
			}
		}
		return false;
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if (paths.length == 1) {
			CmTreeNode node = (CmTreeNode) paths[0].getLastPathComponent();
			UpdateUseCountDialog d = new UpdateUseCountDialog(node, tree);
			d.showDialog();
		}
	}

}
