package com.glaway.mpm.pbombuilder.action;

import java.util.Enumeration;

import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

public class CmCommonSearchAction {
//	private CmTree tree;
	
	public static void bomTreeSearchAction(String searchValue,CmTree tree,String bomName) {
		if (!CmCommonStringUtil.isEmpty(searchValue)) {
			getAllChildNodesFromParent(tree.getRoot(),null, searchValue.trim(),bomName);
			updateSearchFlag(tree.getRoot(),searchValue.trim());
		}
		else{
			tree.getRoot().setSelected(false);
		}
		tree.updateUI();
	}
	
	public static  void getAllChildNodesFromParent(CmTreeNode cmnode,CmTreeNode parent, String searchValue,String bomName) {
		if(bomName.equals(cmnode.toString()) && cmnode.toString().indexOf(searchValue) > -1 ? true : false){
			cmnode.setSelected(true);
			cmnode.getPart().setSearch(true);
			return;
		}
		else{
			if(!bomName.equals(cmnode.toString()) && cmnode.isSelected()){
				cmnode.setSelected(checkParentAttr(cmnode,parent,searchValue,false));
			}
			else{
				checkAttr(cmnode, searchValue);
			}
			
			Enumeration children = cmnode.children();
			while (children.hasMoreElements()) {
				CmTreeNode child = (CmTreeNode) children.nextElement();
				if (CmCommonStringUtil.isPackage(child)) {
					for (CmTreeNode brother : child.getListNode()) {
						getAllChildNodesFromParent(brother,(CmTreeNode)child.getParent(), searchValue,bomName);
					}
				}
				getAllChildNodesFromParent(child,(CmTreeNode)child.getParent(), searchValue,bomName);
			}
		}
	}
	/**
	 * 对搜索的内容与零件编号和零件名字进行匹配
	 * @author chenyunlong
	 * @date  2013-3-15
	 * @param node
	 * @param searchValue
	 * @return
	 *
	 */
	public static  boolean checkObjAttr(CmTreeNode node, String searchValue){
		boolean flag1 = ignoreIndexOf(node,searchValue,true) > -1 ? true : false;
		boolean flag2 = ignoreIndexOf(node,searchValue,false) > -1 ? true : false;
		return (flag1 || flag2);
	}

	private static  boolean checkAttr(CmTreeNode node, String searchValue) {
		boolean flag = checkObjAttr(node,searchValue);
		node.setSelected(flag);
		return flag;
	}

	public  static  boolean checkParentAttr(CmTreeNode node,CmTreeNode parentNode, String searchValue, boolean flag) {
		CmTreeNode parent =node.getParent()==null?parentNode:(CmTreeNode)node.getParent();
		if (null != parent && checkAttr(parent, searchValue)) {
			flag=true;
		} else if(null != parent){
			flag=checkParentAttr(parent,(CmTreeNode)parent.getParent(), searchValue, flag);
		}
		return flag;
	}
	
	public static void updateSearchFlag(CmTreeNode cmnode, String searchValue){
		if(cmnode.isSelected() && checkObjAttr(cmnode,searchValue)){
			cmnode.getPart().setSearch(true);
		}
		else{
			cmnode.getPart().setSearch(false);
		}
		Enumeration children = cmnode.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if (CmCommonStringUtil.isPackage(child)) {
				for (CmTreeNode brother : child.getListNode()) {
					updateSearchFlag(brother,searchValue);
				}
			}
			updateSearchFlag(child,searchValue);
		}
	}
	/**
	 * 字符串模糊匹配
	 * @author chenyunlong
	 * @date  2013-4-15
	 * @param subject
	 * @param searchValue
	 * @return
	 *
	 */
	public static int ignoreIndexOf(CmTreeNode node,String searchValue,boolean flag){
		if(null==node || null==node.getPart() || null == node.getPart().getPartNumber()){
			return -1;
		}
		else{
			String subject = "";
			if(flag){
				subject = node.getPart().getPartNumber();
			}else{
				subject = node.getPart().getPartName();
			}
			for (int i = 0; i < subject.length(); i++) {  
				if(subject.regionMatches(true, i, searchValue, 0, searchValue.length())){  
					return i;  
				}  
			}  
		}
        return -1;  
    }  
}
