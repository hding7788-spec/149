/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.gy;

import com.glaway.mpm.util.ReferenceFactory;
import ext.casc.integrate.senKe.SenKeSynchHelper;
import ext.casc.util.Tools;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import wt.doc.WTDocument;

/**
 * 类功能：主辅工艺关联关系查询
 *
 * @author cjh
 * @date 2022/11/28
 */
@Component
public class SKGetProcessPlanDataCommand implements WebServiceCommand, InitializingBean {
	// 方法标识
	public static final String METHOD_NAME = "getProcessPlanData";

	@Override
	public String execute(String params) {
		String rtnCode ="S";
		String rtnMsg ="";
		JSONObject jparams = null;
		try {
			jparams = new JSONObject(params);
		} catch (JSONException e) {
			rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
		}

		JSONObject jrtnObj = new JSONObject();
		JSONObject jsonData  = new JSONObject();
		String oid = jparams.optString("oid");
		if(!Tools.isNull(oid)){
            try {
              	WTDocument doc =(WTDocument) ReferenceFactory.getObjectbyOid(oid);
				jsonData  = SenKeSynchHelper.getSendSenKeData(doc);
            } catch (Exception e) {
                e.printStackTrace();
				rtnMsg = "通过oid获取对象失败:"+e.getLocalizedMessage() ;
            }
        }else{
			rtnMsg = "oid为不允许为空!" ;
		}

		try {
			jrtnObj.put(WSConstants.RTN_MSG, rtnMsg);
			jrtnObj.put(WSConstants.RTN_CODE, rtnCode);
			jrtnObj.put(WSConstants.DATA, jsonData);

		} catch (JSONException ex) {
		}

		return jrtnObj.toString();
	}



	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}
}
