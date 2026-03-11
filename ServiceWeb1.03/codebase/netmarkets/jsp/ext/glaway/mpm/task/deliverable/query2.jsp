<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.glaway.mpm.task.model.GMItemTask"%>
<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="wt.vc.Versioned"%>
<%@page import="com.glaway.mpm.task.util.ItemTaskUtil"%>
<%
	String number = request.getParameter("numberValue");
	String itemTaskOid = request.getParameter("itemoid");
	GMItemTask itemTask = (GMItemTask)ReferenceFactory.getObjectbyOid(itemTaskOid);
	NmOid nmoid = NmOid.newNmOid(number);
	Object obj = nmoid.getRef();
	
	Versioned ver = (Versioned)obj;
	ItemTaskUtil.createItemToDeliLink(itemTask, ver);
	
%>
