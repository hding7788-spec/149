<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="ext.casc.workflow.tree.GenerateJson"%>
<%
request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");

String oid = request.getParameter("oid");

String json =  new GenerateJson(oid,request).generateTable().toJSONString();
System.out.println("#####"+json);
response.getWriter().print(json);
%>