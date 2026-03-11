/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.gy;

import com.glaway.mpm.util.ReferenceFactory;
import ext.casc.util.Tools;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import ext.casc.workflow.CmWorkflowHelper;
import ext.casc.workflow.PrintHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.work.WfAssignedActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 类功能：主辅工艺关联关系查询
 *
 * @author cjh
 * @date 2022/11/28
 */
@Component
public class SKFeedBackResultCommand implements WebServiceCommand, InitializingBean {
	// 方法标识
	public static final String METHOD_NAME = "feedBackSKResult";

	@Override
	public String execute(String params) {
		String errorMsg = null;
		JSONArray msg = new JSONArray();
		JSONObject jparams = null;
		try {
			jparams = new JSONObject(params);
		} catch (JSONException e) {
			errorMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
		}
		String docOid = jparams.getString("docOid");


		if(!Tools.isNull(docOid)){
			/*
			String state = jparams.getString("state");
			if(Tools.isNull(state)||state.contains("完成")){

			}*/
			try {
				WTDocument doc =(WTDocument) ReferenceFactory.getObjectbyOid(docOid);
				QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(doc, null, null);
				while (qrProcs.hasMoreElements()) {
					WfProcess proc = (WfProcess) qrProcs.nextElement();
					if (proc.getState().equals(WfState.OPEN_RUNNING)){
						List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
						activityList = PrintHelper.getActivities(proc, activityList);
						for (WfAssignedActivity wfAssignedActivity : activityList) {
							String state = wfAssignedActivity.getState().toString();
							if ("OPEN_RUNNING".equals(state)) {
								if(wfAssignedActivity.getTemplate().getName().equals("等待数字样机解析")){
									CmWorkflowHelper.completeActivity(wfAssignedActivity.getPersistInfo().getObjectIdentifier().toString(), "", "森科解析完成");
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				errorMsg = "通过oid获取对象失败:"+e.getLocalizedMessage() ;
			}
		}else{
			errorMsg = "docOid不能为空";
		}
		JSONObject rtnMsgObj = new JSONObject();
		try {
			if(errorMsg!=null&&!"".equals(errorMsg)){
				rtnMsgObj.put("status", "N");
				rtnMsgObj.put("result", errorMsg);
			}else{
				rtnMsgObj.put("status", "S");
				rtnMsgObj.put("result", msg);
			}
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
