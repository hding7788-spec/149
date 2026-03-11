<%@page import="ext.casc.synch.CustomCall"%>
<%@ page import="ext.sast.center.synch.MQDataReceiveHelper" %>
<%@ page import="java.io.File" %>
<%@ page import="ext.sast.center.util.JsonConvertUtil" %>
<%@ page import="org.json.JSONObject" %>
<%@page language="java" pageEncoding="UTF-8"
		contentType="text/html; charset=UTF-8"%>
<%
	File file = JsonConvertUtil.getJsonFromFile("045038_805");
	if(file!=null) {
		String msg = JsonConvertUtil.fileRead(file);
		String result = MQDataReceiveHelper.sendImportStateTo805("",new JSONObject(msg));
		out.print(result);
	}
%>