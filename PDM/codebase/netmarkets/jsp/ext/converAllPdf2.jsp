<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="ext.casc.pdf.ConverAllPdf2"%>

<%@page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%

String id = request.getParameter("id");
ConverAllPdf2.process(id);

%>