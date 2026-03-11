package com.glaway.mpm.visual.bean;

public class VaInstanceData4Custom extends VaInstanceData {
   private static final long serialVersionUID = -3307590249217237167L;

   private String customIdentifier;
   
   public VaInstanceData4Custom(String customIdentifier) {
      super();
      this.customIdentifier = customIdentifier;
      setType(TYPE_INSTANCE_CUSTOM);
   }

   @Override
   public String getIdentifier() {
      return customIdentifier;
   }      
}
