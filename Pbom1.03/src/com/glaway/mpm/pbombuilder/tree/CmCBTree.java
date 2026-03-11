package com.glaway.mpm.pbombuilder.tree;

import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;



/**
 * <br>Created on 2012-10-16
 * @author chenyunlong
 */
public class CmCBTree extends JTree implements TreeSelectionListener {
   private static final long serialVersionUID = -668946784633663171L;

   public CmCBTree() {
	      super(new DefaultTreeModel(new CmCBTreeNode("Select CI"){
	         private static final long serialVersionUID = 0L;

	         @Override
	         public Icon getIcon() {
	            return new ImageIcon("D:\\codebase_back\\codebase\\ext\\ideal\\samc\\images\\cart.gif");
	         }

	         @Override
	         public String getIconPath() {
	            return null;
	         }

	         @Override
	         public Image getImage() {
	            return null;
	         }}));  
	   
	      setOpaque(false);
	      setRowHeight(16);
	      setCellRenderer(new CmTreeRenderer());
	      getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	   }

	   public void valueChanged(TreeSelectionEvent arg0) {}

	   /**
	    * 重写方法，始终显示节点句柄
	    * @return
	    */
	   public boolean getShowsRootHandles() {
	      return true;
	   }
	}
