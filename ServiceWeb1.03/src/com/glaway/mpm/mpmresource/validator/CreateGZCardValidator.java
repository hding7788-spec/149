package com.glaway.mpm.mpmresource.validator;

import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.util.WTPrincipalUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CreateGZCardValidator extends DefaultSimpleValidationFilter {
	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		try {

			WTPrincipal principal = SessionHelper.manager.getPrincipal();
			WTUser user = OrganizationServicesHelper.manager.getUser("wcadmin");
			if (principal.equals(user)) {
				status = UIValidationStatus.ENABLED;
			} else {
				WTGroup group = WTPrincipalUtil.getGroupByName(Constants.ZhuanYeZu);
				if (group.isMember(principal)) {
					if (!"setState".equals(key.getComponentID())) {
						status = UIValidationStatus.ENABLED;
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return status;
	}
}
