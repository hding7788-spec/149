<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page import="com.glaway.mpm.pbom.ui.PBOMResource" %>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.glaway.mpm.pbom.ui.PBOMResource"/>
<fmt:message var="taskTitle" key="<%=PBOMResource.PRIVATE_CONSTANT24%>"/>

	<jca:wizard buttonList="DefaultWizardButtonsNoApply" title="${taskTitle}">
		<jca:wizardStep type="task" action="createTechnicWizard"></jca:wizardStep>
	</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>