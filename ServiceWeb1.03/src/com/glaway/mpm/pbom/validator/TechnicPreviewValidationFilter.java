package com.glaway.mpm.pbom.validator;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import ext.casc.sop.constants.SopConstants;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.type.TypedUtility;

public class TechnicPreviewValidationFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		String id = key.getComponentID();
		Persistable per = criteria.getContextObject().getObject();
		if("openInTechnicPreview1".equals(id)) {
			if(per instanceof WTDocument) {
				String typeName = TypedUtility.getTypeIdentifier(per).getTypename();
				if((typeName.contains("PROCESS_PLAN")&&!typeName.contains("reportTechnics")) || typeName.contains(SopConstants.SOP_TYPE_SOPDOC)
					|| typeName.contains("TechnicsTemplate")) {
					return UIValidationStatus.ENABLED;
				}
			}
		}
		return UIValidationStatus.HIDDEN;
	}

}
