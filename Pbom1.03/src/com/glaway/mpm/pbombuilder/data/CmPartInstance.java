package com.glaway.mpm.pbombuilder.data;

import java.io.Serializable;

import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmPartMaster;

public class CmPartInstance extends CmNode implements Serializable {
   private static final long serialVersionUID = 6215166736192455919L;

   private CmPartMaster      master;

   public CmPartInstance(CmLightPart part) {
      super(part);
   }

   public CmPartMaster getMaster() {
      return master;
   }

   public void setMaster(CmPartMaster master) {
      this.master = master;
   }
   
}
