<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ page import="ext.casc.process.util.ProcessUtil" %>
<%
    String lcmc=request.getParameter("lcmc");
    Map<String,String> map=new HashMap<String,String>();
    try {
        if(!"".equals(lcmc)){
            List<String> list = ProcessUtil.getWfActivityListByProcess(lcmc);
            if(list!=null&&list.size()!=0){
                for(String hd : list) {
                    map.put(hd, hd);
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    out.println(map);
%>






