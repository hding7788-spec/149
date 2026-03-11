<%@taglib   uri="http://www.ptc.com/windchill/taglib/components"    prefix="jca"
%><%@taglib uri="http://www.ptc.com/windchill/taglib/fmt"           prefix="fmt"
%><%@taglib uri="http://www.ptc.com/windchill/taglib/changeWizards" prefix="cwiz"
%><%@include file="/netmarkets/jsp/util/begin.jspf"
%><%@include file="/netmarkets/jsp/components/createEditUIText.jspf"
%><%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"
%>

<!-- Setup the localization string constants -->
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.change2.change2ClientResource" />
<fmt:message var="tableTitle"       key="ATTRIBUTES_TABLE"/>

<fmt:setBundle basename="com.ptc.core.ui.componentRB" />
<fmt:message var="name"        		   key="NAME" />

<%@ include file="/netmarkets/jsp/change/setAttributesReadOnlyPropertyPanel.jspf"%>

<%-->Build a table descriptor and assign it to page variable td<--%>
<jca:describeAttributesTable var="attributesTableDescriptor" id="edit.processEnvelopeDetailsStep" mode="EDIT" scope="request"
	componentType="WIZARD_ATTRIBUTES_TABLE" type="ext.ases.envelope.ProcessEnvelope" label="${tableTitle}">
	<%--
	<jca:describeProperty id="number" />--%>
	<jca:describeProperty id="name" mode="VIEW"/>
	<jca:describeProperty id="description" />
		<%--
	<jca:describeProperty id="ALL_CUSTOM_HARD_ATTRIBUTES_FOR_INPUT_TYPE"/>--%>
	<jca:describeProperty id="ALL_SOFT_NON_CLASSIFICATION_SCHEMA_ATTRIBUTES"/>
</jca:describeAttributesTable>

<cwiz:initializeSubmitNow/>

<%@include file="/netmarkets/jsp/components/setAttributesWizStep.jspf"%>


<%@include file="/netmarkets/jsp/util/end.jspf"%>