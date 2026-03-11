package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
/**
 * 工艺更改标识
 * 只是针对单个节点进行操作，组件不影响到其下的子节点
 * 升版的未归档    PBOM中的零件由于升版，从”已归档“变为”未归档“ 
 * 不满足的零件：外购件  标准件   成果件
 * 已归档的零件通过修订新版升大版本自动新增工艺更改标识
 * 升大版本的零件可以新增更衣更改标识
 * 
 * @author chenyunlong
 *
 */
public class CmProcessChangeMenuItem  extends CmMenuItem {
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;
	public CmProcessChangeMenuItem(CmTree tree, CmTreeNode currNode, boolean canEdit) {
		this.tree = tree;
		this.currNode = currNode;
		if(currNode.getPart().isChange()){
			setText("取消工艺更改标识");
		}else{
			setText("新增工艺更改标识");
		}
		setIconStr("process_plan.gif");
		setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode currNode) {
		boolean isChange = currNode.getPart().isChange();
		TreePath[] paths =  CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if(paths.length==0){
			return false;
		}
		for(TreePath path:paths){
			CmTreeNode node = (CmTreeNode) path.getLastPathComponent();
			if(null == node.getParent()){
				return false;
			}else if(CmCommonStringUtil.isHasFilingOfObj(node)){
				return false;
			}else if("assistant".equals(node.getPart().getPartType())
					|| "PurchasedPart".equals(node.getPart().getPartType())
					|| "StandardPart".equals(node.getPart().getPartType())
					|| "ALKPart".equals(node.getPart().getPartType())
					|| "SHPart".equals(node.getPart().getPartType())){
				return false;
			}else if(isChange != node.getPart().isChange()){
				return false;
			}else if(isFirstVersion(node)){
				return false;
			}else if(!node.getPart().isChange() && node.getPart().getVersionIndex()!=2){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param evt
	 */
	@Override
	protected void actionPerformed(ActionEvent evt) {
		TreePath[] paths =  CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		List<CmTreeNode> list = new ArrayList<CmTreeNode>();
		for(TreePath path:paths){
			CmTreeNode node = (CmTreeNode) path.getLastPathComponent();
			if(!CmCommonNodeUtil.checkHasSameOidInList(node, list)){
				list.add(node);
			}
		}
		processChange(tree.getRoot(),list);
		tree.updateUI();
	}
	
	@SuppressWarnings("unchecked")
	public void processChange(CmTreeNode root,List<CmTreeNode> list){
		Enumeration children = root.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(CmCommonNodeUtil.checkHasSameOidInList(child, list)){
				child.getPart().setChange(!child.getPart().isChange());
			}
			processChange(child,list);
			if(CmCommonStringUtil.isPackage(child)){
				for(CmTreeNode brother:child.getListNode()){
					if(CmCommonNodeUtil.checkHasSameOidInList(brother, list)){
						brother.getPart().setChange(!brother.getPart().isChange());
					}
					processChange(brother,list);
				}
			}
		}
	}
	/**
	 * A 版本的零件不能新增/修改工艺更改标识
	 * @author chenyunlong
	 * @date  2013-6-6
	 * @param node
	 * @return
	 *
	 */
	public static boolean isFirstVersion(CmTreeNode node){
		String[] version = node.getPart().getVersion().split("\\.");
		if("A".equals(version[version.length-2])){
			return true;
		}else{
			return false;
		}
	}
	
}
