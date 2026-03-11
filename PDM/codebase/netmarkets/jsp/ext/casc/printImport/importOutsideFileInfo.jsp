<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@taglib prefix="fmt" uri="http://www.ptc.com/windchill/taglib/fmt"%>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<fmt:setBundle basename="com.ptc.core.ui.navigationRB" />
<fmt:message var="historyOutsideFileImport" key="itf.historyOutsideFileImport.description" />
<jca:wizard title="${historyOutsideFileImport}" buttonList="NoStepsWizardButtons">
	<jca:wizardStep action="historyOutsideFileImport_step" type="itf" label="${historyOutsideFileImport}" />
</jca:wizard>
<%@include file="/netmarkets/jsp/util/end.jspf"%>