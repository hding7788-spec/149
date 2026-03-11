package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

public class StartPbomReleasedNoticeWorkflowMenuItem  extends CmMenuItem{
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;

	public StartPbomReleasedNoticeWorkflowMenuItem(CmTree tree, CmTreeNode currNode,Window owner) {
		 this.tree = tree;
	     this.currNode = currNode;
	     this.owner = owner;
	     setText("提交PBOM发布通知流程");
	     setIconStr("edit.gif");
	     setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode node) {
		if(CmCommonStringUtil.isHasFilingOfObj(node)){
			return true;
		} else if (node == node.getRoot()) {
			return false;
		} else {
			return false;
		}
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		//启动PBOM发布通知流程
		long oid = currNode.getPart().getOid();
		boolean flag = PBOMEditorToWCIntf.startPbomReleasedNoticeWorkflow(oid);
		if(flag) {
			JOptionPane.showMessageDialog(owner, "PBOM发布通知流程已启动,相关型号调度员将会收到发布通知！");
		} else {
			JOptionPane.showMessageDialog(owner, "启动PBOM发布通知流程失败,请联系管理员后再手动启动PBOM发布通知流程！");
		}
	}

}
