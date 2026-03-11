<%@ page import="com.glaway.mpm.intf.ProcessEditorToWCIntfRMI" %>

<%@page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%
	String number = request.getParameter("number");
	ProcessEditorToWCIntfRMI.getFileURLByDocNumber(number);
%>