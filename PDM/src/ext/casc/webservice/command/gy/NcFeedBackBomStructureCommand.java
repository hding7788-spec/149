/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.gy;

import com.ptc.extend.ixb.CmExpImpSearchHelper;
import ext.casc.integrate.nc.ErpSynchHelper;
import ext.casc.integrate.nc.SynchDocRecord;
import ext.casc.integrate.nc.SynchPartRecord;
import ext.casc.util.DBUtil;
import ext.casc.util.DocUtil;
import ext.casc.util.Tools;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import ext.sast.common.fc.CmPersistable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.inf.container.WTContained;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfEngineServerHelper;
import wt.workflow.engine.WfProcess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类功能：主辅工艺关联关系查询
 *
 * @author cjh
 * @date 2022/11/28
 */
@Component
public class NcFeedBackBomStructureCommand implements WebServiceCommand, InitializingBean {
	// 方法标识
	public static final String METHOD_NAME = "feedBackBomStructure";

	@Override
	public String execute(String params) {
		String errorMsg = null;
		JSONObject jparams = null;
		try {
			jparams = new JSONObject(params);
		} catch (JSONException e) {
			errorMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
		}
		String instanceId = jparams.getString("instanceId");
		if(!Tools.isNull(instanceId)){
			String state = jparams.optString("state");
			String message = jparams.optString("msg");
			if(Tools.isNull(state)) state = "NC返回空";

			Map<String,Object> setValue = new HashMap<String,Object>();
			setValue.put("SYNCHSTATE", state);
			setValue.put("NOTE", message);

			Map<String,Object> where = new HashMap<String,Object>();
			where.put("instanceId", instanceId);
			DBUtil.updateByParams("SYNCHPARTRECORD",setValue,where);
			DBUtil.updateByParams("SYNCHDOCRECORD",setValue,where);
			if(state.contains("失败")) {
                WfProcessDefinition wfprocessdefinition = null;
                try {
					CmPersistable record= ErpSynchHelper.getRecord(instanceId);
					WTContained pbo = null;
					String importer = "";
					if(record!=null) {
						if(record instanceof SynchDocRecord){
							SynchDocRecord docRecord = (SynchDocRecord) record;
							List<SynchDocRecord> tmpRecords =  ErpSynchHelper.queryDocRecord(docRecord.getObjectNumber());
							if(tmpRecords.size()>0){
								if(tmpRecords.get(0).getSynchTime().equals(docRecord.getSynchTime())){
									String[] ss = docRecord.getObjectVersion().split("\\.");
									pbo =(WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(WTDocument.class,docRecord.getObjectNumber(),
											ss[0], ss[1]);
									if("流程主动".equals(docRecord.getImporter())) {
										importer = ((WTDocument) pbo).getModifierName();
									}else {
										importer = docRecord.getImporter();
									}

								}
							}
						}else if (record instanceof SynchPartRecord){
							SynchPartRecord partRecord = (SynchPartRecord) record;
							List<SynchPartRecord> tmpRecords =  ErpSynchHelper.queryPartRecord(partRecord.getObjectNumber());
							if(tmpRecords.size()>0){
								if(tmpRecords.get(0).getSynchTime().equals(partRecord.getSynchTime())){
									String[] ss = partRecord.getObjectVersion().split("\\.");
									pbo =(WTPart) CmExpImpSearchHelper.searchWTPartByNumberVersionIterationView(partRecord.getObjectNumber(),
											ss[0], ss[1],"Manufacturing");
									importer = partRecord.getImporter();

								}
							}

						}
						if( pbo!=null){
							boolean access = SessionServerHelper.manager.setAccessEnforced(false);
							try {

								wfprocessdefinition = WfDefinerHelper.service
										.getProcessDefinition("系统集成失败提醒");
								WfProcess wfprocess = WfEngineHelper.service.createProcess(
										wfprocessdefinition, null, pbo.getContainerReference());
								wfprocess.setName("系统集成失败提醒" + "_" + ((RevisionControlled) pbo).getName());
								wt.org.WTUser wtuser = wt.org.OrganizationServicesHelper.manager.getAuthenticatedUser(importer);
								wt.org.WTPrincipalReference ref = wt.org.WTPrincipalReference.newWTPrincipalReference(wtuser);
								wfprocess.setCreator(ref);
								wfprocess = WfEngineServerHelper.service.setPrimaryBusinessObject(wfprocess, (RevisionControlled)pbo);

								ProcessData processdata = wfprocess.getContext();
								processdata.setValue("msg", message);
								WfEngineHelper.service.startProcess(wfprocess, processdata, 1);
							}finally {
								SessionServerHelper.manager.setAccessEnforced(access);

							}
						}

					}

                } catch (Exception e) {
                    e.printStackTrace();
					errorMsg = "反馈处理失败："+e.getLocalizedMessage();
                }


			}

		}else{
			errorMsg = "instanceId不能为空";
		}
		JSONObject rtnMsgObj = new JSONObject();
		try {
			if(errorMsg!=null&&!"".equals(errorMsg)){
				rtnMsgObj.put("status", "N");
			}else{
				rtnMsgObj.put("status", "S");
			}
			rtnMsgObj.put("result", errorMsg);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rtnMsgObj.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}
}
