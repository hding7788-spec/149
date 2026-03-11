package com.glaway.mpm.visual.bean;

public class VaInstanceData4CI extends VaInstanceData {
   private static final long serialVersionUID = -3307590249217237167L;

   public VaInstanceData4CI() {
      super();
      setType(TYPE_INSTANCE_CI);
   }

   @Override
   public String getIdentifier() {
      return PRE_MASTER + getOccId();//getMasterId();
   }      
}
