<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="CUSTOM_UPDATEROLEUSERS" key="CUSTOM_UPDATEROLEUSERS"/>

<jca:wizard title="${PURGE_DATA}" buttonList="NoStepsWizardButtons">
	<jca:wizardStep action="updateRoleUsers_step" type="custom" label="${CUSTOM_UPDATEROLEUSERS}"/>
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>