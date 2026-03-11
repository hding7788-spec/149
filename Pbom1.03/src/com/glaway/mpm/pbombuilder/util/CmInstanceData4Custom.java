package com.glaway.mpm.pbombuilder.util;

public class CmInstanceData4Custom extends CmInstanceData {
   private static final long serialVersionUID = -3307590249217237167L;

   private String customIdentifier;
   
   public CmInstanceData4Custom(String customIdentifier) {
      super();
      this.customIdentifier = customIdentifier;
      setType(TYPE_INSTANCE_CUSTOM);
   }

   @Override
   public String getIdentifier() {
      return customIdentifier;
   }      
}
