<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@page import="ext.casc.part.SignatureHelper"%>
<%@page import="java.util.*"%>
<%@page pageEncoding="UTF-8" %>

<input type="hidden" name="completeReviewhidden" id="completeReviewhidden" onclick="" />

<script>
		var oidArray = new Array();
		var activityName = "";
</script>

<%
	String path = request.getContextPath();
	String workflowProcessOid = request.getParameter("oid");
%>
<script>
	var workflowProcessOid = '<%=workflowProcessOid%>';
</script>

<script>

	function setReviewData() {
   		var xmlHttpRequest;
   		if (window.XMLHttpRequest) { // Mozilla, Safari,...
   			xmlHttpRequest = new XMLHttpRequest();
   			if (xmlHttpRequest.overrideMimeType) {
   				xmlHttpRequest.overrideMimeType('text/xml');
   			}
   		} else if (window.ActiveXObject) { // IE
   			try {
   				xmlHttpRequest = new ActiveXObject("Msxml2.XMLHTTP");
   			} catch (e) {
   				try {
   					xmlHttpRequest = new ActiveXObject("Microsoft.XMLHTTP");
   				} catch (e) {

   				}
   			}
   		}

   		xmlHttpRequest.onreadystatechange = function() {
   			if (xmlHttpRequest.readyState == 4) {
   				if (xmlHttpRequest.status == 200) {
   					var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
   					var result = xmlHttpRequest.responseText;
//    					if(result.trim() == "false"){
//    						alert("当前任务存在未完成子流程，不能完成该任务！");
//    						return;
//    					}else{
   						completehiddenBtn.click();
//    					}
   				}else{

   				}
   			}
   		}
   		xmlHttpRequest.open("POST", "<%=path%>/netmarkets/jsp/ext/glaway/mpm/print/checkPassTwoSub.jsp", true);
   		xmlHttpRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
   		xmlHttpRequest.send("oid=" + workflowProcessOid);
   	}

	function replaceReviewCompleteButton() {
   		var completeBtn = document.getElementsByName("complete")[0];
   		var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
   		if (completeBtn && completehiddenBtn) {
   			completeBtn.oldOnClick = completeBtn.onclick;
   		    completehiddenBtn.onclick = completeBtn.onclick;
   			completeBtn.onclick = setReviewData;
   		}
   	}
   	replaceReviewCompleteButton();


</script>