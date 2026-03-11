package ext.ases.envelope;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.fc.WTReference;
import wt.session.SessionHelper;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class DeleteEnvelopeActionFilter extends DefaultSimpleValidationFilter {
	public UIValidationStatus preValidateAction(UIValidationKey validationKey,
			UIValidationCriteria validationCriteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		try {
			WTReference contextObj = validationCriteria.getContextObject();
			Object obj = null;
			if (contextObj != null) {
				obj = contextObj.getObject();
				if (AccessControlHelper.manager.hasAccess(
						SessionHelper.manager.getPrincipal(), obj,
						AccessPermission.DELETE)) {
					status = UIValidationStatus.ENABLED;
					return status;
				} else {
					return status;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return status;
	}

}
