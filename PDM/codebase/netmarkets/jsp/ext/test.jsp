
<%@page import="ext.sast.center.synch.MQDataImportHandler"%>
<%@ page import="com.glaway.mpm.util.WTPartUtil" %>
<%@ page import="wt.part.WTPart" %>
<%@ page import="com.glaway.mpm.util.Constant" %>
<%@ page import="com.glaway.mpm.util.ReferenceFactory" %>
<%@ page import="wt.part.WTPartUsageLink" %>
<%@ page import="wt.inf.container.WTContainerHelper" %>
<%@ page import="wt.pdmlink.PDMLinkProduct" %>
<%@ page import="wt.session.SessionHelper" %>
<%@ page import="wt.folder.FolderHelper" %>
<%@ page import="wt.org.WTUser" %>
<%@page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%
	WTUser user = (WTUser) SessionHelper.getPrincipal();
	String pusherZh = user.getFullName();
	out.println("当前登录用户："+pusherZh.replaceAll(",","").replaceAll(" ",""));


%>