<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"
%><%@ taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards"
%><%@ taglib prefix="rwiz" uri="http://www.ptc.com/windchill/taglib/reservation"
%><%@ taglib uri="http://www.ptc.com/windchill/taglib/attachments" prefix="attachments"
%><%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@page import="ext.casc.ecn.ecnConstant" %>

<%@include file="/netmarkets/jsp/components/includeWizBean.jspf" %>

<jca:initializeItem operation="${createBean.edit}"/>

<%@include file="/netmarkets/jsp/change/changeWizardConfig.jspf" %>
<%@include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>

<cwiz:initializeChangeWizard changeMode="EDIT" annotationUIContext="change" changeItemClass="wt.change2.ChangeOrderIfc" />
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

    String contextPath = request.getContextPath();
    String oid = request.getParameter("oid");
%>
<SCRIPT LANGUAGE="JavaScript">
    var storeIframes = true;
    var iframeTableId = "changeNotice.wizardImplementationPlan.table";
    var changeNotice = true;
    PTC.wizardIframes.initStoreIframes();

    function validateAttachments() {
        return true;
    }

    function isHandPut(o, id) {
        if (o.checked) {
            var input = document.getElementById(id);
            input.style.display = "block";
            input.removeAttribute("disabled");
            var select = document.getElementById("GONGYIWENJIANMULUNUM");
            select.style.display = "none";
            select.setAttribute("disabled", "true");
        } else {
            var input = document.getElementById(id);
            input.style.display = "none";
            input.setAttribute("disabled", "true");
            var select = document.getElementById("GONGYIWENJIANMULUNUM");
            select.style.display = "block";
            select.removeAttribute("disabled");
        }
    }

    function isCapp(o, id) {
        if (o.checked) {
            var changeValue = document.getElementById(id);
            changeValue.readOnly = false;
        } else {
            var changeValue = document.getElementById(id);
            document.getElementById(id).value = "";
            changeValue.readOnly = true;

        }
    }

    /*window.onload = function(){
        var changeValue = document.getElementById("CHANGEBEFORINFOR");
        if(changeValue.value != ""){
            document.getElementById("cappCheckbox").checked = true ;
        }
    }*/

    function validateCreateChangeOrder2() {
        var changecause = document.getElementById("CHANGECAUSE");
        if (changecause) {
            if (changecause.value == "") {
                alert('<%=EXCEPTIONSTR6%>');
                return false;
            }
        } else {
            alert('<%=EXCEPTIONSTR6%>');
            return false;
        }
        var exceptionstr = '<%=exceptionstr%>';
        /**        var type = document.getElementById("ECNTYPE");**/
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

                // var grid = Ext.getCmp('attachments.list.editable');
                // if (grid) {
                //     var grid_data = grid.getStore();
                //     //var data_ids = "";
                //     var data_values = "";
                //     for (var i = 0; i < grid_data.data.length; i++) {
                //         var row = grid_data.getAt(i);
                //         var contentName = row.data.contentName;
                //         var value = contentName.gui.value;
                //         if (value.indexOf(shengqing) >= 0
                //             || value.indexOf(genggailunzheng) >= 0) {
                //             j++;
                //         }
                //     }
                // }
                //
                // if (j < 2) {
                //     alert(exceptionstr);
                // }
            }
        }
        return true;
    }

    function setEcrNumber() {
        window.open('<%=contextPath%>/netmarkets/jsp/ext/casc/changeRequest/setEcrNumber.jsp?oid=<%=oid%>', '关联工艺更改申请单', 'height=600, width=800, top=150, left=300');
    }
</SCRIPT>

<jca:wizard helpSelectorKey="change_editChangeNotice" buttonList="DefaultWizardButtonsWithSubmitPrompt" formProcessorController="com.ptc.windchill.enterprise.change2.forms.controllers.EffectivityAwareIframeFormProcessorController">
    <%-->Create Change Notice<--%>
    <jca:wizardStep action="custom_editAttributesWizStep" type="custom_object"/>
    <jca:wizardStep action="edit_wizardImplementationPlanStep" type="changeNotice"/>
    <jca:wizardStep action="custom_attachments_step" type="custom_attachments"/>
    <jca:wizardStep action="associatedChangeRequestsStep" type="changeNotice"/>
	<jca:wizardStep action="associatedChangeItemsStep" type="change" />
</jca:wizard>

<rwiz:handleUpdateCount/>
<rwiz:configureReservation reservationType="modify" enforcedByService="true" workflowOverride="true"/>

<attachments:fileSelectionAndUploadApplet/>

<script language='Javascript'>
   change_postLoad();   
</script>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>




