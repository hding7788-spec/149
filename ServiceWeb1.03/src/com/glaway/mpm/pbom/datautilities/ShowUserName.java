package com.glaway.mpm.pbom.datautilities;

import wt.iba.value.IBAHolder;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.util.WTException;

import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.ReferenceFactory;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;

public class ShowUserName extends AbstractDataUtility {

	@SuppressWarnings("static-access")
	public Object getDataValue(String commandId, Object obj, ModelContext modelContext) throws WTException {
		WTPart part = (WTPart)obj;
		
		IBAHelper iba = new IBAHelper((IBAHolder)part);
		String userOid = iba.getIBAValue(part, "responsor");
		GLLogger.debug("userOid===>" + userOid);
		if(userOid != null){
			userOid = userOid.trim();
		}
		
		if(userOid != null && userOid != ""){
//			String userDisplayName = Util.getUserDisplayName(userOid);
			String userDisplayName = ((WTUser)ReferenceFactory.getObjectbyOid(userOid)).getPrincipalDisplayIdentifier();
			return userDisplayName;
		}
		return "";
	}

}
