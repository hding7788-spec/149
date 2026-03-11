<%@page import="ext.sast.common.util.RetryHelper"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    //add by ty 2020-1-7
    String msgId = request.getParameter("msg_id");
    String result = "操作成功";
    try{
        RetryHelper.retryOperationFirst(msgId);
    }catch(Exception e){
        result = RetryHelper.retryOperationSecond(msgId);
    }
    out.print(result);
%>
