/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command;


import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.util.WTDocumentUtil;
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
import wt.util.WTException;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 类功能：PBOM状态查询接口
 *
 * @author hding
 * @date 2020/6/24
 */
public class GetSignInfoCommand implements WebServiceCommand, InitializingBean {
	static Logger LOGGER = Logger.getLogger(GetSignInfoCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "getSignInfo";
	@Override
	public String execute(String params) {
		String rtnCode = "E";
		String rtnMsg="";
        JSONObject jrtnObj = new JSONObject();

		JSONObject jparams;
		try {
			jparams = new JSONObject(params);
			String docNumber =jparams.optString("docNumber");

			WTDocument pbo= WTDocumentUtil.getDocumentByNumber(docNumber);
			if(pbo!=null){
				String ecnBiaoJi = ProcessEditorToWCIntfRMI.getChangeBiaoJiByTechnics(docNumber);
				String ecnNumber = "";
				JSONObject returnData = new JSONObject() ;
				returnData.put("ecnBiaoJi", ecnBiaoJi);
				WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(pbo);
				Iterator it = coll.iterator();
				QueryResult qrProcs = null;
				WTChangeOrder2 ecn = null;
				if (it.hasNext()) {
					ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
					ecnNumber = ecn.getNumber();
					 //qrProcs = WfEngineHelper.service.getAssociatedProcesses(ecn, null, null);
				}else{
					// qrProcs = WfEngineHelper.service.getAssociatedProcesses(pbo, null, null);
				}
				GLFilePrintData filePrintData = GLFilePrintDataHelper.getObject(pbo);
				if(filePrintData!=null){
						JSONObject signObject = new JSONObject();
						signObject.put("ecnBiaoJi", ecnBiaoJi);
						signObject.put("ecnNumber", ecnNumber);
						returnData.put("signObject", signObject);
						JSONObject signInfo = new JSONObject(filePrintData.getPrintData()) ;
						String value = signInfo.optString("SHEJI");
						if(value!=null &&!"".equals(value)){
							String[] ss = value.split("/");
							if(ss.length==3){
								signObject.put("编制者", ss[0]);
								signObject.put("编制者时间", ss[2]);
								if(ecnNumber!=null &&!"".equals(ecnNumber)){
									signObject.put("GENGGAI", ss[0]);
									signObject.put("GENGGAISHIJIAN", ss[2]);

								}
							}
						}
						value = signInfo.optString("JIAODUI");
						if(value!=null &&!"".equals(value)){
							String[] ss = value.split("/");
							if(ss.length==3){
								signObject.put("校对者", ss[0]);
								signObject.put("校对者时间", ss[2]);
							}
						}

						value = signInfo.optString("SHENHE");
						if(value!=null &&!"".equals(value)){
							String[] ss = value.split("/");
							if(ss.length==3){
								signObject.put("审核者", ss[0]);
								signObject.put("审核者时间", ss[2]);
							}
						}
						value = signInfo.optString("BIAOSHEN");
						if(value!=null &&!"".equals(value)){
							String[] ss = value.split("/");
							if(ss.length==3){
								signObject.put("标审者", ss[0]);
								signObject.put("标审者时间", ss[2]);
							}
						}
						value = signInfo.optString("PIZHUN");
						if(value!=null &&!"".equals(value)){
							String[] ss = value.split("/");
							if(ss.length==3){
								signObject.put("批准者", ss[0]);
								signObject.put("批准者时间", ss[2]);
							}
						}

				   		 value = signInfo.optString("HUIQIAN1");
						if(value!=null &&!"".equals(value)){
							String[] huiqians = value.split(";");
							for(int i=0;i<huiqians.length;i++){
								if(!"".equals(huiqians[i])){
									String[] ss = huiqians[i].split("/");
									signObject.put((i+1)+"", ss[0]);
									signObject.put((i+1)+"时间", ss[2]);
									signObject.put((i+1)+"部门", ss[1]);
								}
							}

						}

				}else{
					if(ecn!=null){
						qrProcs = WfEngineHelper.service.getAssociatedProcesses(ecn, null, null);
					}else{
						qrProcs = WfEngineHelper.service.getAssociatedProcesses(pbo, null, null);
					}
					if (qrProcs!=null && qrProcs.hasMoreElements()) {
						WfProcess proc = (WfProcess) qrProcs.nextElement();
						if (proc.getState().equals(WfState.OPEN_RUNNING) || proc.getState().equals(WfState.CLOSED_COMPLETED_EXECUTED)) {
							//获取签名信息
							/**
							 * INFO  : ext.casc.webservice.command.GetSignInfoCommand wcadmin - {wt.doc.WTDocum
							 ent:4253246={MODIFIER=胡, 慧莉, HUIQIAN1=胡, 慧莉/一车间/2021-09-23;苏林林/九车
							 间/2021-09-23, SHENHESHIJIAN=2021-09-23, ECN_MODIFYTIME=, VERSIONITE=space.1, BI
							 AOSHENSHIJIAN=2021-09-23, PIZHUN=胡, 慧莉/一车间/2021-09-23, JIAODUISHIJIAN=2021
							 -09-23, BIAOSHEN=胡, 慧莉/一车间/2021-09-23, NAME=钣金工艺规程(Rz/20200608-1-1Z0
							 1(789)), PIZHUNSHIJIAN=2021-09-23, MIJI=公开, KEYWORD=, HUIQIAN1SHIJIAN=2021-09-
							 23, SHEJISHIJIAN=2021-09-23, NUMBER=1632365339981, SHEJIZHESHIJIAN=胡, 慧莉/一车
							 间/2021-09-23, BIAOZHI=Y, PIZHUNZHESHIJIAN=胡, 慧莉/一车间/2021-09-23, JIAODUIZH
							 ESHIJIAN=胡, 慧莉/一车间/2021-09-23, MODIFYTIME=2021-9-23 2:51:29, DEPT=1, SHEJI
							 =胡, 慧莉/一车间/2021-09-23, BIAOSHENZHESHIJIAN=胡, 慧莉/一车间/2021-09-23, SHEN
							 HEZHESHIJIAN=胡, 慧莉/一车间/2021-09-23, HUIQIAN1ZHESHIJIAN=胡, 慧莉/一车间/2021
							 -09-23;苏林林/九车间/2021-09-23, VERSION=space, NEIBUHUIQIAN=胡, 慧莉/一车间/202
							 1-09-23;苏林林/九车间/2021-09-23, ECN_NUMBER=, JIAODUI=胡, 慧莉/一车间/2021-09-2
							 3, SUMMARY=, SHENHE=胡, 慧莉/一车间/2021-09-23, ECN_MODIFIER=, DOCTYPE=BANJIN_PR
							 OCESSPLAN}}
							 */


							Hashtable hashtable = PrintHelper.getPrintInfo(pbo, proc);
							Set<Map.Entry<String, Hashtable<String, String>>> entrys = hashtable.entrySet();
							for(Map.Entry<String, Hashtable<String, String>> entry:entrys){
								Hashtable<String, String> signInfoTable  = entry.getValue();
								Set<Map.Entry<String,String>> signEntrys = signInfoTable.entrySet();
								JSONObject signObject = new JSONObject();
								signObject.put("ecnBiaoJi", ecnBiaoJi);
								signObject.put("ecnNumber", ecnNumber);
								returnData.put("signObject", signObject);
								for(Map.Entry<String,String> signEntry:signEntrys){
									String key = signEntry.getKey();
									String value = signEntry.getValue();
									if("SHEJI".equals(key)){
										if(value!=null &&!"".equals(value)){
											String[] ss = value.split("/");
											if(ss.length==3){
												signObject.put("编制者", ss[0]);
												signObject.put("编制者时间", ss[2]);
												if(ecnNumber!=null &&!"".equals(ecnNumber)){
													signObject.put("GENGGAI", ss[0]);
													signObject.put("GENGGAISHIJIAN", ss[2]);

												}
											}
										}

									}else if("JIAODUI".equals(key)){
										if(value!=null &&!"".equals(value)){
											String[] ss = value.split("/");
											if(ss.length==3){
												signObject.put("校对者", ss[0]);
												signObject.put("校对者时间", ss[2]);
											}
										}

									}else if("SHENHE".equals(key)){
										if(value!=null &&!"".equals(value)){
											String[] ss = value.split("/");
											if(ss.length==3){
												signObject.put("审核者", ss[0]);
												signObject.put("审核者时间", ss[2]);
											}
										}
									}else if("BIAOSHEN".equals(key)){
										if(value!=null &&!"".equals(value)){
											String[] ss = value.split("/");
											if(ss.length==3){
												signObject.put("标审者", ss[0]);
												signObject.put("标审者时间", ss[2]);
											}
										}
									}else if("PIZHUN".equals(key)){
										if(value!=null &&!"".equals(value)){
											String[] ss = value.split("/");
											if(ss.length==3){
												signObject.put("批准者", ss[0]);
												signObject.put("批准者时间", ss[2]);
											}
										}
									}
									else if("HUIQIAN1".equals(key)){
										if(value!=null &&!"".equals(value)){
											String[] huiqians = value.split(";");
											for(int i=0;i<huiqians.length;i++){
												if(!"".equals(huiqians[i])){
													String[] ss = huiqians[i].split("/");
													signObject.put((i+1)+"", ss[0]);
													signObject.put((i+1)+"时间", ss[2]);
													signObject.put((i+1)+"部门", ss[1]);
												}
											}

										}
									}

								}

							}
							LOGGER.info(hashtable);
						}
					}
				}


				jrtnObj.put(WSConstants.RTN_DATA, returnData);
				rtnCode = "S";
				rtnMsg = "签名信息获取成功";
			}else{
				rtnCode = "S";
				rtnMsg = "没找到相应工艺";
			}

		} catch (JSONException e) {
			rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
			LOGGER.error(rtnMsg, e);
		} catch (WTException e) {
			rtnMsg = "工艺迁移失败： " + e.getLocalizedMessage();
			LOGGER.error(rtnMsg, e);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
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
