package com.glaway.mpm.visual.bean;

import java.io.Serializable;

public class VaEPartMaster extends VaPartMaster implements Serializable {
   private static final long serialVersionUID = -6519088321558962998L;

   public VaEPartMaster(VaLightPart part) {
      super(part);
   }

   public VaEPartMaster(VaLightPart part, int quantity) {
	  
      super(part, quantity);
   }
}
