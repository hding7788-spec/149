<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@taglib prefix="fmt" uri="http://www.ptc.com/windchill/taglib/fmt"%>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="importPhotoTemplate" key="custom.importPhotoTemplate.description" />
<jca:wizard title="${importPhotoTemplate}" buttonList="NoStepsWizardButtons">
	<jca:wizardStep action="importPhotoTemplate_step" type="custom" label="${importPhotoTemplate}" />
</jca:wizard>
<%@include file="/netmarkets/jsp/util/end.jspf"%>