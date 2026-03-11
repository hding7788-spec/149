<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@taglib prefix="fmt" uri="http://www.ptc.com/windchill/taglib/fmt"%>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="addSystemConfiguration" key="custom.addSystemConfiguration.description" />
<jca:wizard title="${addSystemConfiguration}" buttonList="NoStepsWizardButtons">
	<jca:wizardStep action="addSystemConfiguration_step" type="custom" label="${addSystemConfiguration}" />
</jca:wizard>
<%@include file="/netmarkets/jsp/util/end.jspf"%>