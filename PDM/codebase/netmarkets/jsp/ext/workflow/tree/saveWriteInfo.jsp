<%@page import="ext.casc.workflow.WorkflowSaveWriteInfoProcessor"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="ext.casc.workflow.tree.GenerateJson"%>
<%
request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");

String workItemOid = request.getParameter("workItemOid");
String data = request.getParameter("data");;
new WorkflowSaveWriteInfoProcessor().process(workItemOid, data, request);
%>