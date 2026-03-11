package com.glaway.mpm.mpmresource.validator;

import wt.fc.Persistable;
import wt.part.WTPart;
import wt.type.TypedUtility;

import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.mpmresource.TypeNameConstants;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class StartGZWorkflowValidator extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;
		Persistable po = criteria.getContextObject().getObject();
		if (po instanceof WTPart) {
			WTPart part = (WTPart) po;
			String typeName = TypedUtility.getTypeIdentifier(part).getTypename();
			if (typeName.endsWith(TypeNameConstants.gzRootPartTypeName)) {
				status = UIValidationStatus.ENABLED;
			}
		}
		return status;
	}
}
