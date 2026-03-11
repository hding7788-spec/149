<%@page import="ext.casc.process.ProcessConstants"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ taglib prefix="docmgnt" uri="http://www.ptc.com/windchill/taglib/docmgnt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource"/>
<fmt:message var="defineDocWizStepLabel" key="document.create.DEFINE_ITEM_WIZ_STEP_LABEL" />
	<fmt:setBundle basename="ext.ases.technotice.resource.technoticeRB"/>
<%-- <fmt:message var="setRelatedDataStepLabel" key="technotice.create.SET_RELATEDDATA_WIZ_STEP_LABEL" /> --%>
<fmt:message var="setAttributesWizStepLabel" key="technotice.create.SET_ATTRIBUTES_WIZ_STEP_LABEL" />
<jca:initializeItem operation="${createBean.create}"/>

<%@include file="/netmarkets/jsp/change/changeWizardConfig.jspf" %>
    <cwiz:initializeChangeWizard changeMode="CREATE" varianceEffectivity="false" annotationUIContext="change" />

<%
request.setCharacterEncoding("UTF-8");
String oid = (String)request.getParameter("oid");
String type = (String)request.getParameter("type");
//System.out.println("oid is:"+ request.getParameter("oid"));

String name=ProcessConstants.GYFFA_STYLE;
%>
<c:set var="type" value="<%=type%>" />
<c:set var="name" value="<%=name%>" />
<script type="text/javascript">
function validateTemplate(){
   if (document.getElementById("createType") == null){
      JCAAlert("com.ptc.windchill.enterprise.doc.documentResource.NO_TEMPLATES");
      return false;
   }else
      return true;
}
</script>

<docmgnt:validateNameJSTag template="false" />

<jca:initializeItem operation="${createBean.create}" attributePopulatorClass="com.ptc.windchill.enterprise.doc.forms.DocAttributePopulator" baseTypeName="${type}"/>

<jca:wizard title="${name}" buttonList="DefaultWizardButtonsNoApply" helpSelectorKey="DocMgmtDocCreateFromTemplate">
    <jca:wizardStep action="setContextWizStep" type="object"/>

 <%--  <jca:wizardStep action="createTechnoticeFromTemplateSetAttributesWizStep"  type="gongyiFenfangan"/> --%>
  <jca:wizardStep action="createDocumentSetTypeAndAttributesWizStep"  type="document"/>
      <jca:wizardStep action="attachments_step"  type="attachments" />

    </jca:wizard>

   <%--- If we are not DTI then add the applet for doing file browsing and file uploads --%>
   <wctags:fileSelectionAndUploadAppletUnlessMSOI forceApplet='${param.forcedFilePath != null }'/>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>