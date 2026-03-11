/**
 * <br>Created on 2011-3-15
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.qmIntf.decoratePView.showPanel.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.media.j3d.BoundingBox;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.vecmath.Point3d;

import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.tree.VaTreeLinkage;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

public class DpTreeSelectionListener implements TreeSelectionListener {
   private VaTreeLinkage linkage;
   
   private static final VaLogger    log          = VaLogger.getLogger(DpTreeSelectionListener.class);

   public DpTreeSelectionListener(VaTreeLinkage linkage) {
      this.linkage = linkage;
   }

   public DpTreeSelectionListener() {

   }

   public void valueChanged(TreeSelectionEvent e) {
	  log.debug("TreeSelectionEvent...");
      TreePath[] theTreePaths = e.getPaths();
      ArrayList<VaTreeNode> theSelectedNodes = new ArrayList<VaTreeNode>();
      //Treat the removed elements first
      for (int index = 0; index < theTreePaths.length; index++) {
         TreePath theCurrentTreePath = theTreePaths[index];
         VaTreeNode theNode = (VaTreeNode) theCurrentTreePath.getLastPathComponent();
         if (!e.isAddedPath(index))
            setSelectionChangedRecursively(theNode, false);
         else
            theSelectedNodes.add(theNode);
      }

      //Treat the new selection
      Iterator theSelectedNodesIt = theSelectedNodes.iterator();
      while (theSelectedNodesIt.hasNext()) {
         setSelectionChangedRecursively((VaTreeNode) theSelectedNodesIt.next(), true);
      }

      ArrayList<VaTreeNode> theSelectedNodeList = new ArrayList<VaTreeNode>();
      JTree tree = (JTree) e.getSource();
      TreePath[] selectionPaths = tree.getSelectionPaths();
      if (selectionPaths == null)
         return;

    
      for (int index = 0; index < selectionPaths.length; index++) {
         VaTreeNode node = (VaTreeNode) selectionPaths[index].getLastPathComponent();
         //if (node.isLeaf())
            theSelectedNodeList.add(node);
      }
      if (linkage != null)
         linkage.valueChange(theSelectedNodeList);
   }

   private void setSelectionChangedRecursively(VaTreeNode node, boolean selectionMode) {
	   log.debug("setSelectionChangedRecursively...");
      if (node.isLeaf()) {
         try {
            if (!(((VaTreeNode) node).get_pviewShapeInstance() == null))
               ((VaTreeNode) node).get_pviewShapeInstance().SetHighlight(selectionMode);
            
            if(selectionMode){
            	Vector bboxs = node.getBboxes();
            }
         } catch (Exception e1) {
            e1.printStackTrace();
         }
      }
   }
}
