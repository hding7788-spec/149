/**
 * SVN Id: $Id: PVSelectionObserver.java 1689 2010-04-06 10:25:01Z mlangen $ SVN Date: $Date: 2010-04-06 12:25:01 +0200 (Tue, 06 Apr 2010) $ SVN Revision: $Revision: 1689 $ SVN Author: $Author: mlangen $
 * SVN URL: $HeadURL: http://autoutil.ptcnet.ptc.com/eecRepos/Projects/ContextBuilder/tags/Minor_201007151726/overwrite/src/ext/cobi/pview/PVSelectionObserver.java $
 * 
 * bcwti
 * 
 * Copyright (c) 1998-2008 Parametric Technology. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Parametric
 * Technology. You shall not disclose such confidential information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Parametric Technology.
 * 
 * ecwti
 */

package com.glaway.mpm.pbombuilder.action;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.data.CmDefaultTreeLinkage;
import com.glaway.mpm.pbombuilder.data.CmTreeLinkage;
import com.glaway.mpm.pbombuilder.data.CmTreeNodeComparator;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.ptc.pview.pvapi.SelectionObserver;
import com.ptc.pview.pvkapp.Instance;


public class CmMPVSelectionObserver extends SelectionObserver {
   private static final CmLogger log = CmLogger.getLogger(CmMPVSelectionObserver.class);
   private CmTree                tree;

   public CmMPVSelectionObserver(CmTree theCurrentTree) {
      tree = theCurrentTree;
   }

   protected void OnBeginUpdate() {}

   protected void OnEndUpdate() {}

   protected void OnInsertItems(Instance[] items, long recurseMask) {
      TreePath[] theTreePaths = new TreePath[items.length];
      if (tree != null && tree.getRoot().getChildCount() > 0) {
         doJob(tree, theTreePaths, items);
      }
   }

   protected void OnRemoveItems(Instance[] items, long recurseMask) {
   //System.out.println("PVSelectionObserver.OnRemoveItems()");
   }

   protected void OnClearSelection() {
   //ICBMainFrame.getMainFrame().getICBTree().setSelectionPath(null);
   }

   private void doJob(CmTree a_tree, TreePath[] theTreePaths, Instance[] items) {
      List<CmTreeNode> nodeFromPrvList = new ArrayList<CmTreeNode>();//存放选中的可视化信息部件

      if (theTreePaths != null) {
         CmTreeNode theNode = null;
         for (int i = 0; i < items.length; i++) {
            Instance theInstance = items[i];
            theNode = a_tree.getNodeFromInstance(theInstance);
            if(!"PBOM".equals(theNode.toString())&& theNode != null && null==theNode.getParent()){
            	theNode=a_tree.getNodeFromTreeWith(a_tree.getRoot(), theNode);
            	theNode.setIspackage(false);
            }

            if (theNode != null) {
               theNode.setCreoClick(false);
               nodeFromPrvList.add(theNode);
               theTreePaths[i] = new TreePath(theNode.getPath());
            }
         }
         log.debug(nodeFromPrvList.size());
         //向其它CmTree 发送选择更改消息
         CmTree etree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
         CmTree mtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();

         Comparator<CmTreeNode> comtor = new CmTreeNodeComparator();
         CmTreeLinkage linkage = new CmDefaultTreeLinkage(comtor).addLinkageTree(etree).addLinkageTree(mtree);
         linkage.valueChange(nodeFromPrvList);
      }
   }

   public String GetObjectClass() {
      return "ext::ideal::samc::jws::pview::CmMPVSelectionObserver";
   }
}
