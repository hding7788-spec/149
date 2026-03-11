<%@ page import="ext.casc.analysisActivity.helper.AnalysisActivityHelper" %>
<%
	String number = request.getParameter("number");
	String result = AnalysisActivityHelper.verifyData(number);
	response.getWriter().print(result);
%>
