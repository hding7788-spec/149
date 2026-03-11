<%@page import="ext.casc.synch.CustomCall"%>
<%@page language="java" pageEncoding="UTF-8"
		contentType="text/html; charset=UTF-8"%>
<%
	String result = CustomCall.synDSOStateTo805("045038","数据发放单","已发放");
	out.print(result);
%>