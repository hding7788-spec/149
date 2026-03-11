<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<%@include file="/netmarkets/jsp/util/beginPopup.jspf" %>

<style type="text/css">
.STYLE2 {font-size: small} 
.STYLE3 {font-size: 14px}
</style>

<%
	String itemOid = request.getParameter("oid");
	System.out.println("itemOid====>" + itemOid);
	String contextPath = request.getContextPath();
%>

<div class="fieldsetbody">
   <table width="100%" border="0" cellpadding="2" cellspacing="10" class="layoutTable100">
  	<tr>
		<td height="70" valign="middle" class="footer">
			<div align="left" class="STYLE2">&nbsp;&nbsp;&nbsp;&nbsp;<strong>条目任务关联交付物</strong></div>
		</td>
	</tr>
  </table>
  
  	&nbsp;&nbsp;&nbsp;&nbsp;<wctags:itemPicker id="deliverablePicker" label="交付物" pickerTitle="交付物搜索" pickedAttributes="number" displayAttribute="number"></wctags:itemPicker>
	<input type="hidden" name="itemoid" id="itemoid" value="<%=itemOid%>">
</div>

<script>
function submitPopupForm(type) {
	var number = Ext.getDom("deliverablePicker");
	if (number) {
		var numberValue = number.value
		numberValue = encodeURI(numberValue);
		var itemoid = Ext.getDom("itemoid").value;
		
		Ext.Ajax.request({
			url: "<%=contextPath%>/netmarkets/jsp/glaway/mpm/task/deliverable/query2.jsp",
			params: {numberValue: numberValue, itemoid: itemoid},
			method: "POST",
			success: function(result, req) {
				var msg = trim(result.responseText);
				if (msg.length > 0) {
					alert(msg);
				} else {
					if (type == "ok") {
						window.opener= null;
 						window.open("","_self");
						window.close();
					}
					window.opener.reloadTable();
				}
			},
			failure: function(result, req) {
				Ext.MessageBox.alert('Failed', "Failed posted fork: " + result.date);
			}
		});
	}

}
</script>


<%@ include file="/netmarkets/jsp/util/endPopup.jspf"%>