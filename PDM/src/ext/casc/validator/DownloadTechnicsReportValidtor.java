package ext.casc.validator;

import com.ptc.core.meta.common.TypeIdentifierHelper;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.casc.part.FaCiBomHelper;
import wt.part.WTPart;

public class DownloadTechnicsReportValidtor extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		String objectType = key.getObjectType();
		String actionName = key.getComponentID();
		Object object = criteria.getContextObject().getObject();
		if (object instanceof WTPart) {
			WTPart part = (WTPart) object;
			String viewName = part.getViewName();

			//发次BOM
			if ("faciBomExport".equals(actionName) && "customReport".equals(objectType)) {
				String partType = TypeIdentifierHelper.getType(part).toString();
				if(partType.endsWith(FaCiBomHelper.FACI_BOM_TYPE))
				{
					return UIValidationStatus.ENABLED;
				}
			}

			if ("ebomExport".equals(actionName) && "customReport".equals(objectType)) {
				if ("Design".equals(viewName)) {
					return UIValidationStatus.ENABLED;
				}
			} else {
				if ("Manufacturing".equals(viewName)) {
					return UIValidationStatus.ENABLED;
				}
			}
		}
		return UIValidationStatus.HIDDEN;
	}

}
