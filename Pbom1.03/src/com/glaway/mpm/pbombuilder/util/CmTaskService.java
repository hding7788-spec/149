package com.glaway.mpm.pbombuilder.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.glaway.mpm.pbombuilder.bom.CmTaskExecutor;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.log.CmLogger;

/**
 * 任务管理调度实现类，仅提供给CmTaskHelper内部使用 <br>
 * Created on 2012-10-19
 * 
 * @author chenyunlong
 */
class CmTaskService {
   private static final CmLogger                   log                   = CmLogger.getLogger(CmTaskService.class);

   // 定义缺少的任务回调实例，当任务请求者没有提供回调函数时使用
   private static final CmTaskCallback             DEFAULT_TASK_CALLBACK = new CmDefaultTaskCallback();

   // 队列守护线程实例
   private final Thread                            TASK_GUARDIAN_THREAD;

   // 任务标识与其相关的执行者列表的映射
   private Map<Object, List<CmTaskExecutorMethod>> registeredExecutorMap = new HashMap<Object, List<CmTaskExecutorMethod>>(64);

   // 异步执行队列
   private Queue<CmTaskEntry>                      queue                 = new ConcurrentLinkedQueue<CmTaskEntry>();
   // 线程TASK_GUARDIAN_THREAD是否忙的标识，如果不忙，TASK_GUARDIAN_THREAD将一直等待
   private volatile boolean                        isBusy                = false;

   // 对象同步锁
   private Object                                  lock                  = new Object();

   private CmTaskService() {
      TASK_GUARDIAN_THREAD = new CmTaskGuardianThread(1000);
      TASK_GUARDIAN_THREAD.setDaemon(true);
      TASK_GUARDIAN_THREAD.start();
   }

   static CmTaskService newCmTaskService() {
      return new CmTaskService();
   }

   String getTaskExecutorDescription(CmTaskExecutor taskExecutor, int i) {
      StringBuffer ret = new StringBuffer(64);
      ret.append(taskExecutor.getClass().getSimpleName());
      if (i > 0)
         ret.append("(").append(i).append(")");
      return ret.toString();
   }
   
   /**
    * 注销所有所有任务执行者
    * 
    * @param taskId
    * @return 是否有成功注销任务执行者
    */
   boolean unregisterTaskExecutor() {
      synchronized (lock) {
         registeredExecutorMap.clear();
         log.info("unregister all TaskExecutors");
         return true;
      }
   }
   
   /**
    * 注销指定taskId的所有任务执行者
    * 
    * @param taskId
    * @return 是否有成功注销任务执行者
    */
   boolean unregisterTaskExecutor(Object taskId) {
      synchronized (lock) {
         boolean ret = false;
         if (registeredExecutorMap.containsKey(taskId)) {
            if (log.isInfoEnabled()) {
               log.info("remove all taskExecutors for taskId=", taskId);
               List<CmTaskExecutorMethod> taskExecutorMethodList = registeredExecutorMap.get(taskId);
               try {
                  for (CmTaskExecutorMethod taskExecutorMethod : taskExecutorMethodList) {
                     log.info(">>>unregister TaskExecutor for taskId=", taskId, " & taskExecutor=", taskExecutorMethod.getTaskExecutor().getClass()
                        .getName(), " & method=", taskExecutorMethod.getMethod().getName());
                  }
               } catch (Throwable tt) {
               }
            }
            registeredExecutorMap.remove(taskId);
            ret = true;
         }
         return ret;
      }
   }
   
   /**
    * 异步执行任务<br>
    * 如果出现某个未可控制的异常时，将在最后一个回调函数的postOperation处理（可通过其第二个参数taskExecutor==null加以识别）
    * 
    * @param taskInfo
    * @param callback
    * @return 是否正确送至任务管理中心
    * @throws CmTaskException
    */
   boolean postTask(CmTaskInfo taskInfo, CmTaskCallback callback) throws CmTaskException {
      boolean ret = queue.add(CmTaskEntry.newCmTaskEntry(taskInfo, callback));
      isBusy = true;
      return ret;
   }
   
