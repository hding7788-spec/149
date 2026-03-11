<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@page import="ext.casc.fileprint.PrintUtil"%>
<%@page import="java.util.List"%>
<%@page pageEncoding="UTF-8" %>

<input type="hidden" name="completeReviewhidden" id="completeReviewhidden" onclick="" />

<script>
		var oidArray = new Array();
		var activityName = "";
</script>

<%
	String path = request.getContextPath();
	String workflowProcessOid = request.getParameter("oid");
	List<String> oidList =  PrintUtil.getReviewOid(workflowProcessOid);
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
<%
	String docValue = "";
	String docBatch = "";
	String ecnValue = "";
	String ecnBatch = "";
	String wtdocInfo = PrintUtil.getIssuedInfo(workflowProcessOid, true);
	if(wtdocInfo != null && !"".equals(wtdocInfo) && !"null".equals(wtdocInfo)){
		docBatch = wtdocInfo.substring(0, wtdocInfo.indexOf("&"));
		docValue = wtdocInfo.substring(wtdocInfo.indexOf("&") + 1, wtdocInfo.length());
	}
	String wtChangeOrderInfo = PrintUtil.getIssuedInfo(workflowProcessOid, false);
	if(wtChangeOrderInfo != null && !"".equals(wtChangeOrderInfo) && !"null".equals(wtChangeOrderInfo)){
		ecnBatch = wtChangeOrderInfo.substring(0, wtChangeOrderInfo.indexOf("&"));
		ecnValue = wtChangeOrderInfo.substring(wtChangeOrderInfo.indexOf("&") + 1, wtChangeOrderInfo.length());
	}
%>
<%
	String isChange = "";
	String changeID = "";
	String docID = "";
	List<String> idList = PrintUtil.getObjID(workflowProcessOid);
	if(idList != null && !idList.isEmpty()){
		isChange = "isChange";
		for(String id : idList){
			if(id.contains("WTChangeOrder2")){
				changeID = id;
			}else{
				docID = id;
			}
		}
	}
%>
<%
	String defaultDept = PrintUtil.getDefaultDept(workflowProcessOid);

%>

<jsp:include page="${mvc:getComponentURL('ext.casc.workflow.tree.mvc.builder.SetPrintDistributiontureBuilder')}" />

<script>
	function setInitialValue(){
		var docBatch = '<%=docBatch%>';
		var docValue = '<%=docValue%>';
		var ecnBatch = '<%=ecnBatch%>';
		var ecnValue = '<%=ecnValue%>';
		var isChange = '<%=isChange%>';
		var changeID = '<%=changeID%>';
		var docID = '<%=docID%>';
		if(docValue == ""){
			var defaultDept = '<%=defaultDept%>';
			document.getElementById(changeID+"_selected_department").value = defaultDept;
			document.getElementById(docID+"_selected_department").value = defaultDept;
		}else{
			if(isChange == "isChange"){
				document.getElementById(changeID+"_selected_yinzhang").value = docBatch;
				document.getElementById(changeID+"_selected_department").value = docValue;
				document.getElementById(docID+"_selected_yinzhang").value = docBatch;
				document.getElementById(docID+"_selected_department").value = docValue;
			}
		}
	}



	function setReviewData() {
   		var setValues = "";
   		var setYinzhang = "";
   		for ( var i = 0; i < oidArray.length; i++) {
	   		var tempOid = oidArray[i];
	   		var selElementValue = "";
	   		var selYinzhangElementValue = "";
   			var selElement = document.getElementsByName(tempOid + "_selected_department");
   			var selYinzhangElement = document.getElementsByName(tempOid + "_selected_yinzhang");
	   		selElementValue = selElement[0].value;
	   		selYinzhangElementValue = selYinzhangElement[0].value;
	   		if(selElementValue!=""){
	   			if(i == 0){
	   				setValues = tempOid + "○" + selElementValue + "●" + selYinzhangElementValue;
	   			}else{
	   				setValues = setValues + "※" + tempOid + "○" + selElementValue + "●" + selYinzhangElementValue;
	   			}
	   		}else{
	   			alert("请选择分发部门");
	   			return;
	   		}
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

   		xmlHttpRequest.open("POST", "<%=path%>/netmarkets/jsp/ext/workflow/tree/setPrintValue.jsp", true);
   		xmlHttpRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
   		xmlHttpRequest.send("dep=" + setValues + "&oid=" + workflowProcessOid);
   	}

	function replaceReviewCompleteButton() {
   		var completeBtn = document.getElementsByName("complete")[0];
   		var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
   		if (completeBtn && completehiddenBtn) {
   			//completeBtn.oldOnClick = completeBtn.onclick;
   		    completehiddenBtn.onclick = completeBtn.onclick;
   			completeBtn.onclick = setReviewData;
   		}
   	}
   	replaceReviewCompleteButton();

   	setInitialValue();
	function setSelDepartment(obj,versionoid,type){
		var value = document.getElementById(versionoid+type).value;
		var obj = new Object();
		obj.value = value;
		var path = "<%=path%>/netmarkets/jsp/ext/workflow/tree/selectedPrintDepartment.jsp";
		var ret = window.showModalDialog(path,obj,"dialogWidth=400px;dialogHeight=400px;resizable=yes;");
		if(ret != null) {
			if(ret == "cancel"){
				ret = null;
			}
			setValue(obj,versionoid,ret,type);
		}
	}

	function setSelyinzhang(obj,versionoid,type){
		var value = document.getElementById(versionoid+type).value;
		var obj = new Object();
		obj.value = value;
		var path = "<%=path%>/netmarkets/jsp/ext/workflow/tree/selectedYinZhang.jsp";
		var ret = window.showModalDialog(path,obj,"dialogWidth=400px;dialogHeight=400px;resizable=yes;");
		if(ret != null) {
			if(ret == "cancel"){
				ret = null;
			}
			setValue(obj,versionoid,ret,type);
		}
	}
	function setValue(obj,versionoid,ret,type){
		var grid = Ext.getCmp("ext.casc.workflow.tree.mvc.builder.SetPrintDistributiontureBuilder");
		var data = grid.store.data;
		for ( var i = 0; i < data.length; i++) {
			var temp = grid.store.getAt(i).id;
			if (temp.indexOf("EPMDocument") > -1|| temp.indexOf("WTDocument") > -1
 						|| temp.indexOf("WTChangeOrder2") > -1
 						|| temp.indexOf("MPMProcessPlan") > -1) {
				var n = temp.lastIndexOf("$");
				var selectx = temp.substring(n + 4, temp.length - 2);
				if(versionoid == selectx){
					document.getElementById(selectx+type).value=ret;
				}
			}
		}
	}
</script>