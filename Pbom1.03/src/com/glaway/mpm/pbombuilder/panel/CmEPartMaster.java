package com.glaway.mpm.pbombuilder.panel;

import java.io.Serializable;

import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmPartMaster;

public class CmEPartMaster extends CmPartMaster implements Serializable {
   private static final long serialVersionUID = -6519088321558962998L;

   public CmEPartMaster(CmLightPart part) {
      super(part);
   }

   public CmEPartMaster(CmLightPart part, int quantity) {
      super(part, quantity);
   }
}
