<%@ page import="com.glaway.mpm.sjzyk.SjzykSchedule" %>
<%@ page import="wt.part.WTPart" %>
<%@ page import="com.glaway.mpm.util.WTPartUtil" %>
<%@ page import="com.glaway.mpm.util.Constant" %>
<%@ page import="wt.util.WTException" %>
<%@page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%
    String number = request.getParameter("number");
    WTPart part = null;
    try {
        part = WTPartUtil.getPartByNumberAndView(number, Constant.EBOM_VIEW);
    } catch (WTException e) {
        out.print("<font color=\"red\">" + e.getMessage() + "</font>");
        throw new RuntimeException(e);
    }

    SjzykSchedule.updateTechnicMaterialInfo(part);

    out.print("<font color=\"green\">update success</font>");

%>