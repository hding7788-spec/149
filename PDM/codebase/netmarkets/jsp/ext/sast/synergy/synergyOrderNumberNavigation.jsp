<%@ page import="ext.sast.synergy.util.SynergyUtil" %>
<%@ page import="wt.util.WTException" %>
<%@page language="java" session="true" pageEncoding="UTF-8"%>

<%
    //add by ty 2020-1-19
    try {
        String url = SynergyUtil.toOrderNumberDetail(request,response);
        if(url == null){
            out.println("无法根据该对象编号找到对应的单据信息");
        }
    } catch (WTException e) {
        e.printStackTrace();
    }
    
%>
