package ext.casc.report.process;

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

public class AllWorkFlowProcess  extends DefaultObjectFormProcessor{
	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> list)
			throws WTException {
		FormResult result = super.doOperation(commandBean, list);
		String beginTime = (String)commandBean.getText().get("beginTime_col_beginTime");
		String endTime = (String)commandBean.getText().get("endTime_col_endTime");
		String processName = (String)commandBean.getText().get("processName");
		String paramString = "";
		paramString = "?processName="+processName+"&beginTime="+beginTime+"&endTime="+endTime;
		String strCodeBase = "";
		try {
			strCodeBase = WTProperties.getLocalProperties().getProperty(
					"wt.server.codebase", null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String url = "/netmarkets/jsp/ext/casc/report/downloadAllWorkFlowReport.jsp"
				 + paramString;
		String baseUrl = strCodeBase + url;
		result.setJavascript("window.open(\"" + baseUrl + "\",\"_blank\")");
		
		result.setNextAction(FormResultAction.JAVASCRIPT);
		return result;
	}
}
