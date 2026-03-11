<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="ext.casc.analysisActivity.process.GenerateRelatedAnalysisJson" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%
request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");

String workItemOid = request.getParameter("workItemOid");
String data = request.getParameter("data");
new GenerateRelatedAnalysisJson(workItemOid).process(data);
%>