package com.glaway.mpm.visual.view.tree;

import java.io.Serializable;

import com.glaway.mpm.visual.bean.VaLightPart;

public class VaNode implements Serializable {
   public static String      XMLTITLE         = "VaNode";
   private static final long serialVersionUID = 1521289210647934655L;

   private VaLightPart       part;

   public VaNode(VaLightPart part) {
      this.part = part;
   }

   public VaLightPart getPart() {
      return part;
   }

   public void setPart(VaLightPart part) {
      this.part = part;
   }

   @Override
   public int hashCode() {
      final int PRIME = 31;
      int result = 1;
      result = PRIME * result + ((part == null) ? 0 : part.hashCode());
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
      final VaNode other = (VaNode) obj;
      if (part == null) {
         if (other.part != null)
            return false;
      } else if (!part.equals(other.part))
         return false;
      return true;
   }

   public String toString() {
      return part == null ? VaLightPart.EMPTY_PART.toString() : part.toString();
   }
}
