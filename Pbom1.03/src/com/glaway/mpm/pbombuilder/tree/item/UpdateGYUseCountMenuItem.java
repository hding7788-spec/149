package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.UpdateGYUseCountDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

/**
 * 添加工艺中间件
 *
 * @author chenyunlong
 *
 */
public class UpdateGYUseCountMenuItem extends CmMenuItem {
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;

	public UpdateGYUseCountMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
		this.tree = tree;
		this.currNode = currNode;
		this.owner = owner;
		setText("修改工艺使用数量");
		setIconStr("edit.gif");
		// setEnabled(canEdit?displayValidate(this.currNode):canEdit);
		setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode node) {
		String parentNodeName = node.getParent().toString();
		if(!CmCommonStringUtil.isHasFilingOfObj(node) && (node != node.getRoot()) && parentNodeName != "PBOM"){
			return true;
		}

		return false;
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if (paths.length == 1) {
			CmTreeNode node = (CmTreeNode) paths[0].getLastPathComponent();
			UpdateGYUseCountDialog d = new UpdateGYUseCountDialog(node, tree);
			d.showDialog();
		}
	}

}
