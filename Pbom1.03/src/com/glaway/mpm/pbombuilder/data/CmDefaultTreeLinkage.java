package com.glaway.mpm.pbombuilder.data;

import java.util.Comparator;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import com.glaway.mpm.pbombuilder.tree.CmAbstractTreeLinkage;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;

/**
 * <br>Created on 2012-10-18
 * @author chenyunlong
 */
public class CmDefaultTreeLinkage extends CmAbstractTreeLinkage<CmTreeNode> {
   public CmDefaultTreeLinkage(Comparator comtor) {
      super(comtor);
   }

   @Override
   public void doLinkage(JTree tree, List<CmTreeNode> nodes) {
      TreePath[] paths = new TreePath[nodes.size()];
      int i = 0;
      for (CmTreeNode n : nodes) {
         paths[i] = new TreePath(n.getPath());
         i++;
      }
      tree.setSelectionPaths(paths);
      if (paths.length > 0)
         tree.scrollPathToVisible(paths[paths.length - 1]);

   }

   public void setComparator(Comparator comtor) {
      super.comtor = comtor;
   }
}
