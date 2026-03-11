<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="ext.casc.analysisActivity.helper.AnalysisActivityHelper" %>
<%@ page import="org.json.JSONObject" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    String oid = request.getParameter("oid");
    String number = request.getParameter("number");
    String type = request.getParameter("type");
    String partnumber = request.getParameter("partnumber");
    JSONObject json = AnalysisActivityHelper.generateDealRecordData(oid, number, type, partnumber);
    response.getWriter().print(json);
%>