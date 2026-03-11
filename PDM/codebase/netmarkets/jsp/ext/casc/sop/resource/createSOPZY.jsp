<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt"        prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"              prefix="c"%>


<fmt:setBundle basename="ext.casc.sop.ui.SopActionsRB" />
<c:if test="${'CSXM'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="NEW_SOPCSXM" />
</c:if>
<c:if test="${'DZQY'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="NEW_SOPDZQY" />
</c:if>
<c:if test="${'CZGW'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="NEW_SOPCZGW" />
</c:if>
<c:if test="${'ZYLB'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="NEW_SOPZYLB" />
</c:if>
<c:if test="${'GXMC'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="New_SOPGXMC" />
</c:if>
<c:if test="${'CSXMMC'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="New_SOPCSXMMC" />
</c:if>
<c:if test="${'WZLB'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="New_SOPWZLB" />
</c:if>
<c:if test="${'CZMC'==param.mpmResourceType}">
	<fmt:message var="wizardTitle" key="New_SOPCZMC" />
</c:if>


<%-- Set the default part management help --%>
<c:set var="buttonList" value="DefaultWizardButtons" scope="page" />

<jca:initializeItem operation="${createBean.create}" baseTypeName="com.ptc.windchill.mpml.resource.MPMResource" />
<%--create ProcessResource Wizard --%>
<jca:wizard buttonList="${buttonList}" title="${wizardTitle}">
	<jca:wizardStep action="createSOPZYStep" type="sopCustom" />
</jca:wizard>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
