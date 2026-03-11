<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>

<%@ taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards" %>
<script language="JavaScript" src='netmarkets/javascript/util/revisionLabelPicker.js'></script>

<fmt:setBundle basename="ext.ases.envelope.envelopeResource"/>

<fmt:message var="defineEnvelopeWizStepLabel" key="envelope.create.DEFINE_ITEM_WIZ_STEP_LABEL" />
<fmt:message var="setAttributesWizStepLabel" key="envelope.create.SET_ATTRIBUTES_WIZ_STEP_LABEL" />
<fmt:message var="setRelatedDataStepLabel" key="envelope.create.SET_RELATEDDATA_WIZ_STEP_LABEL" />
<fmt:message var="attachmentsWizStepLabel" key="envelope.create.ATTACHMENTS_WIZ_STEP_LABEL" />

<jca:initializeItem operation="${createBean.edit}"/>

<%@include file="/netmarkets/jsp/change/changeWizardConfig.jspf" %>

<%-- populate number attrubute for insert revision action only --%>
<jca:initializeItem operation="${createBean.edit}" />

<c:choose>
      <c:when test='${param.showContextStep != null}'>
         <c:set var="helpKey" value="createSharedDoc" scope="page"/>
         <c:set var="buttonList" value="DefaultWizardButtons" scope="page"/>
         <!-- Exclude Program contexts -->
         <jsp:setProperty name="createBean" property="contextPickerExcludeTypes" value="WCTYPE|wt.projmgmt.admin.Project2|com.ptc.Program"/>
      </c:when>
      <c:otherwise>
         <%-- Set the default doc management help --%>
         <c:set var="helpKey" value="PartCreate_help" scope="page"/>
         <c:set var="buttonList" value="DefaultWizardButtonsNoApply" scope="page"/>
      </c:otherwise>
   </c:choose>

<cwiz:initializeChangeWizard changeMode="EDIT" varianceEffectivity="false" annotationUIContext="change" />
<jca:wizard helpSelectorKey="${helpKey}" buttonList="${buttonList}">
<jca:wizardStep action="attachments_step" type="attachments" />
</jca:wizard>
<cwiz:initializeConcurrentUpdateSupport callbackFunction="validateCount" />
<attachments:fileSelectionAndUploadApplet/>
<%@include file="/netmarkets/jsp/util/end.jspf"%>