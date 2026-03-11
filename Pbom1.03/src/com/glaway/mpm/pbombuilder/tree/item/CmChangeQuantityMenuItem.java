package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmPartMaster;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.CmChangeQuantityNewDialog;
import com.glaway.mpm.pbombuilder.tree.dialog.UpdateAssistModelDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;

/**
 * 修改零件数量
 * @author chenyunlong
 *
 */
public class CmChangeQuantityMenuItem extends CmMenuItem {

	 private static final long serialVersionUID = 4037054113973426246L;
	   private CmTree            tree;
	   private CmTreeNode        currNode;
	   private Window            owner;

	   public CmChangeQuantityMenuItem(CmTree tree, CmTreeNode currNode,Window owner,boolean canEdit) {
	      this.tree = tree;
	      this.currNode = currNode;
	      this.owner = owner;
	      setText("修改零件数量");
	      setEnabled(displayValidate(currNode));
	   }

	   private boolean displayValidate(CmTreeNode currNode) {
//	     Object userObject= currNode.getUserObject();
//	     if(userObject instanceof CmPartMaster){
//	        return true;
//	     }
	      return true;
	   }

	   @Override
	   protected void actionPerformed(ActionEvent evt) {

			TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
			if(paths.length==1){
				CmTreeNode node = (CmTreeNode) paths[0].getLastPathComponent();
				new CmChangeQuantityNewDialog(owner,tree,node).setVisible(true);
			}

//	       new CmChangeQuantityDialog(owner,tree,currNode).setVisible(true);
	   }

	}
