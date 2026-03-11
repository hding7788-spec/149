<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@taglib prefix="fmt" uri="http://www.ptc.com/windchill/taglib/fmt"%>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<fmt:setBundle basename="ext.casc.sop.ui.SopActionsRB" />
<fmt:message var="importSOPPart" key="sopCustom.importSOPPart.description" />
<jca:wizard title="${importSOPPart}" buttonList="NoStepsWizardButtons">
	<jca:wizardStep action="importSOPPart_step" type="sopCustom" label="${importSOPPart}" />
</jca:wizard>
<%@include file="/netmarkets/jsp/util/end.jspf"%>