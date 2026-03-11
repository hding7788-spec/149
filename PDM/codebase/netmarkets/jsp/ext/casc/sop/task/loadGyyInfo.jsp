<%@page import="ext.casc.process.ProcessExtJsonDataGenerator1"%>
<%@ page import="ext.casc.sop.util.SopUtil" %>

<%
    String taskOid = request.getParameter("taskOid");
    String result=SopUtil.getGyyJsonInfo(taskOid);
    out.println(result);
%>