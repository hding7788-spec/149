<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"
%><%@ taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards"
%><%@ taglib uri="http://www.ptc.com/windchill/taglib/attachments" prefix="attachments"
%><%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@include file="/netmarkets/jsp/change/propagationConfiguration.jspf"%>

<jca:initializeItem operation="${createBean.create}" attributePopulatorClass="com.ptc.windchill.enterprise.change2.forms.populators.FlexibleChangeNoticeAttributePopulator" />

<%@include file="/netmarkets/jsp/change/changeWizardConfig.jspf"%>
<%@include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>
<%@page import="ext.casc.ecn.ecnConstant"%>
<cwiz:initializeChangeWizard changeMode="CREATE" annotationUIContext="change" changeItemClass="wt.change2.ChangeOrderIfc" />
<cwiz:initializeSelectedItems />

<%
	String zhengchang = ecnConstant.ZHENGCHANG;
	String zuofei = ecnConstant.ZUOFEI;
	String shoufeihouerlei = ecnConstant.SHOUFEIHOUERLEI;
	String shoufeihouerleilm = ecnConstant.SHOUFEIHOUERLEI_LM;
	String sanlei = ecnConstant.SANLEI;
	String sanleilm = ecnConstant.SANLEI_LM;
	String yilei = ecnConstant.YILEI;
	String yileilm = ecnConstant.YILEI_LM;
	String inputType = ecnConstant.INPUTTYPE;
	String shengqing = ecnConstant.SHENQING;
	String genggailunzheng = ecnConstant.GENGGAILUNZHENG;
	String exceptionstr = ecnConstant.EXCEPTIONSTR;
	String cause = ecnConstant.SHEJIGENGGAI;
	String exceptionstr1 = ecnConstant.EXCEPTIONSTR1;
	String exceptionstr2 = ecnConstant.EXCEPTIONSTR2;
	String exceptionstr3 = ecnConstant.EXCEPTIONSTR3;
	String exceptionstr4 = ecnConstant.EXCEPTIONSTR4;
	String EXCEPTIONSTR6 = ecnConstant.EXCEPTIONSTR6;
	String EXCEPTIONSTR8 = ecnConstant.EXCEPTIONSTR8;
	String contextPath = request.getContextPath();
	String oid = request.getParameter("oid");
