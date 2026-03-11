package com.glaway.mpm.pbombuilder.action;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmTreeNodeAttributProxy;
import com.glaway.mpm.pbombuilder.data.CmXmlDataProxy;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.jws.CmContext;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmCancelNode;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeCancelAction;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmSettings;
import com.glaway.mpm.pbombuilder.util.CmTaskInfo;
import com.glaway.mpm.pbombuilder.util.CmTaskResultSet;
import com.glaway.mpm.pbombuilder.util.CmTypeHelper;
import com.glaway.mpm.pbombuilder.util.PviewTask;

public class CmCommonPasteAction {
	private static final CmLogger log = CmLogger.getLogger(CmCommonPasteAction.class.getName());
	private StringBuffer errorPlanning = null;

	/**
	 * 判断该节点是否有粘贴的功能 工艺辅件，没有子节点的普通零件 没有粘贴功能
	 * 
	 * @date 2012-11-20
	 * @param node
	 * @return
	 * 
	 */
	public boolean isPaste(CmTreeNode node) {
		if (null == node.getParent()) {
			// PBOM节点
			return false;
		} else if (node.getPart().getPartType().equals("assistant")) {
			// 工艺辅件
			return false;
		} else if ((null != node.getParent()) && !node.getPart().getPartType().equals("middle")
				&& !node.children().hasMoreElements()) {
			// 不是PBOM根节点的中间件有粘贴功能
			return false;
		} else {
			// 未归档
			return !CmCommonStringUtil.isHasFilingOfObj(node);
		}
	}

	/**
	 * 过滤CI、LO类型
	 * 
	 * @param nodeList
	 * @param node
	 * @param excludeExtTypes
	 */
	public void addToSelectedNodeList(List<CmTreeNode> nodeList, CmTreeNode node, List<String> excludeExtTypes) {
		if (node != null && node.getPart() != null) {
			String extType = node.getPart().getPartType();
			if (!excludeExtTypes.contains(extType))
				nodeList.add(node);
			else {
				Enumeration en = node.children();
				while (en.hasMoreElements())
					addToSelectedNodeList(nodeList, (CmTreeNode) en.nextElement(), excludeExtTypes);
			}
		}
	}

	/**
	 * 避免嵌套
	 * 
	 * @param nodeList
	 * @return
	 */
	public List<CmTreeNode> filterNodeWithParent(List<CmTreeNode> nodeList) {
		List<CmTreeNode> ret = new ArrayList<CmTreeNode>();

		Iterator<CmTreeNode> iter = nodeList.iterator();
		while (iter.hasNext())
			addToList(ret, iter.next());

		return ret;
	}

	private void addToList(List<CmTreeNode> nodeList, CmTreeNode node) {
		TreeNode[] nodePath = node.getPath();

		Iterator<CmTreeNode> iter = nodeList.iterator();
		while (iter.hasNext()) {
			CmTreeNode node0 = iter.next();

			TreeNode[] nodePath0 = node0.getPath();
			if (nodePath.length < nodePath0.length) {
				if (nodePath0[nodePath.length - 1] == node) { // 物理上相同
					iter.remove();
				}
			} else if (nodePath.length > nodePath0.length) {
				if (nodePath[nodePath0.length - 1] == node0) { // 物理上相同
					node = null;
					break;
				}
			}
		}

		if (node != null)
			nodeList.add(node);
	}

