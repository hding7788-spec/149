<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@ page import="wt.part.WTPart" %>
<%@ page import="com.glaway.mpm.sjzyk.SjzykSchedule" %>
<%@page language="java" pageEncoding="UTF-8"
		contentType="text/html; charset=UTF-8"%>
<%
String oid = request.getParameter("oid");
Object o = ReferenceFactory.getObjectbyOid(oid);
SjzykSchedule.updateSjzykMiddleTable((WTPart)o);
%>