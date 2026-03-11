<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="ext.casc.constants.Constants"%>
<%@page import="ext.casc.workflow.signtrue.zp.HuiQianWorkFlowService"%>
<%@page import="ext.casc.workflow.signtrue.zp.SignatureService"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@page import="ext.casc.part.SignatureHelper"%>
<%@page import="java.util.*"%>
<input type="hidden" name="completeReviewhidden" id="completeReviewhidden" onclick="" />

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<script>
		var oidArray = new Array();
</script>
<%
	String passRadio = "通过";

	String tips = "有未指派的工艺文件,请指派后再完成任务";

	String tips2 = "不能把一组的三维图和二维图分配给不同的人！";

	String tips3 = "无需会签的工艺文件不允许指派！";

	String tips4 = "有指派人的工艺文件填写的会签意见无效！";

	String tips5 = "非组织会签的任务不允许再指派工艺文件！";

	String rejectRadio = "驳回";

	String weichuli = "无需会签";

	String tongyi = "同意";

	String butongyi = "不同意";

	String setConclusion = "批量设置会签结论:";

	String selectData = "请选择数据!";

	String allWeichuli = "请设置您负责的图纸的会签结论!";

	String noAdvise = "请为结论为不同意的图纸填写意见!";
	String workflowProcessOid = request.getParameter("oid");
	String activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(workflowProcessOid);
	String contextPath = request.getContextPath();
	if(activityName.equals(Constants.ACTIVITYNAME_GONGYIYUSHENLUOSHIYIJIANFANKUI)){
		Map signMap = SignatureHelper.getSignature(workflowProcessOid);
		request.setAttribute("signMap", signMap);
	}

	commandBean.getRequestData().getParameterMap().put("oid",workflowProcessOid);
	List<String> oidList = SignatureHelper.getReviewOid(workflowProcessOid);
	if(oidList != null && oidList.size() > 0){
		for(int i = 0 ; i < oidList.size() ; i++){
			String tempOid = oidList.get(i);
%>
<script>
	var tempOid = '<%=tempOid%>';
	oidArray[oidArray.length] = tempOid;
</script>
<%
		}
	}
%>
<input type="hidden" name="oidArray" value="${oidArray}" >
<script>
	var butongyi = '<%=butongyi%>';
	var weichuli = '<%=weichuli%>';
	var tips = '<%=tips%>';
	var tips2 = '<%=tips2%>';
	var tips3 = '<%=tips3%>';
	var tips4 = '<%=tips4%>';
	var tips5 = '<%=tips5%>';
	var workflowProcessOid = '<%=workflowProcessOid%>';

 function selectSignResult(object, veroid){

	var	grid = Ext.getCmp("ext.casc.workflow.tree.mvc.builder.SetPreviewDataSignatureBuilder");

	var selModel = grid.getSelectionModel();
	var selectedRows = selModel.getSelections();
	if (selectedRows.length > 0) {
		for (var i = 0; i < selectedRows.length; i++) {
			//var ss= selectedRows[i].get("partType");
			var temp = selectedRows[i].id;
			if (temp.indexOf("EPMDocument") > -1 || temp.indexOf("WTDocument") > -1 ||
				temp.indexOf("WTChangeOrder2") > -1 ||
				temp.indexOf("MPMProcessPlan") > -1||temp.indexOf("PreviewObject") > -1) {
				var n = temp.lastIndexOf("$");

				var selectx = temp.substring(n + 4, temp.length - 2);
				if (selectx.indexOf("^VR:") > -1) {
					var ojbs = selectx.split("^VR:");
					selectx = ojbs[ojbs.length - 1];
				}

				document.getElementsByName(selectx + "_select")[0].value = object.value;
			}
		}
	}

	//selModel.clearSelections();
}


	</script>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.casc.workflow.tree.resource.asesSignatureResource"/>
