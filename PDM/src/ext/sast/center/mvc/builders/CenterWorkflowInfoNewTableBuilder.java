package ext.sast.center.mvc.builders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import wt.util.WTException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfProcess;

import com.bjsasc.avidm.mq.message.Based;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ds.DataSourceMode;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.process.util.ProcessUtil;
import ext.sast.center.bean.message.WorkflowMessage;
import ext.sast.center.processor.InvokeRestServiceProcessor;
import ext.sast.center.synch.MQConstants;
import ext.sast.center.util.WorkflowUtil;

@ComponentBuilder("ext.sast.center.mvc.builders.CenterWorkflowInfoNewTableBuilder")
public class CenterWorkflowInfoNewTableBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {
        List<WorkflowMessage> workflowMessageList = new ArrayList<WorkflowMessage>();
        WorkflowMessage workflowMessage;

        NmCommandBean commandBean = ((JcaComponentParams) componentParams).getHelperBean().getNmCommandBean();
        String sendStartDate = (String) commandBean.getText().get("sendStart");
		String sendEndDate = (String) commandBean.getText().get("sendEnd");
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Long startTime = null;
		Long endTime = null;
		String msgType = null;
		String url = "";
		String siteIID = MQConstants.SITEIID_149;
		// modify by zxu 20210524日期条件判断容错处理
		if(sendStartDate != null && sendStartDate.trim().length() > 0){
			Date startDate = format.parse(sendStartDate);
			startTime = startDate.getTime();
		}
		Date endDate = null;
		if(sendEndDate != null && sendEndDate.trim().length() > 0){
			endDate = format.parse(sendEndDate);
			endTime = endDate.getTime();
		}
		if(endDate == null){
			// 当前时间
			endDate = new Date();
			endTime = endDate.getTime();
		}
		if(startTime == null){
			// 未指定开始时间,则使用结束时间往前推一个月
			Calendar c = Calendar.getInstance();
			c.setTime(endDate);
			c.add(Calendar.MONTH, -1);
			String start = format.format(c.getTime());
			Date date1 = format.parse(start);
			startTime = date1.getTime();
		}
		
		msgType = (String) commandBean.getText().get("msgType");
		if(msgType == null || "".equals(msgType)){
			url = "http://"+MQConstants.DC_IP+":"+MQConstants.DC_PORT+"/avidm/rest/dc/message/meta/query_by_site_types?start="
	        		+startTime+"&end="+endTime+"&type=meta_msg_type_signature&type=meta_msg_type_distribute&type=meta_msg_type_share&site="+siteIID;
		} else{
			if(msgType.equals("变更单") || msgType.equals("送审单")){
				msgType = Based.META_MSG_TYPE_SIGNATURE;
			}else if(msgType.equals("发放单")){
				msgType = Based.META_MSG_TYPE_DISTRIBUTE;
			}else if(msgType.equals("预审单")){
				msgType = Based.META_MSG_TYPE_SHARE;
			}
			url ="http://"+MQConstants.DC_IP+":"+MQConstants.DC_PORT+"/avidm/rest/dc/message/meta/query_by_site_types?start="
	        		+startTime+"&end="+endTime+"&type="+msgType+"&site="+siteIID;
		}


        String workflowInfo = InvokeRestServiceProcessor.invokeRestService(url);

