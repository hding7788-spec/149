<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="ext.casc.analysisActivity.helper.AnalysisFlowHelper" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    String oid = request.getParameter("oid");
    String json = AnalysisFlowHelper.workflowData(oid);
    response.getWriter().print(json);
%>