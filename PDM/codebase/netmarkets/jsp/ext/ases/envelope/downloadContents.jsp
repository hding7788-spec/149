<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<jca:wizard buttonList="WizardButtonClose">
	<jca:wizardStep action="downloadContents_Step" type="envelope" label="Wizard Label"/>   
</jca:wizard>

<%@include file="/netmarkets/jsp/util/end.jspf"%>