<%@page import="ext.casc.ecn.changeinfo.ModifyChangeinfo"%>
<%@ page language="java" pageEncoding="utf-8"%>
<%
	String cause= request.getParameter("cause");
 	String designnumber= request.getParameter("designnumber");
  	String beforinfor= request.getParameter("beforinfor");
  	String afterinfor= request.getParameter("afterinfor");
  	String objnum = request.getParameter("objnum");
	ModifyChangeinfo.conn(cause, designnumber, beforinfor, afterinfor, objnum);
// 	System.out.println("finish!!!");
%>
<script>
	window.onload = function () {
		window.close();
	}
</script>