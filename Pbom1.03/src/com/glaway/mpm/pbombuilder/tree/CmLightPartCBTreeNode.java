package com.glaway.mpm.pbombuilder.tree;

import java.awt.Image;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import com.glaway.mpm.pbombuilder.util.CmTypeHelper;
import com.glaway.mpm.pbombuilder.util.CmUtil;

public class CmLightPartCBTreeNode extends CmCBTreeNode implements Serializable {
   private static final long serialVersionUID = 7394412243793883583L;

   private CmLightPart       lightPart;
   private transient Icon    icon;

   public CmLightPartCBTreeNode(CmLightPart lightPart) {
      super(lightPart);
      this.lightPart = lightPart;
   }

   public CmLightPart getLightPart() {
      return lightPart;
   }

   @Override
   public Icon getIcon() {
      if (icon == null) {
         if (lightPart != null && !CmUtil.isEmpty(lightPart.getPartType())) {
            Image image = CmTypeHelper.getLightType(lightPart.getPartType(), false).getIconImage();
            if (image != null)
               icon = new ImageIcon(image);
         }
      }
      return icon;
   }

   @Override
   public String getIconPath() {
      return null;
   }

   @Override
   public Image getImage() {
      return null;
   }

}
