package com.glaway.mpm.task;

import java.io.Serializable;

import com.glaway.mpm.task.exception.CmTaskException;

/**
 * 单个任务执行者执行方法后的返回值数据描述 <br>
 * Created on 2010-10-27
 * 
 * @author Dennis Huang - 黄寿疆
 */
public class CmTaskResult implements Serializable {
   private static final long        serialVersionUID = -7287204765562746363L;

   public static final CmTaskResult NULL             = CmTaskResult.newCmTaskResult(// new
                                                                                    // line
                                                        false, // new line
                                                        null,// new line
                                                        new CmTaskException("未执行任务!", CmTaskException.EXCEPTION_TYPE_NOT_EXECUTE)// new
                                                                                                                                 // line
                                                        );

   private boolean                  success;
   private Object                   returnVal;
   private CmTaskException          exception;

   private CmTaskResult() {}

   public static CmTaskResult newCmTaskResult(boolean success, Object returnVal, CmTaskException exception) {
      CmTaskResult ret = new CmTaskResult();
      ret.success = success;
      ret.returnVal = returnVal;
      ret.exception = exception;

      return ret;
   }

   /**
    * @return 执行后抛出的异常
    */
   public CmTaskException getException() {
      return exception;
   }

   /**
    * @return 任务是否成功执行
    */
   public boolean isSuccess() {
      return success;
   }

   /**
    * @return 任务成功执行后的返回值，如果任务执行出现异常，则返回null
    */
   public Object getReturnVal() {
      return returnVal;
   }

   public String toString() {
      StringBuffer buf = new StringBuffer(64).append("[").append(this.success);
      if (!isSuccess())
         buf.append(", exception=").append(getException().getMessage()).append("]");
      else
         buf.append("]");
      return buf.toString();
   }
}
