<%@page import="ext.casc.workflow.WorkflowSaveWriteInfoProcessor" %>
<%@ page contentType="text/html;charset=utf-8" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    String workItemOid = request.getParameter("workItemOid");
    String data = request.getParameter("data");
    new WorkflowSaveWriteInfoProcessor().saveRecord(workItemOid, data);
%>