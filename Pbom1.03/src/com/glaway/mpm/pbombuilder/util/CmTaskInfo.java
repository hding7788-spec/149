package com.glaway.mpm.pbombuilder.util;

import java.io.Serializable;

/**
 * 任务信息数据模型
 * <br>Created on 2012-10-29
 * @author chenyunlong
 */
public class CmTaskInfo implements Serializable {
   private static final long serialVersionUID = 8819046745245713974L;

   private Object            taskId;
   private Object            source;
   private Object            arguments;

   private CmTaskInfo() {}

   public static CmTaskInfo newCmTaskInfo(Object taskId, Object source, Object arguments) {
      CmTaskInfo ret = new CmTaskInfo();

      ret.taskId = taskId;
      ret.source = source;
      ret.arguments = arguments;

      return ret;
   }

   /**    
    * @return 作为任务执行者提供方法的第二个参数提供数据
    */
   public Object getArguments() {
      return arguments;
   }

   /**    
    * @return 指定任务请求者
    */
   public Object getSource() {
      return source;
   }

   /**
    * @return 任务标识，通过可为String类型
    */
   public Object getTaskId() {
      return taskId;
   }

   public String toString() {
      return new StringBuffer().append("taskId=").append(taskId).append(" & source=").append(source).append(" & arguments=").append(arguments)
         .toString();
   }
}
