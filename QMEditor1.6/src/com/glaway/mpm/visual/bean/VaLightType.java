package com.glaway.mpm.visual.bean;

import java.awt.Image;
import java.io.Serializable;

import wt.util.WTContext;

import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;

public class VaLightType implements Serializable {
   private static final long        serialVersionUID = 623514822050075230L;

   private String                   extType;
   private String                   displayName;
   private String                   iconPath;
   private transient Image          iconImage        = null;
   private transient TypeIdentifier ti               = null;

   public VaLightType(String extType, String displayName, String iconPath) {
      this.extType = extType;
      this.displayName = displayName;
      this.iconPath = iconPath;
   }

   public boolean isA(VaLightType type) {
      if (type == null || type.getTypeIdentifier() == null)
         return false;

      return isA(type.getTypeIdentifier());
   }

   public boolean isA(TypeIdentifier ti) {
      if (getTypeIdentifier() == null || ti == null)
         return false;

      return TypeHelper.isA(getTypeIdentifier(), ti);
   }

   public String getDisplayName() {
      return displayName;
   }

   public String getExtType() {
      return extType;
   }

   public Image getIconImage() {
      if (iconImage == null && iconPath != null)
         iconImage = WTContext.getContext().getImage(iconPath);
      return iconImage;
   }

   public TypeIdentifier getTypeIdentifier() {
      if (ti == null && extType != null)
         ti = TypeHelper.getTypeIdentifier(extType);

      return ti;
   }

   public String getIconPath() {
      return iconPath;
   }

   public void setIcon(String icon) {
      iconImage = null;
      this.iconPath = icon;
   }

   public void setTypeIdentifier(TypeIdentifier ti) {
      this.ti = ti;
   }

   @Override
   public int hashCode() {
      final int PRIME = 31;
      int result = 1;
      result = PRIME * result + ((displayName == null) ? 0 : displayName.hashCode());
      result = PRIME * result + ((extType == null) ? 0 : extType.hashCode());
      result = PRIME * result + ((iconPath == null) ? 0 : iconPath.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final VaLightType other = (VaLightType) obj;
      if (displayName == null) {
         if (other.displayName != null)
            return false;
      } else if (!displayName.equals(other.displayName))
         return false;
      if (extType == null) {
         if (other.extType != null)
            return false;
      } else if (!extType.equals(other.extType))
         return false;
      if (iconPath == null) {
         if (other.iconPath != null)
            return false;
      } else if (!iconPath.equals(other.iconPath))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return this.extType;
   }
}
