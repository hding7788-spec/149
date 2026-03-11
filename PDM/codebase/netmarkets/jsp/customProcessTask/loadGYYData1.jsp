<%@page import="ext.casc.process.ProcessExtJsonDataGenerator1"%>

<%
String taskOid = request.getParameter("taskOid");
String result=ProcessExtJsonDataGenerator1.genGyyJsonDataByCurrentUser(taskOid);
out.println(result);
%>