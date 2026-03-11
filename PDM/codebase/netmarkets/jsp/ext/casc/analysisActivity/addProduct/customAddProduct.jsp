<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt"        prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"              prefix="c"%>
<%-- Set the default part management help --%>
<c:set var="buttonList" value="DefaultWizardButtonsNoApply" scope="page" />
<jca:wizard buttonList="${buttonList}" title="${wizardTitle}">
	<jca:wizardStep action="customAddProductStep" type="analysisActivity" />
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>