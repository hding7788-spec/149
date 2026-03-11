<%@page import="java.text.SimpleDateFormat"%><%@page import="java.util.Date"%><%@page import="java.util.HashMap"%><%@page import="java.util.Map"%><%@page pageEncoding="GBK" contentType="text/html; charset=UTF-8"%>
<%@page import="ext.casc.capp.CAPPPartService"%><%
	response.reset();
	String number = request.getParameter("number");
	String version = request.getParameter("version");
	out.print( new CAPPPartService().isExistCAPPDoc(number,version));
%>