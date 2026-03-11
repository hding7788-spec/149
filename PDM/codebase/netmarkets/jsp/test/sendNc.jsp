<%@ page import="ext.casc.integrate.nc.ErpSynchHelper" %>
<%@ page import="com.glaway.mpm.util.ReferenceFactory" %>
<%@ page import="wt.doc.WTDocument" %>
<%@ page import="ext.casc.util.Tools" %>
<%@page language="java" pageEncoding="UTF-8"
		contentType="text/html; charset=UTF-8"%>
<%
	String oid = request.getParameter("oid");
	String type = request.getParameter("type");
	Object pbo =ReferenceFactory.getObjectbyOid(oid);
	if(Tools.isNull(type)){
		type="ALL";
	}
	out.println(ErpSynchHelper.importProcessStructure(pbo,type));
%>
