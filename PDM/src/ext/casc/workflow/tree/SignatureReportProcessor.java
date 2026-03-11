package ext.casc.workflow.tree;

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

import ext.casc.workflow.signtrue.zp.SignatureService;

public class SignatureReportProcessor extends DefaultObjectFormProcessor{
	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> list)
			throws WTException {
		FormResult result = super.doOperation(commandBean, list);
		String oid = commandBean.getActionOid().getOid().toString();
		String paramString = "";
		try {
			paramString = "?oid="+URLEncoder.encode("OR:"+oid, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String strCodeBase = "";
		try {
			strCodeBase = WTProperties.getLocalProperties().getProperty(
					"wt.server.codebase", null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		SignatureService.exportReport(oid,commandBean);
		String url = "/netmarkets/jsp/ext/casc/report/downloadSignatureReport.jsp"
				 + paramString;
		String baseUrl = strCodeBase + url;
		result.setJavascript("window.open(\"" + baseUrl + "\",\"_blank\")");
		
		result.setNextAction(FormResultAction.JAVASCRIPT);
		return result;
	}
}
