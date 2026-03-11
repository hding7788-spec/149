<%@page import="ext.casc.constants.Constants"%>
<%@page import="wt.httpgw.URLFactory"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="ext.casc.workflow.CwbmDBController"%>
<%
String dizhi=request.getParameter("dizhi");
Map<String, String> map;
map=CwbmDBController.selectTemplateIdAndTemplateName(dizhi);
URLFactory fac=new URLFactory();
String herf=fac.getBaseHREF();

%>
<form id="form"  method="post" action="<%=herf%>app/netmarkets/jsp/ext/workflow/deleteTemplate.jsp">
<table style='height:150px;width:350px;text-align:center' >
<tr>
<td><%=Constants.MUBANMINGCHENG%></td>
<td ><select id="name" onchange=change1() style="width:250px;" >
<option ></option>
<%for(String s:map.keySet()){ %>
<option value="<%=s%>"><%=map.get(s)%> </option>
<%} %>
</select></td>
</tr>
</table>
<tr>
<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td>
<input id="ok" type="button" name="ok" onclick="sure()"  value="<%=Constants.JSP_DISPLAY_OK%>"></td>
<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td><input id="cancel" type="button" name="cancel" value="<%=Constants.JSP_DISPLAY_CANCEL%>" onclick="can()" ></td>
</tr>
<input type="hidden" id="nameValue" name="nameValue" value="">
</form>
<script type="text/javascript">
function change1(){
	var obj = document.getElementById("name");
	var txt = obj.options[obj.selectedIndex].value;
	document.getElementById("nameValue").value=txt;

}
function sure(){
	 document.getElementById("form").submit();
	 window.opener.location.reload();
}
function can(){
	window.close();
}
</script>