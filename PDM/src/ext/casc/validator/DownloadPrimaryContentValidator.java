package ext.casc.validator;

import wt.epm.EPMDocument;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class DownloadPrimaryContentValidator extends DefaultSimpleValidationFilter {
    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        Object object = criteria.getContextObject().getObject();
        try {
            WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
            String id = key.getComponentID();
            if (id.indexOf("downloadPrimaryContent") > -1) {
                if (object instanceof EPMDocument) {
                    EPMDocument epmDocument = (EPMDocument)object;
                    String name = epmDocument.getName();
                    if (name.endsWith(".prt")||name.endsWith(".PRT")||name.endsWith(".asm")||name.endsWith(".ASM")) {
                        return UIValidationStatus.ENABLED;
                    }
                }
            }
        } catch (WTInvalidParameterException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
        return UIValidationStatus.HIDDEN;
    }
}
