package com.glaway.mpm.pbombuilder.data;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.glaway.mpm.pbombuilder.tree.CmLightType;

public class CmLightTypeListCellRenderer extends JLabel implements ListCellRenderer {
   private static final long serialVersionUID = 7747972354490500793L;

   public CmLightTypeListCellRenderer() {
      setOpaque(true);
   }

   public Component getListCellRendererComponent(JList list, Object value, int row, boolean isSelected, boolean flag1) {
      if (value == null) {
         setText(null);
         setIcon(null);
      } else if (value instanceof CmLightType) {
         CmLightType lightType = (CmLightType) value;
         setText(lightType.getDisplayName());
         if (lightType.getIconImage() != null)
            setIcon(new ImageIcon(lightType.getIconImage()));
      }

      setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
      setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
      return this;
   }
}
