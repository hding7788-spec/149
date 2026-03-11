<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="ext.casc.gongshidinge.processor.GenerateGongShiUtil" %>
<%@ page import="cn.hutool.core.util.StrUtil" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");

    String param = request.getParameter("param");
    String oid = request.getParameter("oid");
    String result = GenerateGongShiUtil.saveGongshi(param, oid);
    if(StrUtil.isNotEmpty(result)) {
        response.getWriter().print(result);
    }
%>