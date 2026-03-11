package com.glaway.mpm.pbombuilder.exception;

import java.lang.reflect.InvocationTargetException;

/**
 * <br>Created on 2012-11-27
 * @author chenyunlong
 */
public class CmTaskException extends Exception {
   private static final long serialVersionUID                         = 1395750062510771355L;

   public static final int   EXCEPTION_TYPE_UNKNOWN                   = 0;
   public static final int   EXCEPTION_TYPE_TASK_ID_NOT_EXIST         = 1;
   public static final int   EXCEPTION_TYPE_EXECUTOR_METHOD_NOT_MATCH = 2;
   public static final int   EXCEPTION_TYPE_INVOKE_FAILURE            = 3;
   public static final int   EXCEPTION_TYPE_NOT_EXECUTE               = 4;
   public static final int   EXCEPTION_TYPE_INVALID_METHOD            = 5;

   private int               type;

   public CmTaskException(String msg) {
      this(msg, EXCEPTION_TYPE_UNKNOWN);
   }

   public CmTaskException(Throwable tt) {
      super(tt);
      this.setType(EXCEPTION_TYPE_UNKNOWN);
   }

   public CmTaskException(String msg, int type) {
      super(msg);
      setType(type);
   }      

   public static CmTaskException newCmTaskException(Throwable tt, int type) {      
      if (tt instanceof InvocationTargetException) {
         tt = ((InvocationTargetException) tt).getCause();
      }
      CmTaskException ret = new CmTaskException(tt);
      ret.setType(type);
      
      return ret;
   }

   public int getType() {
      return type;
   }

   public void setType(int type) {
      this.type = type;
   }
}
