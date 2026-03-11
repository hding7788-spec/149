package com.glaway.mpm.pbombuilder.util;

public class CmInstanceData4Part extends CmInstanceData {
   private static final long serialVersionUID = -3307590249217237167L;

   public CmInstanceData4Part() {
      super();
      setType(TYPE_INSTANCE_PART);
   }

   @Override
   public String getIdentifier() {
      StringBuffer ret = new StringBuffer(64);
      ret.append(PRE_USE_LINK).append(getLinkId());
//      if (getOccId() > 0) {
//         ret.append('|').append(PRE_USE_OCC).append(getOccId());
//         ret.append('-').append(getOccIdentifierId());
//      }
      if (!"-1".equals(getOccId())&&getOccId().indexOf("$@")==-1) {
  	    ret.append('|').append(PRE_USE_OCC).append(getOccId());
  	    ret.append('-').append(getOccIdentifierId());
      }
      return ret.toString();
   }
}
