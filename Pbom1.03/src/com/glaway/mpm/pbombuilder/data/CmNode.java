package com.glaway.mpm.pbombuilder.data;

import java.io.Serializable;
import javax.swing.tree.DefaultMutableTreeNode;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;

/**
 * 
 * <br>Created on 2012-10-23
 * @author chenyunlong
 */
public class CmNode extends DefaultMutableTreeNode implements Serializable {
	 public static String      XMLTITLE         = "CmNode";
	   private static final long serialVersionUID = 1521289210647934655L;

	   private CmLightPart       part;

	   public CmNode(CmLightPart part) {
	      this.part = part;
	   }

	   public CmLightPart getPart() {
	      return part;
	   }

	   public void setPart(CmLightPart part) {
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
	      final CmNode other = (CmNode) obj;
	      if (part == null) {
	         if (other.part != null)
	            return false;
	      } else if (!part.equals(other.part))
	         return false;
	      return true;
	   }

	   public String toString() {
	      return part == null ? CmLightPart.EMPTY_PART.toString() : part.toString();
	   }
}
