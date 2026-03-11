<%@ page import="ext.casc.integrate.nc.ErpSynchHelper" %>
<%@ page import="com.glaway.mpm.util.ReferenceFactory" %>
<%@ page import="wt.doc.WTDocument" %>
<%@ page import="wt.part.WTPart" %>
<%@page language="java" pageEncoding="UTF-8"
		contentType="text/html; charset=UTF-8"%>
<%
	String oid = request.getParameter("oid");
	WTPart part = (WTPart) ReferenceFactory.getObjectbyOid(oid);
	out.println(ErpSynchHelper.importBomStructure(part));
%>
