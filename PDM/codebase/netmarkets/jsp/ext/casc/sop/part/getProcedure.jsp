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
    String zhuanyedaihao=request.getParameter("zhuanyedaihao");
    String gongweijianhao=request.getParameter("gongweijianhao");
    String sopSeqNumber = "";
    if(zhuanyedaihao != null && !zhuanyedaihao.isEmpty() && gongweijianhao != null && !gongweijianhao.isEmpty()){
        String pre = "S" + zhuanyedaihao + "-" + gongweijianhao + "-";
        sopSeqNumber = SopUtil.getSopSeqNumber(1, pre);
    }
    System.out.println("sopSeqNumber:" + sopSeqNumber);
    IBAUtility ibaUtility;
    Map<String,String> map=new HashMap<String,String>();
    map.put("sopSeqNumber",sopSeqNumber);
    try {
        if(!"".equals(specializedType)){
           List<MPMTooling> list = SopUtil.getAllGxmc(specializedType);
            if(list!=null&&list.size()!=0){
                for(MPMTooling wtPart : list){
                    ibaUtility = new IBAUtility(wtPart);
                    String gxjh = ibaUtility.getIBAValue("GONGXUJIANHAO");
                    map.put(wtPart.getName(), gxjh);
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    out.print(map);
%>






