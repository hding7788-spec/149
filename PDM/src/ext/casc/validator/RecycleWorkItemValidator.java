package ext.casc.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.casc.constants.Constants;
import ext.casc.workflow.CmWorkflowHelper;
import ext.casc.workflow.PrintHelper;

public class RecycleWorkItemValidator extends DefaultSimpleValidationFilter {

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        Object object = criteria.getContextObject().getObject();
        String id = key.getComponentID();

        if(object instanceof WorkItem){
        	WorkItem workItem = (WorkItem)object;
        	WfActivity activity = (WfActivity) workItem.getSource().getObject();
        	boolean enforce = SessionServerHelper.manager
                    .setAccessEnforced(false);
        	try {
        		WTPrincipal currentUser = SessionHelper.getPrincipal();
				if("工时定额编制".equals(activity.getName())) {
					if(currentUser == workItem.getOwnership().getOwner().getPrincipal() && "COMPLETED".equals(workItem.getStatus().toString())) {
						return UIValidationStatus.ENABLED;
					}
				}else {
					WfProcess process = activity.getParentProcess();
					if(process.getCreator().getName().equals(currentUser.getName())){
						return UIValidationStatus.ENABLED;
					}
				}
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				SessionServerHelper.manager.setAccessEnforced(enforce);
			}
        }
       return UIValidationStatus.DISABLED;

    }
}
