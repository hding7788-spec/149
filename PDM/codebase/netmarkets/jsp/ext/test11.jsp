<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@page import="ext.ases.envelope.ProcessEnvelope"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="wt.util.WTException"%>
<%@page import="wt.workflow.definer.WfDefinerHelper"%>
<%@page import="wt.workflow.definer.WfProcessDefinition"%>
<%@page import="wt.workflow.engine.ProcessData"%>
<%@page import="wt.workflow.engine.WfEngineHelper"%>
<%@page import="wt.workflow.engine.WfProcess"%>
<%@ page language="java" pageEncoding="utf-8"%>

<%
WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service.getProcessDefinition("149签审包工艺会签流程");
WfProcess wfprocess = null;
	String oid = request.getParameter("oid");
String mqmsg = "{'aa':'bb'}";

out.print(mqmsg.length());
    Persistable o = ReferenceFactory.getObjectbyOid(oid);

    try {
        wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,((ProcessEnvelope)o).getContainerReference());
    } catch (WTException e) {
        e.printStackTrace();
    }
    wfprocess.setName("149签审包工艺会签流程_test12");
ProcessData processdata = wfprocess.getContext();
processdata.setValue("mqmessage", mqmsg);
	processdata.setValue("primaryBusinessObject", o);

WfEngineHelper.service.startProcess(wfprocess, processdata, 1);

%>