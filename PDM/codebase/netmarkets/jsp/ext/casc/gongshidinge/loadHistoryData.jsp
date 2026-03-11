<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="ext.casc.gongshidinge.processor.GenerateGongShiUtil" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    String oid = request.getParameter("oid");
    JSONObject json = GenerateGongShiUtil.generateHistoryData(oid);
    response.getWriter().print(json);
%>