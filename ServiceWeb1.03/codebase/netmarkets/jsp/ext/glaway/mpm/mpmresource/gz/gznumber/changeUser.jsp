<%@page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<%@include file="/netmarkets/jsp/util/beginPopup.jspf" %>

<style type="text/css">
.STYLE2 {font-size: small} 
.STYLE3 {font-size: 14px}
</style>
<%
String requestNumber = request.getParameter("number");
String contextPath = request.getContextPath();
%>

<div class="fieldsetbody">
   <table width="100%" border="0" cellpadding="2" cellspacing="10" class="layoutTable100">
  	<tr>
		<td height="70" valign="middle" class="footer">
			<div align="left" class="STYLE2">&nbsp;&nbsp;&nbsp;&nbsp;<strong>更改所有者</strong></div>
		</td>
	</tr>
  </table>
  
  	&nbsp;&nbsp;&nbsp;&nbsp;<wctags:userPicker id="user" label="申请人" readOnlyPickerTextBox="true"/>
	<input type="hidden" name="number" id="number" value="<%=requestNumber%>">
</div>

<script>
function submitPopupForm(type) {
	var idUser = Ext.getDom("user");
	if (idUser) {
		var userName = idUser.value
		userName = encodeURI(userName);
		var number = Ext.getDom("number").value;
		Ext.Ajax.request({
			url: "<%=contextPath%>/netmarkets/jsp/glaway/mpm/mpmresource/gz/gznumber/changeUser2.jsp",
			params: {user: userName, number: number},
			method: "POST",
			success: function(result, req) {
				var msg = trim(result.responseText);
				if (msg.length > 0) {
					alert(msg);
				} else {
					if (type == "ok") {
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