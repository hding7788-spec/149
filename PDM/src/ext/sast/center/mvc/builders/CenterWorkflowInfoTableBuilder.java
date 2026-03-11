package ext.sast.center.mvc.builders;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.*;
import com.ptc.mvc.components.ds.DataSourceMode;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.sast.center.bean.message.WorkflowMessage;
import ext.sast.center.processor.InvokeRestServiceProcessor;
import ext.sast.center.synch.MQConstants;
import ext.sast.center.util.WorkflowUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import wt.doc.WTDocument;
import wt.util.WTException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfProcess;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ComponentBuilder("ext.sast.center.mvc.builders.CenterWorkflowInfoTableBuilder")
public class CenterWorkflowInfoTableBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {
		List<WorkflowMessage> workflowMessageList = new ArrayList<WorkflowMessage>();
		WorkflowMessage workflowMessage;
		/**测试机*/
		String url = "http://"+ MQConstants.DC_IP+":"+MQConstants.DC_PORT+"/avidm/rest/dc/message/";
		/**正式机*/
//		String url = "http://10.112.1.33:8080/avidm/rest/dc/message/";
		// 用于签审包tab页
		WfProcess wfProcess = null;
		NmCommandBean cb = ((JcaComponentParams) componentParams).getHelperBean().getNmCommandBean();
		String workflowInfo = "";
		String messageId = "";
		if (cb.getPageOid() != null) {
			Object obj = cb.getPageOid().getRefObject();
			if (obj instanceof ChangePackaged) {
				ChangePackaged changePackaged = (ChangePackaged) obj;
				wfProcess = WorkflowUtil.getProcess(changePackaged);
			} else if (obj instanceof ProcessEnvelope) {
				ProcessEnvelope processEnvelope = (ProcessEnvelope) obj;
				wfProcess = WorkflowUtil.getProcess(processEnvelope);
			} else if (obj instanceof WTDocument) {
				WTDocument document = (WTDocument) obj;
				messageId = document.getNumber();
				if (messageId != null && !messageId.isEmpty()) {
					url += messageId;
				}
				workflowInfo = InvokeRestServiceProcessor.invokeRestService(url);
			}
			if (wfProcess != null) {
				ProcessData processdata = wfProcess.getContext();
				String mqmessage = processdata.getValue("mqmessage").toString();
				if (mqmessage != null && !mqmessage.isEmpty()) {
					JSONObject msgObject = new JSONObject(mqmessage);
					messageId = msgObject.getString("msg_id");
					if (messageId != null && !messageId.isEmpty()) {
						url +=  URLEncoder.encode(messageId ,"UTF-8");
					}
					workflowInfo = InvokeRestServiceProcessor.invokeRestService(url);
				}

			}
		}
		// 用于搜索界面
		// meta/query_by_site?start=&end=&type=&site=
		String searchNumber = String.valueOf(componentParams.getParameter("msg_id"));
		if (searchNumber != null && !"".equals(searchNumber) && !"null".equals(searchNumber)) {
			url += URLEncoder.encode(searchNumber ,"UTF-8");
			workflowInfo = InvokeRestServiceProcessor.invokeRestService(url);
		}
		// url += "meta/query_by_site?site=";
		// String startDate =
		// String.valueOf(componentParams.getParameter("startDate"));
		// String endDate =
		// String.valueOf(componentParams.getParameter("endDate"));
		// String msgType =
		// String.valueOf(componentParams.getParameter("msgType"));
		// if(startDate != null){
		// url = url + "start=" + startDate;
		// }
		// if(endDate != null){
		// url = url + "end=" + endDate;
		// }
		// if(msgType != null){
		// url = url + "msgType=" + msgType;
		// }
		// workflowInfo = InvokeRestServiceProcessor.invokeRestService(url);
		if (!workflowInfo.isEmpty()) {
			JSONArray jsonArray = new JSONArray(workflowInfo);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject msgObj = jsonArray.getJSONObject(i);
				String msgId = msgObj.getString("msg_id");
				String msgTime = msgObj.getString("msg_time");
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				long lt = new Long(msgTime);
				lt = lt + 8*60*60*1000;
				Date date = new Date(lt);
				String time = simpleDateFormat.format(date);
				String msgSite = msgObj.getString("msg_site");
				String msgContent = msgObj.getString("msg_content");
				String msgState = msgObj.getString("msg_state");
				String msgException = "";
				if (msgObj.has("msg_exception_stacktrace")) {
					msgException = msgObj.getString("msg_exception_stacktrace");
				}

				workflowMessage = new WorkflowMessage();
				workflowMessage.setUnit(msgSite);
				workflowMessage.setTaskInfo(msgContent);
				workflowMessage.setTaskStartTime(time);
				workflowMessage.setTaskRemark(msgState);
				workflowMessage.setTaskException(msgException);

				workflowMessageList.add(workflowMessage);
			}
		}
		return workflowMessageList;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		JcaTableConfig tableConfig = (JcaTableConfig) factory.newTableConfig();
		tableConfig.setDataSourceMode(DataSourceMode.SYNCHRONOUS);
		tableConfig.setSelectable(false);
		// tableConfig.setSingleSelect(true);

		tableConfig.setLabel("协同流程记录");

		ColumnConfig columnConfig1 = factory.newColumnConfig("unit", false);
		columnConfig1.setLabel("当前位置");
		tableConfig.addComponent(columnConfig1);

		// ColumnConfig columnConfig2 = factory.newColumnConfig("activityName",
		// false);
		// columnConfig2.setLabel("活动名称");
		// tableConfig.addComponent(columnConfig2);
		//
		// ColumnConfig columnConfig3 = factory.newColumnConfig("responsor",
		// false);
		// columnConfig3.setLabel("负责人");
		// tableConfig.addComponent(columnConfig3);
		//
		// ColumnConfig columnConfig4 = factory.newColumnConfig("role", false);
		// columnConfig4.setLabel("角色");
		// tableConfig.addComponent(columnConfig4);

		ColumnConfig columnConfig5 = factory.newColumnConfig("taskInfo", false);
		columnConfig5.setLabel("当前状态");
		tableConfig.addComponent(columnConfig5);

		ColumnConfig columnConfig6 = factory.newColumnConfig("taskStartTime", false);
		columnConfig6.setLabel("处理时间");
		tableConfig.addComponent(columnConfig6);

		ColumnConfig columnConfig7 = factory.newColumnConfig("taskException", false);
		columnConfig7.setLabel("错误信息");
		tableConfig.addComponent(columnConfig7);

		// ColumnConfig columnConfig7 = factory.newColumnConfig("taskEndTime",
		// false);
		// columnConfig7.setLabel("任务结束时间");
		// tableConfig.addComponent(columnConfig7);
		//
		// ColumnConfig columnConfig8 = factory.newColumnConfig("taskRemark",
		// false);
		// columnConfig8.setLabel("任务备注");
		// tableConfig.addComponent(columnConfig8);

		return tableConfig;
	}

}
