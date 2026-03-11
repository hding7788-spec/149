/**
 * <br>Created on 2011-3-16
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.pbombuilder.tree;

import java.util.List;

//import ext.test.jws.gui.T;

/**
 * 本类天生就是用来把数据处理成CmTreeNode与并CmTree形成关联的
 * 当本类与CmNewObjectBox一起使用时，CmNewObjectBox会把数据以集合的形式
 * 传递给本类的doMerger方法
 * 关联：CmNewObjectBox CmAbstractNewObjectDialog
 * <br>Created on 2011-3-16
 * @author Alex.Huang - 黄勇
 */
public abstract class CmTreeNodeMerger<T> implements CmMerger {
   /**
    *当前操作的树
    */
   protected CmTree     tree;
   /**
    *新节点要插入的父节点
    */
   protected CmTreeNode parent;

   public CmTreeNodeMerger(CmTree tree, CmTreeNode parentNode) {
      this.tree = tree;
      this.parent = parentNode;
   }

   public abstract void doMerger(List<T> list);

   public CmTreeNode getParent() {
      return parent;
   }

   public void setParent(CmTreeNode parent) {
      this.parent = parent;
   }

   public CmTree getTree() {
      return tree;
   }

   public void setTree(CmTree tree) {
      this.tree = tree;
   }

}
