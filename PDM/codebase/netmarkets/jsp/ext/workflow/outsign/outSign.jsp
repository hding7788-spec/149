<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<fmt:setBundle basename="ext.casc.ui.actionsRB"/>
<fmt:message var="outSign" key="custom.outSign.description" />
<jca:wizard buttonList="NoStepsWizardButtons" title="${outSign}">
   <jca:wizardStep action="outSignStep" type="custom" />
</jca:wizard>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>