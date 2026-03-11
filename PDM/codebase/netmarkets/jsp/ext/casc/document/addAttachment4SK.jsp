<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@taglib prefix="fmt" uri="http://www.ptc.com/windchill/taglib/fmt"%>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="addSK" key="custom.addAttachment4SK.description" />
<jca:wizard title="${addSK}" buttonList="NoStepsWizardButtons">
	<jca:wizardStep action="addAttachment4SK_step" type="custom" label="${addSK}" />
</jca:wizard>
<%@include file="/netmarkets/jsp/util/end.jspf"%>