        if(!workflowInfo.isEmpty()){
            JSONArray jsonArray = new JSONArray(workflowInfo);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject msgObj = jsonArray.getJSONObject(i);
                String msgId = msgObj.getString("msg_id");
                String msgSendName = msgObj.getString("src_site_name");
                String msgReceiveName = msgObj.getString("dst_sites_info");
                String msgState = msgObj.getString("msg_status");
                String ordername = msgObj.getString("order_name");
                String orderid = msgObj.getString("order_id");
                if(orderid == null || "".equals(orderid))
                	orderid = msgObj.getString("order_iid");
                if(msgState.equals("1")){
                	msgState = "新建";
                }else if(msgState.equals("2")){
                	msgState = "处理中";
                }else if(msgState.equals("3")){
                	msgState = "处理中（有异常）";
                }else if(msgState.equals("11")){
                	msgState = "已完成";
                }else if(msgState.equals("12")){
                	msgState = "失败";
                }
                String msgTime = msgObj.getString("create_time");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long lt = new Long(msgTime);
                Date date = new Date(lt+8*60*60*1000);
                String time = simpleDateFormat.format(date);
                String msgException = "";
                if(msgObj.has("msg_exception_stacktrace")){
                    msgException = msgObj.getString("msg_exception_stacktrace");
                }

                workflowMessage = new WorkflowMessage();
                workflowMessage.setUnit(msgId);
                workflowMessage.setOrderid(orderid);
                workflowMessage.setOrdername(ordername);
                workflowMessage.setTaskRemark(msgSendName);
                workflowMessage.setResponsor(msgReceiveName);
                workflowMessage.setTaskStartTime(time);
                workflowMessage.setTaskInfo(msgState);
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
        tableConfig.setActionModel("custom_export_processTask");
//        tableConfig.setSingleSelect(true);

        tableConfig.setLabel("协同流程查看");

        ColumnConfig columnConfig1 = factory.newColumnConfig("unit", false);
        columnConfig1.setLabel("msg_id");
        columnConfig1.setHidden(true);
        tableConfig.addComponent(columnConfig1);

        ColumnConfig columnConfig7 = factory.newColumnConfig("orderid", false);
        columnConfig7.setLabel("单号");
        columnConfig7.setAutoSize(true);
        tableConfig.addComponent(columnConfig7);

        ColumnConfig columnConfig6 = factory.newColumnConfig("ordername", false);
        columnConfig6.setLabel("单号名称");
        columnConfig6.setAutoSize(true);
        tableConfig.addComponent(columnConfig6);

        ColumnConfig columnConfig2 = factory.newColumnConfig("taskRemark", false);
        columnConfig2.setAutoSize(true);
        columnConfig2.setLabel("发送单位");
        tableConfig.addComponent(columnConfig2);

        ColumnConfig columnConfig3 = factory.newColumnConfig("responsor", false);
        columnConfig3.setAutoSize(true);
        columnConfig3.setLabel("接收单位");
        tableConfig.addComponent(columnConfig3);

        ColumnConfig columnConfig4 = factory.newColumnConfig("taskStartTime", false);
        columnConfig4.setAutoSize(true);
        columnConfig4.setLabel("发送时间");
        tableConfig.addComponent(columnConfig4);

        ColumnConfig columnConfig5 = factory.newColumnConfig("taskInfo", false);
        columnConfig5.setAutoSize(true);
        columnConfig5.setLabel("当前状态");
        columnConfig5.setInfoPageLink(true);
        columnConfig5.setDataUtilityId("CenterWorkflowInfoUtility");
        tableConfig.addComponent(columnConfig5);

        ColumnConfig operation = factory.newColumnConfig("operation", false);
        operation.setAutoSize(true);
        operation.setLabel("中心域操作");
        operation.setDataUtilityId("CenterWorkflowInfoUtility");
        tableConfig.addComponent(operation);

        ColumnConfig operation2 = factory.newColumnConfig("operation2", false);
        operation2.setAutoSize(true);
        operation2.setLabel("本地操作");
        operation2.setDataUtilityId("CenterWorkflowInfoUtility");
        tableConfig.addComponent(operation2);


        ColumnConfig feedback = factory.newColumnConfig("feedback", false);
        feedback.setAutoSize(true);
        feedback.setLabel("发送意见");
        feedback.setDataUtilityId("CenterWorkflowInfoUtility");
        tableConfig.addComponent(feedback);

        ColumnConfig receiveFeedback = factory.newColumnConfig("receiveFeedback", false);
        receiveFeedback.setAutoSize(true);
        receiveFeedback.setLabel("接收意见");
        receiveFeedback.setDataUtilityId("CenterWorkflowInfoUtility");
        tableConfig.addComponent(receiveFeedback);

//        ColumnConfig columnConfig6 = factory.newColumnConfig("taskStartTime", false);
//        columnConfig6.setLabel("处理时间");
//        tableConfig.addComponent(columnConfig6);
//
//        ColumnConfig columnConfig7 = factory.newColumnConfig("taskException", false);
//        columnConfig7.setLabel("错误信息");
//        tableConfig.addComponent(columnConfig7);

//        ColumnConfig columnConfig7 = factory.newColumnConfig("taskEndTime", false);
//        columnConfig7.setLabel("任务结束时间");
//        tableConfig.addComponent(columnConfig7);
//
//        ColumnConfig columnConfig8 = factory.newColumnConfig("taskRemark", false);
//        columnConfig8.setLabel("任务备注");
//        tableConfig.addComponent(columnConfig8);

        return tableConfig;
    }


}
