package com.glaway.mpm.task;

/**
 * 在任务执行者提供的方法中可以调用的方法接口
 * <br>Created on 2010-10-27
 * @author Dennis Huang - 黄寿疆
 */
public interface CmTaskExecutorCallback {
   /**
    * 任务执行者提供的方法中可调用的方法接口 
    * @param o 任务执行者在执行时提供的实时信息
    */
   public void doOperation(Object o);
}
