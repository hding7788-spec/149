package com.glaway.mpm.pbombuilder.action;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeNode;

import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

public class CmCommonPackageAction {
	/**
	 * 全部打包
	 *
	 * @date 2012-11-26
	 * @param tree
	 *
	 */
	public boolean packageAllNode(CmTree tree) {
		CmTreeNode root = (CmTreeNode) tree.getRoot().children().nextElement();
		boolean flag = packageTheNode(root,false);
		CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
		tree.updateUI();
		return flag;
	}

	@SuppressWarnings("unchecked")
	public boolean packageTheNode(CmTreeNode node,boolean flag) {
		Enumeration children = node.children();
		List<CmTreeNode> nodelist = new ArrayList<CmTreeNode>();//记录某节点下的子节点
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(!"assistant".equals(child.getPart().getPartType())){
				nodelist.add(child);
			}
		}
		List<CmTreeNode> packageNodelist = new ArrayList<CmTreeNode>();//记录可以打包的节点信息
		for (CmTreeNode cnode : nodelist) {
			if(!CmCommonNodeUtil.checkHasSameOidInList(cnode, packageNodelist) && CmCommonNodeUtil.checkHasBrotherNodeIsInList(nodelist, cnode)){
				flag = true;
				packageBrotherNode(nodelist, cnode);
				packageNodelist.add(cnode);
				flag = packageTheNode(cnode,flag);
				if(CmCommonStringUtil.isPackage(cnode)){
					for(CmTreeNode brother:cnode.getListNode()){
						flag = packageTheNode(brother,flag);
					}
				}
			}
			else if(!CmCommonNodeUtil.checkHasSameOidInList(cnode, packageNodelist) && !CmCommonNodeUtil.checkHasBrotherNodeIsInList(nodelist, cnode)){
				// 当前节点不能打包
				flag = packageTheNode(cnode,flag);
			}
			if(CmCommonStringUtil.isPackage(cnode)){
				for(CmTreeNode cm:cnode.getListNode()){
					flag = packageTheNode(cm,flag);
				}
			}
		}
		return flag;
	}
	/**
	 * 检查某打包节点或可以打包的节点下的结构是否完全一样
	 * @author chenyunlong
	 * @date  2013-4-11
	 * @param root
	 * @param node
	 * @param flag
	 * @param packageNodelist
	 * @return
	 *
	 */
	public StringBuffer checkThePackagePbomTree(CmTreeNode root,CmTreeNode node,StringBuffer errorBuf,List<CmTreeNode> packageNodelist,String tree){
		List<CmTreeNode> bomlist = new ArrayList<CmTreeNode>();//获取PBOM中所有带结构的或中间件节点信息
		CmCommonNodeUtil.getAllHasChildNodes(root,bomlist);
		lable:
			for(CmTreeNode bom:bomlist){
				for(CmTreeNode common:bomlist){
					if(CmCommonNodeUtil.checkNodeIsCommon(bom, common)
							&& !compareStructureIsCommon(bom, common)){
						bom.setSelected(true);
						bom.getPart().setSearch(true);
						common.setSelected(true);
						common.getPart().setSearch(true);
						buildPath(common,errorBuf,"PBOM");
						errorBuf.append("\n和\n");
						buildPath(bom,errorBuf,"PBOM");
						errorBuf.append("\n");
						errorBuf.insert(0, "下面"+tree+"上的编号相同的节点：\n");
						errorBuf.append("\n结构不完全一致，不能进行打包！\n");
						break lable;
					}
				}
			}
		return errorBuf;
	}
	/**
	 * 判断选择的节点在PBOM树上，结构是否一样
	 * @author chenyunlong
	 * @date  2013-4-22
	 * @param root
	 * @param packageNode
	 * @param flag
	 * @return
	 *
	 */
	public StringBuffer checkThePackageNode(CmTreeNode root,CmTreeNode packageNode,String oper,boolean isLight){
		StringBuffer errorBuf = new StringBuffer(1024);
		if(CmCommonNodeUtil.checkNodeHasStructure(packageNode)){
			Map<String,List<CmTreeNode>> bommap = new HashMap<String,List<CmTreeNode>>();//获取PBOM中所有带结构的或中间件节点信息
			getAllHasChildNodes(root,bommap);
			List<CmTreeNode> childlist = new ArrayList<CmTreeNode>();
			childlist.add(packageNode);
			getNodesWithHasStructure(packageNode, childlist);
			lable:
				for(int i=0;i<childlist.size();i++){
					List<CmTreeNode> nodelist = bommap.get(childlist.get(i).getPart().getPartNumber());
					for(int j=0;j<nodelist.size();j++){
						for(int k=j+1;k<nodelist.size();){
							errorBuf = compareStructureIsCommon(nodelist.get(j),nodelist.get(k),"PBOM",oper,isLight);
							if(errorBuf.toString().length()>0){
								break lable;
							}else{
								break;
							}
						}
					}
				}
		}
		return errorBuf;
	}
	/**
	 * 比较两个节点下的结构是否相同
	 * @author chenyunlong
	 * @date  2013-4-2
	 * @param obj
	 * @param common
	 * @return
	 *
	 */
	public StringBuffer compareStructureIsCommon(CmTreeNode bom,CmTreeNode common,String tree,String oper,boolean isLight){
		boolean flag = true;
		List<CmTreeNode> objList = getChildListOfParent(bom);
		List<CmTreeNode> commonList = getChildListOfParent(common);
		StringBuffer errorBuf = new StringBuffer(1024);
		if(objList.size()==0 && commonList.size()==0){
			flag =true;
		}else if(objList.size() != commonList.size()){
			flag =false;
		}
		else {
			lable:
			for (Iterator<CmTreeNode> it = objList.iterator(); it.hasNext();) {
				CmTreeNode cmnode = it.next();
				boolean iscommon = true;
				for(int j=0;j<commonList.size();j++){
					if (CmCommonNodeUtil.checkNodeIsCommon(cmnode,commonList.get(j)) && CmCommonStringUtil.isEqual(cmnode.getOccId(), commonList.get(j).getOccId())) {
						commonList.remove(j);
						iscommon = true;
						it.remove();
						break;
					}else{
						iscommon = false;
					}
				}
				if(!iscommon){
					break lable;
				}
			}
		}
		if(flag && objList.size()>0){
			for(CmTreeNode objnode:objList){
				objnode.setSelected(isLight);
				objnode.getPart().setSearch(isLight);
				buildPath(objnode,errorBuf,"PBOM");
				errorBuf.append("\n");
			}
			errorBuf.append("和\n");
			for(CmTreeNode commonnode:commonList){
				commonnode.setSelected(isLight);
				commonnode.getPart().setSearch(isLight);
				buildPath(commonnode,errorBuf,"PBOM");
				errorBuf.append("\n");
			}
			errorBuf.append("\n");
			errorBuf.insert(0, "下面"+tree+"上的编号相同的节点：\n");
			errorBuf.append("\n在相同的父节点下位号不一致，不能进行"+oper+"！\n");
		}else if(!flag){
			bom.setSelected(isLight);
			bom.getPart().setSearch(isLight);
			common.setSelected(isLight);
			common.getPart().setSearch(isLight);
			buildPath(common,errorBuf,"PBOM");
			errorBuf.append("\n和\n");
			buildPath(bom,errorBuf,"PBOM");
			errorBuf.append("\n");
			errorBuf.insert(0, "下面"+tree+"上的编号相同的节点：\n");
			errorBuf.append("\n结构不完全一致，不能进行"+oper+"！\n");
		}
		return errorBuf;
	}
	/**
	 * 获取零件下有结构的节点
	 * @author chenyunlong
	 * @date  2013-8-27
	 * @param root
	 * @param list
	 *
	 */
	public void getNodesWithHasStructure(CmTreeNode root,List<CmTreeNode> list){
		Enumeration children = root.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(CmCommonNodeUtil.checkNodeHasStructure(child) && !CmCommonNodeUtil.checkHasSameOidInList(child, list)){
				list.add(child);
			}
			getNodesWithHasStructure(child,list);
		}
	}
	/**
	 * 获取某节点下的所有有结构的节点和中间件
	 * @author chenyunlong
	 * @date  2013-5-10
	 * @param root
	 *
	 */
	public void getAllHasChildNodes(CmTreeNode root,Map<String,List<CmTreeNode>> bommap){
		Enumeration children = root.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(CmCommonNodeUtil.checkNodeHasStructure(child)){
				List<CmTreeNode> list = bommap.get(child.getPart().getPartNumber());
				if(null == list){
					list = new ArrayList<CmTreeNode>();
				}
				list.add(child);
				bommap.put(child.getPart().getPartNumber(), list);
			}
			getAllHasChildNodes(child,bommap);
		}
	}
	/**
	 * 判断选择的节点在PBOM树上，结构是否一样
	 * @author chenyunlong
	 * @date  2013-4-22
	 * @param root
	 * @param packageNode
	 * @param flag
	 * @return
	 *
	 */
	public StringBuffer checkThePackageNode(CmTreeNode root,CmTreeNode packageNode,String oper){
		List<CmTreeNode> bomlist = new ArrayList<CmTreeNode>();//获取PBOM中所有带结构的或中间件节点信息
		CmCommonNodeUtil.getAllHasChildNodes(root,bomlist);
		List<CmTreeNode> childlist = new ArrayList<CmTreeNode>();//获取当前打包节点和它的带结构的子节点信息
		CmCommonNodeUtil.getAllHasChildNodes(packageNode,childlist);
		childlist.add(packageNode);
		StringBuffer errorBuf = new StringBuffer(1024);
		lable:
		for(CmTreeNode child:childlist){
			for(CmTreeNode bom:bomlist){
				if(CmCommonStringUtil.isCommon(child, bom)){
					if(!compareStructureIsCommon(child,bom)){
						System.out.println("------child--"+child.getPart().getPartNumber());
						System.out.println("------bom---"+bom.getPart().getPartNumber());
						bom.setSelected(true);
						bom.getPart().setSearch(true);
						child.setSelected(true);
						child.getPart().setSearch(true);
						buildPath(child,errorBuf,"PBOM");
						errorBuf.append("\n和\n");
						buildPath(bom,errorBuf,"PBOM");
						errorBuf.append("\n");
						errorBuf.insert(0, "下面PBOM上的编号相同的节点：\n");
						errorBuf.append("\n结构不完全一致，不能进行"+oper+"！\n");
						break lable;
					}
				}
			}
		}
		return errorBuf;
	}

	/**
	 * 对一个节点打包
	 * @author chenyunlong
	 * @date  2013-3-28
	 * @param root
	 * @param packageNode
	 *
	 */
	public void packageOneNode(CmTreeNode root,CmTreeNode packageNode){
		Enumeration children = root.children();
		lable:
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(CmCommonStringUtil.isCommon(child, packageNode)){
				CmTreeNode parent = (CmTreeNode) child.getParent();
				List<CmTreeNode> list=new ArrayList<CmTreeNode>();
				Enumeration enmu =parent.children();
				while(enmu.hasMoreElements()){
					CmTreeNode brother = (CmTreeNode) enmu.nextElement();
					if(CmCommonStringUtil.isCommon(brother, packageNode)){
						if(CmCommonStringUtil.isPackage(brother)){
							for(CmTreeNode cm:brother.getListNode()){
								list.add(cm);
							}
							brother.setListNode(null);
							list.add(brother);
						}
						else{
							list.add(brother);
						}
					}
				}
				if(list.size()>1){
					List<CmTreeNode> brotherList=new ArrayList<CmTreeNode>();
					for(int i=1;i<list.size();i++){
						getParentPath(list.get(i));
						list.get(i).removeFromParent();
						brotherList.add(list.get(i));
					}
					list.get(0).setListNode(brotherList);
					parent.add(list.get(0));
				}
				break lable;
			}
			packageOneNode(child,packageNode);
			if(CmCommonStringUtil.isPackage(child)){
				for(CmTreeNode cmnode:child.getListNode()){
					packageOneNode(cmnode,packageNode);
				}
			}
		}
	}

	// 打包
	public void packageBrotherNode(List<CmTreeNode> list, CmTreeNode node) {
		List<CmTreeNode> brotherList = new ArrayList<CmTreeNode>();
		if (CmCommonStringUtil.isPackage(node)) {
			for (CmTreeNode cm : node.getListNode()) {
				brotherList.add(cm);
			}
			node.setListNode(null);
		}
		for (CmTreeNode cmnode : list) {
			if (("middle2".equals(node.getPart().getPartType()) && CmCommonStringUtil.checkIsCommonMiddleNodeButNotSame(node, cmnode))
					|| (!"middle2".equals(node.getPart().getPartType()) && CmCommonNodeUtil.checkHasSameOidAndNotSameOccpath(node, cmnode))
					|| (!"middle2".equals(node.getPart().getPartType()) && CmCommonNodeUtil.checkHasSameOccpath(node, cmnode)) && !CmCommonStringUtil.isEqual(node.getPart().getMiddleIndex(), cmnode.getPart().getMiddleIndex())) {
				if(CmCommonStringUtil.isPackage(cmnode)){
					for(CmTreeNode cmbrother:cmnode.getListNode()){
						brotherList.add(cmbrother);
					}
					cmnode.setListNode(null);
					brotherList.add(cmnode);
				}
				else{
					brotherList.add(cmnode);
				}
				getParentPath(cmnode);
				cmnode.removeFromParent();
			}
		}
		if(brotherList.size()>0){
			node.setListNode(brotherList);
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
	public boolean checkTheNodeIsInList(List<CmTreeNode> list, CmTreeNode node) {
		boolean flag = false;
		for (CmTreeNode cmNode : list) {
			if (CmCommonStringUtil.checkNodeIsCommonButNotSame(cmNode, node)) {
				flag = true;
			}
		}
		return flag;
	}
	/**
	 * 全部拆包
	 *
	 * @date 2012-11-26
	 * @param tree
	 *
	 */
	public boolean uppackageAllNode(CmTree tree) {
		boolean flag = uppackageTheNode(tree.getRoot(),false);
//		CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
		tree.updateUI();
		return flag;
	}
	/**
	 * 右键对一个节点拆包
	 * @author chenyunlong
	 * @date  2013-3-28
	 * @param root
	 * @param unpackageNode
	 *
	 */
	public void unpackageOneNode(CmTreeNode root,CmTreeNode unpackageNode){
		Enumeration children = root.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(CmCommonStringUtil.isCommon(child, unpackageNode) && CmCommonStringUtil.isPackage(child)){
				List<CmTreeNode> list = child.getListNode();
				CmTreeNode parent = (CmTreeNode) child.getParent();
				for (CmTreeNode brotherNode : list) {
					parent.add(brotherNode);
				}
				child.setListNode(null);
			}
			unpackageOneNode(child,unpackageNode);
			if(CmCommonStringUtil.isPackage(child)){
				for(CmTreeNode cmnode:child.getListNode()){
					unpackageOneNode(cmnode,unpackageNode);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public boolean uppackageTheNode(CmTreeNode node,boolean flag) {
		if(CmCommonStringUtil.isPackage(node)){
			flag = true;
			CmTreeNode parent = (CmTreeNode) node.getParent();
			List<CmTreeNode> list = node.getListNode();
			for (CmTreeNode brotherNode : list) {
				parent.add(brotherNode);
			}
			node.setListNode(null);
		}
		Enumeration children = node.children();
		while (children.hasMoreElements()) {
			CmTreeNode cmnode = (CmTreeNode) children.nextElement();
			flag = uppackageTheNode(cmnode,flag);
		}
		return flag;
	}

	/**
	 * 比较两个节点下的结构是否相同
	 * @author chenyunlong
	 * @date  2013-4-2
	 * @param obj
	 * @param common
	 * @return
	 *
	 */
	public boolean compareStructureIsCommon(CmTreeNode obj,CmTreeNode common){
		boolean flag = true;
		List<CmTreeNode> objList = getChildListOfParent(obj);
		List<CmTreeNode> commonList = getChildListOfParent(common);
		if(objList.size()==0 && commonList.size()==0){
			return true;
		}else if(objList.size() != commonList.size()){
			return false;
		}
		else {
			for(int i=0;i<objList.size();i++){
				for(int j=0;j<commonList.size();j++){
					if(CmCommonNodeUtil.checkHasSameOidInList(commonList.get(j),objList)){
						commonList.remove(j);
					}else{
						flag = false;
						break;
					}
				}
			}
		}
		return flag;
	}

	/**
	 * 获取全部子节点
	 * @author chenyunlong
	 * @date  2013-4-2
	 * @param parent
	 * @return
	 *
	 */
	public static List<CmTreeNode> getChildListOfParent(CmTreeNode parent){
		List<CmTreeNode> list = new ArrayList<CmTreeNode>();
		Enumeration children = parent.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode)children.nextElement();
			list.add(child);
			if(CmCommonStringUtil.isPackage(child)){
				for(CmTreeNode brother:child.getListNode()){
					list.add(brother);
				}
			}
		}
		return list;
	}

	public void getParentPath(CmTreeNode node){
		TreeNode[] path = node.getPath();
		StringBuffer buffer = new StringBuffer(1024);
		buffer.append("PBOM");
		for (int i = 1; i < path.length-1; i++) {
			CmTreeNode pathNode = (CmTreeNode) path[i];
			buffer.append('/').append(pathNode.getPart().getPartNumber());
		}
		node.getPart().setParentPath(buffer.toString());
	}

	public static void buildPath(CmTreeNode child,StringBuffer errorBuf,String treeName){
		TreeNode[] path = child.getPath();
		if(path.length==1){
			errorBuf.append(child.getPart().getParentPath());
			errorBuf.append('/').append(child.getPart().getPartNumber());
		}
		else if(null == child.getParent()
				&& !"PBOM".equals(child.getPart().getPartNumber())
				&& !"EBOM".equals(child.getPart().getPartNumber())
				&& path.length > 1){
			errorBuf.append(child.getPart().getParentPath());
			for (int i = 1; i < path.length; i++) {
				CmTreeNode pathNode = (CmTreeNode) path[i];
				errorBuf.append('/').append(pathNode.getPart().getPartNumber());
			}
		}else{
			errorBuf.append(treeName);
			for (int i = 1; i < path.length; i++) {
				CmTreeNode pathNode = (CmTreeNode) path[i];
				errorBuf.append('/').append(pathNode.getPart().getPartNumber());
			}
		}
	}
}
