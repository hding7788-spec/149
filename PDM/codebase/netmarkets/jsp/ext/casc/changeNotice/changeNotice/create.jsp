<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/attachments" prefix="attachments" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@include file="/netmarkets/jsp/change/propagationConfiguration.jspf"%>

<jca:initializeItem operation="${createBean.create}" attributePopulatorClass="com.ptc.windchill.enterprise.change2.forms.populators.FlexibleChangeNoticeAttributePopulator" />

<%@include file="/netmarkets/jsp/change/changeWizardConfig.jspf"%>
<%@include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>
<%@page import="ext.casc.ecn.ecnConstant"%>
<cwiz:initializeChangeWizard changeMode="CREATE" annotationUIContext="change" changeItemClass="wt.change2.ChangeOrderIfc" />
<cwiz:initializeSelectedItems />

<SCRIPT LANGUAGE="JavaScript">
	var storeIframes = true;
	var iframeTableId = "changeNotice.wizardImplementationPlan.table";
	var changeNotice = true;
	PTC.wizardIframes.initStoreIframes();

	function validateAttachments(){
		return true;
	}

	function validateCreateChangeOrder2(){
		return true;
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
</jca:wizard>

<attachments:fileSelectionAndUploadApplet/>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
