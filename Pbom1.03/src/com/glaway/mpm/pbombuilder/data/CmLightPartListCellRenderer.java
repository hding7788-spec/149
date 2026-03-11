package com.glaway.mpm.pbombuilder.data;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmLightType;
import com.glaway.mpm.pbombuilder.util.CmTypeHelper;

public class CmLightPartListCellRenderer extends JLabel implements ListCellRenderer {
   private static final long serialVersionUID = 7747972354490500793L;

   public CmLightPartListCellRenderer() {
      setOpaque(true);
   }

   public Component getListCellRendererComponent(JList list, Object value, int row, boolean isSelected, boolean flag1) {      
      if (value == null) {
         setText(null);
         setIcon(null);
      } else if (value instanceof CmLightPart) {
         CmLightPart lightPart = (CmLightPart) value;
         setText(lightPart.getPartNumber() + " - " + lightPart.getPartName() + " " + lightPart.getVersion());                  
         CmLightType lightType = CmTypeHelper.getLightType(lightPart.getPartType(), false);
         if (lightType.getIconImage() != null)
            setIcon(new ImageIcon(lightType.getIconImage()));
      }

      setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
      setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
      return this;
   }
}
