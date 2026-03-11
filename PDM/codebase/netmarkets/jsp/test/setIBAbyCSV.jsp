<%@ page import="ext.sast.common.util.CSVUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ptc.windchill.mpml.resource.MPMTooling" %>
<%@ page import="com.glaway.mpm.util.MPMResourceUtil" %>
<%@ page import="ext.casc.sop.constants.SopConstants" %>
<%@ page import="java.util.Map" %>
<%@ page import="ext.casc.util.IBAHelper" %>
<%@page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<%
    try {
        List<CSVUtil.Data> dataList = CSVUtil.readCSV("D:\\设备工时.csv");
        MPMTooling tooling = null;
        if (!dataList.isEmpty()) {
            for (CSVUtil.Data data : dataList) {
                String type = data.getDataType();
                String number = data.getNumber();
                String name = data.getName();
                Map<String, String> maps = data.getAttributes();
                if ("gxmc".equalsIgnoreCase(type)) {
                    tooling = MPMResourceUtil.getMPMToolingByName(name, SopConstants.SOP_TYPE_PROCEDUCENAME);
                    if (null != tooling) {
                        //遍历maps
                        for (Map.Entry<String, String> entry : maps.entrySet()) {
                            System.out.println("number" + number + " name" + name + " Key = " + entry.getKey() + ", Value = " + entry.getValue());
                            IBAHelper.setIBAStringValue(tooling, entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
        }
    } catch (Exception e) {
        out.print("e.getMessage()" + e.getLocalizedMessage());
    }
%>