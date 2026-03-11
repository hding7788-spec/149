
<%@page import="wt.util.WTProperties"%><%@ taglib uri="http://www.ptc.com/windchill/taglib/components"  prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt"         prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc"         prefix="mvc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"               prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers"    prefix="w"%>


<%@page language="java" pageEncoding="GBK" contentType="text/html; charset=GBK"%>
<%
String strCodeBase = WTProperties.getLocalProperties().getProperty("wt.server.codebase", null);
String url=request.getRequestURL().toString();
String mpmResourceType=request.getParameter("mpmResourceType");
 %>
<script language="javascript">

	function reload(){ 
	   PTC.jca.table.Utils.reload("AddMPMResourceLinkTable", {}, true);
	}
	
	function refresh(){
		var number=document.getElementById("number").value;
		var name=document.getElementById("name").value;
		window.location="<%=url%>"+"?mpmResourceType=<%=mpmResourceType%>&number="+number+"&name="+name;
	} 
</script > 
<table>
	<tr>
		<td>
			<b>±àºÅ:</b>
		</td>
		<td>
			<w:textBox name="number" id="number" value="${param.number}"/>
		</td>
	</tr>
	<tr>
		<td>
			<b>Ãû³Æ:</b>
		</td>
		<td>
			<w:textBox name="name" id="name" value="${param.name}"/>
		</td>
		<td>
			<input type="button" name="search" id="search" value="Search" onclick="reload()" />
		</td>
	</tr>
</table>

<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.mpmresource.AddMPMResourceLinkBuilder')}" flush="true" />
