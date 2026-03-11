package com.glaway.mpm.pbombuilder.tree;

import java.awt.Image;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * <br>Created on 2012-10-16
 * @author Alex.Huang
 */
public abstract class CmCBTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;
	public final static int SINGLE_SELECTION = 0;
   public final static int DIG_IN_SELECTION = 4;

   protected int           selectionMode;
   protected boolean       isSelected;

   public CmCBTreeNode() {
      super("");
   }

   public CmCBTreeNode(Object userObject, boolean allowsChildren, boolean isSelected) {
      super(userObject, allowsChildren);
      this.isSelected = isSelected;
      setSelectionMode(DIG_IN_SELECTION);
   }

   public CmCBTreeNode(Object userObject) {
      this(userObject, true, false);
   }

   @Override
   public boolean getAllowsChildren() {
      if (isRoot()) {
         return false;
      } else {
         return true;
      }
   }

   public boolean isSelected() {
      return isSelected;
   }

   public void setSelected(boolean isSelected) {
      setSelectedWithoutPropagate(isSelected);

      Enumeration allChilds = breadthFirstEnumeration();
      while (allChilds.hasMoreElements()) {
         CmCBTreeNode child = (CmCBTreeNode) allChilds.nextElement();
         child.setSelectedWithoutPropagate(isSelected);
      }

      if (isSelected) {
         CmCBTreeNode father = (CmCBTreeNode) getParent();
         while (father != null && !father.isSelected()) {
            father.setSelectedWithoutPropagate(true);
            father = (CmCBTreeNode) father.getParent();
         }
      } else {
         // ��������ֵܽڵ�δѡ�У�����Ҫȡ��ѡ�и��ڵ��
         unselectFatherIfNoSelectedSiblings(this);
      }
   }

   private void unselectFatherIfNoSelectedSiblings(CmCBTreeNode node) {
      CmCBTreeNode siblNode = (CmCBTreeNode) node.getPreviousSibling();
      while (siblNode != null && !siblNode.isSelected()) {
         siblNode = (CmCBTreeNode) siblNode.getPreviousSibling();
      }
      if (siblNode == null) {// no selected sibl node found!
         siblNode = (CmCBTreeNode) node.getNextSibling();
         while (siblNode != null && !siblNode.isSelected()) {
            siblNode = (CmCBTreeNode) siblNode.getNextSibling();
         }
      }
      if (siblNode == null) {// no selected sibl node found!
         CmCBTreeNode father = (CmCBTreeNode) node.getParent();
         if (father != null) {
            father.setSelectedWithoutPropagate(false);
            unselectFatherIfNoSelectedSiblings(father);
         }
      }
   }

   private void setSelectedWithoutPropagate(boolean isSelected) {
      this.isSelected = isSelected;
   }

   public int getSelectionMode() {
      return selectionMode;
   }

   public void setSelectionMode(int selectionMode) {
      this.selectionMode = selectionMode;
   }

   public abstract String getIconPath();

   public abstract Image getImage();

   public abstract Icon getIcon();
}
