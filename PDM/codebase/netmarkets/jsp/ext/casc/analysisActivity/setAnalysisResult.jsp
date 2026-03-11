<%@ page import="ext.casc.analysisActivity.helper.AnalysisActivityHelper" %>
<%
	String workItemOid = request.getParameter("workItemOid");
	String data = request.getParameter("data");
	String type = request.getParameter("type");
	String result = AnalysisActivityHelper.setAnalysisResult(workItemOid, data, type);
	response.getWriter().print(result);
%>
