package ext.casc.webservice.command.gy;

import cn.hutool.core.util.StrUtil;
import ext.casc.mpm.GyCsServerHelper;
import ext.casc.mpm.process.GLProcessParamDefinition;
import ext.casc.mpm.process.GLProcessParamValues;
import ext.casc.mpm.process.GLProcessParams;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import ext.casc.webservice.command.CommandHelper;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import wt.epm.EPMDocument;
import wt.log4j.LogR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GetParamsMapAndValuesCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(GetParamsMapAndValuesCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "getParamsMapAndValues";

	public String execute(String params) {
		String rtnCode = "S";
		String rtnMsg;
		JSONObject jrtnObj = new JSONObject();

		StringBuilder errorMsg = new StringBuilder("");
		JSONArray jparams;
		try {
			LOGGER.info(params);
			if("".equals(params)){
				rtnCode = "N";
				errorMsg.append("参数为空！");
			}else {
				jparams = new JSONArray(params);
				JSONArray jsonArray = new JSONArray();
				jrtnObj.put(WSConstants.RTN_DATA, jsonArray);

				for(int i=0;i<jparams.length();i++) {
					JSONObject	jparam  = jparams.getJSONObject(i);
					String cadName = jparam.optString("cadName");
					String cadVersion = jparam.optString("cadVersion");
					String templateId = jparam.optString("templateId");
					if(templateId!=null&&!templateId.startsWith("OR:wt.doc.WTDocument:")){
						templateId = "OR:wt.doc.WTDocument:" +templateId;
					}
					String bVersion = cadVersion;
					if(cadVersion.contains(".")){
						String[] ss = cadVersion.split("\\.");
						bVersion = ss[0];
					}
					EPMDocument epm = CommandHelper.getEPMDocumentByCADNumber(cadName,bVersion);
					if(epm == null){
						errorMsg.append(cadName+"在PDM系统不存在;");
						continue;
					}

					JSONObject data = new JSONObject();
					jsonArray.put(data);

					data.put("cadName",cadName);
					data.put("cadVersion",cadVersion);

					Map<String,String> queryParams = new HashMap<String,String>();
					queryParams.put("cadName",cadName);
					queryParams.put("cadVersion",cadVersion);
					queryParams.put("templateId",templateId);

					List<GLProcessParamValues> pvs = (List<GLProcessParamValues>)GyCsServerHelper.queryGLObjects(GLProcessParamValues.class, queryParams);
					JSONArray datas = new JSONArray();
					data.put("datas",datas);
					data.put("templateId",templateId);
					if(pvs.isEmpty()){

						List<GLProcessParamDefinition> pds = (List<GLProcessParamDefinition>)GyCsServerHelper.queryGLObjects(GLProcessParamDefinition.class, GLProcessParamDefinition.TEMPLATEID,templateId);
						for(GLProcessParamDefinition pd:pds){
							GLProcessParams paramDef =  GyCsServerHelper.getProcessParamDefinition(pd.getGyParamNumber(),true);
							if(paramDef!=null && "工艺".equals(paramDef.getSource())){
								continue;
							}
							JSONObject d = new JSONObject();
							datas.put(d);
							d.put("gyParamNumber", pd.getGyParamNumber());
							d.put("name_gy", pd.getGyParamName());
							d.put("name_sj", pd.getSjParamName());
							d.put("value", "");
							d.put("unit", "");
							d.put("gongchengzhi", "");
							d.put("shangpiancha", "");
							d.put("xiapiancha", "");
							d.put("fuhao", "");
							d.put("jizhun1", "");
							d.put("jizhun2", "");
							d.put("jizhun3", "");
						}
					}else{
						for(GLProcessParamValues pv:pvs){
							GLProcessParams paramDef =  GyCsServerHelper.getProcessParamDefinition(pv.getGyParamNumber(),true);
							if(paramDef!=null && "工艺".equals(paramDef.getSource())){
								continue;
							}
							JSONObject d = new JSONObject();
							datas.put(d);
							d.put("gyParamNumber", pv.getGyParamNumber());
							d.put("name_gy", pv.getGyParamName());
							d.put("name_sj", pv.getSjParamName());
							d.put("value", pv.getParamValue());
							d.put("unit", pv.getParamUnit());
							d.put("gongchengzhi", pv.getGongChengZhi());
							d.put("shangpiancha", pv.getShangPianCha());
							d.put("xiapiancha", pv.getXiaPianCha());
							d.put("fuhao", pv.getFuHao());
							d.put("jizhun1", pv.getJiZhun1());
							d.put("jizhun2", pv.getJiZhun2());
							d.put("jizhun3", pv.getJiZhun3());
						}
					}
				}
			}
			if(StrUtil.isNotEmpty(errorMsg) || "N".equals(rtnCode)) {
				rtnCode = "N";
				rtnMsg = errorMsg.toString();
			} else {
				rtnMsg = "获取成功";
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
		LOGGER.info(jrtnObj);
		return jrtnObj.toString();
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}
}
