<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="ext.casc.constants.Constants,ext.casc.constants.QueryDepartmentUtils" %>
<%@page import="java.util.ArrayList,java.util.List" %>

<body bgcolor="#C0C0C0">
<table align="center" bgcolor="#C0C0C0">
	<%
	String type=request.getParameter("type");
	List<String> allReleaseDepartment= Constants.allReleaseDepartment;
	if("electric".equals(type)){
		allReleaseDepartment=QueryDepartmentUtils.getElectronicDepartment();
	} else if("paper".equals(type)){
		allReleaseDepartment= QueryDepartmentUtils.getAllDepartment();
	}
	
	for(String department :allReleaseDepartment){ %>
	<tr bgcolor="#C0C0C0">
		<td><input type="checkbox" name="department" value="<%= department%>"><%=department%></td>
		<td><%= Constants.JSP_DISPLAY_FENSHU%><input type="text" name="amount" size="5" value="1"></td>
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
	 				returnValue = val + ":" + f2+"份";
	 			} else {
	 				returnValue = returnValue + ";" + val + ":" + f2+"份";
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