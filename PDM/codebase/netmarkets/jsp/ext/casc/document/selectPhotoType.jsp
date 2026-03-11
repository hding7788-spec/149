<%@page import="java.util.List,com.glaway.mpm.constants.Constants"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<body bgcolor="#C0C0C0">
<table align="center" bgcolor="#C0C0C0">
	<tr bgcolor="#C0C0C0">
		<td><input type="checkbox" name="photoName" value="对外接口类">对外接口类</td>
	</tr>
	<tr bgcolor="#C0C0C0">
		<td><input type="checkbox" name="photoName" value="易错/难操作类">易错/难操作类</td>
	</tr>
	<tr bgcolor="#C0C0C0">
		<td><input type="checkbox" name="photoName" value="极性类">极性类</td>
	</tr>
	<tr bgcolor="#C0C0C0">
		<td><input type="checkbox" name="photoName" value="不可检/不可测类">不可检/不可测类</td>
	</tr>
	<tr bgcolor="#C0C0C0">
		<td><input type="checkbox" name="photoName" value="阶段检查类">阶段检查类</td>
	</tr>
	<tr bgcolor="#C0C0C0">
		<td><input type="checkbox" name="photoName" value="Ⅰ、Ⅱ类单点类">Ⅰ、Ⅱ类单点类</td>
	</tr>
	<tr bgcolor="#C0C0C0">
		<td><input type="checkbox" name="photoName" value="其余一般记录类">其余一般记录类</td>
	</tr>
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
		var sel = document.getElementsByName("photoName");
	 	var val="";
	 	var returnValue = null;
	 	for(var i=0;i<sel.length;i++){
	 		if(sel[i].checked){
	 			val = sel[i].value;
	 			if(returnValue == null) {
	 				returnValue = val;
	 			} else {
	 				returnValue = returnValue + "," + val;
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