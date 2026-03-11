<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="ext.casc.workflow.util.PrintDistributionHelper,ext.casc.constants.Constants" %>
<body bgcolor="#C0C0C0">
<table align="center" bgcolor="#C0C0C0">
	<%for(String department : PrintDistributionHelper.getAllYinZhang()){ %>
	<tr bgcolor="#C0C0C0">
		<td><input type="checkbox" name="yinzhang" value="<%= department%>"><%=department%></td>
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
		var obj = window.dialogArguments
		var value = obj.value
		var val = value.split(";");
		var boxes = document.getElementsByName("yinzhang");
		for(i=0;i<boxes.length;i++){
			for(j=0;j<val.length;j++){
				if(boxes[i].value == val[j]){
	                boxes[i].checked = true;
	                break;
	            }
			}
		}
	}
	function ok(){
		var sel = document.getElementsByName("yinzhang");
	 	var val="";
	 	var returnValue = null;
	 	for(var i=0;i<sel.length;i++){
	 		if(sel[i].checked){
	 			val = sel[i].value;
	 			if(returnValue == null) {
	 				returnValue = val;
	 			} else {
	 				returnValue = returnValue + ";" + val ;
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
		window.returnValue= null;
		window.close();
	}
</script>