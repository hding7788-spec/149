package com.glaway.mpm.visual.bean;

public class VaInstanceData4Part extends VaInstanceData {
   private static final long serialVersionUID = -3307590249217237167L;

   public VaInstanceData4Part() {
      super();
      setType(TYPE_INSTANCE_PART);
   }

   @Override
   public String getIdentifier() {
      StringBuffer ret = new StringBuffer(64);
      ret.append(PRE_USE_LINK).append(getLinkId());
      if (getOccId() > 0) {
         ret.append('|').append(PRE_USE_OCC).append(getOccId());
         ret.append('-').append(getOccIdentifierId());
      }
      
      return ret.toString();
   }      
}
