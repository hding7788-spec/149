/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command;

import com.glaway.mpm.util.WTPartUtil;
import ext.casc.util.IBAHelper;
import ext.casc.util.IBAUtility;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.iba.value.IBAHolder;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.util.WTPropertyVetoException;

import java.rmi.RemoteException;


public class GetCbKongZhiDianCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(GetCbKongZhiDianCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "getCbKongZhiDian";
	@Override
	public String execute(String params) {
		String rtnCode = "E";
		String rtnMsg="";
		JSONObject jrtnObj = new JSONObject();
		StringBuilder errorMsg = new StringBuilder("");
		JSONObject jparams;
		try {
			rtnCode = "S";
			if("".equals(params)){
				rtnCode = "N";
			}else{
				jparams = new JSONObject(params);
				String partNum = jparams.getString("partNum");
				WTPart part = WTPartUtil.getLatestPartByNumberAndView(partNum,"Manufacturing");
				if(part == null){
					rtnCode = "N";
					errorMsg.append(partNum+"零件在PDM系统中不存在;");
				}else {
					String cbKongZhiDian =  IBAHelper.getIBAStringValue(part,"cbKongZhiDian");
					if(cbKongZhiDian==null){
						cbKongZhiDian="";
					}
					jrtnObj.put("cbKongZhiDian", cbKongZhiDian);

				}
			}
			if("S".equals(rtnCode)){
				rtnMsg = "获取成功";
			}else{
				rtnMsg = errorMsg.toString();
			}
		} catch (JSONException e) {
			rtnCode ="N";
			rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
			LOGGER.error(rtnMsg, e);
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rtnCode ="N";
			rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
			LOGGER.error(rtnMsg, e);
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
