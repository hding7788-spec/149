package com.glaway.mpm.pbombuilder.util;

import com.glaway.mpm.pbombuilder.bom.CmTaskExecutor;


public class CmDefaultTaskCallback implements CmTaskCallback {
   public CmDefaultTaskCallback() {}

   public void preOperation(CmTaskInfo taskInfo, CmTaskExecutor taskExecutor) {}

   public void doOperation(Object o) {}

   public void postOperation(CmTaskInfo taskInfo, CmTaskExecutor taskExecutor, CmTaskResult taskResult) {}
}
