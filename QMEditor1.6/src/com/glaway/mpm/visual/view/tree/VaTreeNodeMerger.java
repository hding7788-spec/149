/**
 * <br>Created on 2011-3-16
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.tree;

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
public abstract class VaTreeNodeMerger<T> implements VaMerger {
   /**
    *当前操作的树
    */
   protected VaTree     tree;
   /**
    *新节点要插入的父节点
    */
   protected VaTreeNode parent;

   public VaTreeNodeMerger(VaTree tree, VaTreeNode parentNode) {
      this.tree = tree;
      this.parent = parentNode;
   }

   public abstract void doMerger(List<T> list);

   public VaTreeNode getParent() {
      return parent;
   }

   public void setParent(VaTreeNode parent) {
      this.parent = parent;
   }

   public VaTree getTree() {
      return tree;
   }

   public void setTree(VaTree tree) {
      this.tree = tree;
   }

}
