
<%@ page import="ext.sast.center.synch.MQDataReceiveHelper" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    String msgId = request.getParameter("msgId");
	String fileName = request.getParameter("fileName");
	MQDataReceiveHelper.receiveData(msgId,fileName);

%>
