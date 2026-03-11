<%@page import="com.glaway.mpm.intf.SopProcessEditorToWCIntfRMI"%>
<%@ page import="ext.casc.sop.util.SopUtil" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String mpmresourceType=request.getParameter("mpmresourceType");
    String sopSeqNumber = "";
    if(mpmresourceType != null && !mpmresourceType.isEmpty()){
        String pre = mpmresourceType;
        sopSeqNumber = mpmresourceType + SopUtil.getSopZYSeqNumber(1, pre);
    }
    out.print(sopSeqNumber);
%>






