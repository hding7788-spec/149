package com.glaway.mpm.pbombuilder.util;

import java.io.Serializable;

import com.glaway.mpm.pbombuilder.exception.CmTaskException;

/**
 * 单个任务执行者执行方法后的返回值数据描述 <br>
 * Created on 2012-10-15
 * 
 * @author ylchen
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
    * @return ִ�к��׳����쳣
    */
   public CmTaskException getException() {
      return exception;
   }

   /**
    * @return �����Ƿ�ɹ�ִ��
    */
   public boolean isSuccess() {
      return success;
   }

   /**
    * @return ����ɹ�ִ�к�ķ���ֵ���������ִ�г����쳣���򷵻�null
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