   /**
    * 注册任务执行者
    * 
    * @param taskId
    *        任务标识
    * @param taskExecutor
    *        任务执行者
    * @param method
    *        任务执行提供的方法，该方法必须且有如：public void xxx(Object 任务请求者, Object 指定的参数,
    *        CmTaskExecutorCallback 回调接口)的签名
    * @throws CmTaskException
    *         任务执行异常
    */
   void registerTaskExecutor(Object taskId, CmTaskExecutor taskExecutor, String method) throws CmTaskException {
      CmTaskExecutorMethod taskExecutorInfo = CmTaskExecutorMethod.newCmTaskExecutorMethod(taskExecutor, method);
      registerTaskExecutor(taskId, taskExecutorInfo);
   }
   /**
    * 注册任务执行者
    * 
    * @param taskId
    *        任务标识
    * @param taskExecutorMethod
    *        任务执行信息，包含任务执行者及其提供的方法名称信息
    */
   void registerTaskExecutor(Object taskId, CmTaskExecutorMethod taskExecutorMethod) {
      synchronized (lock) {
         taskExecutorMethod.getTaskExecutor().setExecutorActive(true);
         List<CmTaskExecutorMethod> taskExecutorMethodList = registeredExecutorMap.get(taskId);
         if (taskExecutorMethodList == null) {
            taskExecutorMethodList = new LinkedList<CmTaskExecutorMethod>();
            registeredExecutorMap.put(taskId, taskExecutorMethodList);
         }
         if (!taskExecutorMethodList.contains(taskExecutorMethod)) {
            taskExecutorMethodList.add(taskExecutorMethod);
            try {
               log.info("success register TaskExecutor taskId=", taskId, " & taskExecutor=", taskExecutorMethod.getTaskExecutor().getClass()
                  .getName(), " & method=", taskExecutorMethod.getMethod().getName());
            } catch (Throwable tt) {
            }
         } else
            try {
               log.info("already exists TaskExecutor taskId=", taskId, " & taskExecutor=", taskExecutorMethod.getTaskExecutor().getClass().getName(),
                  " & method=", taskExecutorMethod.getMethod().getName());
            } catch (Throwable tt) {
            }
      }
   }
   
   /**
    * 队列信息入口模型 <br>
    * 
    */
   private static final class CmTaskEntry {
      private CmTaskInfo     taskInfo;
      private CmTaskCallback callback;

      private CmTaskEntry(CmTaskInfo taskInfo, CmTaskCallback callback) {
         this.taskInfo = taskInfo;
         this.callback = callback;
      }

      public static CmTaskEntry newCmTaskEntry(CmTaskInfo taskInfo, CmTaskCallback callback) {
         return new CmTaskEntry(taskInfo, callback);
      }

      public CmTaskCallback getCallback() {
         return callback;
      }

      public CmTaskInfo getTaskInfo() {
         return taskInfo;
      }
   }
   
   /**
    * 异步执行任务的队列守护线程，负责调度队列中的所有异步任务 <br>
    * 
    */
   private final class CmTaskGuardianThread extends Thread {
      private int waitTime;

      public CmTaskGuardianThread(int waitTime) {
         this.waitTime = waitTime;
      }

      public void run() {
         while (true) {
            while (!isBusy)
               try {
                  Thread.sleep(waitTime);
               } catch (InterruptedException e) {
               }

            try {
               CmTaskEntry taskEntry = queue.poll();
               while (taskEntry != null) {
                  try {
                     log.traceContext("execute postTask taskInfo.getTaskId=" + taskEntry.getTaskInfo().getTaskId(), "Enter");
                     sendTask(taskEntry.getTaskInfo(), taskEntry.getCallback());
                     log.traceContext("execute postTask taskInfo.getTaskId=" + taskEntry.getTaskInfo().getTaskId(), "Leave");
                  } catch (CmTaskException e) {
                     log.error("execute postTask taskInfo.getTaskId=" + taskEntry.getTaskInfo().getTaskId(), " catch error=", e.getMessage());
                     taskEntry.callback.postOperation(taskEntry.getTaskInfo(), null, CmTaskResult.newCmTaskResult(false, null, e));
                  }
                  taskEntry = queue.poll();
               }
            } catch (Throwable tt) {
               log.error(tt);
            }

            isBusy = false;
         }
      }
   }
   
   
   /**
    * 同步执行任务
    * 
    * @param taskInfo
    * @param callback
    * @return 任务执行结果集（每个）
    * @throws CmTaskException
    */
   CmTaskResultSet sendTask(CmTaskInfo taskInfo, CmTaskCallback callback) throws CmTaskException {
      if (log.isTraceEnabled())
         log.traceContext("sendTask taskInfo.getTaskId()=" + taskInfo.getTaskId(), "Enter");
      if (taskInfo == null || taskInfo.getTaskId() == null)
         throw new CmTaskException("taskInfo及taskId不能为空!", CmTaskException.EXCEPTION_TYPE_TASK_ID_NOT_EXIST);

      // 根据taskInfo.taskId从调度中心中获取所有相关的已注册CmTaskExecutor列表
      List<CmTaskExecutorMethod> taskExecutorMethodList = registeredExecutorMap.get(taskInfo.getTaskId());
//      if (log.isDebugEnabled())
//         log.debug("TaskExecutorMethods for taskId=", taskInfo.getTaskId(), " & return list.size=", taskExecutorMethodList == null ? 0
//            : taskExecutorMethodList.size());

      // 针对每一个CmTaskExecutor实例
      unregisterDeadTaskExecutors(taskExecutorMethodList);

      if (taskExecutorMethodList == null || taskExecutorMethodList.isEmpty())
         throw new CmTaskException("找不到指定taskId为" + taskInfo.getTaskId() + "的已注册的TaskExecutor", CmTaskException.EXCEPTION_TYPE_TASK_ID_NOT_EXIST);

      if (callback == null)
         callback = DEFAULT_TASK_CALLBACK;

      CmTaskResultSet resultSet = CmTaskResultSet.newCmTaskResultSet();
      if (log.isTraceEnabled())
         log.traceContext("do all operations for taskInfo.getTaskId()=" + taskInfo.getTaskId(), "enter");
      int i = 0;
      for (CmTaskExecutorMethod taskExecutorInfo : taskExecutorMethodList) {
         // 1. 调用callback的onTaskStart
         if (log.isTraceEnabled())
            log.traceContext("do pre/do/post operations >>> " + getTaskExecutorDescription(taskExecutorInfo.getTaskExecutor(), i), "enter");
         callback.preOperation(taskInfo, taskExecutorInfo.getTaskExecutor());

         // 2. 通过反射调用CmTaskExecutor注册的方法
//         if (log.isTraceEnabled())
//            log.traceContext("do execute >>> " + getTaskExecutorDescription(taskExecutorInfo.getTaskExecutor(), i), "enter");
         CmTaskResult taskResult;
         try {
            Object returnVal = execute(taskInfo, callback, taskExecutorInfo);
            taskResult = CmTaskResult.newCmTaskResult(true, returnVal, null);
         } catch (CmTaskException e) {
            taskResult = CmTaskResult.newCmTaskResult(false, null, e);
         }
//         if (log.isTraceEnabled())
//            log.traceContext("do execute >>> " + getTaskExecutorDescription(taskExecutorInfo.getTaskExecutor(), i), "leave");

         // 3. 调用callback的onTaskCompleted方法
         callback.postOperation(taskInfo, taskExecutorInfo.getTaskExecutor(), taskResult);
//         if (log.isTraceEnabled())
//            log.traceContext("do pre/do/post operations >>> " + getTaskExecutorDescription(taskExecutorInfo.getTaskExecutor(), i), "leave");
         resultSet.addResult(taskResult);

//         if (log.isInfoEnabled()) {
//            log.info("run taskId=", taskInfo.getTaskId(), " & taskExecutor=", getTaskExecutorDescription(taskExecutorInfo.getTaskExecutor(), i),
//               " & result=", taskResult);
//            if (!taskResult.isSuccess()) 
//               log.info(taskResult.getException());            
//         }

         i++;
      }
//      if (log.isTraceEnabled())
//         log.traceContext("do all operations for taskInfo.getTaskId()=" + taskInfo.getTaskId(), "leave");
//
//      if (log.isTraceEnabled())
//         log.traceContext("sendTask taskInfo.getTaskId()=" + taskInfo.getTaskId(), "Leave with result.size=" + resultSet.getSize());
      return resultSet;
   }
   
