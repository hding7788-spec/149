
<%@page import="com.glaway.mpm.constants.Constants"%><%@ taglib
	uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%@ page import="com.glaway.mpm.mpmresource.ui.MPMResourceRB"%>
<fmt:setBundle basename="com.glaway.mpm.mpmresource.ui.MPMResourceRB" />
<c:if test="${'GW'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="Add_GW" />
</c:if>
<c:if test="${'GZhong'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="Add_GZhong" />
</c:if>
<c:if test="${'SB'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="Add_SB" />
</c:if>
<c:if test="${'GYCYY'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="Add_GYCYY" />
</c:if>
<c:if test="${'GXMC'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="Add_GXMC" />
</c:if>
<c:if test="${'GJ'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="Add_GJ" />
</c:if>
<c:if test="${'ChildSB'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="Add_ChildSB" />
</c:if>
<c:if test="${'GZhong2'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="Add_GZhong" />
</c:if>

<jca:wizard title="${wizardTitle}" buttonList="DefaultWizardButtons">
	<jca:wizardStep action="addMPMResourceLinkWizardStep"
		type="MPMResource" />
</jca:wizard>



<%@ include file="/netmarkets/jsp/util/end.jspf"%>
