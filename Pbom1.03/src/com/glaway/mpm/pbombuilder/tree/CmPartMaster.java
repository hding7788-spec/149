package com.glaway.mpm.pbombuilder.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.glaway.mpm.pbombuilder.data.CmNode;
import com.glaway.mpm.pbombuilder.data.CmPartInstance;

public class CmPartMaster extends CmNode implements Serializable {
   private static final long    serialVersionUID = 6215166736192455919L;

   private int                  quantity;
   private List<CmPartInstance> instances;

   public CmPartMaster(CmLightPart part) {
      this(part, 1);
   }

   public CmPartMaster(CmLightPart part, int quantity) {
      super(part);
      this.quantity = quantity;
      instances = new ArrayList<CmPartInstance>();
   }

   public void addInstance(CmPartInstance instance) {
      instances.add(instance);
   }

   public void removeInstance(CmPartInstance instance) {
      instances.remove(instance);
   }

   public int getQuantity() {
      return quantity;
   }

   public void setQuantity(int quantity) {
      this.quantity = quantity;
   }

   @Override
   public int hashCode() {
      final int PRIME = 31;
      int result = super.hashCode();
      result = PRIME * result + quantity;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      final CmPartMaster other = (CmPartMaster) obj;
      if (quantity != other.quantity)
         return false;
      return true;
   }
   
   public String toString() {
      return super.toString() + " x " + getQuantity();
   }
}
