package com.glaway.mpm.task;

import java.lang.reflect.Method;

import com.glaway.mpm.task.exception.CmTaskException;

/**
 * 任务执行者提供的方法描述
 * <br>Created on 2010-10-27
 * @author Dennis Huang - 黄寿疆
 */
public class CmTaskExecutorMethod {
   private static final Class<?>[] PARAMETER_TYPES = {Object.class, Object.class, CmTaskExecutorCallback.class};
   private CmTaskExecutor       taskExecutor;
   private Method               method;

   private CmTaskExecutorMethod(CmTaskExecutor taskExecutor) {
      this.taskExecutor = taskExecutor;
   }

   public static CmTaskExecutorMethod newCmTaskExecutorMethod(CmTaskExecutor taskExecutor, String method) throws CmTaskException {
      if (taskExecutor == null || method == null)
         throw new CmTaskException("taskExecutor及method不能为空", CmTaskException.EXCEPTION_TYPE_INVALID_METHOD);

      CmTaskExecutorMethod ret = new CmTaskExecutorMethod(taskExecutor);
      try {
         ret.method = taskExecutor.getClass().getDeclaredMethod(method, PARAMETER_TYPES);
      } catch (Exception e) {
         throw CmTaskException.newCmTaskException(e, CmTaskException.EXCEPTION_TYPE_EXECUTOR_METHOD_NOT_MATCH);
      }
      return ret;
   }

   /**
    * 任务执行者提供的方法签名
    * @return
    */
   public Method getMethod() {
      return method;
   }

   /**
    * 任务执行者的参考引用
    * @return
    */
   public CmTaskExecutor getTaskExecutor() {
      return taskExecutor;
   }

   /**
    * 检测当前任务执行者是否仍然可用
    * @return
    */
   public boolean isExecutorActive() {
      try {
         return taskExecutor.isExecutorActive();
      } catch (Throwable tt) {
         return false;
      }
   }

   @Override
   public int hashCode() {
      final int PRIME = 31;
      int result = 1;
      result = PRIME * result + ((taskExecutor == null) ? 0 : taskExecutor.hashCode());
      return result;
   }

   /**
    * 要求：每个taskId对应的taskExecutor不可重复
    * @param obj
    * @return
    */
   @Override
   public boolean equals(Object obj) { 
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final CmTaskExecutorMethod other = (CmTaskExecutorMethod) obj;
      if (taskExecutor == null) {
         if (other.taskExecutor != null)
            return false;
      } else if (!taskExecutor.equals(other.taskExecutor))
         return false;
      return true;
   }
}
