<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%@ page import="com.glaway.mpm.processplan.ui.ProcessplanResource"%>
<fmt:setBundle basename="com.glaway.mpm.processplan.ui.ProcessplanResource"/>
<fmt:message var="wizardTitle" key="Add_User" />

<jca:wizard title="${wizardTitle}"  buttonList="DefaultWizardButtons">
	<jca:wizardStep action="addUserWizardStep" type="processplan" />
</jca:wizard>



<%@ include file="/netmarkets/jsp/util/end.jspf"%>
