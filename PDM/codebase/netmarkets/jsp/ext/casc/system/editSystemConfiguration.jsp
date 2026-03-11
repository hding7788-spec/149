<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@taglib prefix="fmt" uri="http://www.ptc.com/windchill/taglib/fmt"%>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="editSystemConfiguration" key="custom.editSystemConfiguration.description" />
<jca:wizard title="${editSystemConfiguration}" buttonList="NoStepsWizardButtons">
   <jca:wizardStep action="editSystemConfiguration_step" type="custom" label="${editSystemConfiguration}" />
</jca:wizard>
<%@include file="/netmarkets/jsp/util/end.jspf"%>