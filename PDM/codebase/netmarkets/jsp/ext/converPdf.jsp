<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="ext.casc.pdf.ConverAllPdf"%>

<%@page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%
String oid = request.getParameter("oid");
Object o = ReferenceFactory.getObjectbyOid(oid);
ConverAllPdf.replacePdf((WTDocument)o);
%>