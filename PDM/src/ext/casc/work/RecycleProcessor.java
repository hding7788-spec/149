package ext.casc.work;

import java.util.ArrayList;
import java.util.List;

import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.workflow.CmWorkflowHelper;
import ext.casc.workflow.PrintHelper;

public class RecycleProcessor extends DefaultObjectFormProcessor{

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public FormResult doOperation(NmCommandBean commandBean,List<ObjectBean> list) throws WTException {
		 boolean accessFlag = SessionServerHelper.manager
	                .setAccessEnforced(false);
		FormResult form =  super.doOperation(commandBean, list);
		FeedbackMessage message = new FeedbackMessage();
		try {
        	NmOid nmOid = commandBean.getActionOid();
    		Object obj = nmOid.getRefObject();
            if(obj instanceof WorkItem){
            	WorkItem item = (WorkItem)obj;
            	WfActivity activity = (WfActivity) item.getSource().getObject();
            	WfProcess process = (WfProcess) activity.getParentProcess();
				String activityName = activity.getName();
				List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
				activityList = PrintHelper.getActivities(process, activityList);
				if("工时定额编制".equals(activityName)) {
					for(WfAssignedActivity wfAssignedActivity : activityList) {
						String state = wfAssignedActivity.getState().toString();
						if("OPEN_RUNNING".equals(state) && wfAssignedActivity.getTemplate().getName().equals("工时定额审批")) {
							CmWorkflowHelper.completeActivity(wfAssignedActivity.getPersistInfo().getObjectIdentifier().toString(), "驳回", "工时定额员，回退任务");
							form.setStatus(FormProcessingStatus.SUCCESS);
							message.addMessage("回退任务，处理完成");
							form.addFeedbackMessage(message);
							form.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE);
							return form;
						}
					}
					form.setStatus(FormProcessingStatus.FAILURE);
					message.addMessage("暂无进行中的工时定额审批，请确认工时定额是否正在审批中！");
					form.addFeedbackMessage(message);
					form.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE);
				}else {
					// 终止所有的会签相关活动
					// WfBlock wfblock = PrintHelper.getBlock(process);
					//List<WfBlock> allWfBlocks;
					//allWfBlocks = PrintHelper.getAllBlock(process);
					/*for (WfBlock wfblock : allWfBlocks) {
						PrintHelper.getActivities(wfblock, activityList);
					}*/
					for(WfAssignedActivity wfAssignedActivity : activityList) {
						String state = wfAssignedActivity.getState().toString();
						if("OPEN_RUNNING".equals(state)) {
							if(wfAssignedActivity.getTemplate().getName().equals("校对")
									|| wfAssignedActivity.getTemplate().getName().equals("审核")
									|| wfAssignedActivity.getTemplate().getName().equals("内部会签")
									//||wfAssignedActivity.getTemplate().getName().equals("外部会签")
									|| wfAssignedActivity.getTemplate().getName().equals("标审")
									|| wfAssignedActivity.getTemplate().getName().equals("批准")) {
								CmWorkflowHelper.completeActivity(wfAssignedActivity.getPersistInfo().getObjectIdentifier().toString(), "驳回", "流程启动者，回退任务");
								form.setStatus(FormProcessingStatus.SUCCESS);
								message.addMessage("回退任务，处理完成");
								form.addFeedbackMessage(message);
								//form.setNextAction(FormResultAction.NONE);
								form.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE);
							} else if(wfAssignedActivity.getTemplate().getName().equals("外部会签")) {
								form.setStatus(FormProcessingStatus.FAILURE);
								message.addMessage("流程已经流转到外部会签，不允许回退！需等待外部会签环节结束。");
								form.addFeedbackMessage(message);
								form.setNextAction(FormResultAction.NONE);
							}
						}
					}
				}
            }
        }catch (WTException e) {
        	form.setStatus(FormProcessingStatus.FAILURE);
            message.addMessage("回退任务，处理失败");
            form.addFeedbackMessage(message);
            form.setNextAction(FormResultAction.NONE);
            //form.setNextAction(FormResultAction.REFRESH_OPENER);
            e.printStackTrace();
        }finally{
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
		return form;
	}


}
