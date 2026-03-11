package com.glaway.mpm.pbombuilder.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.util.WTException;

import com.glaway.mpm.pbombuilder.bom.WTPartUtil;
import com.glaway.mpm.pbombuilder.tree.CmCancelNode;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmScrollPaneTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;

public class CmCommonNodeUtil {

	public static boolean checkNodeIsCommon(CmTreeNode common,CmTreeNode cmnode){
		if(null != common
				&& null != common.getPart()
				&& null!= cmnode
				&& null != cmnode.getPart()
				&& CmCommonStringUtil.isEqual(common.getPart().getPartNumber(), cmnode.getPart().getPartNumber())){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 判断list中是否拥有oid相同的零件
	 *
	 * @author chenyunlong
	 * @date 2013-5-10
	 * @param common
	 * @param list
	 * @return
	 *
	 */
	public static boolean checkHasSameOidInList(CmTreeNode common, List<CmTreeNode> list) {
		boolean flag = false;
		for (CmTreeNode cmnode : list) {
			if (checkNodeIsCommon(cmnode,common)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public CmTreeNode getSameOidInList(CmTreeNode common, List<CmTreeNode> list) {
		CmTreeNode same = null;
		for (CmTreeNode cmnode : list) {
			if (checkNodeIsCommon(cmnode,common)) {
				same = cmnode;
				break;
			}
		}
		return same;
	}

	/**
	 * 判list中是否拥有occpath相同的零件
	 *
	 * @author chenyunlong
	 * @date 2013-5-10
	 * @param common
	 * @param list
	 * @return
	 *
	 */
	public static boolean checkHasSameOccpathInList(CmTreeNode common, List<CmTreeNode> list) {
		boolean flag = false;
		for (CmTreeNode cmnode : list) {
			if (isEqualOfOccpath(common, cmnode)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 从list中获取相同occpath的零件
	 * @author chenyunlong
	 * @date  2013-5-23
	 * @param common
	 * @param list
	 * @return
	 *
	 */
	public CmTreeNode getSameOccpathInList(CmTreeNode common, List<CmTreeNode> list){
		CmTreeNode same = null;
		for (CmTreeNode cmnode : list) {
//			if (isEqualOfOccpath(common, cmnode)) {
			if (checkNodeIsCommon(common, cmnode)){
				same = cmnode;
				break;
			}
		}
		return same;
	}

	/**
	 * 判断两个节点是oid相同但是occpath不同的零件
	 *
	 * @author chenyunlong
	 * @date 2013-5-10
	 * @param common
	 * @param list
	 * @return
	 *
	 */
	public static boolean checkHasSameOidAndNotSameOccpath(CmTreeNode cmnode, CmTreeNode common) {
		if (checkNodeIsCommon(cmnode,common)
				&& !isEqualOfOccpath(common, cmnode)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean checkHasSameOccpath(CmTreeNode cmnode, CmTreeNode node) {
		if(isEqualOfOccpath(cmnode, node)){
			if("middle".equals(cmnode.getPart().getPartType())){
				if(CmCommonStringUtil.isEqual(node.getPart().getMiddleIndex(), cmnode.getPart().getMiddleIndex())){
					return true;
				}else{
					return false;
				}
			}else{
				return true;
			}

		}else{
			return false;
		}
	}

	/**
	 * 判断当前节点(非工艺中间件)是否拥有结构（工艺辅件除外）  ---是否为组件
	 *
	 * AL1 ~  AL6
	 *
	 * ALK1 ~ ALK6
	 *
	 * @date 2013-1-17
	 * @return
	 *
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkNodeHasStructure(CmTreeNode currNode) {
//		if ("middle".equals(currNode.getPart().getPartType())) {
//			return true;
//		}else if("assistant".equals(currNode.getPart().getPartType())){
//			return false;
//		}
		return checkPartNumberHasStructure(currNode.getPart().getPartNumber());
	}

	/**
	 * 判断编号是不是组件
	 * @author chenyunlong
	 * @date  2013-5-18
	 * @param partNumber
	 * @return
	 *
	 */
	public static boolean checkPartNumberHasStructure(String partNumber){
//		boolean flag = false;
//		char[] charArray = partNumber.toCharArray();
//		if(("AL".equals(partNumber.substring(0, 2)) && charArray[2] >= '1' && charArray[2] <= '6')
//				|| ("ALK".equals(partNumber.substring(0, 3)) && charArray[3] >= '1' && charArray[3] <= '6')
//				|| "GAL".equals(partNumber.substring(0, 3))
//				|| "GK".equals(partNumber.substring(0, 2))
//				|| "GT".equals(partNumber.substring(0, 2))){
//			flag = true;
//		}
//		return flag;
		//TODO   判断编号是不是组件
		return true;
	}

	/**
	 * 判断partNumber是否已AL,ALK开头
	 * @author chenyunlong
	 * @date  2013-8-13
	 * @param partNumber
	 * @return
	 *
	 */
	public static boolean checkTheHeaderOfPartNumber(String partNumber){
		if("AL".equals(partNumber.substring(0, 2))
				|| "ALK".equals(partNumber.substring(0, 3))){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 把相同的父节点下的相同编号的零件都删除掉
	 *
	 * @author chenyunlong
	 * @date 2013-5-10
	 * @param delnode
	 * @param delParent
	 * @param root
	 * @param list
	 *
	 */
	@SuppressWarnings("unchecked")
	public static void deleteCommonNodeWithCommonParent(CmTreeNode delnode, CmTreeNode delParent, CmTreeNode root,
			List<CmCancelNode> list) {
		Enumeration children = root.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(checkNodeHasStructure(child)){
				boolean flag = true;
				flag = deleteCommonNode(child, delnode, delParent, list, flag);
				if (flag) {
					deleteCommonNodeWithCommonParent(delnode, delParent, child, list);
				} else if (CmCommonStringUtil.isPackage(child)) {
					for (CmTreeNode brother : child.getListNode()) {
						deleteCommonNode(brother, delnode, delParent, list, flag);
					}
				}
			}
		}
	}

	/**
	 * 把相同的父节点下的相同编号的零件都删除掉
	 *
	 * @author chenyunlong
	 * @date 2013-5-10
	 * @param delnode
	 * @param delParent
	 * @param root
	 * @param list
	 *
	 */
	@SuppressWarnings("unchecked")
	public static void deleteCommonNodeWithCommonParent2(CmTreeNode delnode, CmTreeNode delParent, CmTreeNode root,
			List<CmCancelNode> list) {
		Enumeration children = root.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(checkNodeHasStructure(child)){
				boolean flag = true;
				flag = deleteCommonNode2(child, delnode, delParent, list, flag);
				if (flag) {
					deleteCommonNodeWithCommonParent2(delnode, delParent, child, list);
				} else if (CmCommonStringUtil.isPackage(child)) {
					for (CmTreeNode brother : child.getListNode()) {
						deleteCommonNode2(brother, delnode, delParent, list, flag);
					}
				}
			}
		}
	}

	/**
	 * 删除一个零件
	 *
	 * @author chenyunlong
	 * @date 2013-5-10
	 * @param child
	 * @param delnode
	 * @param delParent
	 * @param list
	 * @param flag
	 *
	 */
	@SuppressWarnings("unchecked")
	public static boolean deleteCommonNode(CmTreeNode child, CmTreeNode delnode, CmTreeNode delParent,
			List<CmCancelNode> list, boolean flag) {
		if (child.getPart().getOid() == delParent.getPart().getOid()) {
			Enumeration cChildren = child.children();
			List<CmTreeNode> comlist = new ArrayList<CmTreeNode>();
			while (cChildren.hasMoreElements()) {
				CmTreeNode cc = (CmTreeNode) cChildren.nextElement();
				if (cc.getPart().getOid() == delnode.getPart().getOid()) {
					comlist.add(cc);
				}
			}
			for(CmTreeNode cm:comlist){
				flag = false;
				list.add(CmCommonStringUtil.createNewCancelNode(cm, null, true, "delete"));// 节点的回退
				CmCommonStringUtil.updateNodeOperType(cm, "delete");// 更新节点的操作状态
				CmTreeNode parent = (CmTreeNode) cm.getParent();
				cm.removeFromParent();
				CmCommonStringUtil.checkNodeIsChangeOfStructure(parent, false);
			}
		}
		return flag;
	}

	/**
	 * 删除一个零件
	 *
	 * @author chenyunlong
	 * @date 2013-5-10
	 * @param child
	 * @param delnode
	 * @param delParent
	 * @param list
	 * @param flag
	 *
	 */
	@SuppressWarnings("unchecked")
	public static boolean deleteCommonNode2(CmTreeNode child, CmTreeNode delnode, CmTreeNode delParent,
			List<CmCancelNode> list, boolean flag) {
		if (child.getPart().getOid() == delParent.getPart().getOid()) {
			Enumeration cChildren = child.children();
			List<CmTreeNode> comlist = new ArrayList<CmTreeNode>();
			while (cChildren.hasMoreElements()) {
				CmTreeNode cc = (CmTreeNode) cChildren.nextElement();
				if (cc.getPart().getOid() == delnode.getPart().getOid()) {
					comlist.add(cc);
				}
			}
			for(CmTreeNode cm:comlist){
				flag = false;
				list.add(CmCommonStringUtil.createNewCancelNode(cm, null, true, "delete"));// 节点的回退
				//CmCommonStringUtil.updateNodeOperType(cm, "delete");// 更新节点的操作状态
				CmTreeNode parent = (CmTreeNode) cm.getParent();
				cm.removeFromParent();
				CmCommonStringUtil.checkNodeIsChangeOfStructure(parent, false);
			}
		}
		return flag;
	}

	/**
	 * 获取某节点下的所有有结构的节点和中间件
	 * @author chenyunlong
	 * @date  2013-5-10
	 * @param root
	 *
	 */
	public static void getAllHasChildNodes(CmTreeNode root,List<CmTreeNode> bomlist){
		Enumeration children = root.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(CmCommonNodeUtil.checkNodeHasStructure(child)){
				bomlist.add(child);
			}
			getAllHasChildNodes(child,bomlist);
			if(CmCommonStringUtil.isPackage(child)){
				for(CmTreeNode brother:child.getListNode()){
					if(CmCommonNodeUtil.checkNodeHasStructure(brother)){
						bomlist.add(brother);
					}
					getAllHasChildNodes(brother,bomlist);
				}
			}
		}
	}

	/**
	 * 判断是不是相同结构内部的操作
	 * @author chenyunlong
	 * @date  2013-5-13
	 * @param oper
	 * @param selectedCopyNodeList
	 * @return
	 *
	 */
	public static boolean checkIsInnerOpertion(CmTreeNode oper,List<CmTreeNode> selectedCopyNodeList){
		boolean flag = false;
		List<CmTreeNode> operlist =  getParentNode(oper,new ArrayList<CmTreeNode>());
		lable:
		for(CmTreeNode sel:selectedCopyNodeList){
			if(oper.getPart().getOid() == sel.getPart().getOid()){
				//相同的兄弟节点
				flag = true;
				break;
			}else if(null != sel.getParent()
					&&	checkNodeIsCommon(oper,(CmTreeNode)sel.getParent())){
				//相同的父节点
				Enumeration children = ((CmTreeNode)oper.getParent()).children();
				while(children.hasMoreElements()){
					CmTreeNode child = (CmTreeNode) children.nextElement();
					if(CmCommonStringUtil.isEqual(child.getOccId(), sel.getOccId())){
						flag = true;
						break lable;
					}
				}
			}else{
				for(int i=0;i<operlist.size();i++){
					if(sel.getPart().getOid() == operlist.get(i).getPart().getOid() && i>0){
						flag = true;
						break lable;
					}
				}
			}
		}
		return flag;
	}

	/**
	 * 获取所有的叶子节点
	 * @author chenyunlong
	 * @date  2013-11-7
	 * @param parent
	 * @param list
	 * @return
	 *
	 */
	public static List<CmTreeNode> getAllLeafChildNode(CmTreeNode parent,List<CmTreeNode> list){
		Enumeration children = parent.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(checkNodeIsLeaf(child)){
				if(null == list){
					list = new ArrayList<CmTreeNode>();
				}
				list.add(child);
			}
			list = getAllLeafChildNode(child,list);
			if(CmCommonStringUtil.isPackage(child)){
				for(CmTreeNode brother:child.getListNode()){
					if(checkNodeIsLeaf(brother)){
						if(null == list){
							list = new ArrayList<CmTreeNode>();
						}
						list.add(brother);
					}
					list = getAllLeafChildNode(brother,list);
				}
			}
		}
		return list;
	}

	/**
	 * 获取节点到顶层节点的父节点list
	 * @author chenyunlong
	 * @date  2013-5-10
	 * @param node
	 * @param parentlist
	 * @return
	 *
	 */
	public static List<CmTreeNode> getParentNode(CmTreeNode node,List<CmTreeNode> parentlist){
		if(null != node.getParent().getParent()){
			parentlist.add(node);
			getParentNode((CmTreeNode)node.getParent(),parentlist);
		}
		return parentlist;
	}

	/**
	 * 从某节点中中获取相同的零件
	 * @author chenyunlong
	 * @date  2013-5-15
	 * @param common
	 * @param root
	 * @param list
	 *
	 */
	public static void getCommonNodeFromTree(CmTreeNode common,CmTreeNode root,List<Map<String,CmTreeNode>> list){
		Enumeration children = root.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(checkNodeIsCommon(common, child)){
				Map<String,CmTreeNode> map = new HashMap<String,CmTreeNode>();
				map.put("obj", child);
				map.put("parent", (CmTreeNode)child.getParent());
				list.add(map);
			}
			getCommonNodeFromTree(common,child,list);
			if(CmCommonStringUtil.isPackage(child)){
				for(CmTreeNode brother:child.getListNode()){
					if(checkNodeIsCommon(common, brother)){
						Map<String,CmTreeNode> map = new HashMap<String,CmTreeNode>();
						map.put("obj", brother);
						map.put("parent", (CmTreeNode)child.getParent());
						list.add(map);
					}
					getCommonNodeFromTree(common,brother,list);
				}
			}
		}
	}

	/**
	 * list里面还存在其他的相同的零件
	 * @author chenyunlong
	 * @date  2013-4-2
	 * @param list
	 * @param node
	 * @return
	 *
	 */
	public static boolean checkHasBrotherNodeIsInList(List<CmTreeNode> list, CmTreeNode node) {
		int i=0;
		for (CmTreeNode cmNode : list) {
			if (CmCommonStringUtil.isEqual(cmNode.getPart().getPartNumber(), node.getPart().getPartNumber())) {
				i++;
			}
		}
		return i>1?true:false;
	}

	/**
	 *
	 * @author chenyunlong
	 * @date  2013-5-16
	 * @param paths
	 *
	 */
	public static TreePath[] removeSelectNodeFromPackage(TreePath[] paths ){
		List<TreePath> list = new ArrayList<TreePath>();
		for(TreePath path:paths){
			list.add(path);
		}
		for (Iterator<TreePath> it = list.iterator(); it.hasNext();) {
			CmTreeNode node = (CmTreeNode) it.next().getLastPathComponent();
			if (null == node.getParent()){
				it.remove();
			}
		}
		TreePath[] treepath = new TreePath[list.size()];
		for(int i=0;i<list.size();i++){
			treepath[i] = list.get(i);
		}
		return treepath;
	}

	@SuppressWarnings("unchecked")
	public static List<CmTreeNode> getChildNodeFromParent(CmTreeNode parent){
		List<CmTreeNode> list = new ArrayList<CmTreeNode>();
		Enumeration children = parent.children();
		while(children.hasMoreElements()){
			list.add((CmTreeNode)children.nextElement());
		}
		return list;
	}

	/**
	 * 判断两个零件的Occpath是否相等。
	 * @author chenyunlong
	 * @date  2013-7-17
	 * @param cmnode
	 * @param node
	 * @return
	 *
	 */
	public static boolean isEqualOfOccpath(CmTreeNode cmnode,CmTreeNode node){
		if(checkNodeIsCommon(cmnode, node)){
			if(null == cmnode.getOccpath() || "".equals(cmnode.getOccpath())){
				return false;
			}else if(cmnode.getOccpath().equals(node.getOccpath())){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

	/**
	 * 获取某零件在打开PBOM的时候的字零件信息(工艺中间件和工艺辅件除外)
	 * @author chenyunlong
	 * @date  2013-7-17
	 * @param node
	 * @return
	 *
	 */
//	public List<CmTreeNode> getChildListFromPbomlist(CmTreeNode node){
//		List<CmTreeNode> childlist = new ArrayList<CmTreeNode>();
//		List<CmTreeNode> pbomList = CmScrollPaneTree.pbomlist;
//		for(CmTreeNode pbom:pbomList){
//			if(!CmCommonStringUtil.isNewNode(pbom.getPart().getPartType())
//					&& isEqualOfOccpath(node,(CmTreeNode)pbom.getParent())){
//				childlist.add(pbom);
//			}
//		}
//		return childlist;
//	}

	/**
	 * 获取某零件在打开PBOM的时候的字零件信息(工艺中间件和工艺辅件除外)
	 *
	 * @author chenyunlong
	 * @date 2013-7-17
	 * @param node
	 * @return
	 *
	 */
	public List<CmTreeNode> getChildListFromPbomlist(CmTreeNode node) {
		List<CmTreeNode> childlist = new ArrayList<CmTreeNode>();
		Collection<CmTreeNode> c = CmScrollPaneTree.pbomMap.values();
		for (CmTreeNode pbom : c) {
			if (!CmCommonStringUtil.isNewNode(pbom.getPart().getPartType())
					&& isEqualOfOccpath(node, (CmTreeNode) pbom.getParent())) {
				childlist.add(pbom);
			}
		}
		return childlist;
	}

	/**
	 * 零件的调整 只往上级调整不往下级调整   2
	 * 中间件只添加同级的零件     1
	 * @author chenyunlong
	 * @date  2013-7-12
	 * @param obj
	 * @param move
	 * @return
	 *
	 */
	public static int checkIsMoveToParent(CmTreeNode obj,CmTreeNode move){
		int i=0;
		String objOccpath = obj.getOccpath();
		String moveOccpath = move.getOccpath();
		if(!CmCommonStringUtil.isEmpty(objOccpath)){
			if("middle".equals(obj.getPart().getPartType())
					&& !objOccpath.substring(0, objOccpath.lastIndexOf("+")).equals( moveOccpath.substring(0, moveOccpath.lastIndexOf("+")))){
				i = 1;
			}else if(!"middle".equals(obj.getPart().getPartType())
						&& moveOccpath.indexOf(objOccpath) == -1){
				i = 2;
			}
			if("middle2".equals(obj.getPart().getPartType())
					&& !objOccpath.substring(0, objOccpath.lastIndexOf("+")).equals( moveOccpath.substring(0, moveOccpath.lastIndexOf("+")))){
				i = 1;
			}else if(!"middle2".equals(obj.getPart().getPartType())
						&& moveOccpath.indexOf(objOccpath) == -1){
				i = 2;
			}
			if("zuhe".equals(obj.getPart().getPartType())
					&& !objOccpath.substring(0, objOccpath.lastIndexOf("+")).equals( moveOccpath.substring(0, moveOccpath.lastIndexOf("+")))){
				i = 1;
			}else if(!"zuhe".equals(obj.getPart().getPartType())
						&& moveOccpath.indexOf(objOccpath) == -1){
				i = 2;
			}
			if("mp".equals(obj.getPart().getPartType())
					&& !objOccpath.substring(0, objOccpath.lastIndexOf("+")).equals( moveOccpath.substring(0, moveOccpath.lastIndexOf("+")))){
				i = 1;
			}else if(!"mp".equals(obj.getPart().getPartType())
						&& moveOccpath.indexOf(objOccpath) == -1){
				i = 2;
			}
		}
		return i;
	}

	/**
	 * 清除BOM树上搜索到的结果
	 * @author chenyunlong
	 * @date  2013-7-25
	 * @param root
	 *
	 */
	@SuppressWarnings("unchecked")
	public void clearBomSearchResult(CmTreeNode root){
		Enumeration children = root.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			child.getPart().setSearch(false);
			child.setSelected(false);
			clearBomSearchResult(child);
			if(CmCommonStringUtil.isPackage(child)){
				for(CmTreeNode brother:child.getListNode()){
					brother.getPart().setSearch(false);
					brother.setSelected(false);
					clearBomSearchResult(brother);
				}
			}
		}
	}

	/**
	 * 设置辅制部门
	 * @param node
	 */
	public  static void setTreeNodeFzbm(CmTreeNode node ){
		CmLightPart part = node.getPart();
		part.setFzcj(part.getSecondePlantStr());
	}


//	public static void updateNodeAtrribute(CmTreeNode node){
//		CmLightPart part = node.getPart();
//		String partNumber = part.getPartNumber();
//		WTPart newPart = null;
//		try {
//			 WTPartMaster master = 	WTPartUtil.getWTPartMasterByNumber(partNumber);
//			 newPart = WTPartUtil.getLatestManufacturingPartByMaster(master);
//		} catch (WTException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		String fzbm = null;
//		if(newPart!=null){ //覆盖  xml 中的属性
//			CmIBAHelper  helper = new CmIBAHelper(newPart);
//
////			part.setE_version(e.attributeValue("e_version"));
////			part.setEu_number(e.attributeValue("eu_number"));
////			part.setEu_version(e.attributeValue("eu_version"));
//
//			try {
//				if(!part.isReversion()) {
//					part.setOid(PersistenceHelper.getObjectIdentifier(newPart).getId());
//					part.setVersion(newPart.getVersionIdentifier().getValue() + "." + newPart.getIterationIdentifier().getValue());
//				}
//
////				part.setMaterialType(helper.getIBAValue(newPart, "CTYPE"));
////				part.getWzk().setInvtype(helper.getIBAValue(newPart,"XHPHCL"));
////				part.getWzk().setInvspec(helper.getIBAValue(newPart,"WZGG"));
////				part.getWzk().setDef2(helper.getIBAValue(newPart,"ZGFBZHJSTJ"));
////				part.getWzk().setDef3(helper.getIBAValue(newPart,"XXGF"));
////				part.getWzk().setMeasname(helper.getIBAValue(newPart,"JLDWMC"));
////				part.getWzk().setDef4(helper.getIBAValue(newPart,"ZLDJ"));
////				part.getWzk().setDef5(helper.getIBAValue(newPart,"FZXS"));
////				part.getWzk().setCustname(helper.getIBAValue(newPart,"SCCJ"));
////				part.getWzk().setMpcc(helper.getIBAValue(newPart,"MPCC"));
////				part.getWzk().setClcode(helper.getIBAValue(newPart,"CLBM"));
////				part.getWzk().setClName(helper.getIBAValue(newPart,"CMAT"));
//////				part.getWzk().setInvcode(e.attributeValue("WZBM"));
//////				part.getWzk().setInvname(e.attributeValue("WZMC"));
//////				part.getWzk().setZxsl(e.attributeValue("ZXSL"));
////				part.getWzk().setWzlb(helper.getIBAValue(newPart,"WZLB"));
////				part.setFirstPlant(helper.getIBAValue(newPart,"ZZBM"));
////				fzbm = helper.getIBAValue(newPart,"FZBM");
////				part.setFzbm(fzbm);
////				part.getWzk().setInvclasscode(helper.getIBAValue(newPart,"CLFLBM"));
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//		}
//
//		String[] plants = LoadConfig.getInstance().getMainPlant();
//		Map<String,Boolean> plantMap = new HashMap<String,Boolean>();
//
//		for(int i=0;i<plants.length;i++){
//			plantMap.put(plants[i], false);
//		}
//
//		for(int i=0;i<plants.length;i++){
//			if(null!=fzbm&&!"".equals(fzbm)){
//				String [] fzbms = fzbm.split("-");
//				if(fzbms!=null){
//					for(int j=0;j<fzbms.length;j++){
//						if(plants[i].equals(fzbms[j])){
//							plantMap.put(plants[i], true);
//						}
//					}
//				}
//			}
//		}
//
//		part.setSecondePlant(plantMap);
//	}

	/**
	 * 获取所有的子节点的occpath
	 * @author chenyunlong
	 * @date  2013-11-19
	 * @param pbom
	 *
	 */
	@SuppressWarnings("unchecked")
	public static void getAllChildrenOccpath(CmTreeNode pbom){
		List<String> occpathlist = new ArrayList<String>();
		Enumeration children = pbom.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode)children.nextElement();
			if(!"assistant".equals(child.getPart().getPartType())){
				occpathlist.add(child.getOccpath());
			}
			if(CmCommonStringUtil.isPackage(child)){
				for(CmTreeNode brother:child.getListNode()){
					occpathlist.add(brother.getOccpath());
				}
			}
		}
		pbom.setChildOccpathList(occpathlist);
	}
	/**
	 * 判断节点是否是叶子节点
	 * 判断条件：节点下没有子节点或子节点都是工艺辅件
	 * @author chenyunlong
	 * @date  2013-11-21
	 * @param node
	 * @return
	 *
	 */
	public static boolean checkNodeIsLeaf(CmTreeNode node){
		boolean flag = true;
		Enumeration children = node.children();
		if(children.hasMoreElements()){
			while(children.hasMoreElements()){
				CmTreeNode child = (CmTreeNode) children.nextElement();
				if(!"assistant".equals(child.getPart().getPartType())){
					flag = false;
					break;
				}
			}
		}
		return flag;
	}

	public static void isAddErpNode(CmTreeNode pNode,CmTreeNode node){
		Enumeration<CmTreeNode> en = pNode.children();
		CmTreeNode tNode;
		while(en.hasMoreElements()){
			tNode = en.nextElement();
			if(CmCommonStringUtil.isCommon(tNode, node)){

			}
		}
	}

	/**
	 * 判断节点是否可以保存可视化3D模型
	 * @author chenyunlong
	 * @date  2013-11-19
	 * @param node
	 * @return
	 *
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkNoodIsSavePview(CmTreeNode node){
		List<String> occpathlist = node.getChildOccpathList();
	    boolean flag = true;
		if(null != occpathlist && occpathlist.size()>0){
			int i=0;
			Enumeration children = node.children();
			lable:
			while(children.hasMoreElements()){
				CmTreeNode child = (CmTreeNode) children.nextElement();
				if(!"assistant".equals(child.getPart().getPartType())
						&& !"middle2".equals(child.getPart().getPartType())
						&& !"mp".equals(child.getPart().getPartType())
						&& !"zuhe".equals(child.getPart().getPartType())
						&& !occpathlist.contains(child.getOccpath())){
					flag = false;
					break lable;
				}
				i++;
				if(CmCommonStringUtil.isPackage(child)){
					for(CmTreeNode brother:child.getListNode()){
						if(!occpathlist.contains(brother.getOccpath())){
							flag = false;
							break lable;
						}
						i++;
					}
				}
			}
			if(flag && occpathlist.size() > i){
				flag = false;
			}
		}
		else{
			Enumeration children = node.children();
			lable:
			while(children.hasMoreElements()){
				CmTreeNode child = (CmTreeNode) children.nextElement();
				if(!"assistant".equals(child.getPart().getPartType())
						&& !"middle2".equals(child.getPart().getPartType())
						&& !"mp".equals(child.getPart().getPartType())
						&& !"zuhe".equals(child.getPart().getPartType())){
					flag = false;
					break lable;
				}
			}
		}
		return flag;
	}
}
