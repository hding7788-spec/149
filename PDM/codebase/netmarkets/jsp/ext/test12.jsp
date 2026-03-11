<%@page import="wt.inf.container.WTContainerReferenceSearch"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="ext.ases.envelope.ProcessEnvelopeUtil"%>
<%@page import="ext.ases.envelope.ProcessEnvelope"%>
<%@page import="wt.workflow.definer.WfDefinerHelper"%>
<%@page import="wt.workflow.engine.ProcessData"%>
<%@page import="wt.workflow.engine.WfEngineHelper"%>
<%@page import="wt.workflow.engine.WfProcess"%>
<%@page import="wt.workflow.definer.WfProcessDefinition"%>
<%@page import="java.util.Vector"%>
<%@page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%
	WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service.getProcessDefinition("149正式发放包流程");
	WfProcess wfprocess = null;
	QueryResult qr = ProcessEnvelopeUtil.getProcessEnvelopeByNumber("Test000002");
	if (qr.hasMoreElements()) {
		ProcessEnvelope pe = (ProcessEnvelope) qr.nextElement();
		System.out.println(pe.getName());
		System.out.println(pe.getNumber());
		System.out.println(pe.getContainerReference().getName());
		wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null, pe.getContainerReference());
		wfprocess.setName("149正式发放包流程_" + pe.getNumber());
		ProcessData processdata = wfprocess.getContext();
		processdata.setValue("primaryBusinessObject", pe);// 设置流程主对象
		processdata.setValue("wfProcessOid", "wfProcessOid");
		processdata.setValue("activityOid805", "wfProcessOid");
		processdata.setValue("activityName", "activityName");
		processdata.setValue("activityTemplateID", "activityTemplateID");
		processdata.setValue("sendFrom", "sendFrom");
		processdata.setValue("orderIID", "orderIID");
		WfEngineHelper.service.startProcess(wfprocess, processdata, 1);

	}
%>