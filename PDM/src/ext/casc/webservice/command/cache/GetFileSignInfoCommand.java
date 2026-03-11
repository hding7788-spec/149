/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.cache;

import com.ptc.extend.ixb.CmExpImpSearchHelper;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import ext.casc.fileprint.cache.GLFilePrintData;
import ext.casc.fileprint.cache.GLFilePrintDataHelper;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import ext.casc.workflow.PrintHelper;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.log4j.LogR;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class GetFileSignInfoCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(GetFileSignInfoCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "getFileSignInfo";

	/**
	 {"rtnMsg":"获取成功","rtnCode":"S","data":{"GENGGAISHIJIAN":"2023-07-28","GENGGAI":"Site, Administrator",
	 "HUIQIAN2ZHESHIJIAN":"张三/149/2023-07-28;李四/800/2023-07-28","JIAODUIZHESHIJIAN":"胡, 慧莉/一车间/2023-07-28","NUMBER":"1663306129293",
	 "SHEJIZHESHIJIAN":"Site, Administrator/八车间/2023-07-28","VERSION":"space","NEIBUHUIQIAN":"胡, 慧莉/一车间/2023-07-28",
	 "SHENHEZHESHIJIAN":"胡, 慧莉/一车间/2023-07-28","NAME":"电装工艺(RZ/1CF3402-103Z05)","WAIBUHUIQIAN":"张三/149/2023-07-28;李四/800/2023-07-28",
	 "HUIQIAN1ZHESHIJIAN":"胡, 慧莉/一车间/2023-07-28","MIJI":"内部","HUIQIAN1SHIJIAN":"2023-07-28","BIAOSHENZHESHIJIAN":"胡, 慧莉/一车间/2023-07-28",
	 "PIZHUNZHESHIJIAN":"胡, 慧莉/一车间/2023-07-28"}}
	 */
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
			}else {
				jparams = new JSONObject(params);
				String docNumber = jparams.getString("docNumber");
				String docVersion = jparams.getString("docVersion");
				String[] ss = docVersion.split("\\.");
				WTDocument doc = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(WTDocument.class,
						docNumber, ss[0], ss[1]);
				if (doc != null) {
					GLFilePrintData data = GLFilePrintDataHelper.getObject(doc);
					if (data != null) {
						String signInfo = data.getPrintData();
						JSONObject jsonObject = new JSONObject(signInfo);
						remove(jsonObject);
						jrtnObj.put(WSConstants.DATA, jsonObject);
					} else {
						WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(doc);
						Iterator it = coll.iterator();
						QueryResult qrProcs = null;
						if (it.hasNext()) {
							WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
							qrProcs = WfEngineHelper.service.getAssociatedProcesses(ecn, null, null);
						} else {
							qrProcs = WfEngineHelper.service.getAssociatedProcesses(doc, null, null);
						}
						if (qrProcs != null && qrProcs.hasMoreElements()) {
							WfProcess proc = (WfProcess) qrProcs.nextElement();
							if (proc.getState().equals(WfState.OPEN_RUNNING) || proc.getState().equals(WfState.CLOSED_COMPLETED_EXECUTED)) {
								Hashtable hashtable = PrintHelper.getPrintInfo(doc, proc);
								Set<Map.Entry<String, Hashtable<String, String>>> entrys = hashtable.entrySet();
								for(Map.Entry<String, Hashtable<String, String>> entry:entrys) {
									Hashtable<String, String> signInfoTable = entry.getValue();
									GLFilePrintDataHelper.cache(signInfoTable,doc);
									JSONObject jsonObject = new JSONObject(signInfoTable);
									remove(jsonObject);
									jrtnObj.put(WSConstants.DATA, jsonObject);
								}
							}
						}
					}
				}else{
					rtnCode = "N";
					errorMsg.append(docNumber + "(" + docVersion + ")工艺在PDM系统中不存在;");
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

	private void remove(JSONObject jsonObject) {
		jsonObject.remove("SHEJISHIJIAN");
		jsonObject.remove("MODIFYTIME");
		//jsonObject.remove("SUMMARY");
		//jsonObject.remove("KEYWORD");
		//jsonObject.remove("ECN_NUMBER");
		jsonObject.remove("PIZHUNSHIJIAN");
		jsonObject.remove("SHENHESHIJIAN");
		jsonObject.remove("MODIFIER");
		//jsonObject.remove("ECN_MODIFIER");
		jsonObject.remove("BIAOSHENSHIJIAN");
		//jsonObject.remove("DEPT");
		//jsonObject.remove("ECN_MODIFYTIME");
		//jsonObject.remove("DOCTYPE");
		jsonObject.remove("JIAODUISHIJIAN");
		//jsonObject.remove("HUIQIAN2SHIJIAN");
		//jsonObject.remove("BIAOZHI");
		jsonObject.remove("SHEJI");
		jsonObject.remove("JIAODUI");
		jsonObject.remove("HUIQIAN1");
		jsonObject.remove("HUIQIAN2");
		jsonObject.remove("BIAOSHEN");
		jsonObject.remove("SHENHE");
		jsonObject.remove("PIZHUN");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}
}
