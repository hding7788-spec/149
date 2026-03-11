package ext.casc.webservice.command.gy;

import cn.hutool.core.util.StrUtil;
import ext.casc.mpm.GyCsServerHelper;
import ext.casc.mpm.process.GLProcessTemplateLink;
import ext.casc.part.cache.JsonObjectCache;
import ext.casc.persistence.PersistenceCommonHelper;
import ext.casc.util.DocUtil;
import ext.casc.util.IBAHelper;
import ext.casc.util.Tools;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import ext.casc.webservice.command.CommandHelper;
import ext.sast.common.fc.CmPersistable;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.log4j.LogR;
import wt.util.WTException;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class GetGyTemplateListCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(GetGyTemplateListCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "getGyTemplateList";
	boolean cacheFlag = false;

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
				JSONArray jsonArray = new JSONArray();
				jrtnObj.put(WSConstants.RTN_DATA, jsonArray);
				for(int i=0;i<jparams.length();i++) {
					JSONObject	jparam  = jparams.getJSONObject(i);
					String cadName = jparam.optString("cadName");

					String cadVersion = jparam.optString("cadVersion");
					if(cadVersion.contains(".")){
						String[] ss = cadVersion.split("\\.");
						cadVersion = ss[0];
					}
					EPMDocument epm = null;
					if (!Tools.isNull(cadName)) {
						 epm = CommandHelper.getEPMDocumentByCADNumber(cadName,cadVersion);
					}
					if(epm == null){
						errorMsg.append(cadName+"在PDM系统不存在;");
						continue;
					}


					JSONObject data = new JSONObject();
					jsonArray.put(data);

					data.put("cadName",cadName);
					JSONArray templateList = new JSONArray();
					data.put("templateList",templateList);
					data.put("cadVersion",jparam.optString("cadVersion"));

					String modeType = jparam.optString("modeType");
					JSONObject params2 = jparam.optJSONObject("params");

					if(params2!=null ){
						String clfl = params2.optString("clfl");
						String yxx = params2.optString("yxx");
						String rcl = params2.optString("rcl");
						String bmcl = params2.optString("bmcl");
						if(Tools.isNull(clfl)&&Tools.isNull(yxx)&&Tools.isNull(rcl)&&Tools.isNull(bmcl)){

							//根据专业获取模板清单
							if(!Tools.isNull(modeType)){
								if(cacheFlag){
									if(JsonObjectCache.gyCsTemplateCache.get(modeType)!=null){
										data.put("templateList",JsonObjectCache.gyCsTemplateCache.get(modeType));
									}else{
										getCommonTemplate(templateList,modeType);
										JsonObjectCache.gyCsTemplateCache.put(modeType,templateList);
									}
								}else{
									getCommonTemplate(templateList,modeType);
								}


							}else{
								errorMsg.append(cadName+"的模型类型输入错误！");
							}
						}else{
							//根据映射配置获取模板清单
							Map<String,String> queryParams = new HashMap<String, String>();
							queryParams.put("CAILIAOFENLEI",clfl);
							queryParams.put("YAXIAXIAN",yxx);
							queryParams.put("RECHULI",rcl);
							queryParams.put("BIAOMIANCHULI",bmcl);
							String cachekey = queryParams.toString();
							if(JsonObjectCache.gyCsTemplateCache.get(cachekey)!=null){
								data.put("templateList",JsonObjectCache.gyCsTemplateCache.get(cachekey));
							}else{
								List<CmPersistable> list = GyCsServerHelper.queryGLObjects(GLProcessTemplateLink.class,queryParams);
								for(CmPersistable p:list){
									GLProcessTemplateLink templateLink = (GLProcessTemplateLink)p;
									String templateNumber = templateLink.getTemplateNumber();
									WTDocument doc = DocUtil.getDoc(templateNumber,false);
									if(doc!=null){
										JSONObject jsonDoc  = genTemplateJson(doc,modeType);
										templateList.put(jsonDoc);
									}
								}
								JsonObjectCache.gyCsTemplateCache.put(cachekey,templateList);
							}
						}
					}else{
						if(!Tools.isNull(modeType)){
							if(JsonObjectCache.gyCsTemplateCache.get(modeType)!=null){
								data.put("templateList",JsonObjectCache.gyCsTemplateCache.get(modeType));
							}else{
								getCommonTemplate(templateList,modeType);
								JsonObjectCache.gyCsTemplateCache.put(modeType,templateList);
							}

						}else{
							errorMsg.append(cadName+"的模型类型输入错误！");
							continue;
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

	private void getCommonTemplate(JSONArray templateList, String modeType) throws RemoteException, WTException {
		Map<String,String> paramsMap = new HashMap<String, String>();
		paramsMap.put(DocUtil.QUERY_TYPE,"wt.doc.WTDocument|casc.sast.149.TechnicsTemplate|casc.sast.149.TechnicsParamTemplate");
		paramsMap.put(DocUtil.QUERY_CONTAINER_ID,"");
		Map<String,String> ibaMap = new HashMap<String, String>();
		ibaMap.put("productType",modeType);
		List<WTDocument> docs = DocUtil.getLastestDocs(paramsMap,ibaMap);
		for(WTDocument doc :docs){
			JSONObject jsonDoc  = genTemplateJson(doc,modeType);
			templateList.put(jsonDoc);
		}

	}

	private JSONObject genTemplateJson(WTDocument doc, String modeType) throws WTException {
		JSONObject jsonDoc = new JSONObject();
		jsonDoc.put("templateId",PersistenceCommonHelper.getOid(doc));
		jsonDoc.put("templateNumber",doc.getNumber());
		jsonDoc.put("name",doc.getName());
		jsonDoc.put("productType",modeType);
		jsonDoc.put("speciality", IBAHelper.getIBAStringValue(doc,"speciality"));
		jsonDoc.put("DEPT", IBAHelper.getIBAStringValue(doc,"DEPT"));
		return jsonDoc;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}
}
