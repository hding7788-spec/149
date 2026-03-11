<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="ext.casc.analysisActivity.process.GenerateRelatedAnalysisJson" %>
<%@ page import="cn.hutool.core.util.StrUtil" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    String oid = request.getParameter("oid");
    String type = request.getParameter("type");
    String deal = request.getParameter("deal");
    if(StrUtil.isNotEmpty(deal)){
        String json = new GenerateRelatedAnalysisJson(oid).generateDealTable(type).toString();
        response.getWriter().print(json);
    }else {
        String json = new GenerateRelatedAnalysisJson(oid).generateTable(type).toString();
        response.getWriter().print(json);
    }
%>