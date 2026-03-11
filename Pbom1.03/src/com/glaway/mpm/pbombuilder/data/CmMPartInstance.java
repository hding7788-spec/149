package com.glaway.mpm.pbombuilder.data;

import java.io.Serializable;

import com.glaway.mpm.pbombuilder.tree.CmLightPart;

public class CmMPartInstance extends CmPartInstance implements Serializable {
   private static final long serialVersionUID = -6153361941100481253L;

   public CmMPartInstance(CmLightPart part) {
      super(part);
   }
   
   public CmMPartInstance(CmEPartInstance epartInst) {
      this(epartInst.getPart());
   }
}
