package com.glaway.mpm.pbombuilder.util;

import com.glaway.mpm.pbombuilder.bom.CmTaskExecutor;

/**
 * 任务回调接口
 * <br>Created on 2012-10-15
 * @author ylchen
 */
public interface CmTaskCallback extends CmTaskExecutorCallback {
   /**
    * 在taskExecutor提供的方法前执行的回调接口
    * @param taskInfo 任务信息
    * @param taskExecutor 任务执行者
    */
   public void preOperation(CmTaskInfo taskInfo, CmTaskExecutor taskExecutor);

   /**
    * 在taskExecutor提供的方法后执行的回调接口
    * @param taskInfo 任务信息
    * @param taskExecutor 任务执行者
    * @param taskResult 任务执行结果
    */
   public void postOperation(CmTaskInfo taskInfo, CmTaskExecutor taskExecutor, CmTaskResult taskResult);
}
