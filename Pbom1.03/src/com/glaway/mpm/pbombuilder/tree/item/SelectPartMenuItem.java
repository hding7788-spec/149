package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;

import wt.part.WTPart;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.SelectPartDialog;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

public class SelectPartMenuItem extends CmMenuItem{

	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode node;
	private Window owner;

	public SelectPartMenuItem(CmTree tree,CmTreeNode node,Window owner){
		this.setText("选取零部件");
		this.setIconStr("view2d.png");
		this.owner = owner;
		this.tree = tree;
		this.node = node;
		setEnabled(displayValidate(this.node));
	}
	private boolean displayValidate(CmTreeNode node) {
		CmTreeNode rootPart = (CmTreeNode)tree.getRoot().children().nextElement();
		String mtype = node.getPart().getMtype();
		if("PBOM".equals(node.toString())){
			return false;
		} else if ("标准件".equals(mtype)||"元器件".equals(mtype)||"外购件".equals(mtype)) {
			return false;
		} else if ("mp".equals(node.getPart().getPartType())) {
			return false;
		} else if (rootPart.getPart().getContainerId() != node.getPart().getContainerId()) {//借用件
			return false;
		} else if (CmCommonStringUtil.isHasFilingOfObj(node)) {
			return false;
		}
		return true;
	}
	protected void actionPerformed(ActionEvent evt) {
//		  if(node.isLeaf()){
//			  JOptionPane.showMessageDialog(owner, "查询创建元器件/紧固件节点不能选择叶子节点");
//			 return;
//		  }
	      CmLightPart lightPart = node.getPart();
	      WTPart part = CmBizObjUtil.getWTPartFromLightPart(lightPart);
	      String title = "为【"+part.getName()+"】"+"选取零部件";
	      new SelectPartDialog(tree,owner,node,title);

	}
}
