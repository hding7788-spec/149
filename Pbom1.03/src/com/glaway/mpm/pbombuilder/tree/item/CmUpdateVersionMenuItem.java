package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeUpdateAction;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

public class CmUpdateVersionMenuItem  extends CmMenuItem{
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;

	public CmUpdateVersionMenuItem(CmTree tree, CmTreeNode currNode) {
		 this.tree = tree;
	     this.currNode = currNode;
	     setText("修订新版");
	     setIconStr("revise.png");
	     setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode node) {
		//TreePath[] paths =  CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		String mtype = node.getPart().getMtype();
//		if (null==paths || paths.length > 1) {
//			return false;
//		}
		if(!CmCommonStringUtil.isHasFilingOfObj(node)){
			return false;
		}
		if(!"自制件".equals(mtype) && !"外配套件".equals(mtype) && !"带料委外件".equals(mtype) && !"不带料委外件".equals(mtype)&& !"配套产品".equals(mtype)) {
			return false;
		}
		//只有顶层节点才能修订
//		if(node.getParent() == node.getRoot()) {
//			return true;
//		}
		return true;
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		//updateVersion(tree.getRoot(),currNode);
		PbomTreeUpdateAction.updateVersion(currNode);
		tree.updateUI();
	}

	@SuppressWarnings("unchecked")
	public void updateVersion(CmTreeNode root,CmTreeNode updateNode){
		Enumeration children = root.children();
		String mtype = "";
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(CmCommonNodeUtil.checkNodeIsCommon(child, updateNode)){
				mtype = child.getPart().getMtype();
				if("自制件".equals(mtype)||"外配套件".equals(mtype)
						||"带料委外件".equals(mtype)||"不带料委外件".equals(mtype)) {
					PbomTreeUpdateAction.updateVersion(child);
				}

				if(CmCommonStringUtil.isPackage(child)){
					for(CmTreeNode brother:child.getListNode()){
						mtype = brother.getPart().getMtype();
						if("自制件".equals(mtype)||"外配套件".equals(mtype)
								||"带料委外件".equals(mtype)||"不带料委外件".equals(mtype)) {
							PbomTreeUpdateAction.updateVersion(brother);
						}

						updateVersion(brother,updateNode);
					}
				}

				//循环修订子节点
				Enumeration<CmTreeNode> childs = child.children();
				while(childs.hasMoreElements()) {
					CmTreeNode childNode = childs.nextElement();
					updateVersion(root,childNode);
				}

			}else{
				updateVersion(child,updateNode);
				if(CmCommonStringUtil.isPackage(child)){
					for(CmTreeNode brother:child.getListNode()){
						updateVersion(brother,updateNode);
					}
				}
			}
		}
	}

}
