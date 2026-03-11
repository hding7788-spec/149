<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@include file="/netmarkets/jsp/change/changeWizardConfig.jspf" %>
<%@ taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards" %>
<cwiz:initializeChangeWizard changeMode="CREATE" varianceEffectivity="false" annotationUIContext="change" />
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="CUSTOM_GYRWPRINTTASK" key="CUSTOM_GYRWPRINTTASK"/>
<jca:wizard title="${PURGE_DATA}" buttonList="NoStepsWizardButtons"> 
	<jca:wizardStep action="gywjPrintTask_step" type="custom" label="${CUSTOM_GYRWPRINTTASK}"/>
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>