package ext.casc.validator;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTaskItem;
import wt.fc.Persistable;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

public class WorkflowPboAttachmentsValidator extends DefaultSimpleValidationFilter {

	@Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        Object pageObj = criteria.getPageObject().getObject();
        String componentId = key.getComponentID();
        if(pageObj instanceof ProcessTaskItem) {
        	ProcessTaskItem taskItem = (ProcessTaskItem)pageObj;
        	if(componentId.equals("addPboAttach") || componentId.equals("deletePboAttach")) {
        		if(!ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN.equals(taskItem.getTaskItemState())){
        			return UIValidationStatus.HIDDEN;
            	} else {
            		return UIValidationStatus.ENABLED;
            	}
        	}
        }
        if(pageObj instanceof WorkItem) {
        	WorkItem workItem = (WorkItem)pageObj;
        	if(componentId.equals("addPboWorkFlowAttach") || componentId.equals("deletePboWorkFlowAttach")) {
        		if(workItem.isComplete()){
        			return UIValidationStatus.HIDDEN;
            	} else {
            		boolean isOwnership=checkUserOwnership(workItem);
            		if(isOwnership) {
            			return UIValidationStatus.ENABLED;
            		} else {
            			return UIValidationStatus.HIDDEN;
            		}
            	}
        	}

        }
        return UIValidationStatus.HIDDEN;
	}

	/** 
	  * @Description: 检查当前用户是否任务责任人
	  * @date 2025年9月25日上午10:34:47
	  * @author Liluwen
	  * @param workItem
	  * @return  
	  * @return 
	*/
	private boolean checkUserOwnership(WorkItem workItem) {
		Ownership ownership=workItem.getOwnership();
		if(ownership==null) {
			return false;
		}
		WTPrincipalReference wtPrincipalReference=ownership.getOwner();
		Persistable persistable=wtPrincipalReference.getObject();
		try {
			WTUser currentUser = (WTUser) SessionHelper.manager.getPrincipal();
			if(persistable instanceof WTUser) {
				WTUser owner=(WTUser)persistable;
				if(currentUser.getName().equals(owner.getName())) {
					return true;
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

}
