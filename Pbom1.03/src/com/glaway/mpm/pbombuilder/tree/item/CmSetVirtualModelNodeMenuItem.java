package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmCancelNode;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeCancelAction;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

/**
 * 设为虚拟件 <br>
 *
 * 该节点移除，子节点位置上移一级
 * Created on 2012-10-29
 *
 * @author chenyunlong
 *
 */
public class CmSetVirtualModelNodeMenuItem extends CmMenuItem {
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;

	public CmSetVirtualModelNodeMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
		this.tree = tree;
		this.currNode = currNode;
		this.owner = owner;
		setText("设为虚拟件");
		setIconStr("virtual.png");
		setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode node) {
		CmTreeNode rootPart = (CmTreeNode)tree.getRoot().children().nextElement();
		// PBOM节点下的第一层节点
		TreePath[] paths =  CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if(paths.length>1){
			return false;
		}
		else if (null == node.getParent()
				|| null==node.getParent().getParent()
				|| "assistant".equals(node.getPart().getPartType()) ) {
			return false;
		}
		else if(CmCommonStringUtil.isHasFilingOfObj(node)){
			//当前节点已经归档
			return false;
		}
		else if(CmCommonStringUtil.isPackageOfParent(node)){
			//当前父节点或间接父节点是打包结构
			return false;
		}
		else if (rootPart.getPart().getContainerId() != node.getPart().getContainerId()) {//借用件
			return false;
		}
//		else if(!"middle".equals(node.getPart().getPartType())){
//			return false;
//		}
		else {
			CmCommonPackageAction common = new CmCommonPackageAction();
			if (common.checkThePackageNode(tree.getRoot(), this.currNode, "设置虚拟件")
					.toString().length() > 0) {
				return false;
			}
			return true;
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	protected void actionPerformed(ActionEvent evt) {
		TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		List<CmCancelNode> list = new ArrayList<CmCancelNode>();
		for (TreePath path : paths) {
			CmTreeNode node = (CmTreeNode) path.getLastPathComponent();
			List<Map<String,CmTreeNode>> comlist = new ArrayList<Map<String,CmTreeNode>>();
			CmCommonNodeUtil.getCommonNodeFromTree(node, tree.getRoot(), comlist);
			for(Map<String,CmTreeNode> map:comlist){
				list.add(CmCommonStringUtil.createNewCancelNode(map.get("obj"),null,true,"virtual"));//节点的回退
				CmCommonStringUtil.updateOperType(map.get("obj"), "delete");//更新节点的操作状态
				CmTreeNode parent = map.get("parent");
				Enumeration children = map.get("obj").children();
				map.get("obj").removeFromParent();
				addChildrenNode(children, parent);
			}
		}
		EbomTreeCancelAction.addPbomTreeChange(null,null, "virtual",list);
		CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
		tree.updateUI();
	}

	@SuppressWarnings("unchecked")
	public List<CmTreeNode> addChildrenNode(Enumeration children, CmTreeNode parent) {
		List<CmTreeNode> cm = new ArrayList<CmTreeNode>();
		while (children.hasMoreElements()) {
			CmTreeNode childNode = (CmTreeNode) children.nextElement();
			cm.add(childNode);
			CmCommonStringUtil.checkNodeIsEdit(childNode);//PBOM节点位置改变，需要更新其状态
		}
		for (CmTreeNode child : cm) {
			parent.add(child);
		}
		return cm;
	}

}
