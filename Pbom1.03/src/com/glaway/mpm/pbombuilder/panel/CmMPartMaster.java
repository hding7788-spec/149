package com.glaway.mpm.pbombuilder.panel;

import java.io.Serializable;

import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmPartMaster;

public class CmMPartMaster extends CmPartMaster implements Serializable {
   private static final long serialVersionUID = -4463503529266429185L;

   public CmMPartMaster(CmLightPart part) {
      super(part);
   }

   public CmMPartMaster(CmLightPart part, int quantity) {
      super(part, quantity);
   }
   
   public CmMPartMaster(CmEPartMaster epartMaster) {
      this(epartMaster.getPart(), epartMaster.getQuantity());
   }
}
