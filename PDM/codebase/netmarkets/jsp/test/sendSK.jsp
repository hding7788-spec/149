<%@ page import="ext.casc.integrate.nc.ErpSynchHelper" %>
<%@ page import="com.glaway.mpm.util.ReferenceFactory" %>
<%@ page import="wt.doc.WTDocument" %>
<%@ page import="ext.casc.integrate.senKe.SenKeSynchHelper" %>
<%@page language="java" pageEncoding="UTF-8"
		contentType="text/html; charset=UTF-8"%>
<%
	String oid = request.getParameter("oid");
	WTDocument doc = (WTDocument)ReferenceFactory.getObjectbyOid(oid);
	out.println(SenKeSynchHelper.sendProcessPlanData(doc));
%>
