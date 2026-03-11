<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="ext.casc.analysisActivity.process.GenerateRelatedAnalysisJson" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    String oid = request.getParameter("oid");
    String type = request.getParameter("type");
    String json = new GenerateRelatedAnalysisJson(oid).generateChangeAfter(type).toString();
    response.getWriter().print(json);
%>