package com.glaway.mpm.pbombuilder.util;

public class CmInstanceData4LO extends CmInstanceData {
   private static final long serialVersionUID = -3307590249217237167L;

   public CmInstanceData4LO() {
      super();
      setType(TYPE_INSTANCE_LO);
   }

   @Override
   public String getIdentifier() {
      return PRE_MASTER + getMasterId();
   }      
}
