
package ext.casc.webservice.command.gy;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.WTPartUtil;
import ext.casc.integrate.util.BomUtil;
import ext.casc.mpm.process.GLProcessParamValues;
import ext.casc.util.DBUtil;
import ext.casc.util.Tools;
import ext.casc.version.VersionCommonHelper;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import ext.casc.webservice.command.CommandHelper;
import ext.sast.common.fc.CmPersistenceHelper;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import wt.epm.EPMDocument;
import wt.log4j.LogR;
import wt.part.WTPart;

import java.util.HashMap;
import java.util.Map;

@Component
public class SaveParamsMapAndValuesCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(SaveParamsMapAndValuesCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "saveParamsMapAndValues";

	@Override
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

				for(int i=0;i<jparams.length();i++) {
					JSONObject	jparam  = jparams.getJSONObject(i);
					String cadName = jparam.optString("cadName");
					String cadVersion = jparam.optString("cadVersion");
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
					WTPart part = BomUtil.getPartByEPMDocument(epm);

					if(part == null){
						errorMsg.append(cadName+"在PDM系统无对应的零件;");
						continue;
					}
					String partNumber ="";
					partNumber = part.getNumber();
					String partVersion = "";
	                WTPart mpart = WTPartUtil.getLatestPartByNumberAndView(part, com.glaway.mpm.constants.Constants.planning);

	                if(mpart!=null){
	 	                 partVersion = VersionCommonHelper.getVersion(mpart);
	                }

					String templateId = jparam.optString("templateId");
					if(templateId!=null&&!templateId.startsWith("OR:wt.doc.WTDocument:")){
						templateId = "OR:wt.doc.WTDocument:" +templateId;
					}
					String templateName = jparam.optString("templateName");
					JSONArray datas = jparam.optJSONArray("datas");
					for(int j=0;j<datas.length();j++) {
						JSONObject	jsonObject  = datas.getJSONObject(j);
                        String gyParamNumber = jsonObject.optString("gyParamNumber");
                        String gyName = jsonObject.optString("name_gy");
                        String sjName = jsonObject.optString("name_sj");
                        String value = jsonObject.optString("value");
                        String unit = jsonObject.optString("unit");
                        String gongchengzhi = jsonObject.optString("gongchengzhi");
                        String shangpiancha = jsonObject.optString("shangpiancha");
                        String xiapiancha = jsonObject.optString("xiapiancha");
                        String fuhao = jsonObject.optString("fuhao");
                        String jizhun1 = jsonObject.optString("jizhun1");
                        String jizhun2 = jsonObject.optString("jizhun2");
                        String jizhun3 = jsonObject.optString("jizhun3");
                        if(Tools.isNull(gyParamNumber)){
		                	gyParamNumber = gyName;
		                }
						GLProcessParamValues processParamValues = new GLProcessParamValues();
						processParamValues.setKeyId(partNumber+"_"+gyParamNumber);
						processParamValues.setCadName(cadName);
						processParamValues.setCadVersion(cadVersion);
						processParamValues.setPartNumber(partNumber);
						processParamValues.setPartVersion(partVersion);
						processParamValues.setDataFrom("SENKE");
						processParamValues.setTemplateId(templateId);
						processParamValues.setTemplateName(templateName);
						processParamValues.setGyParamNumber(gyParamNumber);
						processParamValues.setGyParamName(gyName);
						processParamValues.setSjParamName(sjName);
						processParamValues.setParamValue(value);
						processParamValues.setParamUnit(unit);
						processParamValues.setGongChengZhi(gongchengzhi);
						processParamValues.setShangPianCha(shangpiancha);
						processParamValues.setXiaPianCha(xiapiancha);
						processParamValues.setFuHao(fuhao);
						processParamValues.setJiZhun1(jizhun1);
						processParamValues.setJiZhun2(jizhun2);
						processParamValues.setJiZhun3(jizhun3);
						CmPersistenceHelper.manager.save(processParamValues);


						Map<String,Object> setValue = new HashMap<String,Object>();
						setValue.put("SjParamName", sjName);

						Map<String,Object> where = new HashMap<String,Object>();
						where.put("TemplateId", templateId);
						where.put("GyParamName", gyName);

						DBUtil.updateByParams("GLProcessParamDefinition",setValue,where);



                    }

				}
			}
			if(StrUtil.isNotEmpty(errorMsg) || "N".equals(rtnCode)) {
				rtnCode = "N";
				rtnMsg = errorMsg.toString();
			} else {
				rtnMsg = "保存成功";
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
