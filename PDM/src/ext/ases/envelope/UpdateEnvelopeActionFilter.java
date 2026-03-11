package ext.ases.envelope;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;

import org.apache.log4j.Logger;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.access.AdHocAccessKey;
import wt.access.AdHocControlled;
import wt.access.WTAclEntry;
import wt.fc.WTReference;
import wt.log4j.LogR;
import wt.org.WTPrincipalReference;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class UpdateEnvelopeActionFilter extends DefaultSimpleValidationFilter{
	private static final Logger logger = LogR.getLogger(UpdateEnvelopeActionFilter.class.getName());

    public UIValidationStatus preValidateAction(UIValidationKey validationKey, UIValidationCriteria validationCriteria) {
    	UIValidationStatus status = UIValidationStatus.HIDDEN;
    	ArrayList validState = new ArrayList();
    	try{
        	WTReference contextObj = validationCriteria.getContextObject();
			String objState = "";
			String number = "";
			Object obj = null;
    		if(contextObj != null){
    			obj = contextObj.getObject();   	
    			String acl = showAdHocAcl(obj);
    			if (AccessControlHelper.manager.hasAccess(SessionHelper.manager.getPrincipal(),
    					  obj,
      	                  AccessPermission.MODIFY)) {
					status = UIValidationStatus.ENABLED;
					return status;
				}else{
					return status;
				}
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}	
        return status;
    }
    
    public static String showAdHocAcl(Object obj)
    throws WTException
	{
	    if(!(obj instanceof AdHocControlled))
	        return "";
	    StringBuffer stringbuffer = new StringBuffer();
	    for(Enumeration enumeration = AccessControlHelper.manager.getEntries((AdHocControlled)obj); enumeration.hasMoreElements();)
	    {
	        stringbuffer.append("\n        ");
	        WTAclEntry wtaclentry = (WTAclEntry)enumeration.nextElement();
	        AdHocAccessKey adhocaccesskey = wtaclentry.getOwnerKey();
	        stringbuffer.append(adhocaccesskey.getDisplay());
	        stringbuffer.append(" (" + wtaclentry.getOwnerId() + ") ");
	        WTPrincipalReference wtprincipalreference = wtaclentry.getPrincipalReference();
	        stringbuffer.append(" " + wtprincipalreference.getName() + " (" + wtprincipalreference.getIdentity() + "): ");
	        for(wt.util.EnumeratorVector enumeratorvector = wtaclentry.getPermissions(); enumeratorvector.hasMoreElements();)
	        {
	            AccessPermission accesspermission = (AccessPermission)enumeratorvector.nextElement();
	            stringbuffer.append(accesspermission.getDisplay(Locale.US));
	            if(enumeratorvector.hasMoreElements())
	                stringbuffer.append(", ");
	        }
	
	    }
	
	    return stringbuffer.toString();
	}
}
