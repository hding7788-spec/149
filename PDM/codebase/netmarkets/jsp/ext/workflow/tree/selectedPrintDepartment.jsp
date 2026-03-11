<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="ext.casc.workflow.util.PrintDistributionHelper,ext.casc.constants.Constants" %>

<body bgcolor="#C0C0C0">
<table align="center" bgcolor="#C0C0C0">
	<%for(String department : PrintDistributionHelper.getAllPrintDept()){ %>
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
setValue();
function setValue(){
	var obj = window.dialogArguments;
	if(obj&&obj.value){
		var value = obj.value;
		var val = value.split(";");
		var boxes = document.getElementsByName("department");
		var texts = document.getElementsByName("amount");
		for(i=0;i<boxes.length;i++){
			for(j=0;j<val.length;j++){
				var str1 = val[j].substring(0,val[j].indexOf(":"));
				if(boxes[i].value == str1){
	                boxes[i].checked = true;
	                var str2 = val[j].substring(val[j].indexOf(":") + 1, val[j].indexOf("份"));
	                texts[i].value = str2;
	                break;
	            }
			}
		}
	}
}
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
	 	if(returnValue == null){
	 		returnValue = "cancel";
	 	}
	 	window.returnValue=returnValue;
	 	window.close();
	}

	function cancel(){
		window.returnValue=null;
		window.close();
	}
</script>