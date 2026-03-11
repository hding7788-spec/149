/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command;

import com.ptc.extend.ixb.CmExpImpSearchHelper;
import ext.casc.integrate.util.CldeUtil;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.util.WTProperties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static ext.casc.util.SearchErrorProcessDocUtility.fileToBytes;


public class GetProcessStructureCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(GetProcessStructureCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "getProcessStructure";
	private static String zip_temp_dir ;
	static {
		WTProperties pro = null;
		try {
			pro = WTProperties.getLocalProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
		zip_temp_dir= pro.getProperty("wt.temp");
	}
	/**
	 {"rtnMsg":"获取成功","rtnCode":"S","data":{"technicsName":"导管工艺规程(Rz/AXX002（XZ）_0-07-1 F07-444)","pplanNumber":"Rz/AXX002（XZ）_0-07-1 F07-444","steps":[{"stepNumber":"10","paces":[{"procedureContent":"<html> <head> <\/head> <body> <p style='margin-top:5'> &nbsp;工步内容1 <\/p> <\/body> <\/html>","paceName":"","paceNumber":"1"},{"procedureContent":"<html> <head> <\/head> <body> <p style='margin-top:5'> &nbsp;工步内容2 <\/p> <\/body> <\/html>","paceName":"","paceNumber":"2"}],"stepName":"装填"},{"stepNumber":"20","paces":[{"procedureContent":"<html> <head> <\/head> <body> <p style='margin-top:5'> 工步内容3 <\/p> <\/body> <\/html>","paceName":"","paceNumber":"1"},{"procedureContent":"<html> <head> <\/head> <body> <p style='margin-top:5'> 工步内容4 <\/p> <\/body> <\/html>","paceName":"","paceNumber":"2"}],"stepName":"蓝白钝化"},{"stepNumber":"30","paces":[],"stepName":"检验"},{"stepNumber":"31","paces":[],"stepName":"焊接"},{"stepNumber":"40","paces":[],"stepName":"称重"},{"stepNumber":"41","paces":[],"stepName":"检验"},{"stepNumber":"60","paces":[],"stepName":"试验"}]}}
	 */
	@Override
	public String execute(String params) {
		String rtnCode ;
		String rtnMsg;
		JSONObject jrtnObj = new JSONObject();
		StringBuilder errorMsg = new StringBuilder("");
		JSONObject jparams;
		try {
			rtnCode = "S";
			if("".equals(params)){
				rtnCode = "N";
			}else {
				jparams = new JSONObject(params);
				String docNumber = jparams.getString("docNumber");
				WTDocument doc = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumber(WTDocument.class,
						docNumber);
				if (doc != null) {
					QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
					ApplicationData data = null;
					if (qr.hasMoreElements()) {
						data = (ApplicationData) qr.nextElement();
					}
					if (data != null) {
						String zipFilePath = zip_temp_dir + File.separator + "webService" + File.separator + doc.getNumber() + File.separator + doc.getNumber();
						File techDir = new File(zipFilePath);
						if (!techDir.exists()) {
							techDir.mkdirs();
						}

						String xmlFile = zipFilePath + File.separator + doc.getNumber() + ".xml";
						InputStream inputStream = ContentServerHelper.service.findContentStream(data);
						byte[] bytes = fileToBytes(inputStream);
						ZipUtil.unZip(bytes, zipFilePath);

						File file = new File(xmlFile);
						if (file.exists() && file.length() > 0) {
							SAXReader reader = new SAXReader();
							Document document = reader.read(file);
							Element rootElement = document.getRootElement();
							//工艺文件信息
							List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
							Element technicElement;
							for (Object object : pplanList) {
								JSONObject jsonObject  = new JSONObject();
								jrtnObj.put(WSConstants.DATA, jsonObject);
								technicElement = (Element) object;
								jsonObject.put("technicsName",technicElement.attributeValue("technicsName"));
								jsonObject.put("pplanNumber",technicElement.attributeValue("pplanNumber"));
								List<Element> stepElementList = technicElement.selectNodes("steps/QMProcedureInfo");
								JSONArray stepObjects = new JSONArray();
								jsonObject.put("steps",stepObjects);
								for (Element stepElement : stepElementList) {
									JSONObject stepObject = new JSONObject();
									stepObject.put("bsoID",stepElement.attributeValue("bsoID"));
									stepObject.put("preBsoID",stepElement.attributeValue("preBsoID"));
									stepObject.put("nextBsoID",stepElement.attributeValue("nextBsoID"));
									stepObject.put("stepNumber",stepElement.attributeValue("stepNumber"));
									stepObject.put("stepName",stepElement.attributeValue("stepName"));
									stepObject.put("gxjs",stepElement.attributeValue("GXJS"));

									stepObjects.put(stepObject);

									JSONArray paceObjects = new JSONArray();
									stepObject.put("paces",paceObjects);
									List<Element> paceElementList = stepElement.selectNodes("paces/QMProcedureInfo");
									for (Element paceElement : paceElementList) {
										JSONObject paceObject = new JSONObject();
										paceObject.put("preBsoID",paceElement.attributeValue("preBsoID"));
										paceObject.put("nextBsoID",paceElement.attributeValue("nextBsoID"));
										paceObject.put("paceNumber",paceElement.attributeValue("stepNumber"));
										paceObject.put("paceName",paceElement.attributeValue("stepName"));
										/*String procedureContent = paceElement.elementText("procedureContent");
										paceObject.put("procedureContent",procedureContent);
*/
										paceObjects.put(paceObject);
									}
								}
							}
						}
						//删除临时文件
						CldeUtil.deleteFiles(new File(zip_temp_dir + File.separator + "webService" + File.separator + doc.getNumber()));
					}

				}else{
					rtnCode = "N";
					errorMsg.append(docNumber + "工艺在PDM系统中不存在;");
				}
			}
			if ("S".equals(rtnCode)) {
				rtnMsg = "获取成功";
			} else {
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
