package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.SetPBOMPhaseCodeDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

/**
 * PBOM转阶段
 *
 * @author LongXiuChuan 2014-2-17
 *
 */
public class SetPBOMPhaseCodeMenuItem extends CmMenuItem {
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;

	public SetPBOMPhaseCodeMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
		this.tree = tree;
		this.currNode = currNode;
		this.owner = owner;
		setText("PBOM转阶段");
		setIconStr("revise.png");
		// setEnabled(canEdit?displayValidate(this.currNode):canEdit);
		setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode node) {
		if(!CmCommonStringUtil.isHasFilingOfObj(node)){
			return false;
		}
		//只有顶层节点才能设置PBOM转阶段
		else if(node.getParent() == node.getRoot()) {
			return true;
		}

		return false;
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if (paths.length == 1) {
			CmTreeNode node = (CmTreeNode) paths[0].getLastPathComponent();
			SetPBOMPhaseCodeDialog d = new SetPBOMPhaseCodeDialog(node, tree);
			d.showDialog();
		}
	}

}
