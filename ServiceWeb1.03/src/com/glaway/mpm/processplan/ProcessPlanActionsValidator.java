package com.glaway.mpm.processplan;

import ext.casc.sop.constants.SopConstants;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.folder.SubFolder;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.casc.access.AccessAdminUtil;
import ext.casc.constants.Constants;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTaskItem;

public class ProcessPlanActionsValidator extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,
			UIValidationCriteria criteria) {
		Object object = criteria.getContextObject().getObject();
		Object pageObj = criteria.getPageObject().getObject();
		String id = key.getComponentID();
		WTUser currentUser;
		try {
			currentUser = (WTUser) SessionHelper.getPrincipal();
			if ( currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()){
		     	   return UIValidationStatus.ENABLED;
		     }
			if (id.indexOf("CustomOpenInProcessEditor") > -1||id.indexOf("openInProcessEditor") > -1) {
				if (object instanceof WTPart) {
					WTPart part = (WTPart)object;
					if("Manufacturing".equals(part.getViewName())){
						return UIValidationStatus.ENABLED;
					}
					
					if(part.getNumber().endsWith("PROCESSPLAN")||part.getNumber().endsWith("PROCESS_PLAN")){
						return UIValidationStatus.ENABLED;
					}
					if (pageObj instanceof ProcessTaskItem) {
						ProcessTaskItem taskItem = (ProcessTaskItem) pageObj;
						String state = taskItem.getTaskItemState();
						String executeRole = taskItem.getExecutorRole();
						if(taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI) || SopConstants.SOP_TASK_TASKTYPE.equals(taskItem.getTaskType()) || SopConstants.SOP_TASK_TASKTYPE_CHANGE.equals(taskItem.getTaskType())){
							return UIValidationStatus.HIDDEN;
						}
						if (state.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
								&& executeRole.equals(ProcessConstants.ROLE_GONGYIYUAN)) {
							return UIValidationStatus.ENABLED;
						}else{
                            return UIValidationStatus.HIDDEN;
                        }
					}else if(pageObj instanceof WorkItem){
						WorkItem workItem = (WorkItem) pageObj;
						WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
						if(wfAct.getName().contains("编制")||wfAct.getName().contains("修改")){
							return UIValidationStatus.ENABLED;
						}else{
                            return UIValidationStatus.HIDDEN;
                        }
					}else if((pageObj instanceof SubFolder || pageObj instanceof WTPart ) && "Manufacturing".equals(part.getViewName())){
                        return UIValidationStatus.ENABLED;
                    }
				} else if(object instanceof MPMProcessPlan) {
					if (pageObj instanceof ProcessTaskItem) {
						ProcessTaskItem taskItem = (ProcessTaskItem) pageObj;
						if(taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)){
							return UIValidationStatus.HIDDEN;
						}
						String typeName = TypedUtility.getTypeIdentifier(object).getTypename();
						if(typeName.contains(SopConstants.SOP_TYPE_SOPPROCESSPLAN)){
							return UIValidationStatus.HIDDEN;
						}else{
							return UIValidationStatus.ENABLED;
						}
					}
					return UIValidationStatus.ENABLED;
				}else if(object instanceof WTDocument) {
					WTDocument doc = (WTDocument)object;
					String typeName = TypedUtility.getTypeIdentifier(object).getTypename();
					if(typeName.contains("PROCESS_PLAN")) {
						if(pageObj instanceof WorkItem){
							WorkItem workItem = (WorkItem) pageObj;
							WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
							if(wfAct.getName().contains("编制")||wfAct.getName().contains("修改")){
								return UIValidationStatus.ENABLED;
							}
						}else {
							if(doc.getModifier().getPrincipal().getName().equals(currentUser.getName())){
								return UIValidationStatus.ENABLED;
							}

						}
					}

				}
			}
		} catch (WTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return UIValidationStatus.HIDDEN;
	}
}
