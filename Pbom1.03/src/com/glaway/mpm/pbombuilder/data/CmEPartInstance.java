package com.glaway.mpm.pbombuilder.data;

import java.io.Serializable;

import com.glaway.mpm.pbombuilder.tree.CmLightPart;

public class CmEPartInstance extends CmPartInstance implements Serializable {
   private static final long serialVersionUID = -3266033292459668024L;
   public CmEPartInstance(CmLightPart part) {
      super(part);
   }
}
