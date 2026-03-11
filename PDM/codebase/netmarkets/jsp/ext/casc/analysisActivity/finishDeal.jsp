<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="ext.casc.analysisActivity.process.GenerateRelatedAnalysisJson" %>
<%
request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");

String analysisNumber = request.getParameter("analysisNumber");
String ids = request.getParameter("ids");
String type = request.getParameter("type");
GenerateRelatedAnalysisJson.finishDeal(analysisNumber,ids,type);
%>