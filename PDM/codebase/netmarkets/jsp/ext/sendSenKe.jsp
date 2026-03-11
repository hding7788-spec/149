<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="ext.casc.pdf.ConverAllPdf"%>
<%@ page import="ext.casc.integrate.senKe.SenKeSynchHelper" %>
<%@ page import="org.json.JSONObject" %>

<%@page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%
String oid = request.getParameter("oid");
WTDocument document =(WTDocument) ReferenceFactory.getObjectbyOid(oid);
	JSONObject jsonObject = SenKeSynchHelper.getSendSenKeData(document);
	out.println(jsonObject);
%>