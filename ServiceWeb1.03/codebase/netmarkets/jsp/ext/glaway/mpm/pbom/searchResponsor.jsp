<%@ page language="java" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="wt.part.WTPart" %>
<%@ page import="com.glaway.mpm.util.Util" %>
<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%
	String roleGroup = request.getParameter("para");
	System.out.println("roleGroup===>" + roleGroup);
	String partOid = request.getParameter("oid");
	WTPart partTemp = (WTPart)ReferenceFactory.getObjectbyOid(partOid);
	String allUser = Util.getCurrentContainerTeamTheRoleUserStr(partTemp, roleGroup);
	System.out.println(allUser);
	out.println(allUser);
%>