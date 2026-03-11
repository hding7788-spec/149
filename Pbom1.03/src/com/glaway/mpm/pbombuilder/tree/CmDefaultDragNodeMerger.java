package com.glaway.mpm.pbombuilder.tree;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.action.CmCommonPasteAction;
import com.glaway.mpm.pbombuilder.data.CmTreeNodeAttributProxy;
import com.glaway.mpm.pbombuilder.data.CmXmlDataProxy;
import com.glaway.mpm.pbombuilder.jws.CmContext;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeCancelAction;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

/**
 * <br>Created on 2013-1-17
 * @author chenyunlong
 */
public class CmDefaultDragNodeMerger extends CmTreeNodeMerger<CmTreeNode> {
	   private StringBuffer errorBuf = null;
	   private StringBuffer errorPlanning = null;
	   private CmLogger log=CmLogger.getLogger();
	   public CmDefaultDragNodeMerger(CmTree tree, CmTreeNode parentNode, Window owner) {
	      super(tree, parentNode);
	   }
	   @Override
	   public void doMerger(List<CmTreeNode> nodes) {
	      nodes = filterNodeWithParent(nodes);
	      errorBuf = new StringBuffer(1024);
	      errorPlanning  = new StringBuffer(1024);
	      List<CmCancelNode> list = new ArrayList<CmCancelNode>();
	      CmXmlDataProxy proxy = CmXmlDataProxy.getCmXmlDataProxy();
		  CmTreeNodeAttributProxy attribut = (CmTreeNodeAttributProxy) proxy.getProxy(CmXmlDataProxy.MBOM_ATTRIBUTE_PROXY);
		  boolean isPbom = false;
	      for (CmTreeNode node : nodes) {
	        // log.debug(node+" 路径:"+node.getPathFromCI());
	    	 boolean isonly= nodes.size() == 1;
			 isPbom = CmCommonStringUtil.checkisPbomNode(node);
			 CmTreeNode newparent = (CmTreeNode) node.getParent();
	         CmTreeNode nodeToAdd = addToChild(tree, parent, node,newparent,isPbom,isonly,attribut);

	         if (nodeToAdd != null && nodeToAdd.getChildCount() > 0){
	        	 tree.expandPath(new TreePath(nodeToAdd.getPath()));
	         }
	         else {
	            tree.expandPath(new TreePath(parent.getPath()));
	         }
	         if(nodeToAdd != null && nodes.size()>1){
	        	 list.add(CmCommonStringUtil.createNewCancelNode(nodeToAdd,newparent,!isPbom,null));//节点的回退
	         }
	      }
	      CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
	      tree.updateUI();
	      BomTreeReportAction.updateBomReport();
	      if (errorBuf.toString().length() > 0) {
	    	  errorBuf.insert(0, "下列"+CmCommonStringUtil.getRootNode(nodes.get(0)).toString()+"树上节点：\n");
	    	  errorBuf.append("\n在PBOM树上已经存在!");
	    	  if(errorPlanning.toString().length()>0){
	    		  errorPlanning.insert(0, "下列"+CmCommonStringUtil.getRootNode(nodes.get(0)).toString()+"树上节点：\n");
	    		  errorPlanning.append("\n创建Planning视图失败，请联系管理员！");
	    		  errorBuf.append(errorPlanning);
	    	  }
	    	  JOptionPane.showMessageDialog(CmContext.getMainFrame(), errorBuf);
	      }
	      else if(errorPlanning.toString().length() > 0){
				errorPlanning.insert(0, "下列EBOM树上节点：\n");
				errorPlanning.append("\n创建Planning视图失败，请联系管理员！");
				JOptionPane.showMessageDialog(CmContext.getMainFrame(), errorPlanning.toString());
	      }
	      else if(null!=list && list.size()>0){
	    	  EbomTreeCancelAction.addPbomTreeChange(null,null, "create",list);
	    	  if(isPbom){
	    		  EbomTreeCancelAction.addPbomTreeChange(null,null, "move", list);
			  }else{
				EbomTreeCancelAction.addPbomTreeChange(null,null, "create", list);
			}
	      }
	   }

