package ext.casc.validator;

import com.ptc.core.htmlcomp.tableview.TableViewDescriptor;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptorHelper;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import ext.casc.constants.Constants;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import java.util.Locale;

public class WorkflowValidator extends DefaultSimpleValidationFilter {

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        String id = key.getComponentID();
        if(id.indexOf("saveWriteInfo") > -1) {
            Object object = criteria.getContextObject().getObject();
            if(object instanceof WorkItem) {
                WorkItem workItem = (WorkItem) object;
                String status = workItem.getStatus().getDisplay(Locale.CHINA);
                //已完成的活动隐藏保存按钮
                if(Constants.WF_WORKITEM_STATUS_COMPLETED.equals(status)) {
                    return UIValidationStatus.HIDDEN;
                } else {
                    return UIValidationStatus.ENABLED;
                }
            }
        } else if(id.indexOf("setHideProcess") > -1) {
            try {
                TableViewDescriptor currTableViewDescriptor = TableViewDescriptorHelper.getCurrentActiveView(Constants.CONST_EXT_PLAN_HOME_OVERVIEW_WORKLIST_TABLE_ID, SessionHelper.getLocale());
                String select = currTableViewDescriptor.getName();
                if (!Constants.WORKITEM_NAME_YIYINCANG.equals(select)) {
                    return UIValidationStatus.ENABLED;
                }
            } catch(WTException e) {
                throw new RuntimeException(e);
            }
        } else if(id.indexOf("setShowProcess") > -1) {
            try {
                TableViewDescriptor currTableViewDescriptor = TableViewDescriptorHelper.getCurrentActiveView(Constants.CONST_EXT_PLAN_HOME_OVERVIEW_WORKLIST_TABLE_ID, SessionHelper.getLocale());
                String select = currTableViewDescriptor.getName();
                if (Constants.WORKITEM_NAME_YIYINCANG.equals(select)) {
                    return UIValidationStatus.ENABLED;
                }
            } catch(WTException e) {
                throw new RuntimeException(e);
            }
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);
        return UIValidationStatus.HIDDEN;
    }
}
