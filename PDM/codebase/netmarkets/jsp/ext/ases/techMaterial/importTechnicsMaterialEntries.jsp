<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@taglib prefix="fmt" uri="http://www.ptc.com/windchill/taglib/fmt"%>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<fmt:setBundle basename="ext.ases.techMaterial.mvc.builder.TechnicsMaterialEntriesReourceRB" />
<fmt:message var="importTme" key="technicsMaterialEntries.importTechnicsMaterialEntries.description" />
<jca:wizard title="${importTme}" buttonList="NoStepsWizardButtons">
	<jca:wizardStep action="importTechnicsMaterialEntries_step" type="technicsMaterialEntries" label="${importTme}" />
</jca:wizard>
<%@include file="/netmarkets/jsp/util/end.jspf"%>