package ext.casc.validator;

import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class SignatrueReportValidator extends DefaultSimpleValidationFilter {
	@Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		Object object = criteria.getContextObject().getObject();
        if(object instanceof WorkItem){
        	WorkItem wi =(WorkItem)object;
        	WfActivity wfAct = (WfActivity) wi.getSource().getObject();
        	if(wfAct.getName().equals("工艺会签汇总")){
        		return UIValidationStatus.ENABLED;
        	}
        	
        }
        return UIValidationStatus.HIDDEN;
    }
	
}
