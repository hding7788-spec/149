package com.glaway.mpm.pbombuilder.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import wt.util.WTContext;
import com.glaway.mpm.pbombuilder.gui.CmTheme;
import com.glaway.mpm.pbombuilder.tree.CmCBTreeNode;
/**
 * 
 * @author chenyunlong
 *
 */
public class CmCBTreeRenderer extends JPanel implements TreeCellRenderer {
   private static final long serialVersionUID = 1L;

   protected JCheckBox       check;
   protected JLabel          label;

   public CmCBTreeRenderer() {
      setLayout(null);
      setOpaque(false);
      add(check = new JCheckBox());
      add(label = new JLabel());

      check.setOpaque(false);

      label.setForeground(Color.black);
      label.setOpaque(false);
   }

   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

      setEnabled(tree.isEnabled());
      CmCBTreeNode node = (CmCBTreeNode) value;

      check.setSelected(node.isSelected());
      check.setEnabled(true);

      String stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);
      label.setText(stringValue);

      if (sel) {
         label.setOpaque(true);
         label.setBackground(CmTheme.CM_TURQUOISE);
         label.setForeground(Color.WHITE);
      } else {
         label.setOpaque(false);
         label.setForeground(Color.BLACK);
      }

      if (node.getIcon() != null)
         label.setIcon(node.getIcon());
      else if (node.getImage() != null)
         label.setIcon(new ImageIcon(node.getImage()));
      else if (node.getIconPath() != null) {
         label.setIcon(new ImageIcon(WTContext.getContext().getImage(node.getIconPath())));
      }

      return this;
   }

   public Dimension getPreferredSize() {

      Dimension d_check = check.getPreferredSize();
      Dimension d_label = label.getPreferredSize();

      return new Dimension(d_check.width + d_label.width + 2, 16);

   }

   public void doLayout() {
      Dimension d_check = check.getPreferredSize();
      Dimension d_label = label.getPreferredSize();

      int y_check = 0;
      int y_label = 0;

      if (d_check.height > d_label.height)
         y_check = (d_label.height - d_check.height) / 2;

      check.setLocation(0, y_check);
      check.setBounds(0, y_check, d_check.width, d_check.height);

      label.setLocation(d_check.width, y_label);
      label.setBounds(d_check.width, y_label, d_label.width + 2, d_label.height);
   }
}
