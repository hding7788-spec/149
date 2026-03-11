package com.glaway.mpm.visual.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.glaway.mpm.visual.view.tree.VaNode;

public class VaPartMaster extends VaNode implements Serializable {
   private static final long    serialVersionUID = 6215166736192455919L;

   private int                  quantity;
   private List<VaPartInstance> instances;

   public VaPartMaster(VaLightPart part) {
      this(part, 1);
   }

   public VaPartMaster(VaLightPart part, int quantity) {
      super(part);
      this.quantity = quantity;
      instances = new ArrayList<VaPartInstance>();
   }

   public void addInstance(VaPartInstance instance) {
      instances.add(instance);
   }

   public void removeInstance(VaPartInstance instance) {
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
      final VaPartMaster other = (VaPartMaster) obj;
      if (quantity != other.quantity)
         return false;
      return true;
   }
   
   public String toString() {
      return super.toString() + " x " + getQuantity();
   }
}
