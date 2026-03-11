<%@page import="ext.casc.sop.util.SopUtil"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="ext.casc.util.IBAUtility"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.util.WTException"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%
    String specializedType=request.getParameter("specializedType");
    System.out.println(specializedType);
    IBAUtility ibaUtility;
    Map<String,String> map=new HashMap<String,String>();
    try {
        if(!"".equals(specializedType)){
           List<WTPart> list = SopUtil.getAllCsxmmc(specializedType);
            if(list!=null&&list.size()!=0){
                for(WTPart wtPart : list){
                    map.put(wtPart.getName(),wtPart.getName());
                }
            }

        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    out.print(map);
%>






