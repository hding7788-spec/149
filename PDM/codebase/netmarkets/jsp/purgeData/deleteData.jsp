<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.casc.puege.purgeDataReource" />
<fmt:message var="PURGE_DATA" key="PURGE_DATA"/>

<jca:wizard title="${PURGE_DATA}" buttonList="NoStepsWizardButtons">
	<jca:wizardStep action="deleteData_step" type="purgeData" label="${PURGE_DATA}"/>
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>