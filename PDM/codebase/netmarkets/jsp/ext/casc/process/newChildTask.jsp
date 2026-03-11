<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page import="com.glaway.mpm.mpmresource.ui.MPMResourceRB"%>
<fmt:setBundle basename="com.glaway.mpm.mpmresource.ui.MPMResourceRB" />


<jca:wizard title="${wizardTitle}" buttonList="DefaultWizardButtons">
	<jca:wizardStep action="newChildProcessPlanStep"
		type="customProcessPlan" />
</jca:wizard>



<%@ include file="/netmarkets/jsp/util/end.jspf"%>