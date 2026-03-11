package ext.casc.validator;

import wt.part.WTPart;
import wt.util.WTInvalidParameterException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class GYWJPrintTaskValidator extends DefaultSimpleValidationFilter {

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        Object object = criteria.getContextObject().getObject();
        try {
        	if(object instanceof WTPart){
        		WTPart part =( WTPart)object;
        		if("Manufacturing".equals(part.getViewName())){
                     return UIValidationStatus.ENABLED;
        		}
        		
        	}
        } catch (WTInvalidParameterException e) {
            e.printStackTrace();
        } 
        return UIValidationStatus.HIDDEN;
    }
}
