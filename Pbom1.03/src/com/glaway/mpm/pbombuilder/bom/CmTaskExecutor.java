package com.glaway.mpm.pbombuilder.bom;

/**
 * 任务执行者接口
 * <br>Created on 2012-10-16
 * @author chenyunlong
 */
public interface CmTaskExecutor {
   /**
    * @return 当前Executor是否可用
    */
   public boolean isExecutorActive();

   /**
    * 设置当前Executor的可用性
    * @param executorActive
    */
   public void setExecutorActive(boolean executorActive);

   // public void xxx(Object sender, Object params, CmTaskExecutorCallback);
}
