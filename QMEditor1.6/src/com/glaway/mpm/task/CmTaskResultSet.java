package com.glaway.mpm.task;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.glaway.mpm.task.exception.CmTaskException;

/**
 * 同步的任务请求最终返回的结果集，结果集中的每个CmTaskResult对应着一个CmTaskExecutor执行后的结果
 * <br>Created on 2010-10-27
 * @author Dennis Huang - 黄寿疆
 */
public class CmTaskResultSet implements Serializable {
   private static final long  serialVersionUID = -2207884210944381624L;

   private List<CmTaskResult> taskResultList;
   private CmTaskResult       firstTaskResult;
   private int                size;
   private boolean success;
   private CmTaskException e;

   private CmTaskResultSet() {
      this.e = null;
      this.success = true;
      this.taskResultList = new LinkedList<CmTaskResult>();
      this.firstTaskResult = CmTaskResult.NULL;
      this.size = 0;
   }

   public static CmTaskResultSet newCmTaskResultSet() {
      return new CmTaskResultSet();
   }

   public static CmTaskResultSet newCmTaskResultSet(CmTaskResult taskResult) {
      return newCmTaskResultSet().addResult(taskResult);
   }

   public CmTaskResultSet addResult(CmTaskResult taskResult) {
      if (this.size == 0)
         this.firstTaskResult = taskResult;
      
      this.taskResultList.add(taskResult);
      this.size++;
      
      if (!taskResult.isSuccess() && isSuccess()) {
         success = false;
         e = taskResult.getException();
      }
      
      return this;
   }

   public Iterator<CmTaskResult> iterator() {
      return taskResultList.iterator();
   }

   public int getSize() {
      return size;
   }
   
   public boolean isSuccess() {
      return success;
   }
   
   public CmTaskException getException() {
      return e;
   }

   public CmTaskResult getFirstResult() {
      return this.firstTaskResult;
   }

   public boolean getFirstSuccess() {
      return getFirstResult().isSuccess();
   }

   public Object getFirstReturnVal() {
      return getFirstResult().getReturnVal();
   }
   
   public CmTaskException getFirstException() {
      return getFirstResult().getException();
   }
}
