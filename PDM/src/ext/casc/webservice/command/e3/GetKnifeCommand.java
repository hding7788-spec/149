/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.e3;

import com.glaway.mpm.constants.TypeNameConstants;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.Util;
import com.ptc.windchill.mpml.resource.MPMTooling;
import ext.casc.webservice.TestConfig;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.log4j.LogR;

import java.util.List;



public class GetKnifeCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(GetKnifeCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "getKnife";
	@Override
	public String execute(String params) {
		String rtnCode = "E";
		String rtnMsg="";
		JSONObject jrtnObj = new JSONObject();

		JSONObject jparams;
		try {
			jparams = new JSONObject(params);
			String startTime =jparams.optString("startTime");
			List<MPMTooling> knifeList = MPMResourceUtil.getAllKnifes(TypeNameConstants.DJ);
			if(!knifeList.isEmpty()){
				JSONArray jsonArray = new JSONArray();
				int i = 0;
				for (MPMTooling tooling : knifeList) {
					i++;
					if(i> TestConfig.MAX_COUNT) break;
					JSONObject jsonObject = new JSONObject();
					IBAHelper helper = new IBAHelper(tooling);
					jsonObject.put("oid",tooling.getPersistInfo().getObjectIdentifier().getId());
					jsonObject.put("number",tooling.getNumber());
					jsonObject.put("name",tooling.getName());
					jsonObject.put("EnglishName", Util.formateString(helper.getIBAValue("EnglishName")));
					jsonObject.put("state",tooling.getState().getState().getDisplay());
					jsonObject.put("type","工具");
					jsonObject.put("location",tooling.getFolderPath());
					jsonObject.put("KNIFETYPE",Util.formateString(helper.getIBAValue("KNIFETYPE")));
					jsonObject.put("CMAT",Util.formateString(helper.getIBAValue("CMAT")));
					jsonObject.put("RKZJ",Util.formateString(helper.getIBAValue("RKZJ")));
					jsonObject.put("JCZJ",Util.formateString(helper.getIBAValue("JCZJ")));
					jsonObject.put("RKCD",Util.formateString(helper.getIBAValue("RKCD")));
					jsonObject.put("ZCD",Util.formateString(helper.getIBAValue("ZCD")));
					jsonObject.put("GC",Util.formateString(helper.getIBAValue("GC")));
					jsonObject.put("ZXJGCC",Util.formateString(helper.getIBAValue("ZXJGCC")));
					jsonObject.put("ZDJGCC",Util.formateString(helper.getIBAValue("ZDJGCC")));
					jsonObject.put("JGXS",Util.formateString(helper.getIBAValue("JGXS")));
					jsonObject.put("JKLX",Util.formateString(helper.getIBAValue("JKLX")));
					jsonObject.put("JSBZ",Util.formateString(helper.getIBAValue("JSBZ")));
					jsonObject.put("RKYJBJ",Util.formateString(helper.getIBAValue("RKYJBJ")));
					jsonObject.put("CS",Util.formateString(helper.getIBAValue("CS")));
					jsonObject.put("REMARK",Util.formateString(helper.getIBAValue("REMARK")));
					jsonArray.put(jsonObject);
				}
				jrtnObj.put(WSConstants.RTN_DATA, jsonArray);
				rtnCode = "S";
				rtnMsg = "找到所有数据信息";
			}else{
				rtnCode = "N";
				rtnMsg = "没找到相应的数据";
			}
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
