package com.glaway.mpm.pbombuilder.data;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import com.glaway.mpm.pbombuilder.tree.CmCBTreeNode;

/**
 * <br>
 * Created on 2012-10-16
 * 
 * @author chenyunlong
 */
public class CmCBTreeMouseAdapter extends MouseAdapter {
	   JTree tree;

	   public CmCBTreeMouseAdapter(JTree tree) {
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
	            CmCBTreeNode node = (CmCBTreeNode) path.getLastPathComponent();
	            boolean isSelected = !node.isSelected();
	            node.setSelected(isSelected);
	            ((DefaultTreeModel) tree.getModel()).nodeChanged(node);

	            // 为了做连动节点，必须重画
	            tree.revalidate();
	            tree.repaint();
	         }
	      }
	   }
	}
