<%@page import="com.bjsasc.avidm.mq.handler.HandlerProcess"%>
<%@page import="java.io.File"%>
<%@page import="ext.sast.center.util.JsonConvertUtil"%>
<%@page import="com.bjsasc.avidm.mq.sender.Sender"%>
<%@page import="org.json.JSONObject"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    String msgId = request.getParameter("msg_id");
    String result = "操作成功";
    try{
    	File file = JsonConvertUtil.getSendJsonFromFile(msgId);
    	if(file!=null){
    		String msg = JsonConvertUtil.fileRead(file);
    		Sender sender = Sender.getInstance();
    		JSONObject obj = new  JSONObject(msg);
			sender.send(obj);
    	}else{
    		result = "没有对应的消息文件！";
    	}
    }catch(Exception e){
    	e.printStackTrace();
        result = "操作失败";
    }
	out.print(result);


%>
