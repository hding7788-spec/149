<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
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

	String rejectRadio = "驳回";

	String weichuli = "无需会签";

	String tongyi = "同意";

	String butongyi = "不同意";

	String setConclusion = "批量设置会签结论:";

	String selectData = "请选择数据!";

	String allWeichuli = "请设置您负责的图纸的会签结论!";

	String noQianming = "请设置签名信息!";

	String workflowProcessOid = request.getParameter("oid");
	String contextPath = request.getContextPath();
	commandBean.getRequestData().getParameterMap().put("oid",workflowProcessOid);
	List<String> oidList = SignatureHelper.getReviewOid(workflowProcessOid);
	System.out.println(oidList);
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
<script>
		var butongyi = '<%=butongyi%>';
		var weichuli = '<%=weichuli%>';
	var workflowProcessOid = '<%=workflowProcessOid%>';
	function addComment(oid){
		window.open("<%=contextPath%>/netmarkets/jsp/ext/workflow/addComment.jsp?useJSCA=false&oid="+oid,"","height=480, width=640, top=300, left=600, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no");
	}
	</script>
<div>
<table border="0" style="margin-left: 12px" desciption="Edit Components Table" width="100%">
	<tbody>
		<tr align="center">
			<td align="left" width="120"><h4><%=setConclusion%></h4></td>
			<td align="left">
				  <select name="editablecolumns"  id="editablecolumns" onchange='setConclusion()'>
						<option selected value="<%=tongyi%>"><%=tongyi%></option>
						<option  value="<%=weichuli%>"><%=weichuli%></option>
						<option value="<%=butongyi%>"><%=butongyi%></option>
					</select></td>
					<td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td>
					<td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td>
					<td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td><td>&nbsp</td>
		</tr>
		</tbody>
	</table>
</div>
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
   <jca:describeTableTree  id="SignatureTreeHandler1"
                           var="tree_descriptor4"
                           type="wt.part.WTPart"
                           label="${treeName}"
                           nodeColumn="number"
                           configurable="true"
                           expansion="full"
                           helpContext="DEFAULT_HELP_PAGE"
                           disableAction="false"
                           summary="test">
      <jca:setComponentProperty key="selectable"     value="true"/>
      <jca:describeColumn id="nmActions" label="Actions" sortable="false">
	  	<jca:setComponentProperty key="actionModel" value="custom_folderbrowser_toolbar_open_submenu" />
      </jca:describeColumn>
      <jca:describeColumn id="icontype" label="" dataUtilityId="SignatureTreeDataUtility2" displayLengthInTables="5"/>
      <jca:describeColumn id="number" displayLengthInTables="20"/>
      <jca:describeColumn id="name" displayLengthInTables="20"/>
	  <jca:describeColumn id="version" need="versionInfo.identifier.versionId" displayLengthInTables="20"/>
      <jca:describeColumn id="sign_result" label="${sign_result}" dataUtilityId="SignatureTreeDataUtility2" displayLengthInTables="20"/>
	  <jca:describeColumn id="sign_message" label="${sign_message}" dataUtilityId="SignatureTreeDataUtility2" displayLengthInTables="20"/>
	  <jca:describeColumn id="sign_advise" label="${sign_advise}" dataUtilityId="SignatureTreeDataUtility2" displayLengthInTables="20"/>
	  <jca:describeColumn id="sign_button" label="  " dataUtilityId="SignatureTreeDataUtility2" displayLengthInTables="20"/>
   </jca:describeTableTree>

   <%-->Get a component model for our tree<--%>
   <jca:getModel var="tree_model4"
                 descriptor="${tree_descriptor4}"
                 treeHandler="SignatureTreeHandler1"/>

   <%-->Get the NmHTMLTableTree from the command<--%>
   <jca:renderTableTree model="${tree_model4}" showTreeLines="true" useJSCA="false"/>
   	
   	<script>
   		function setReviewData(){
   			var flag = true;
   			var proxy = "no";
		//		var flag1 = true;
   			var signValue = "";
   			for(var i = 0 ; i < oidArray.length ; i++){
   				var tempOid = oidArray[i];
   				var selectElement = document.getElementById(tempOid+"_select");
   				var tempSelect = selectElement[selectElement.selectedIndex].value;
   				if(tempSelect==butongyi){
   					flag = false;	
   				}
   		/*		if(tempSelect!=weichuli){
   					flag1 = false;	
   				}*/
   				var inputMessageElement = document.getElementById(tempOid+"_message");
   				var inputAdviseElement = document.getElementById(tempOid+"_advise");
   				signValue = signValue+tempOid+";;;qqq"+tempSelect+";;;qqq"+inputMessageElement.value+";;;qqq"+inputAdviseElement.value;
   				if((tempSelect!=weichuli)&&inputMessageElement.value.length==0){
   					var noQianming = '<%=noQianming%>';
					alert(noQianming);
   					return;
   				}
   				if(i<oidArray.length-1){
   					signValue += ";;;ppp";
   				}
   			}
   	/*		if(flag1){
   				var allWeichuli = '<%=allWeichuli%>';
				alert(allWeichuli);
   				return;
   			}*/
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
			
			if(document.getElementById("CustActVarproxyCustActVar")!=null){
				proxy = document.getElementById("CustActVarproxyCustActVar").value;
			}

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
			xmlHttpRequest.send("value="+signValue+"&oid="+workflowProcessOid+"&proxy="+proxy);
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
				if(temp.indexOf("EPMDocument")>-1||temp.indexOf("WTDocument")>-1||temp.indexOf("MPMProcessPlan")>-1)
				{
					var n = temp.lastIndexOf("$");
					//alert(n);
					var selectx = temp.substring(n+4,temp.length-2);
					//alert("selectx is:" + selectx);
					if(selectx.indexOf("^")>-1){
						var m = selectx.lastIndexOf("^");
						selectx = selectx.substring(m+4,selectx.length);
						selectx = selectx.replace(":",">");
						//alert("selectx is:" + selectx);
					}else{
						selectx = selectx.replace(":",">");
					}
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
