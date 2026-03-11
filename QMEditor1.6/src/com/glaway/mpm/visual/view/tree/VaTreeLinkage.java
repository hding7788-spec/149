/**
 * <br>Created on 2011-3-18
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.tree;

import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * JTree�������ӿ�
 * <br>Created on 2011-3-18
 * @author Alex.Huang - ����
 */
public interface VaTreeLinkage<T extends DefaultMutableTreeNode> {
   /**
    *当注册的CmTree中选择项改变时调用
    * @param nodes
    */
   void valueChange(List<T> nodes);
   
   void nodeChange(List<T> nodes);
   
   void modelNodeAdd(T node);
   
   void modelNodeRemove(T node);
}
