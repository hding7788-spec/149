package com.glaway.mpm.visual.bean;

public class VaInstanceData4LO extends VaInstanceData {
   private static final long serialVersionUID = -3307590249217237167L;

   public VaInstanceData4LO() {
      super();
      setType(TYPE_INSTANCE_LO);
   }

   @Override
   public String getIdentifier() {
      return PRE_MASTER + getMasterId();
   }      
}