	public CmTreeNode addToChild(CmTree tree, CmTreeNode parent, CmTreeNode node,CmTreeNode newparent, StringBuffer errorBuf,
			boolean isOnly,boolean isEbom,CmTreeNodeAttributProxy attribut) {
		CmTreeNode ret = null;

		if (node != null) {
			// node在tree中不能存在
			if(isEbom){
				if (!exists(tree, node)) {
					ret = CmBizObjUtil.convertEBomNode2MBomNode2(node,true);
					CmBizObjUtil.setMatrixPaste(ret);
					removeDeleteNode(ret);
					CmCommonStringUtil.checkNodeIsChangeOfStructure(ret, false);
					CmTreeNode unplanningNode = null;
					unplanningNode = CmCommonStringUtil.getPbomPlanning(ret,unplanningNode);
					if (null != unplanningNode) {
						// 该节点获取planning视图失败
						log.debug("获取Planning视图失败");
						TreeNode[] path = node.getPath();
						errorPlanning.append("EBOM");
						for (int i = 1; i < path.length; i++) {
							CmTreeNode pathNode = (CmTreeNode) path[i];
							errorPlanning.append('/').append(pathNode.getPart().getPartNumber());
						}
						errorPlanning.append("\n");
					} else if (parent != null)
						log.debug("获取Planning视图成功");
						parent.add(ret);
//						ret.setParent(parent);
						CmCommonStringUtil.addNodeToCheckList(ret);
						CmCommonStringUtil.isMoved(ret);
						CmCommonStringUtil.updateNodeOperType(ret, "");// 更新节点的操作状态
						CmCommonStringUtil.setIsNewTop(ret);
						CmCommonStringUtil.checkNodeIsChangeOfStructure(parent, false);
						if (isOnly) {
							EbomTreeCancelAction.addPbomTreeChange(ret,null, "create", null);
						}
				} else{
					// 统计错误，反馈给客户
					TreeNode[] path = node.getPath();
					errorBuf.append("EBOM");
					for (int i = 1; i < path.length; i++) {
						CmTreeNode pathNode = (CmTreeNode) path[i];
						errorBuf.append('/').append(pathNode.getPart().getPartNumber());
					}
					errorBuf.append("\n");
				}
			}
			else if(parent != null){
				//PBOM的复制---只能通过剪切
				attribut.deleteAttributNode((CmTreeNode) node.getParent(), node);
				node.removeFromParent();
				CmCommonStringUtil.checkNodeIsChangeOfStructure(newparent, false);
				ret = CmBizObjUtil.convertEBomNode2MBomNode2(node,true);
				CmBizObjUtil.setMatrixPaste(ret);
				parent.add(ret);
				ret.setParent(parent);
				CmCommonStringUtil.checkNodeIsChangeOfStructure(parent, false);
				CmCommonStringUtil.addNodeToCheckList(ret);
				CmCommonStringUtil.isMoved(ret);
				CmCommonStringUtil.updateNodeOperType(ret, "");// 更新节点的操作状态
				if (isOnly) {
					EbomTreeCancelAction.addPbomTreeChange(ret,newparent, "move", null);
				}
			}
		}
		return ret;
	}

	
	
	private boolean exists(CmTree tree, CmTreeNode node) {
		boolean ret = false;

		List<String> nodeOccIdList = getAllNodesOccIdList(node);

		Enumeration en = tree.getRoot().breadthFirstEnumeration();
		while (en.hasMoreElements()) {
			CmTreeNode treeNode = (CmTreeNode) en.nextElement();
			if (null != treeNode.getParent() 
					&& nodeOccIdList.contains(treeNode.getOccpath())) { // 实例信息相同，则认为是已经存在
				ret = true;
				break;
			} else if (CmCommonStringUtil.isPackage(treeNode)) {
				for (CmTreeNode brother : treeNode.getListNode()) {
					if (nodeOccIdList.contains(brother.getOccpath())) {
						ret = true;
						break;
					}
				}
			}
			if (ret) {
				break;
			}
		}

		return ret;
	}

	private List<String> getAllNodesOccIdList(CmTreeNode node) {
		List<String> ret = new ArrayList<String>();

		Enumeration en = node.breadthFirstEnumeration();
		while (en.hasMoreElements()) {
			CmTreeNode item = (CmTreeNode) en.nextElement();
			ret.add(item.getOccpath());
		}
		if(CmCommonStringUtil.isPackage(node)){
	    	  for(CmTreeNode brother:node.getListNode()){
	    		  Enumeration enume = brother.breadthFirstEnumeration();
	    	      while (enume.hasMoreElements()) {
	    	         CmTreeNode item = (CmTreeNode) enume.nextElement();
	    	         ret.add(item.getOccpath());
	    	      }
	    	  }
	      }
		return ret;
	}

	public List<CmTreeNode> getCopyNodeList(List<CmTreeNode> selectedCopyNodeList) {
		CmTaskInfo pasteTaskInfo = CmTaskInfo.newCmTaskInfo("CmEBomTreePanel.getSelectedCopyNodeList", this, null);
		try {
			CmTaskResultSet resultSet = PviewTask.sendTask(pasteTaskInfo, null);
			selectedCopyNodeList = (List<CmTreeNode>) resultSet.getFirstReturnVal();
		} catch (CmTaskException e) {
			e.printStackTrace();
		}
		return selectedCopyNodeList;
	}

	public List<CmTreeNode> getCurrentCopyNodeList(List<CmTreeNode> selectedCopyNodeList) {
		CmSettings settings = CmSettings.getSection(CmSettings.SECTION_MBOM);
		String[] unPasteTypes = settings.get("mbom.newNode.unPaste.type", new String[0]);
		List<String> unPastes = new ArrayList<String>();
		for (String unPasteType : unPasteTypes) {
			String extType = CmTypeHelper.getExtType(unPasteType);
			if (extType != null)
				unPastes.add(extType);
		}

		List<CmTreeNode> nodeList = new ArrayList<CmTreeNode>();
		Iterator<CmTreeNode> iter = selectedCopyNodeList.iterator();
		while (iter.hasNext())
			addToSelectedNodeList(nodeList, iter.next(), unPastes);

		nodeList = filterNodeWithParent(nodeList);
		return nodeList;
	}

