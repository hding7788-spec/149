<%@page import="java.net.URLDecoder"%>
<%@page language="java" session="true" pageEncoding="GBK"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.number.GZNumberRegister"%>

<%
String number = request.getParameter("number");
number = URLDecoder.decode(number, "utf8");
String user = request.getParameter("user");
String contextPath = request.getContextPath();
GZNumberRegister.manager.resignNumberToUser(number, user);
%>
