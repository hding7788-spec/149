<%@page import="com.glaway.mpm.intf.SopProcessEditorToWCIntfRMI"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String zhuanyedaihao=request.getParameter("zhuanyedaihao");
    String gongweijianhao=request.getParameter("gongweijianhao");
    String sopSeqNumber = "";
    if(zhuanyedaihao != null && !zhuanyedaihao.isEmpty() && gongweijianhao != null && !gongweijianhao.isEmpty()){
        String pre = "S" + zhuanyedaihao + "-" + gongweijianhao + "-";
        sopSeqNumber = SopProcessEditorToWCIntfRMI.getSopSeqNumber(1, pre);
    }
    out.print(sopSeqNumber);
%>






