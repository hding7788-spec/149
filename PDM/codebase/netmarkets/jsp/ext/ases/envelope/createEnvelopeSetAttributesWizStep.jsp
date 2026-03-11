<%@taglib    uri="http://www.ptc.com/windchill/taglib/components"   prefix="jca"     
%><%@taglib uri="http://www.ptc.com/windchill/taglib/fmt"           prefix="fmt"      
%><%@taglib uri="http://www.ptc.com/windchill/taglib/changeWizards" prefix="cwiz"      
%><%@include file="/netmarkets/jsp/util/begin.jspf"
%><%@include file="/netmarkets/jsp/components/createEditUIText.jspf"
%><%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"
%>   
<%-- attributes used by attachments code TODO remove this --%>
<%
String createType = "ext.ases.envelope.ProcessEnvelope";
request.setAttribute("createType", createType);%>
<!-- Setup the localization string constants -->
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.ases.envelope.envelopeResource" />
<fmt:message var="tableTitle"       key="envelope.create.SET_ATTRIBUTES_WIZ_STEP_LABEL"/>

<%-- When coming from EDA Compare this set the file for use in the attachment component
     But also it tells the attachments component to upload the file.
 --%>
<c:if test='${param.forcedFilePath != null }'>
 <c:set var="fixedFilePath" value="${param.forcedFilePath}" scope="request" />
 <c:set var="fixedFileUpload" value="true" scope="request" />
</c:if>

<%-- displays type and id attributes from the previous step --%>
<%@ include file="/netmarkets/jsp/components/setAttributesReadOnlyPropertyPanel.jspf"%>

<%-->Build a table descriptor and assign it to page variable td

This defines all of the attributes in the attributes step
<--%>
<jca:describeAttributesTable var="attributesTableDescriptor" 
							id="createSetAttributes" 
							mode="CREATE" 
							scope="request"
							componentType="WIZARD_ATTRIBUTES_TABLE" 
							type="ext.ases.envelope.ProcessEnvelope" 
							label="${tableTitle}">
  <jca:describeProperty id="number" />
  <jca:describeProperty id="name"/>
  <jca:describeProperty id="description"/>
  <jca:describeProperty id="folder.id"/>
  <jca:describeProperty id="lifeCycle.id"/>
  <jca:describeProperty id="teamTemplate.id"/>
    <jca:describeProperty id="IBA|FAWANGDANWEI"/>



    <%-->  	HARD ATTRIBUTE HOW TO CONTROL DISPLAY?
    <jca:describeProperty id="ALL_CUSTOM_HARD_ATTRIBUTES_FOR_INPUT_TYPE"/><--%>
  <jca:describeProperty id="ALL_SOFT_NON_CLASSIFICATION_SCHEMA_ATTRIBUTES"/>
</jca:describeAttributesTable> 

<%-- renders data defined above --%>
<%@ include file="/netmarkets/jsp/document/setAttributesWizStepWithContent.jspf"%>

<script language='Javascript'>
	loadAllRemainingNonRequiredSteps(); 
</script>

<%@include file="/netmarkets/jsp/util/end.jspf"%>