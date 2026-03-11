<%@page import="ext.casc.process.ProcessExtJsonDataGenerator"%>

<%
String taskOid = request.getParameter("taskOid");
String result=ProcessExtJsonDataGenerator.genGyyJsonDataByCurrentUser(taskOid);
out.println(result);
%>