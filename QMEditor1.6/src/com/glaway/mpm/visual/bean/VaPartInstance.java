package com.glaway.mpm.visual.bean;

import java.io.Serializable;

import com.glaway.mpm.visual.view.tree.VaNode;

public class VaPartInstance extends VaNode implements Serializable {
   private static final long serialVersionUID = 6215166736192455919L;

   private VaPartMaster      master;

   public VaPartInstance(VaLightPart part) {
      super(part);
   }

   public VaPartMaster getMaster() {
      return master;
   }

   public void setMaster(VaPartMaster master) {
      this.master = master;
   }
   
}
