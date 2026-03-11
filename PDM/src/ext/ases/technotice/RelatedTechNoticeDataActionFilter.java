package ext.ases.technotice;

import java.util.Enumeration;
import java.util.Locale;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.access.AdHocAccessKey;
import wt.access.AdHocControlled;
import wt.access.WTAclEntry;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.org.WTPrincipalReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;

import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.casc.util.IBAHelper;

public class RelatedTechNoticeDataActionFilter extends DefaultSimpleValidationFilter {

    public UIValidationStatus preValidateAction(UIValidationKey validationKey, UIValidationCriteria validationCriteria) {
        UIValidationStatus status = UIValidationStatus.HIDDEN;
        try {
            WTReference contextObj = validationCriteria.getContextObject();
            Object obj = null;
            if (contextObj != null) {
                obj = contextObj.getObject();
                String type = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(obj);
                if (type.indexOf("TECHNOTICE_DOC")>0) {
                    status = UIValidationStatus.ENABLED;
                    return status;
                } else {
                    status = UIValidationStatus.HIDDEN;
                    return status;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return status;
    }

    public static String showAdHocAcl(Object obj)
            throws WTException {
        if (!(obj instanceof AdHocControlled))
            return "";
        StringBuffer stringbuffer = new StringBuffer();
        for (Enumeration enumeration = AccessControlHelper.manager.getEntries((AdHocControlled) obj); enumeration
                .hasMoreElements();) {
            stringbuffer.append("\n        ");
            WTAclEntry wtaclentry = (WTAclEntry) enumeration.nextElement();
            AdHocAccessKey adhocaccesskey = wtaclentry.getOwnerKey();
            stringbuffer.append(adhocaccesskey.getDisplay());
            stringbuffer.append(" (" + wtaclentry.getOwnerId() + ") ");
            WTPrincipalReference wtprincipalreference = wtaclentry.getPrincipalReference();
            stringbuffer.append(" " + wtprincipalreference.getName() + " (" + wtprincipalreference.getIdentity()
                    + "): ");
            for (wt.util.EnumeratorVector enumeratorvector = wtaclentry.getPermissions(); enumeratorvector
                    .hasMoreElements();) {
                AccessPermission accesspermission = (AccessPermission) enumeratorvector.nextElement();
                stringbuffer.append(accesspermission.getDisplay(Locale.US));
                if (enumeratorvector.hasMoreElements())
                    stringbuffer.append(", ");
            }

        }

        return stringbuffer.toString();
    }
}
