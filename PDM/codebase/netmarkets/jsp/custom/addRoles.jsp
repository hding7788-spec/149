<%@ taglib prefix="util" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="CUSTOM_ADDROLES" key="CUSTOM_ADDROLES"/>

<util:wizard helpSelectorKey="team.addRoles" title="${CUSTOM_ADDROLES}">
   <util:wizardStep action="addRoles_step" type="custom" />
</util:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
