/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.e3;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.Util;
import com.ptc.windchill.mpml.resource.MPMPlant;
import ext.casc.webservice.TestConfig;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.fc.QueryResult;
import wt.log4j.LogR;


public class GetMPMPlantCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(GetMPMPlantCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "getMPMPlant";
	@Override
	public String execute(String params) {
		String rtnCode = "E";
		String rtnMsg="";
		JSONObject jrtnObj = new JSONObject();
		try {
			QueryResult qr = MPMResourceUtil.getAllPlant();
			JSONArray jsonArray = new JSONArray();
			int i = 0;
			while (qr.hasMoreElements()) {
				i++;
				if(i> TestConfig.MAX_COUNT) break;
				MPMPlant plant = (MPMPlant) qr.nextElement();
				JSONObject jsonObject = new JSONObject();
				IBAHelper helper = new IBAHelper(plant);
				jsonObject.put("oid",plant.getPersistInfo().getObjectIdentifier().getId());

				jsonObject.put("number",plant.getNumber());
				jsonObject.put("name",plant.getName());
				jsonObject.put("EnglishName", Util.formateString(helper.getIBAValue("EnglishName")));
				jsonObject.put("state",plant.getState().getState().getDisplay());
				jsonObject.put("type","制造单位");
				jsonObject.put("location",plant.getFolderPath());
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
