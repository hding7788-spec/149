package com.glaway.mpm.processplan.processors;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import wt.util.WTException;
import wt.util.WTProperties;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class StartPEProcessor extends DefaultObjectFormProcessor {
	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> list)
			throws WTException {
		FormResult result = super.doOperation(commandBean, list);
		String oid = commandBean.getActionOid().getOid().toString();
		String paramString = "";
		try {
			paramString = "?oid="+URLEncoder.encode("OR:"+oid, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String strCodeBase = "";
		try {
			strCodeBase = WTProperties.getLocalProperties().getProperty("wt.server.codebase", null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String url = "/netmarkets/jsp/ext/glaway/mpm/startPE.jsp?ty=1";
		String baseUrl = strCodeBase + url;
		result.setJavascript("window.open(\"" + baseUrl + "\",\"_blank\")");

		result.setNextAction(FormResultAction.JAVASCRIPT);
		return result;
	}
}
