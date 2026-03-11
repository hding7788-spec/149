<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="util"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page errorPage="/netmarkets/jsp/util/error.jsp"%>
<%@ page import="ext.casc.ui.actionsRB"%>
<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="REFRESHBATCHES_LABEL" key="<%=actionsRB.REFRESHBATCHES_2%>" />
<util:wizard buttonList="DefaultWizardButtonsNoApply" title="${REFRESHBATCHES_LABEL}">
	<util:wizardStep action="refreshBatches_step" type="custom"/>
	<util:wizardStep action="refreshBatches_step2" type="custom"/>
</util:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>