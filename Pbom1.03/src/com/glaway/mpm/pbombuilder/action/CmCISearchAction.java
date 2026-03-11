package com.glaway.mpm.pbombuilder.action;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.glaway.mpm.pbombuilder.bom.CmCIMerger;
import com.glaway.mpm.pbombuilder.panel.CmCISearchDialog;
import com.glaway.mpm.pbombuilder.panel.CmCollectBox;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.CmTreeNodeMerger;
import com.glaway.mpm.pbombuilder.util.CmUtil;

/**
 * <br>Created on 2012-10-18
 * @author chenyunlong
 */
public class CmCISearchAction extends CmAction {
   private static final long serialVersionUID = 7898274664858654232L;
   private ImageIcon         searchImage      = new ImageIcon(CmUtil.getImageFromServer("search.png"));

   private CmTree            tree;
   private CmTreeNode        node;
   private Window            owner;

   public CmCISearchAction(CmTree tree, CmTreeNode node, Window owner) {
      setIcon(searchImage);
      setToolTipText("搜索");
      this.tree=tree;
      this.node=node;
      this.owner=owner;
   }

   @Override
   public void actionPerformed(ActionEvent evt) {

      CmCISearchDialog dialog = new CmCISearchDialog(owner);
      CmTreeNodeMerger merger = new CmCIMerger(tree, node, owner);
      CmCollectBox box = new CmCollectBox(dialog, merger);
      box.doWork();
   }

}
