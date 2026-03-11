package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;

import wt.part.WTPart;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.AddPartDialog;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

public class AddPartItem extends CmMenuItem{

	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode node;
	private Window owner;

	public AddPartItem(CmTree tree,CmTreeNode node,Window owner){
		this.setText("创建零部件");
		this.setIconStr("view2d.png");
		this.owner = owner;
		this.tree = tree;
		this.node = node;
		setEnabled(displayValidate(this.node));
	}
	private boolean displayValidate(CmTreeNode node) {
		if(node.isLeaf()||"PBOM".equals(node.toString())){
			//父节点是打包结构时
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
	      String title = "为【"+part.getName()+"】"+"创建子零部件";
	      AddPartDialog dialog = new AddPartDialog(tree,owner,node,title);
	      dialog.showDialog();

	}
}
