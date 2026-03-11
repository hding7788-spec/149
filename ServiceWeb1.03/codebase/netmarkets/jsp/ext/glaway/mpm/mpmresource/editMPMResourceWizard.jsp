<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%@ page import="com.glaway.mpm.mpmresource.ui.MPMResourceRB"%>
<fmt:setBundle basename="com.glaway.mpm.mpmresource.ui.MPMResourceRB"/>
<fmt:message var="wizardTitle" key="Edit_MPMResource" />

<fmt:setBundle basename="ext.casc.sop.ui.SopActionsRB" />
<c:if test="${'sop'==param.sopType}">
	<fmt:message var="wizardTitle" key="EDIT_SOPZY" />
</c:if>
<jca:initializeItem operation="${createBean.edit}" baseTypeName="com.ptc.windchill.mpml.resource.MPMSkill"/>



<jca:wizard title="${wizardTitle}"  buttonList="DefaultWizardButtons">
	<jca:wizardStep action="editMPMResourceWizardStep" type="MPMResource" />
</jca:wizard>



<%@ include file="/netmarkets/jsp/util/end.jspf"%>
