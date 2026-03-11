<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="CUSTOM_SETPROCESSPATH" key="CUSTOM_SETPROCESSPATH"/>

<jca:wizard title="${PURGE_DATA}" buttonList="NoStepsWizardButtons">
	<jca:wizardStep action="setProcessPath_step" type="custom" label="${CUSTOM_SETPROCESSPATH}"/>
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>