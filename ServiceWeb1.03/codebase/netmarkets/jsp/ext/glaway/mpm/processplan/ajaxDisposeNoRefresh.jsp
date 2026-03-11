
<%@page import="com.glaway.mpm.processplan.ProcessPlanReport"%>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<%@page language="java" pageEncoding="utf-8"
	contentType="text/html; charset=utf-8"%>
<%@page import="java.util.*"%>
<%@page import="com.glaway.gpms.report.ToolsReport"%>

<%
	//报表相关参数
	String product = request.getParameter("product");
	ProcessPlanReport processPlanReport = new ProcessPlanReport();
	Vector<String> vec = processPlanReport.getPrimaryFileByDocument(
			"wt.doc.WTDocument|com.nriet.工艺文档|com.nriet.零件工艺", product);
	String reportType = request.getParameter("reportType");
	if (vec != null && vec.size() != 0) {
		String path = vec.get(0).substring(0, vec.get(0).lastIndexOf("\\"));
		System.out.println("path:" + path);
		ToolsReport report = new ToolsReport();
		String resultURL = report.backReportUrl(vec, path, reportType);
		resultURL = resultURL.split("codebase")[1].replace("\\", "/");
		request.setAttribute("resultURL", resultURL);
	}
%>
<jsp:include page="${resultURL }"></jsp:include>
