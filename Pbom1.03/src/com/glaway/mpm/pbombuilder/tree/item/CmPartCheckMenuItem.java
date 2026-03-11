package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import javax.swing.tree.TreePath;
import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;

/**
 * 零件核查
 * <br>Created on 2012-10-29
 * @author chenyunlong
 * 
 */
public class CmPartCheckMenuItem extends CmMenuItem {
	 private static final long serialVersionUID = 8913174253192296965L;
	   private CmTree            tree;
	   private CmTreeNode        currNode;
	   public CmPartCheckMenuItem(CmTree tree, CmTreeNode currNode){
	      this.tree = tree;
	      this.currNode = currNode;
	      setText("零件核查");
	      setIconStr("details.gif");
	   }
	   @Override
	   protected void actionPerformed(ActionEvent evt) {
	      List<CmTreeNode> list=new Vector<CmTreeNode>(); 
	      CmLightPart currPart=currNode.getPart();
	      if(currPart==null)return;
	      Enumeration<CmTreeNode> children=tree.getRoot().breadthFirstEnumeration();
	      while(children.hasMoreElements()){
	         CmTreeNode child=children.nextElement();
	         CmLightPart childPart=child.getPart();
	         if(childPart==null)continue;
	         if(childPart.getPartNumber().equals(currPart.getPartNumber())){
	            list.add(child);
	         }
	      }
	      TreePath[] paths = new TreePath[list.size()];
	      int i = 0;
	      for (CmTreeNode n : list) {
	         paths[i] = new TreePath(n.getPath());
	         i++;
	      }
	      tree.setSelectionPaths(paths);
	   }
	   

	}
