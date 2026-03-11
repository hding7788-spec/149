package com.glaway.mpm.pbombuilder.data;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 
 * <br>Created on 2011-12-5
 * @author chenyunlong
 */
public interface CmTreeLinkage<T extends DefaultMutableTreeNode> {
   /**
    *当注册的CmTree中选择项改变时调用
    * @param nodes
    */
   void valueChange(List<T> nodes);
}
