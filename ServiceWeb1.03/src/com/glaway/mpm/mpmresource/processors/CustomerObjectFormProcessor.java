package com.glaway.mpm.mpmresource.processors;

import java.util.HashMap;
import java.util.List;

import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.httpgw.URLFactory;
import wt.util.WTException;

import com.glaway.mpm.util.GLLogger;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmActionServiceHelper;

public class CustomerObjectFormProcessor extends DefaultObjectFormProcessor {
	protected String redirectURL = null;
	private static String CLASSNAME = CustomerObjectFormProcessor.class.getName();

	public FormResult setResultNextAction(FormResult paramFormResult, NmCommandBean paramNmCommandBean,
			List<ObjectBean> paramList) throws WTException {
		GLLogger.debug(CLASSNAME, "FormProcessingStatus--" + paramFormResult.getStatus());
		if ((paramFormResult.getStatus() == FormProcessingStatus.SUCCESS)
				|| (paramFormResult.getStatus() == FormProcessingStatus.NON_FATAL_ERROR)) {
			if (this.redirectURL == null) {
				GLLogger.debug(CLASSNAME, "REFRESH_OPENER");
				paramFormResult.setNextAction(FormResultAction.REFRESH_OPENER);

			} else {
				GLLogger.debug(CLASSNAME, "LOAD_OPENER_URL");
				paramFormResult.setNextAction(FormResultAction.LOAD_OPENER_URL);
				paramFormResult.setURL(this.redirectURL);

			}
			paramFormResult.addFeedbackMessage(getSuccessFeedbackMessage());
		} else if (paramFormResult.getStatus() == FormProcessingStatus.FAILURE) {

			paramFormResult.setNextAction(FormResultAction.NONE);
		}

		return paramFormResult;
	}

	protected String getURL(Object paramObject, String paramString) throws WTException {
		ReferenceFactory localReferenceFactory = new ReferenceFactory();
		String str1 = localReferenceFactory.getReferenceString((Persistable) paramObject);
		URLFactory localURLFactory = new URLFactory();
		HashMap localHashMap = new HashMap();
		localHashMap.put("oid", str1);
		String str2 = NmActionServiceHelper.service.getAction("object", "view").getUrl();
		this.redirectURL = localURLFactory.getHREF(str2, localHashMap, true);
		return this.redirectURL;
	}

}