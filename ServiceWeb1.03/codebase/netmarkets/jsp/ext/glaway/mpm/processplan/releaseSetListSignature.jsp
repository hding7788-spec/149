<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@page import="ext.casc.part.SignatureHelper"%>
<%@page import="java.util.*"%>

<input type="hidden" name="completeReviewhidden" id="completeReviewhidden" onclick="" />

<script>
		var oidArray = new Array();
		var activityName = "";
</script>

<%
	String path = request.getContextPath();
	String workflowProcessOid = request.getParameter("oid");
	List<String> oidList = SignatureHelper.getReviewOid2(workflowProcessOid);
	System.out.println(oidList);
	if(oidList != null && oidList.size() > 0){
		for(int i = 0 ; i < oidList.size() ; i++){
			String tempOid = oidList.get(i);
%>
<script>
	var workflowProcessOid = '<%=workflowProcessOid%>';
	var tempOid = '<%=tempOid%>';
	oidArray[oidArray.length] = tempOid;
</script>
<%
		}
	}
%>

<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.processplan.ProcessPlanReviewBuilder')}" />

<script>

	function setReviewData() {
   		var setValues = "";
   		var isSet = true;
   		for ( var i = 0; i < oidArray.length; i++) {
	   		var tempOid = oidArray[i];
	   		//alert("---->>>tempOid:"+tempOid);
	   		var selElementValue = "";
   			var selElement = document.getElementsByName(tempOid + "_selected_department");
	   		//alert("---->>>selValue:"+selElement);
	   		if(!selElement[0]) {
	   			continue;
	   		}
	   		selElementValue = selElement[0].value;
   			if(isSet && selElementValue!=""){
   				isSet = false;
			} else {
				continue;
			}
   			setValues = setValues + tempOid + "~" + selElementValue + "@";
   		}

		if(isSet){
			var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
   			completehiddenBtn.click();
   			return;
		}
		setValues = encodeURI(setValues);

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
   					completehiddenBtn.click();
   				} else {

   				}
   			}
   		}

   		xmlHttpRequest.open("POST", "<%=path%>/netmarkets/jsp/ext/glaway/mpm/processplan/setSelDepValue.jsp", true);
   		xmlHttpRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
   		xmlHttpRequest.send("value=" + setValues + "&oid=" + workflowProcessOid);
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


	function setSelDepartment(obj,versionoid){
		var obj = new Object();
		var path = "<%=path%>/netmarkets/jsp/ext/glaway/mpm/processplan/selectedDepartment.jsp";
		var ret = window.showModalDialog(path,obj,"dialogWidth=400px;dialogHeight=400px;resizable=yes;");
		if(ret != null) {
			setValue(obj,versionoid,ret);
		}
	}

	function setValue(obj,versionoid,ret){
		var checkboxElement = document.getElementsByName("pjl_selPJLsa1__1");
		var flag = false;
		for ( var i = 0; i < checkboxElement.length; i++) {
			if (checkboxElement[i].checked) {
				flag = true;
				break;
			}
		}

		if(flag){
			var size = checkboxElement.length;
			for ( var i = 0; i < checkboxElement.length; i++) {
				if (checkboxElement[i].checked) {
					var tempValue = checkboxElement[i];
					var temp = tempValue.value;
					if (temp.indexOf("WTPart") > -1) {
						var n = temp.lastIndexOf("$");
						var selectx = temp.substring(n + 4, temp.length - 2);
						document.getElementById(selectx+"_selected_department").value=ret;
					}
				}
			}
			var grid = Ext.getCmp("com.glaway.mpm.mvc.builders.processplan.ProcessPlanReviewBuilder");
			var selModel = grid.getSelectionModel();
			selModel.clearSelections();
		}else{
			document.getElementById(versionoid+"_selected_department").value=ret;
		}
	}
</script>