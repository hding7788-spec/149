<%@page import="ext.casc.constants.Constants"%>
<%@page import="ext.casc.workflow.CwbmDBController"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
    Map<String,String> map=new HashMap<String,String>();
  String xiaodui= request.getParameter("xiaoduizhe");
  String shenhezhe= request.getParameter("shenhezhe");
  String neibuhuiqianzhe= request.getParameter("neibuhuiqianzhe");
  String biaoshenzhe= request.getParameter("biaoshenzhe");
  String pizhunzhe= request.getParameter("pizhunzhe");
  String dayinzhe= request.getParameter("dayinzhe");
  String gongshidiongeyuan= request.getParameter("gongshidiongeyuan");
  String waibuhuiqianzhe= request.getParameter("waibuhuiqianzhe");
  String mingcheng= request.getParameter("mingcheng");
  String mblx= request.getParameter("mblx1");
  map.put("mblx", mblx);
 map.put("xiaodui", xiaodui);
 map.put("pizhunzhe", pizhunzhe);
 map.put("dayinzhe", dayinzhe);
 map.put("shenhezhe", shenhezhe);
 map.put("neibuhuiqianzhe", neibuhuiqianzhe);
 map.put("biaoshenzhe", biaoshenzhe);
 map.put("gongshidiongeyuan", gongshidiongeyuan);
 map.put("waibuhuiqianzhe", waibuhuiqianzhe);
 CwbmDBController.insertData(map,mingcheng,mblx);
%>
<script>
	window.onload = function () {
		window.close();
	}
</script>