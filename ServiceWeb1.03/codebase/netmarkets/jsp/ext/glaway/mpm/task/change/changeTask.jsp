<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%@page import="com.glaway.mpm.task.ui.TaskResource"%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.glaway.mpm.task.ui.TaskResource"/>
<fmt:message var="changeTaskTitle" key="<%=TaskResource.PRIVATE_CONSTANT_53 %>"/>

<jca:wizard buttonList="DefaultWizardButtonsNoApply" title="${changeTaskTitle}" progressMessage="${changeTaskTitle}">
	<jca:wizardStep type="task" action="changeTaskWizard"/>
</jca:wizard>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>