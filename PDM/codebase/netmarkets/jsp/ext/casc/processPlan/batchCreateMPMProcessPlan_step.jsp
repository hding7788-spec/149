<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    String contextPath = request.getContextPath();

%>
<script type="text/javascript">

    function validateSelected(){
        var table = PTC.jca.table.Utils.getTable('ext.casc.processPlan.mvc.builder.BatchCreateMPMProcessPlanBuilder');
        debugger;
        var allSelection = table.getSelectionModel().getSelections();
        var templateName = document.getElementById('processTemplate_' + allSelection[0].data.number).value;
        var templateOid = document.getElementById('processTemplate_' + allSelection[0].data.number+'_oid').value;

		if(templateOid){
			if(""==templateOid){
				alert("<%=ext.casc.processPlan.Constants.MSG_1%>");
				 return false;
			}
		}else{
			 alert("<%=ext.casc.processPlan.Constants.MSG_1%>");
			 return false;
		}
        document.getElementsByName("templateOid")[0].value = templateOid;
        for (var i = 1; i < allSelection.length; i++) {
            var currentTemplateOid = document.getElementById('processTemplate_'+ allSelection[i].data.number+'_oid').value;

            if (currentTemplateOid == templateOid) {

            }else{
            	alert("<%=ext.casc.processPlan.Constants.MSG_2%>");
                return false;
            }
        }
        templateOid = encodeURI(templateOid);

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

        xmlHttpRequest.onreadystatechange = function () {
            if (xmlHttpRequest.readyState == 4) {
                if (xmlHttpRequest.status == 200) {

                } else {
                }
            }
        }

        xmlHttpRequest.open("POST", "<%=contextPath%>/netmarkets/jsp/ext/casc/processPlan/setProcessParamValue.jsp", false);
        xmlHttpRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        xmlHttpRequest.send("templateOid=" + templateOid);

        return true;

    }

</script>
<input type="hidden" name="templateOid" id="templateOid" value="" >
<jsp:include page="${mvc:getComponentURL('ext.casc.processPlan.mvc.builder.BatchCreateMPMProcessPlanBuilder')}"/>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>