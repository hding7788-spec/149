/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.e3;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTDocumentUtil;
import ext.casc.webservice.TestConfig;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.doc.WTDocument;
import wt.log4j.LogR;

import java.util.List;


public class GetTYProcessPlanCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(GetTYProcessPlanCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "getTYProcessPlan";
	@Override
	public String execute(String params) {
		String rtnCode = "E";
		String rtnMsg="";
		JSONObject jrtnObj = new JSONObject();
		try {
			String type = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.TY_PROCESS_DOC";
			List<WTDocument> list = WTDocumentUtil.getDocumentByType( type);
			JSONArray jsonArray = new JSONArray();
			int i = 0;
			for(WTDocument document:list){
				i++;
				if(i> TestConfig.MAX_COUNT) break;
				JSONObject jsonObject = new JSONObject();
				IBAHelper helper = new IBAHelper(document);
				jsonObject.put("oid",document.getPersistInfo().getObjectIdentifier().getId());
				jsonObject.put("number",document.getNumber());
				jsonObject.put("name",document.getName());
				jsonObject.put("location",document.getFolderPath());
				jsonObject.put("state",document.getState().getState().getDisplay());
				jsonObject.put("SECRET", Util.formateString(helper.getIBAValue("SECRET")));
				jsonObject.put("PPNUMBER", Util.formateString(helper.getIBAValue("PPNUMBER")));
				jsonArray.put(jsonObject);
			}
			jrtnObj.put(WSConstants.RTN_DATA, jsonArray);
			rtnCode = "S";
			rtnMsg = "找到所有数据信息";
		} catch (JSONException e) {
			rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
			LOGGER.error(rtnMsg, e);
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			jrtnObj.put(WSConstants.RTN_MSG, rtnMsg);
			jrtnObj.put(WSConstants.RTN_CODE, rtnCode);
		} catch (JSONException ex) {
			LOGGER.error("Error building JSON: ", ex);
		}

		return jrtnObj.toString();
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}
}
