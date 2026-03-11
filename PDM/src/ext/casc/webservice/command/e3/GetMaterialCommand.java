/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.e3;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.Util;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
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


public class GetMaterialCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(GetMaterialCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "getMaterial";
	@Override
	public String execute(String params) {
		String rtnCode = "E";
		String rtnMsg="";
		JSONObject jrtnObj = new JSONObject();

		JSONObject jparams;
		try {
			jparams = new JSONObject(params);
			String startTime =jparams.optString("startTime");
			List<MPMProcessMaterial> list = MPMResourceUtil.getAllMPMProcessMaterial();
			if(!list.isEmpty()){
				JSONArray jsonArray = new JSONArray();
				int i = 0;
				for (MPMProcessMaterial material : list) {
					i++;
					if(i> TestConfig.MAX_COUNT) break;
					JSONObject jsonObject = new JSONObject();
					IBAHelper helper = new IBAHelper(material);
					jsonObject.put("oid",material.getPersistInfo().getObjectIdentifier().getId());

					jsonObject.put("number",material.getNumber());
					jsonObject.put("name",material.getName());
					jsonObject.put("EnglishName", Util.formateString(helper.getIBAValue("EnglishName")));
					jsonObject.put("state",material.getState().getState().getDisplay());
					jsonObject.put("type","工艺辅料");
					jsonObject.put("location",material.getFolderPath());
					jsonObject.put("CSIZE",Util.formateString(helper.getIBAValue("CSIZE")));
					jsonObject.put("JSTJ",Util.formateString(helper.getIBAValue("JSTJ")));
					jsonObject.put("FJTJ",Util.formateString(helper.getIBAValue("FJTJ")));
					jsonObject.put("MINDEX",Util.formateString(helper.getIBAValue("MINDEX")));
					jsonObject.put("JLDW",Util.formateString(helper.getIBAValue("JLDW")));
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
