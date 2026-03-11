<%@ page import="ext.casc.analysisActivity.helper.AnalysisActivityHelper" %>
<%
	String workItemOid = request.getParameter("workItemOid");
	AnalysisActivityHelper.dealAnalysisResult(workItemOid);
%>
<script>
	window.close();
</script>
