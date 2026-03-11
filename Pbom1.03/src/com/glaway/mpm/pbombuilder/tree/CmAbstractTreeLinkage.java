package com.glaway.mpm.pbombuilder.tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.glaway.mpm.pbombuilder.data.CmTreeLinkage;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

/**
 * 根据提供的java.util.Comparator实现类 在valueChange方法中判断传入的节点与CmTree中的节点是否相同，判断完成后将所有相同节点的集合发送给doLinkage方法。
 * 使用本类，您可能需要一个继承于本类的实例类来实现本类的doLinkage方法。
 * 另外，如果你想按照你自己想法判断两个实例是否相同，你需要一个实现了java.util.Comparator接口的实例，这里把它叫做【比较方案类】
 * 如果，你没有提供这样的一个类，将会使用本类中提供的默认比较方案类来作比较，尽管那样并不太合适
 * @param comtor
 *
 * <br>Created on 2013-3-18
 * @author chenyunlong
 */
public abstract class CmAbstractTreeLinkage<T extends DefaultMutableTreeNode> implements CmTreeLinkage<T> {
   //protected Map<String, JTree> trees  = new HashMap<String, JTree>();
   protected List<JTree> trees  = new ArrayList<JTree>();
   protected Comparator  comtor = new DefaultComparator();
   private CmTreeNode linkNode =null;

   public CmAbstractTreeLinkage(Comparator comtor) {
      this.comtor = comtor;
   }

   public void valueChange(List<T> nodes) {
	   if(null != nodes && nodes.size()>0){
		   reLinkNodes((CmTreeNode)nodes.get(nodes.size()-1),(CmTree)trees.get(0));
	   }
      linkageBefore(nodes);

      Iterator<JTree> it = trees.iterator();
      while (it.hasNext()) {
         JTree tree = it.next();
         List<T> selectNodes = new ArrayList<T>();
         for (T node : nodes) {
            comparat(tree, node,selectNodes);
         }
         doLinkage(tree, selectNodes);
      }
      linkageAfter(nodes);

   }

   protected void comparat(JTree tree, T node,List<T> selectNodes) {
      T root = (T) tree.getModel().getRoot();
      Enumeration<T> children = root.breadthFirstEnumeration();
      while (children.hasMoreElements()) {
         T treeNode = children.nextElement();
         if (this.comtor.compare(node, treeNode) == 0) {
            selectNodes.add(treeNode);
         }
      }
   }

   protected void linkageBefore(List<T> nodes) {

   }

   protected void linkageAfter(List<T> nodes) {

   }

   /**
    *匹配成功后执行的操作
    * @param tree
    * @param node
    */
   public abstract void doLinkage(JTree tree, List<T> node);

   /**
    *添加关联树
    * @param tree
    * @param node
    */
   public CmAbstractTreeLinkage addLinkageTree(CmTree tree) {
      trees.add(tree);
      return this;
   }

   /**
    *删除关联树
    * @param key
    * @param tree
    */
   public CmAbstractTreeLinkage delLinkageTree(String key) {
      trees.remove(key);
      return this;
   }

   class DefaultComparator implements Comparator<CmTreeNode> {
      public int compare(CmTreeNode o1, CmTreeNode o2) {
         if (CmCommonNodeUtil.isEqualOfOccpath(o1, o2)) {
            return 0;
         }
         return -1;
      }
   }

   public void rePackageNode(CmTreeNode linkNode,CmTreeNode packageNode,CmTree tree,CmTreeNode parentNode){
	   List<CmTreeNode> list = new ArrayList<CmTreeNode>();
	   for(CmTreeNode brother:packageNode.getListNode()){
		   if(!CmCommonNodeUtil.isEqualOfOccpath(linkNode, brother)){
			   list.add(brother);
		   }
	   }
	   packageNode.setListNode(null);
	   packageNode.removeFromParent();
	   list.add(packageNode);
	   linkNode.setListNode(list);
	   parentNode.add(linkNode);
	   CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
	   tree.updateUI();
   }

   public void reLinkNodes(CmTreeNode objNode,CmTree tree){
	   CmTreeNode root = (CmTreeNode) tree.getModel().getRoot();
	   Enumeration<CmTreeNode> children = root.breadthFirstEnumeration();
	      while (children.hasMoreElements()) {
	    	  CmTreeNode treeNode = children.nextElement();
	    	  if(this.comtor.compare(objNode, treeNode) == 0){
	    		  break;
	    	  }else if(CmCommonStringUtil.isCommon(objNode, treeNode) && CmCommonStringUtil.isPackage(treeNode)){
	    		  for(CmTreeNode brother:treeNode.getListNode()){
	    			  if(this.comtor.compare(objNode, brother) == 0){
	    				  rePackageNode(brother,treeNode,tree,(CmTreeNode)treeNode.getParent());
	    				  break;
	    			  }
	    		  }
	    	  }
	      }
   }
	public CmTreeNode findLinkNode(CmTreeNode node, CmTree tree) {
		CmTreeNode root = (CmTreeNode) tree.getModel().getRoot();
		@SuppressWarnings("unchecked")
		Enumeration<CmTreeNode> children = root.breadthFirstEnumeration();
		while (children.hasMoreElements()) {
			CmTreeNode treeNode = children.nextElement();
			if (this.comtor.compare(node, treeNode) == 0) {
				return treeNode;
			} else if (CmCommonNodeUtil.checkNodeIsCommon(node, treeNode) && CmCommonStringUtil.isPackage(treeNode)) {
				for (CmTreeNode bother : treeNode.getListNode()) {
					if (comtor.compare(node, bother) == 0)
						return bother;
				}
			}
		}
		return null;
	}
}
