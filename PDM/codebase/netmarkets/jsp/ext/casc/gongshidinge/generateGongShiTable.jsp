<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="ext.casc.gongshidinge.processor.GenerateGongShiUtil" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    String oid = request.getParameter("oid");
    String result = GenerateGongShiUtil.generateProcessPlanTable(oid).toString();
    response.getWriter().print(result);
%>