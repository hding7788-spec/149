
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page import="com.glaway.mpm.mpmresource.ui.MPMResourceRB"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt"        prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"              prefix="c"%>


<fmt:setBundle basename="com.glaway.mpm.mpmresource.ui.MPMResourceRB" />
<c:if test="${'ZZDW'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="New_ZZDW" />
</c:if>
<c:if test="${'GW'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="New_GW" />
</c:if>
<c:if test="${'SB'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="New_SB" />
</c:if>
<c:if test="${'GJ'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="New_GJ" />
</c:if>
<c:if test="${'GZhong'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="New_GZhong" />
</c:if>
<c:if test="${'GYFL'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="New_GYFL" />
</c:if>
<c:if test="${'DJ'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="New_DJ" />
</c:if>
<c:if test="${'GXMC'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="New_GXMC" />
</c:if>
<c:if test="${'GYCYY'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="New_GYCYY" />
</c:if>
<c:if test="${'DMSB'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="New_DMSB" />
</c:if>


<%-- Set the default part management help --%>
<c:set var="buttonList" value="DefaultWizardButtonsNoApply" scope="page" />

<jca:initializeItem operation="${createBean.create}" baseTypeName="com.ptc.windchill.mpml.resource.MPMResource" />
<%--create ProcessResource Wizard --%>
<jca:wizard buttonList="${buttonList}" title="${wizardTitle}">
	<jca:wizardStep action="createMPMResourceWizardStep" type="MPMResource" />
</jca:wizard>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
