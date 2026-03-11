<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="ext.casc.pdf.ConverAllPdf"%>
<%@ page import="ext.casc.util.ProcessErrorFixTool" %>

<%@page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%
String oid = request.getParameter("oid");
String number = request.getParameter("number");
out.println(ProcessErrorFixTool.rePreStep(oid,number));
%>