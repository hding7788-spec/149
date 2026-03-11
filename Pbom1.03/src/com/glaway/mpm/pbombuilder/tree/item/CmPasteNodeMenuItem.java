package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.action.CmCommonPasteAction;
import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

/**
 * 粘贴
 *
 * <br>
 * Created on 2012-10-28
 *
 * @author chenyunlong
 */
public class CmPasteNodeMenuItem extends CmMenuItem {
	private static final CmLogger log = CmLogger.getLogger(CmPasteNodeMenuItem.class.getName());
	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;
	private StringBuffer errorBuf;
	private CmCommonPasteAction cmCommonPasteAction;
	private static final long serialVersionUID = -1676220621674073524L;
	private List<CmTreeNode> selectedCopyNodeList = new ArrayList<CmTreeNode>();
	private List<CmTreeNode> nodeList = new ArrayList<CmTreeNode>();

	public CmPasteNodeMenuItem(CmTree tree, CmTreeNode currNode, Window owner, boolean canEdit) {
		this.tree = tree;
		this.currNode = currNode;
		this.owner = owner;
		setText("粘贴");
		setIconStr("paste.png");
		setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode node) {
		TreePath[] paths =  CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if (paths.length > 1) {
			//选中多个PBOM节点
			return false;
		}
		else if(null==node.getParent()){
			//PBOM节点
			return false;
		}
		else if ("assistant".equals(node.getPart().getPartType())) {
			//工艺辅件
			return false;
		} else if ("标准件".equals(node.getPart().getMtype())
				||"元器件".equals(node.getPart().getMtype())
				||"外购件".equals(node.getPart().getMtype())) {
			//标准件,元器件,外购件不能粘贴
			return false;
		}
		else if(CmCommonStringUtil.isPackage(node) || CmCommonStringUtil.isPackageOfParent(node)){
			//当前节点或某个父节点是打包结构，不能粘贴
			return false;
		}
		else if(CmCommonStringUtil.isHasFilingOfObj(node)){
			//已归档
			return false;
		}
		else {
			cmCommonPasteAction = new CmCommonPasteAction();
			selectedCopyNodeList = cmCommonPasteAction.getCopyNodeList(selectedCopyNodeList);
			if (selectedCopyNodeList.size() == 0) {
				return false;
			}
			else{
				nodeList =cmCommonPasteAction.getCurrentCopyNodeList(selectedCopyNodeList);
				if(isMoveToTheChildNode(nodeList,this.currNode)){
					//不能向该节点的子节点
					return false;
				}
				else if(isMoveToBrotherNode(nodeList,this.currNode)){
					 //不能向该节点的兄弟节点
					return false;
				}
				else if(!checkIsMoveToParent(nodeList, this.currNode)){
					return true;//false
				}
				else if(CmCommonNodeUtil.checkIsInnerOpertion(currNode, nodeList)
						&& checkNodeHasChildInList(currNode, nodeList)){
					//判断是不是相同结构之间的操作
					return false;
				}
				else if(CmCommonStringUtil.checkisPbomNode(nodeList.get(0)) && !checkHasPasteNodesFromCut(this.currNode,nodeList)){
					//所有剪切的节点的父节点与当前节点相同
					return false;
				}
				else if(!CmCommonNodeUtil.checkNodeHasStructure(this.currNode)
							&& !"PBOM".equals(this.currNode.getParent().toString())){
					return false;
				}else if("PBOM".equals(CmCommonStringUtil.getRootNode(nodeList.get(0)).toString())
						&& CmCommonStringUtil.isEqual(nodeList.get(0).getPart().getParentPartNumber(), this.currNode.getPart().getPartNumber())
						&& this.currNode.getOccpath().indexOf(nodeList.get(0).getOccpath().substring(0, nodeList.get(0).getOccpath().lastIndexOf("+")))==-1){
					//相同组件下，不存在相同零件的不同实例互换
					return false;
				}
				else{
					return true;
				}
			}
		}
	}
	/**
	 * 判断移动的节点是否移动到该节点的子节点下面
	 * @date  2013-1-24
	 * @param parentNode
	 * @param currentNode
	 * @return
	 *
	 */
	public static boolean isMoveToTheChildNode(List<CmTreeNode> list,CmTreeNode currentNode){
		boolean flag=false;
		for(CmTreeNode parentNode:list){
			if(flag){
				break;
			}
			else{
				flag=checkIsTheChildNode(parentNode,currentNode,flag);
			}
		}
		return flag;
	}

	public static boolean checkIsTheChildNode(CmTreeNode parentNode,CmTreeNode currentNode,boolean flag){
		Enumeration children = parentNode.children();
		while(children.hasMoreElements()){
			CmTreeNode child =(CmTreeNode) children.nextElement();
			if(CmCommonStringUtil.checkNodeIsCommon(child, currentNode)){
				flag=true;
				break;
			}
			flag=checkIsTheChildNode(child,currentNode,flag);
		}
		return flag;
	}
	/**
	 * 判断节点是否移动到自己的兄弟节点
	 * @param list
	 * @param currentNode
	 * @return
	 *
	 */
	public static boolean isMoveToBrotherNode(List<CmTreeNode> list,CmTreeNode currentNode){
		boolean flag=false;
		for(CmTreeNode cmnode:list){
			if(CmCommonStringUtil.checkNodeIsCommon(cmnode, currentNode)){
				flag=true;
				break;
			}
		}
		return flag;
	}


	/**
	 * @param evt
	 */
	@Override
	protected void actionPerformed(ActionEvent evt) {
		cmCommonPasteAction.pasteCopyNodeList(selectedCopyNodeList, tree, currNode, errorBuf);
		BomTreeReportAction.updateBomReport();
		CmBizObjUtil biz = new CmBizObjUtil();
		biz.clearData();
	}
	/**
	 * 剪切的节点中是否可以粘贴
	 *
	 *如果有剪切的节点的父节点，与当前要挂载的节点不同，就是true
	 * @author chenyunlong
	 * @date  2013-4-18
	 * @param obj
	 * @param list
	 * @return
	 *
	 */
	public boolean checkHasPasteNodesFromCut(CmTreeNode obj,List<CmTreeNode> list){
		boolean flag = false;
		for(CmTreeNode cut:list){
			if(!CmCommonStringUtil.checkNodeIsSame(obj,(CmTreeNode)cut.getParent())){
				flag = true;
				break;
			}
		}
		return flag;
	}

	public static boolean checkNodeHasChildInList(CmTreeNode oper,List<CmTreeNode> selectedCopyNodeList){
		CmCommonNodeUtil common = new CmCommonNodeUtil();
		List<CmTreeNode> childlist = common.getChildListFromPbomlist(oper);
		boolean flag = false;
		for(CmTreeNode cmnode:selectedCopyNodeList){
			if(!CmCommonNodeUtil.checkHasSameOccpathInList(cmnode, childlist)){
				flag = true;
				break;
			}
		}
		return flag;
	}
	/**
	 * 判断复制的零件是否可以粘贴到目标节点上
	 * @author chenyunlong
	 * @date  2013-8-2
	 * @param list
	 * @param obj
	 * @return
	 *
	 */
	public boolean checkIsMoveToParent(List<CmTreeNode> list ,CmTreeNode obj){
		int i=0;
		CmCommonNodeUtil common = new CmCommonNodeUtil();
		for(CmTreeNode move : list){
			i = common.checkIsMoveToParent(obj, move);
			if(i != 0){
				break;
			}
		}
		return i==0;
	}
}
