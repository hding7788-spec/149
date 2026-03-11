<%@ page import="ext.casc.ecn.ecnConstant" %>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"
%><%@taglib prefix="c"           uri="http://java.sun.com/jsp/jstl/core"
%><%@taglib prefix="cwiz"        uri="http://www.ptc.com/windchill/taglib/changeWizards"
%><%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments"
%><%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@include file="/netmarkets/jsp/change/propagationConfiguration.jspf"%>

<%
	String exrattachmentstr = ecnConstant.EXRATTACHMENTSTR;
	String inputType = ecnConstant.INPUTTYPE;
%>

<SCRIPT LANGUAGE="JavaScript">
	function validateAttachments() {
		var noFile = true;
		var exrattachmentstr = '<%=exrattachmentstr%>';
		var inputType = '<%=inputType%>';
		var inputs = document.getElementsByTagName('input');
		for (var i = 0; i < inputs.length; i++) {
			if (inputs[i].type == inputType) {
				noFile = false;
			}
		}
		if (noFile) {
			alert(exrattachmentstr);
			return false;
		}
		return true;
	}

</SCRIPT>

<jca:initializeItem operation="${createBean.create}" attributePopulatorClass="com.ptc.windchill.enterprise.change2.forms.populators.ChangeRequestAttributePopulator"/>


<%@include file="/netmarkets/jsp/change/changeWizardConfig.jspf" %>
<%@include file="/netmarkets/jsp/annotation/wizardConfig.jspf" %>

<cwiz:initializeChangeWizard changeMode="CREATE" varianceEffectivity="false" annotationUIContext="change" changeItemClass="wt.change2.ChangeRequestIfc" />

<jca:wizard helpSelectorKey="change_createChangeRequest" buttonList="DefaultWizardButtonsWithSubmitPrompt" formProcessorController="com.ptc.windchill.enterprise.change2.forms.controllers.ChangeItemFormProcessorController" wizardSelectedOnly="true">
 	<jca:wizardStep action="setChangeContextWizStep" type="change"/>
	<jca:wizardStep action="defineItemAttributesWizStep" type="object"/>
	<jca:wizardStep action="securityLabelStep" type="securityLabels"/>
<%--	<jca:wizardStep action="affectedEndItemsStep" type="change" />--%>
<%--	<jca:wizardStep action="affectedDataStep" type="change" />--%>
	<jca:wizardStep action="custom_attachments_step" type="custom_attachments" />
<%--	<jca:wizardStep action="associatedChangeIssuesStep" type="changeRequest" />--%>
</jca:wizard>

<attachments:fileSelectionAndUploadApplet/>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>