	public void pasteCopyNodeList(List<CmTreeNode> selectedCopyNodeList, CmTree tree, CmTreeNode currNode,
			StringBuffer errorBuf) {
		List<CmTreeNode> nodeList = getCurrentCopyNodeList(selectedCopyNodeList);
		boolean isEbom = nodeList.size()==0?false:!CmCommonStringUtil.checkisPbomNode(nodeList.get(0));
		if(!isEbom){//剪切---粘贴--如果剪切的节点中，有节点的父节点与当前要挂载的节点相同，就没必要参与粘贴
			for (Iterator<CmTreeNode> it = nodeList.iterator(); it.hasNext();) {
				CmTreeNode cmnode = it.next();
				if (CmCommonStringUtil.checkNodeIsSame((CmTreeNode)cmnode.getParent(), currNode)){
					it.remove();
				}
			}
		}
		errorBuf = new StringBuffer(1024);
		errorPlanning = new StringBuffer(1024);
		List<CmCancelNode> list = new ArrayList<CmCancelNode>();
		CmXmlDataProxy proxy = CmXmlDataProxy.getCmXmlDataProxy();
		CmTreeNodeAttributProxy attribut = (CmTreeNodeAttributProxy) proxy.getProxy(CmXmlDataProxy.MBOM_ATTRIBUTE_PROXY);
		for (CmTreeNode node : nodeList) {
			boolean isonly= nodeList.size() == 1;
			isEbom = !CmCommonStringUtil.checkisPbomNode(node);
			CmTreeNode newparent = (CmTreeNode) node.getParent();
			CmTreeNode nodeToAdd = addToChild(tree, currNode, node,newparent, errorBuf, isonly,isEbom,attribut);

			if (nodeToAdd != null && nodeToAdd.getChildCount() > 0)
				tree.expandPath(new TreePath(nodeToAdd.getPath()));
			else {
				tree.expandPath(new TreePath(currNode.getPath()));
			}
			if (nodeToAdd != null && nodeList.size() > 1) {
				list.add(CmCommonStringUtil.createNewCancelNode(nodeToAdd,newparent,isEbom,null));// 节点的回退
			}
		}
		CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
		tree.updateUI();
		if (errorBuf.toString().length() > 0) {
			if(nodeList.get(0).isRoot() && !CmCommonStringUtil.checkisPbomNode(nodeList.get(0))){
				errorBuf.insert(0, "下列PBOM树上节点：\n");
			}
			else{
				errorBuf.insert(0, "下列EBOM树上节点：\n");
			}
			errorBuf.append("\n在PBOM上已经存在!\n");
			if (errorPlanning.toString().length() > 0) {
				errorPlanning.insert(0, "下列EBOM树上节点：\n");
				errorPlanning.append("\n创建Planning视图失败，请联系管理员！");
				errorBuf.append(errorPlanning);
			}
			JOptionPane.showMessageDialog(CmContext.getMainFrame(), errorBuf.toString());
		}
		else if(errorPlanning.toString().length() > 0){
			errorPlanning.insert(0, "下列EBOM树上节点：\n");
			errorPlanning.append("\n创建Planning视图失败，请联系管理员！");
			JOptionPane.showMessageDialog(CmContext.getMainFrame(), errorPlanning.toString());
		}
		else if (null != list && list.size() > 0) {
			if(isEbom){
				EbomTreeCancelAction.addPbomTreeChange(null,null, "create", list);
			}else{
				EbomTreeCancelAction.addPbomTreeChange(null,null, "move", list);
			}
		}
	}
	/**
	 * 去掉删除的节点
	 * @author chenyunlong
	 * @date  2013-5-28
	 * @param node
	 *
	 */
	public void removeDeleteNode(CmTreeNode node){
		List<CmTreeNode> list = new ArrayList<CmTreeNode>();
		getDeleteNode(node,list);
		if(CmCommonStringUtil.isPackage(node)){
			for(CmTreeNode bro:node.getListNode()){
				getDeleteNode(bro,list);
			}
		}
		for(CmTreeNode cmnode:list){
			cmnode.removeFromParent();
		}
	}
	
	public void getDeleteNode(CmTreeNode node,List<CmTreeNode> list){
		Enumeration children = node.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(child.getPart().getEchangeIndex() == 3){
				list.add(child);
			}else{
				getDeleteNode(child,list);
			}
			if(CmCommonStringUtil.isPackage(child)){
				for(CmTreeNode brother:child.getListNode()){
					getDeleteNode(brother,list);
				}
			}
		}
	}
}
