/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command;

import com.glaway.mpm.util.*;
import ext.casc.util.IBAUtility;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;

import wt.log4j.LogR;
import wt.part.WTPart;
import wt.util.WTPropertyVetoException;

import java.rmi.RemoteException;


/**
 * 类功能：质量系统图号控制措施清单批准之后，生成工艺措施反馈任务时
 *
 * @author chenjianhui
 * @date 2023/06/29
 */

public class PushProcessMesureCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(PushProcessMesureCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "pushProcessMesure";
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
				String partNumber = jparams.getString("partNumber");
				WTPart part = WTPartUtil.getLatestPartByNumberAndView(partNumber,"Manufacturing");
				if(part == null){
					rtnCode = "N";
					errorMsg.append(partNumber+"零件在PDM系统中不存在;");
				}else {
					String objectId = jparams.getString("objectId");
					IBAUtility iba = new IBAUtility(part);
					try {
						iba.setIBAValue("cbKongZhiMeasures", objectId);
						part = (WTPart)iba.updateAttributeContainer(part);
						iba.updateIBAHolder(part);
					} catch (WTPropertyVetoException e) {
						rtnCode = "N";
						e.printStackTrace();
					} catch (RemoteException e) {
						rtnCode = "N";
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						rtnCode = "N";
						e.printStackTrace();
					}
				}
			}
			if("S".equals(rtnCode)){
				rtnMsg = "反馈成功";
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
