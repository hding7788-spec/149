<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="ext.casc.workflow.tree.GenerateProcecssTaskJson" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    String oid = request.getParameter("oid");
    String json = new GenerateProcecssTaskJson(oid).generateTable(session).toJSONString();

    response.getWriter().print(json);
%>