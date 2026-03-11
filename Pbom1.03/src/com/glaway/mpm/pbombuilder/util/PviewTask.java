package com.glaway.mpm.pbombuilder.util;

import com.glaway.mpm.pbombuilder.bom.CmTaskExecutor;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;


public class PviewTask {
	private static final CmTaskService service = CmTaskService.newCmTaskService();
	
	public static void registerTaskExecutor(String taskId, CmTaskExecutor taskExecutor, boolean unique) throws CmTaskException {
	      String method = taskId;
	      if (taskId != null) {
	         int idx = taskId.lastIndexOf('.');
	         if (idx > -1)
	            method = taskId.substring(idx + 1);
	      }
	      registerTaskExecutor(taskId, taskExecutor, method, unique);
	   }
	 /**
	    * 注册任务执行者
	    * @param taskId 任务标识
	    * @param taskExecutor 任务执行者
	    * @param method 任务执行提供的方法，该方法必须且有如：public void xxx(Object 任务请求者, Object 指定的参数, CmTaskExecutorCallback 回调接口)的签名
	    * @param unique 要求指定taskId的任务执行者必须唯一，仅取最近的一个
	    * @throws CmTaskException 任务执行异常
	    */
	   public static void registerTaskExecutor(Object taskId, CmTaskExecutor taskExecutor, String method, boolean unique) throws CmTaskException {
	      if (unique){
	    	  service.unregisterTaskExecutor(taskId);
	    	  service.registerTaskExecutor(taskId, taskExecutor, method);
	      }
	   }
	   
	   /**
	    * 异步执行任务<br>
	    * 如果出现某个未可控制的异常时，将在最后一个回调函数的postOperation处理（可通过其第二个参数taskExecutor==null加以识别）
	    * @param taskInfo
	    * @param callback
	    * @return 是否正确送至任务管理中心
	    * @throws CmTaskException
	    */
	   public static boolean postTask(CmTaskInfo taskInfo, CmTaskCallback callback) throws CmTaskException {
	      return service.postTask(taskInfo, callback);
	   }
	   
	   /**
	    * 简化的PostTask处理，将忽略异常
	    * @param taskId
	    * @param params
	    */
	   public static void postTask(String taskId, Object params) {
	      try {
	         postTask(CmTaskInfo.newCmTaskInfo(taskId, null, params), null);
	      } catch (CmTaskException e) {
	         // ignore...
	      }
	   }
	   
	   /**
	    * 简化的SendTask处理，将忽略异常
	    * @param taskId
	    * @param params
	    */
	   public static CmTaskResultSet sendTask(String taskId, Object params) {
	      try {
	         return sendTask(CmTaskInfo.newCmTaskInfo(taskId, null, params), null);
	      } catch (CmTaskException e) {
	         return CmTaskResultSet.newCmTaskResultSet(CmTaskResult.newCmTaskResult(false, null, e));
	      }
	   }
	   
	   /**
	    * 同步执行任务
	    * @param taskInfo
	    * @param callback
	    * @return 任务执行结果集（每个）
	    * @throws CmTaskException
	    */
	   public static CmTaskResultSet sendTask(CmTaskInfo taskInfo, CmTaskCallback callback) throws CmTaskException {
	      return service.sendTask(taskInfo, callback);
	   }
	   
	   /**
	    * 注册任务执行者，任务执行的方法名同taskId，这里提供了常用的简化使用方式
	    * @param taskId 任务标识
	    * @param taskExecutor 任务执行者
	    * @throws CmTaskException 任务执行异常
	    */
	   public static void registerTaskExecutor(String taskId, CmTaskExecutor taskExecutor) throws CmTaskException {
	      registerTaskExecutor(taskId, taskExecutor, true);
	   }
	   
	   /**
	    * 注册任务执行者
	    * @param taskId 任务标识
	    * @param taskExecutor 任务执行者
	    * @param method 任务执行提供的方法，该方法必须且有如：public void xxx(Object 任务请求者, Object 指定的参数, CmTaskExecutorCallback 回调接口)的签名
	    * @throws CmTaskException 任务执行异常
	    */
	   public static void registerTaskExecutor(Object taskId, CmTaskExecutor taskExecutor, String method) throws CmTaskException {
	      registerTaskExecutor(taskId, taskExecutor, method, true);
	   }
	   
	   /**
	    * 注册任务执行者
	    * @param taskId 任务标识
	    * @param taskExecutorInfo 任务执行信息，包含任务执行者及其提供的方法名称信息
	    */
	   public static void registerTaskExecutor(Object taskId, CmTaskExecutorMethod taskExecutorInfo) {
	      registerTaskExecutor(taskId, taskExecutorInfo, true);
	   }
	   
	   /**
	    * 注册任务执行者
	    * @param taskId 任务标识
	    * @param taskExecutorInfo 任务执行信息，包含任务执行者及其提供的方法名称信息
	    * @param unique 要求指定taskId的任务执行者必须唯一，仅取最近的一个
	    */
	   public static void registerTaskExecutor(Object taskId, CmTaskExecutorMethod taskExecutorInfo, boolean unique) {
	      if (unique)
	         service.unregisterTaskExecutor(taskId);
	      service.registerTaskExecutor(taskId, taskExecutorInfo);
	   }
	   
	   /**
	    * 注销指定taskId的所有任务执行者
	    * @param taskId
	    * @return 是否有成功注销任务执行者
	    */
	   public static boolean unregisterTaskExecutor(Object taskId) {
	      return service.unregisterTaskExecutor(taskId);
	   }
	   
	   /**
	    * 注销所有所有任务执行者
	    * @param taskId
	    * @return 是否有成功注销任务执行者
	    */
	   public static boolean unregisterTaskExecutor() {
	      return service.unregisterTaskExecutor();
	   }
	   
	   /**
	    * 尝试反注册指定对象
	    * @param obj
	    */
	   public static void tryUnregisterTaskExecutor(Object obj) {
	      if (obj instanceof CmTaskExecutor) {
	         CmTaskExecutor taskExecutor = (CmTaskExecutor) obj;

	         unregisterTaskExecutor(taskExecutor);
	      }
	   }
	   
}
