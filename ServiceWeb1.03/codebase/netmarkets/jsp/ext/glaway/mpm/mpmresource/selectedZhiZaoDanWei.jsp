<%@page import="java.util.List,com.glaway.mpm.constants.Constants"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="com.glaway.mpm.mpmresource.helper.MPMResourceHelper" %>

<%
List<String> list = MPMResourceHelper.getAllMPMPlant();

%>

<body bgcolor="#C0C0C0">
<table align="center" bgcolor="#C0C0C0">
	<%for(String value : list) {%>
		<tr bgcolor="#C0C0C0">
			<td><input type="checkbox" name="danwei" value="<%= value%>"><%= value%></td>
		</tr>
	<%} %>
	<tr>
		<td>
			<input type="button" onclick="ok()" value="<%= Constants.JSP_DISPLAY_OK%>">
			<input type="button" onclick="cancel()" value="<%= Constants.JSP_DISPLAY_CANCEL%>">
		</td>
	</tr>
	
</table>
</body>

<script>
	function ok(){
		var sel = document.getElementsByName("danwei");
	 	var val="";
	 	var returnValue = null;
	 	for(var i=0;i<sel.length;i++){
	 		if(sel[i].checked){
	 			val = sel[i].value;
	 			if(returnValue == null) {
	 				returnValue = val;
	 			} else {
	 				returnValue = returnValue + ";" + val;
	 			}
	 		}	 		
	 	}
	 	window.returnValue=returnValue;
	 	window.close();
	}
	
	function cancel(){
		window.returnValue=null;
		window.close();
	}
</script>