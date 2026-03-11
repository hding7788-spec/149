<%@page import="ext.casc.sop.util.SopUtil"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="ext.casc.util.IBAUtility"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ page import="com.ptc.windchill.mpml.resource.MPMTooling" %>
<%
    String specializedType=request.getParameter("specializedType");
    IBAUtility ibaUtility;
    Map<String,String> map=new HashMap<String,String>();
    try {
        if(!"".equals(specializedType)){
           List<MPMTooling> list = SopUtil.getAllGxmc(specializedType);
            if(list!=null&&list.size()!=0){
                for(MPMTooling wtPart : list){
                    ibaUtility = new IBAUtility(wtPart);
                    map.put(wtPart.getName(), wtPart.getName());
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    out.print(map);
%>






