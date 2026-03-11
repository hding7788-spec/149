<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@taglib prefix="fmt" uri="http://www.ptc.com/windchill/taglib/fmt"%>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<fmt:setBundle basename="com.ptc.core.ui.navigationRB" />
<fmt:message var="historyInsideTechFileImport" key="itf.historyInsideTechFileImport.description" />
<jca:wizard title="${historyInsideTechFileImport}" buttonList="NoStepsWizardButtons">
	<jca:wizardStep action="historyInsideTechFileImport_step" type="itf" label="${historyInsideTechFileImport}" />
</jca:wizard>
<%@include file="/netmarkets/jsp/util/end.jspf"%>