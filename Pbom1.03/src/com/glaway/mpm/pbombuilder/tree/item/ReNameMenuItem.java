package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.ReNameDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;

/**
 * 添加工艺中间件
 *
 * @author chenyunlong
 *
 */
public class ReNameMenuItem extends CmMenuItem {
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;

	public ReNameMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
		this.tree = tree;
		this.currNode = currNode;
		this.owner = owner;
		setText("重命名");
		setIconStr("edit.gif");
		// setEnabled(canEdit?displayValidate(this.currNode):canEdit);
		setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode node) {
		return true;
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if (paths.length == 1) {
			CmTreeNode node = (CmTreeNode) paths[0].getLastPathComponent();
			ReNameDialog d = new ReNameDialog(node, tree);
			d.showDialog();
		}
	}

}
