package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import com.glaway.mpm.pbombuilder.bom.CmCIMerger;
import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.data.CmPartWithOcc;
import com.glaway.mpm.pbombuilder.tree.CmScrollPaneTree;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

public class SychronizePartCountMenuItem extends CmMenuItem {

	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;

	public SychronizePartCountMenuItem(CmTree tree, CmTreeNode currNode, Window owner){
		this.tree = tree;
		this.currNode = currNode;
		this.owner = owner;
		setText("同步部件设计使用数量");
		setIconStr("edit.gif");
		// setEnabled(canEdit?displayValidate(this.currNode):canEdit);
		setEnabled(displayValidate(this.currNode));
	}


	private boolean displayValidate(CmTreeNode node) {
		if(CmCommonStringUtil.isHasFilingOfObj(node)){
			return false;
		}
		//顶层节点不允许，不是打包节点不允许
		else if(node.getParent() == node.getRoot()) {
			return false;
		}else if(node.isIspackage()){
			return true;
		}

		return false;
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		// TODO Auto-generated method stub

		String nodename=CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree().getSelectedNode().toString();
		String usecount=null;
		int start=nodename.indexOf("×");
		int end=nodename.lastIndexOf("(");
		if(start>-1 && end>-1 && end>start ){
			usecount=nodename.substring(start+1, end);
		}

		int ecount=Integer.valueOf(usecount);
		int pcount=currNode.getListNode().size()+1;
		for(CmTreeNode node:currNode.getListNode()){
			System.out.println(node.getOccId());
			System.out.println("----");
			System.out.println(node.getOccpath());
		}
		if(ecount>pcount){
			for(int i=0;i<ecount-pcount;i++){

			}
		}else if(ecount<pcount){
			for(int i=pcount;i>ecount;i--){
				 currNode.getListNode().remove(i);
				}
		}else{
			 currNode.getListNode().remove(currNode.getListNode().size()-1);

//			CmTreeNode newnode=currNode.getListNode().get(currNode.getListNode().size()-1);
//			newnode.getOccpath();
		}

	    tree.updateUI();
	}


}
