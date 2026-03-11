package com.glaway.mpm.task;

/**
 * �ṩ����ص�����ȱʡʵ��
 * <br>Created on 2010-10-27
 * @author Dennis Huang - ���ٽ�
 */
public class CmDefaultTaskCallback implements CmTaskCallback {
   public CmDefaultTaskCallback() {}

   public void preOperation(CmTaskInfo taskInfo, CmTaskExecutor taskExecutor) {}

   public void doOperation(Object o) {}

   public void postOperation(CmTaskInfo taskInfo, CmTaskExecutor taskExecutor, CmTaskResult taskResult) {}
}
