package ext.casc.validator;

import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.casc.access.AccessAdminUtil;

public class ProductTeamManagementValidator extends DefaultSimpleValidationFilter {

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        try {
            WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
            String id = key.getComponentID();
            if (id.indexOf("productManagment") > -1){
                if (currentUser.getName().equals("Administrator")||AccessAdminUtil.isAdmin()||AccessAdminUtil.isSysAdmin()) {
                    return UIValidationStatus.ENABLED;
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return UIValidationStatus.HIDDEN;
    }
}