   /**
    * 注销taskExecutorInfoList中所有不可用的任务执行者
    * 
    * @param taskExecutorMethodList
    * @return
    */
   private boolean unregisterDeadTaskExecutors(List<CmTaskExecutorMethod> taskExecutorMethodList) {
      log.traceContext("unregisterDeadTaskExecutors(List<CmTaskExecutorMethod> taskExecutorMethodList)", "Enter");
      boolean ret = false;
      if (taskExecutorMethodList != null && !taskExecutorMethodList.isEmpty()) {
         synchronized (lock) {
            Iterator<CmTaskExecutorMethod> iter = taskExecutorMethodList.iterator();
            while (iter.hasNext()) {
               CmTaskExecutorMethod taskExecutorMethod = iter.next();
               if (!taskExecutorMethod.isExecutorActive()) {
                  iter.remove();
                  ret = true;
                  try {
                     log.info("remove disactive taskExecutor=", taskExecutorMethod.getTaskExecutor().getClass().getName());
                  } catch (Throwable tt) {
                  }
               }
            }
         }
      }
      log.traceContext("unregisterDeadTaskExecutors(List<CmTaskExecutorMethod> taskExecutorMethodList)", "Leave with ret=" + ret);
      return ret;
   }
   
   
   /**
    * 通过反射调用任务执行者提供的方法
    * 
    * @param taskInfo
    * @param callback
    * @param taskExecutorMethod
    * @return
    * @throws CmTaskException
    */
   private Object execute(CmTaskInfo taskInfo, CmTaskExecutorCallback callback, CmTaskExecutorMethod taskExecutorMethod) throws CmTaskException {
      Object ret = null;

      try {
         ret = taskExecutorMethod.getMethod().invoke(// new line
            taskExecutorMethod.getTaskExecutor(), // new line
            new Object[]{taskInfo.getSource(), taskInfo.getArguments(), callback}//
            );
      } catch (IllegalArgumentException e) {
         throw CmTaskException.newCmTaskException(e, CmTaskException.EXCEPTION_TYPE_INVALID_METHOD);
      } catch (IllegalAccessException e) {
         throw CmTaskException.newCmTaskException(e, CmTaskException.EXCEPTION_TYPE_INVALID_METHOD);
      } catch (InvocationTargetException e) {
         throw CmTaskException.newCmTaskException(e.getCause(), CmTaskException.EXCEPTION_TYPE_INVOKE_FAILURE);
      }

      return ret;
   }
}
