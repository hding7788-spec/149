<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="ext.casc.constants.Constants" %>

<body bgcolor="#C0C0C0">
<table align="center" bgcolor="#C0C0C0">
	<tr bgcolor="#C0C0C0">
		<td><input type="checkbox" name="department" value="<%= Constants.ROLE_VALUE_YISHI%>"><%= Constants.ROLE_VALUE_YISHI%></td>
		<td><%= Constants.JSP_DISPLAY_FENSHU%><input type="text" name="amount" size="5" value="1"></td>
	</tr>
	<tr>
		<td><input type="checkbox" name="department" value="<%= Constants.ROLE_VALUE_ERSHI%>"><%= Constants.ROLE_VALUE_ERSHI%></td>
		<td><%= Constants.JSP_DISPLAY_FENSHU%><input type="text" name="amount" size="5" value="1"></td>
	</tr>
	<tr bgcolor="#C0C0C0">
		<td><input type="checkbox" name="department" value="<%= Constants.ROLE_VALUE_SANSHI%>"><%= Constants.ROLE_VALUE_SANSHI%></td>
		<td><%= Constants.JSP_DISPLAY_FENSHU%><input type="text" name="amount" size="5" value="1"></td>
	</tr>
	<tr>
		<td><input type="checkbox" name="department" value="<%= Constants.ROLE_VALUE_SISHI%>"><%= Constants.ROLE_VALUE_SISHI%></td>
		<td><%= Constants.JSP_DISPLAY_FENSHU%><input type="text" name="amount" size="5" value="1"></td>
	</tr>
	<tr bgcolor="#C0C0C0">
		<td><input type="checkbox" name="department" value="<%= Constants.ROLE_VALUE_WUSHI%>"><%= Constants.ROLE_VALUE_WUSHI%></td>
		<td><%= Constants.JSP_DISPLAY_FENSHU%><input type="text" name="amount" size="5" value="1"></td>
	</tr>
	<tr>
		<td><input type="checkbox" name="department" value="<%= Constants.ROLE_VALUE_LIUSHI%>"><%= Constants.ROLE_VALUE_LIUSHI%></td>
		<td><%= Constants.JSP_DISPLAY_FENSHU%><input type="text" name="amount" size="5" value="1"></td>
	</tr>
	<tr bgcolor="#C0C0C0">
		<td><input type="checkbox" name="department" value="<%= Constants.ROLE_VALUE_ZONGZHUANGZHONGXIN%>"><%= Constants.ROLE_VALUE_ZONGZHUANGZHONGXIN%></td>
		<td><%= Constants.JSP_DISPLAY_FENSHU%><input type="text" name="amount" size="5" value="1"></td>
	</tr>
	<tr>
		<td><input type="checkbox" name="department" value="<%= Constants.ROLE_VALUE_DIANZHUANGZHONGXIN%>"><%= Constants.ROLE_VALUE_DIANZHUANGZHONGXIN%></td>
		<td><%= Constants.JSP_DISPLAY_FENSHU%><input type="text" name="amount" size="5" value="1"></td>
	</tr>	
	<tr>
		<td><input type="checkbox" name="department" value="<%= Constants.ROLE_VALUE_KEJICHU%>"><%= Constants.ROLE_VALUE_KEJICHU%></td>
		<td><%= Constants.JSP_DISPLAY_FENSHU%><input type="text" name="amount" size="5" value="1"></td>
	</tr>
	<tr bgcolor="#C0C0C0">
		<td><input type="checkbox" name="department" value="<%= Constants.ROLE_VALUE_XINDANGCHU%>"><%= Constants.ROLE_VALUE_XINDANGCHU%></td>
		<td><%= Constants.JSP_DISPLAY_FENSHU%><input type="text" name="amount" size="5" value="1"></td>
	</tr>
	<tr>
		<td><input type="checkbox" name="department" value="<%= Constants.ROLE_VALUE_ZHILIANGCHU%>"><%= Constants.ROLE_VALUE_ZHILIANGCHU%></td>
		<td><%= Constants.JSP_DISPLAY_FENSHU%><input type="text" name="amount" size="5" value="1"></td>
	</tr>
	<tr bgcolor="#C0C0C0">
		<td><input type="checkbox" name="department" value="<%= Constants.ROLE_VALUE_WAIXIE%>"><%= Constants.ROLE_VALUE_WAIXIE%></td>
		<td><%= Constants.JSP_DISPLAY_FENSHU%><input type="text" name="amount" size="5" value="1"></td>
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
		var sel = document.getElementsByName("department");
		var fenshu = document.getElementsByName("amount");
	 	var val="";
	 	var f2 = "";
	 	var returnValue = null;
	 	for(var i=0;i<sel.length;i++){
	 		if(sel[i].checked){
	 			val = sel[i].value;
	 			f2 = fenshu[i].value;
	 			if(returnValue == null) {
	 				returnValue = val + ":" + f2;
	 			} else {
	 				returnValue = returnValue + ";" + val + ":" + f2;
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