<%@ taglib prefix="util" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page errorPage="/netmarkets/jsp/util/error.jsp"
%><%@ page import="ext.casc.sop.constants.SopConstants"
%>
><%
  String pageTitle = SopConstants.SOP_MSG_SOPRESOURCEIMPORT;
  request.setAttribute("pageTitle", pageTitle );
%>

<c:set var="pageTitle" value="${pageTitle}"/>
<util:wizard buttonList="DefaultWizardButtonsNoApply" title="${pageTitle}" >
    <util:wizardStep action="sopMPMResourceImportStep" type="sopCustom"/>
</util:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