%>
<SCRIPT LANGUAGE="JavaScript">
	var storeIframes = true;
	var iframeTableId = "changeNotice.wizardImplementationPlan.table";
	var changeNotice = true;
	PTC.wizardIframes.initStoreIframes();

	function isHandPut(o,id){
		if(o.checked){
			var input = document.getElementById(id);
			input.style.display = "block";
			input.removeAttribute("disabled");
			var select = document.getElementById("GONGYIWENJIANMULUNUM");
			select.style.display = "none";
			select.setAttribute("disabled","true");
		}else{
			var input = document.getElementById(id);
			input.style.display = "none";
			input.setAttribute("disabled","true");
			var select = document.getElementById("GONGYIWENJIANMULUNUM");
			select.style.display = "block";
			select.removeAttribute("disabled");
		}
	}

	function isCapp(o,id){
		if(o.checked){
			var changeValue = document.getElementById(id);
			changeValue.readOnly = false;
		}else{
			var changeValue = document.getElementById(id);
			document.getElementById(id).value = "";
			changeValue.readOnly = true;
		}
	}

    function validateAttachments(){
		return true;
	}

	function validateSheJiChangeNum() {
		var exceptionstr1 = '<%=exceptionstr1%>';
		/**var changecause = document.getElementById("CHANGECAUSE");**/
		var cause = '<%=cause%>';
		var fenshu = document.getElementById("FAFANGFENSHU");
		var exceptionstr2 = '<%=exceptionstr2%>';
		var pattern = /^[0-9]*$/;
		var fafangdanwei = document.getElementById("FAFANGDANWEI");
		var exceptionstr3 = '<%=exceptionstr3%>';
		var exceptionstr4 = '<%=exceptionstr4%>';
		/**if(cause==changecause.value){
          var sheJiChangeNum = document.getElementById("DESIGNCHANGENUM");
          if(sheJiChangeNum.value.length==0){
        	  alert(exceptionstr1);
        	  return false;
          }
        }**/

		if (fafangdanwei) {
			if (fafangdanwei.value.length == 0) {
				if (fenshu) {
					if (fenshu.value.length > 0) {
						alert(exceptionstr3);
						return false;
					}
				}
			} else {
				if (fenshu) {
					if (fenshu.value == 0) {
						alert(exceptionstr4);
						return false;
					} else {
						if (!pattern.test(fenshu.value)) {
							alert(exceptionstr2);
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	function validateCreateChangeOrder2(){
		var changecause = document.getElementById("CHANGECAUSE");
		if(changecause){
			if(changecause.value==""){
				alert('<%=EXCEPTIONSTR6%>');
				return false;
			}
		}else{
			alert('<%=EXCEPTIONSTR6%>');
			return false;
		}
		var exceptionstr = '<%=exceptionstr%>';
		/**    	var type = document.getElementById("ECNTYPE");**/
    	var changeType = document.getElementById("CHANGENOTICETYPE");
    	var zhengchang = '<%=zhengchang%>';
    	var zuofei = '<%=zuofei%>';
    	var shoufeihouerlei = '<%=shoufeihouerlei%>';
    	var shoufeihouerleilm = '<%=shoufeihouerleilm%>';
    	var sanlei = '<%=sanlei%>';
    	var yilei = '<%=yilei%>';
    	var yileilm = '<%=yileilm%>';
    	var inputType = '<%=inputType%>';
        var shengqing = '<%=shengqing%>';
        var genggailunzheng = '<%=genggailunzheng%>';
        var j = 0;
        if (changeType) {
			if (shoufeihouerlei == changeType.value || yilei == changeType.value || shoufeihouerleilm == changeType.value || yileilm == changeType.value) {
				var ecrNumber = Ext.getDom('ECRNUMBER').value;
				if(ecrNumber == null || ecrNumber == '') {
					alert(exceptionstr);
					return false;
				}
			}
		}
		return true;
	}

	function collectProcessDoc(){
		var table =PTC.jca.table.Utils.getTable("ext.casc.common.mvc.builder.CollectProcessDocBuilder");
		var allSelection = table.getSelectionModel().getSelections();
		var params ="";
		for(var i=0;i<allSelection.length;i++){
			var mpmOid = allSelection[i].data.oid;
			params = params+mpmOid+"$";
		}
		if(params==""){
			return true;
		}
		var url =  "<%=contextPath%>/netmarkets/jsp/ext/casc/changeNotice/setCollectDocsValue.jsp?oid=<%=oid%>";


		url=encodeURI(url);
		Ext.Ajax.request({
			url: url,
			params:{data:params},
			method: "POST",
			async: false,
			success: function(response) {
				return true;
			},
			failure: function(response) {
				return true;
			}
		});
		return true;

	}

	function setEcrNumber() {
		window.open('<%=contextPath%>/netmarkets/jsp/ext/casc/changeRequest/setEcrNumber.jsp?oid=<%=oid%>', '关联工艺更改申请单', 'height=600, width=800, top=150, left=300');
	}
</SCRIPT>

<jca:wizard helpSelectorKey="change_createChangeNotice" buttonList="DefaultWizardButtonsWithSubmitPrompt" formProcessorController="com.ptc.windchill.enterprise.change2.forms.controllers.ChangeTaskTemplatedFormProcessorController" wizardSelectedOnly="true">
	<%-->Create Change Notice<--%>
	<jca:wizardStep action="setChangeContextWizStep" type="change"/>
	<jca:wizardStep action="custom_defineItemAttributesWizStep" type="custom_defineItemAttributes"/>
	<jca:wizardStep action="securityLabelStep" type="securityLabels"/>
	<jca:wizardStep action="create_wizardImplementationPlanStep" type="changeNotice"/>
	<jca:wizardStep action="custom_attachments_step" type="custom_attachments"/>
	<jca:wizardStep action="associatedChangeRequestsStep" type="changeNotice"/>
	<jca:wizardStep action="collectProcessDocStep" type="customChangeNotice"/>
</jca:wizard>

<attachments:fileSelectionAndUploadApplet/>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>