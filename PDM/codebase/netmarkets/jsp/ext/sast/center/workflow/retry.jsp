<%@page import="com.bjsasc.avidm.mq.sender.Sender"%>
<%@page import="org.json.JSONObject"%>
<%@page import="com.bjsasc.avidm.mq.queue.RetryQueue"%>
<%@ page import="java.net.URLEncoder" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    String contextPath = request.getContextPath();
    String msgId = request.getParameter("msg_id");
    String result = "操作成功";
    try{
    	JSONObject msg = RetryQueue.getInstance().getMessage(msgId);
    	Sender.getInstance().sendToSelf(msg);
    	RetryQueue.getInstance().finish(msgId);
    	out.print("");
    }catch(Exception e){
    	e.printStackTrace();
        result = "操作失败";

    }
	out.print(result);


%>
