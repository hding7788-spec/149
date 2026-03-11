<%@ page import="ext.casc.workflow.tree.GenerateJson" %>
<%@ page contentType="text/html;charset=utf-8" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    String searchValue = request.getParameter("searchValue");
    System.out.println("searchValue=====" + searchValue);
    String json= GenerateJson.searchUser(searchValue);

    response.getWriter().print(json);
%>