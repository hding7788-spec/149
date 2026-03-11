package com.glaway.mpm.pbombuilder.util;

public class CmInstanceData4CI extends CmInstanceData {
   private static final long serialVersionUID = -3307590249217237167L;

   public CmInstanceData4CI() {
      super();
      setType(TYPE_INSTANCE_CI);
   }

   @Override
   public String getIdentifier() {
      return PRE_MASTER + getMasterId();
   }      
}
