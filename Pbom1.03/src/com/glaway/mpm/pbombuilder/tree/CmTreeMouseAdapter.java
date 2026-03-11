package com.glaway.mpm.pbombuilder.tree;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;


/**
 * <br>
 * Created on 2012-10-23
 * 
 * @author chenyunlong
 */
public class CmTreeMouseAdapter extends MouseAdapter {
   private JTree tree;

   public CmTreeMouseAdapter(JTree tree) {
      this.tree = tree;
   }

   public void mouseClicked(MouseEvent e) {
      int x = e.getX();
      int y = e.getY();
      int row = tree.getRowForLocation(x, y);
      Rectangle rect = tree.getRowBounds(row);
      int height = 0;

      if (rect != null)
         height = rect.height + rect.x;

      if (x < height) {
         TreePath path = tree.getPathForRow(row);
         if (path != null) {
            CmTreeNode node = (CmTreeNode) path.getLastPathComponent();
            boolean isSelected = !node.isSelected();
            node.setSelected(isSelected);

            ((DefaultTreeModel) tree.getModel()).nodeChanged(node);
            // Dennis 为了做连动节点，必须重画
            tree.revalidate();
            tree.repaint();
         }
      }
   }
}
