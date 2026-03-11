package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.SetPbomAttributeDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

/**
 * 编辑PBOM属性
 *  <br>Created on 2012-10-29
 * @author chenyunlong
 *
 */
public class CmEditPBOMPropertyMenuItem extends CmMenuItem{
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;
	private String type;
	String title = null;
	public CmEditPBOMPropertyMenuItem(CmTree tree, CmTreeNode currNode,Window owner,String type) {
		 this.tree = tree;
		 this.type = type;
	     this.currNode = currNode;
	     this.owner = owner;
	     if("1".equals(type)){
	    	 this.title="定义零组件生产类型";
	     }else if("2".equals(type)){
	    	 this.title = "定义零组件工艺路线 ";
	     }else if("4".equals(type)){
	    	 this.title = "修改零组件属性 ";
	     }
	     setText(title);
	     setIconStr("mbom_edit.png");
	     setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode node) {
		CmTreeNode rootPart = (CmTreeNode)tree.getRoot().children().nextElement();
		CmTreeNode selectedPart = tree.getSelectedNode();
		TreePath[] paths =  CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if(paths.length>1){
			return false;
		} else if (null==node.getParent()
				|| (null==node.getParent()&& node.children().hasMoreElements() && CmCommonStringUtil.isHasFilingOfObj((CmTreeNode)node.children().nextElement()))) {
			return false;
		} else if (rootPart.getPart().getContainerId() != selectedPart.getPart().getContainerId()) {//借用件不能修改
			return false;
		}
		else {
			return !CmCommonStringUtil.isHasFilingOfObj(node);
		}
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {

		TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if(paths.length==1){
			 CmTreeNode node = (CmTreeNode) paths[0].getLastPathComponent();
//			 CmCommonPackageAction common = new CmCommonPackageAction();
//			 StringBuffer errorBuf = common.checkThePackageNode(this.tree.getRoot(), node,"编辑PBOM属性");
//			 if(errorBuf.toString().length()>0){
//				 JOptionPane.showMessageDialog(tree.getRootPane(),errorBuf);
//			 }else{
//				 SetPbomAttributeDialog dialog=SetPbomAttributeDialog.getInstance(node,tree);
//				 dialog.showDialog(node,tree);
				 if("1".equals(type)){
					 SetPbomAttributeDialog dialog=SetPbomAttributeDialog.getInstance(node,tree);
					 dialog.showDialog(node,tree);
					 dialog.dialog.setTitle(title);
				 }else if("2".equals(type)){
					 SetPbomAttributeDialog dialog=SetPbomAttributeDialog.getLineInstance(node, tree);
					 dialog.showDialog(node,tree);
					 dialog.dialog.setTitle(title);
				 }else if("4".equals(type)){
					 SetPbomAttributeDialog dialog=SetPbomAttributeDialog.getChangeIBAUtilityInstance(node, tree);
					 dialog.showDialog(node,tree);
					 dialog.dialog.setTitle(title);
				 }
//			 }
		}

		tree.updateUI();
	}
}