<fmt:message var="treeName"    key="PART_TREE_LABEL"/>
<fmt:message var="sign_result"    key="SIGN_RESULT"/>
<fmt:message var="sign_message"    key="SIGN_MESSAGE"/>
<fmt:message var="sign_advise"    key="SIGN_ADVISE"/>
<fmt:message var="neibuhuiqian"    key="NEIBUHUIQIAN"/>
<fmt:message var="waibuhuiqian"    key="WAIBUHUIQIAN"/>
<fmt:message var="neibugongyihuiqian"    key="NEIBUGONGYIHUIQIAN"/>
<fmt:message var="gongyihuiqian"    key="GONGYIHUIQIAN"/>
<fmt:message var="waibugongyihuiqian"    key="WAIBUGONGYIHUIQIAN"/>
<%--<fmt:message var="actionsName" key="ACTIONS_COLUMN_LABEL"/>--%>
   <%-->Build a descriptor and assign it to page variable treeDescriptor<--%>

<jsp:include page="${mvc:getComponentURL('ext.casc.workflow.tree.mvc.builder.SetPreviewDataSignatureBuilder')}" />

<script>
	 function find2(name) {
		 //alert("find2 name:"+name);
		 //alert("mainform.elements:"+document.mainform.elements);
		 if(document.mainform.elements!=null){
			 for(var i=0;i<document.mainform.elements.length;i++){
			var e = document.mainform.elements[i];
			//alert("e.name:"+e.name);
			if (e.name.indexOf(name)>=0 && e.name.indexOf("old")<0)
				return e;
			}
		 }

		return null;
	}
	 function setReviewData(){
	 	var bhToGYZZEl = document.getElementById("routingChoice_驳回到工艺组长");
		if (bhToGYZZEl && bhToGYZZEl.checked) {
			var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
			completehiddenBtn.click();
			return ;
		}
		var zpGYZZ = "0";//如果是0代表是工艺会签，1代表指派工艺组组长//add by hding 2012/11/21
		var proxy = "no";
	 	var flag = true;
	 	var flag1 = true;
   			var signValue = "";
   			var signValue2 = "";

   			for(var i = 0 ; i < oidArray.length ; i++){
   				var tempOid = oidArray[i];
				var	zpOid = tempOid;
   				var selectElement = document.getElementById(tempOid+"_select");
   				if(selectElement==null)  continue;
   				var tempSelect = selectElement[selectElement.selectedIndex].value;
   				//alert("tempSelect is: " + tempSelect);
   				if(tempSelect==butongyi){
   					flag = false;
   				}
   				if(tempSelect!=weichuli){
   					flag1 = false;
   				}

   				var inputAdviseElement = document.getElementById(tempOid+"_advise");



	   			var inputPersonsElementVal = "";
				var zhuzhichejian = "";
				var fuzhichejian = "";



				//无需会签 不允许填写会签意见
   				if(tempSelect==weichuli&&inputAdviseElement.value!=""){
   					alert(number+"无需会签，不允许填写会签意见");
					inputAdviseElement.focus();
   					return;
   				}


   				if (tempSelect != weichuli) {
					signValue = signValue + tempOid + ";;;qqq" + tempSelect + ";;;qqq" + "" + ";;;qqq" + inputAdviseElement.value + ";;;qqq" + inputPersonsElementVal + ";;;qqq" + inputPersonsElementVal + ";;;qqq" + zhuzhichejian + ";;;qqq" + fuzhichejian + ";;;qqq" + tempOid;
				}
				if(i<oidArray.length-1){
					if (tempSelect != weichuli) {
						signValue += ";;;ppp";
					}
   				}
   			}

   			if(flag1){
   					var allWeichuli = '<%=allWeichuli%>';
					//alert("allWeichuli is: " + allWeichuli);
					var zzhq = find2("isHuiqian");

   					//alert("zzhq is: " + zzhq);
   					if(zzhq!=null && (zzhq && zzhq.checked == false)){
						alert(allWeichuli);
   						return;
   					}
   			}

   			if(!flag){
   				var passRadio = document.getElementById('<%=passRadio%>');
				var rejectRadio = document.getElementById('<%=rejectRadio%>');
				if(passRadio&&rejectRadio){
					rejectRadio.checked=true;
				}
   			}else{
	   			var passRadio = document.getElementById('<%=passRadio%>');
				var rejectRadio = document.getElementById('<%=rejectRadio%>');
				if(passRadio&&rejectRadio){
					passRadio.checked=true;
				}
   			}
   			signValue=encodeURI(signValue);
   			signValue=encodeURI(signValue);
   			signValue2=encodeURI(signValue2);

			if(document.getElementById("CustActVarproxyCustActVar")!=null){
				proxy = document.getElementById("CustActVarproxyCustActVar").value;
			}
			//alert("------proxy:"+proxy);

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

	xmlHttpRequest.onreadystatechange=function(){
		if(xmlHttpRequest.readyState==4){
			if (xmlHttpRequest.status == 200) {
				var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
			 	completehiddenBtn.click();
			}else{
				}
		}
	}

			//xmlHttpRequest.open("GET","<%=contextPath%>/netmarkets/jsp/ext/workflow/tree/setSignValue.jsp?value="+signValue+"&oid="+workflowProcessOid,true);
			//xmlHttpRequest.setRequestHeader("If-Modified-Since","0");
			//xmlHttpRequest.send(null);
			xmlHttpRequest.open("POST","<%=contextPath%>/netmarkets/jsp/ext/workflow/tree/setSignValue.jsp",true);
			xmlHttpRequest.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
			xmlHttpRequest.send("value="+signValue+"&oid="+workflowProcessOid+"&zpGYZZ="+zpGYZZ+"&proxy="+proxy);
   	}



	function replaceReviewCompleteButton() {
			var completeBtn = document.getElementsByName("complete")[0];
    	var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
    	if (completeBtn&&completehiddenBtn) {
        //completeBtn.oldOnClick = completeBtn.onclick;
        completehiddenBtn.onclick = completeBtn.onclick;
    		completeBtn.onclick = setReviewData;
    	}
	}
	replaceReviewCompleteButton();
		function hiddenRadio(){
			var passRadio = document.getElementById('<%=passRadio%>');
			var rejectRadio = document.getElementById('<%=rejectRadio%>');
			if(passRadio&&rejectRadio){
				passRadio.style.display="none";
				rejectRadio.style.display="none";
				var labelElements = document.getElementsByTagName("label");
				for(var i = 0 ; i < labelElements.length ; i++){
					if(labelElements[i].innerHTML == '<%=passRadio%>' || labelElements[i].innerHTML == '<%=rejectRadio%>'){
						labelElements[i].style.display = "none";
					}
				}
			}
	}
	hiddenRadio();
		function setConclusion(){
		var checkboxElement = document.getElementsByName("pjl_selPJLsa1__1");
		var conclusion = document.getElementById('editablecolumns');
		var flag = false;
		for(var i = 0 ; i < checkboxElement.length ; i++){
			if(checkboxElement[i].checked){
				flag = true;
				break;
			}
		}
		if(!flag){
			var selectData = '<%=selectData%>';
			alert(selectData);
			return;
		}
		var size = checkboxElement.length;
		//alert(checkboxElement.length);
		for(var i = 0 ; i < checkboxElement.length ; i++){
			//var tempValue = checkboxElement[i].checked;
			//alert(checkboxElement[i].value);
			//alert(checkboxElement[i].checked);
			if(checkboxElement[i].checked){
				var tempValue = checkboxElement[i];
				var temp = tempValue.value;
				if(temp.indexOf("EPMDocument")>-1||temp.indexOf("WTDocument")>-1||temp.indexOf("WTChangeOrder2")>-1||temp.indexOf("MPMProcessPlan")>-1)
				{
					var n = temp.lastIndexOf("$");
					//alert(n);
					var selectx = temp.substring(n+4,temp.length-2);
					if(selectx.indexOf("^VR:") > -1){
						var ojbs = selectx.split("^VR:");
						selectx = ojbs[ojbs.length-1];
					}
					//alert("selectx is:" + selectx);
					selectx = selectx.replace(":",">");
					//alert("selectx is:" + selectx);
					var selecta = selectx + "_select";
					//alert("selecta is:" + selecta);
					//var selectb = window.opener.document.getElementById(selecta);
					var selectb = document.getElementsByName(selecta);
					//alert("selectb is: " + selectb.length);
					for(var j=0;j<selectb.length;j++){
						var selectc = selectb[j];
						selectc.selectedIndex=conclusion.selectedIndex;
						//alert("selectc.value is:" + selectc.value);
					}
			  }
			}
		}
	}


   	</script>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>