	   /**
	    * 避免嵌套
	    * @param nodeList
	    * @return
	    */
	   private List<CmTreeNode> filterNodeWithParent(List<CmTreeNode> nodeList) {
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

	   private CmTreeNode addToChild(CmTree tree, CmTreeNode parent, CmTreeNode node,CmTreeNode newparent,boolean isPbom,boolean isOnly,CmTreeNodeAttributProxy attribut) {
	      CmTreeNode ret = null;

	      if (node != null) {
	    	  if(isPbom){
	    		  //PBOM树上自己的拖拽
	    		  attribut.deleteAttributNode((CmTreeNode) node.getParent(), node);
	    		  node.removeFromParent();
	    		  CmCommonStringUtil.checkNodeIsChangeOfStructure(newparent, false);
	    		  ret = CmBizObjUtil.convertEBomNode2MBomNode(node,true);
	    		  node.removeFromParent();
		          if (parent != null){
		            	parent.add(ret);
		            	CmCommonStringUtil.checkNodeIsChangeOfStructure(parent, false);
		            	CmCommonStringUtil.getPbomPlanning(ret,null);
		            	CmCommonStringUtil.isMoved(ret);
		            	CmCommonStringUtil.addNodeToCheckList(ret);
		            	CmCommonStringUtil.updateNodeOperType(ret, "");//更新节点的操作状态
		            	if(isOnly){
		            		EbomTreeCancelAction.addPbomTreeChange(ret,newparent, "move",null);
		            	}
		          }
	    	  }
	    	  else{
	    		//EBOM向PBOM树上拖拽
	    		  if (!exists(tree, node)) {
	    			  ret = CmBizObjUtil.convertEBomNode2MBomNode(node,true);
	    			  CmCommonPasteAction common = new CmCommonPasteAction();
	    			  common.removeDeleteNode(ret);
	    			  CmTreeNode unplanningNode = null;
	    			  unplanningNode = CmCommonStringUtil.getPbomPlanning(ret,unplanningNode);
	    			  if(null!=unplanningNode){
	    				  log.debug("获取Planning视图失败");
	    				  //该节点获取planning视图失败
	    				  TreeNode[] path = node.getPath();
	    				  errorPlanning.append("EBOM");
		    			  for (int i = 1; i < path.length; i++) {
		    				  CmTreeNode pathNode = (CmTreeNode) path[i];
		    				  errorPlanning.append('/').append(pathNode.getPart().getPartNumber());
		    			  }
		    			  errorPlanning.append("\n");
	    			  }
	    			  else if (parent != null){
	    				  log.debug("获取Planning视图成功");
	    				  parent.add(ret);
	    				  CmCommonStringUtil.isMoved(ret);
	    				  CmCommonStringUtil.addNodeToCheckList(ret);
	    				  CmCommonStringUtil.updateNodeOperType(ret, "new");//更新节点的操作状态
	    				  CmCommonStringUtil.checkNodeIsChangeOfStructure(parent, false);
	    				  if(isOnly){
							EbomTreeCancelAction.addPbomTreeChange(ret,null, "create",null);
						 }
	    			  }
	    		  } 
	    		  else {
	    			  // 统计错误，反馈给客户
	    			  CmTreeNode existNode= getExitsNode(tree,node);
	    			  TreeNode[] path = existNode.getPath();
	    			  errorBuf.append("EBOM");
	    			  for (int i = 1; i < path.length; i++) {
	    				  CmTreeNode pathNode = (CmTreeNode) path[i];
	    				  errorBuf.append('/').append(pathNode.getPart().getPartNumber());
	    			  }
	    			  errorBuf.append("\n");
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
	         }
	         else if(CmCommonStringUtil.isPackage(treeNode)){
				for(CmTreeNode brother:treeNode.getListNode()){
					if(nodeOccIdList.contains(brother.getOccpath())){
						ret = true;
						break;
					}
				}
			}
			if(ret){
				break;
			}
	      }

	      return ret;
	   }
	   
	   public CmTreeNode getExitsNode(CmTree tree, CmTreeNode node){
		   List<String> nodeOccIdList = getAllNodesOccIdList(node);
		   Enumeration children = tree.getRoot().breadthFirstEnumeration();
		      while (children.hasMoreElements()) {
		         CmTreeNode child = (CmTreeNode) children.nextElement();
		         if (nodeOccIdList.contains(child.getOccpath())) { // 实例信息相同，则认为是已经存在
		            return child;
		         }
		         else if(CmCommonStringUtil.isPackage(child)){
					for(CmTreeNode brother:child.getListNode()){
						if(nodeOccIdList.contains(brother.getOccpath())){
							return child;
						}
					}
				}
		      }
		   return null;
	   }
	   
	   public String getExistsNodeOfOccid(CmTree tree, CmTreeNode node){
		   List<String> nodeOccIdList = getAllNodesOccIdList(node);
		      Enumeration en = tree.getRoot().breadthFirstEnumeration();
		      while (en.hasMoreElements()) {
		         CmTreeNode treeNode = (CmTreeNode) en.nextElement();
		         if (nodeOccIdList.contains(treeNode.getOccpath())) { // 实例信息相同，则认为是已经存在
		            return treeNode.getOccId();
		         }
		         else if(CmCommonStringUtil.isPackage(treeNode)){
					for(CmTreeNode brother:treeNode.getListNode()){
						if(nodeOccIdList.contains(brother.getOccpath())){
							return brother.getOccId();
						}
					}
				}
		      }
		      return null;
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

	}
