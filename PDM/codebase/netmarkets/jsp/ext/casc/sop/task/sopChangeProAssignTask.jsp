<%@ taglib prefix="util" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page errorPage="/netmarkets/jsp/util/error.jsp" %>
<%@ page import="ext.casc.sop.ui.SopActionsRB,java.util.ResourceBundle"%>

<%!
  private static final String RESOURCE = "ext.casc.sop.ui.SopActionsRB";
%>
<%
  ResourceBundle rb = ResourceBundle.getBundle(RESOURCE, localeBean.getLocale());
  String pageTitle = rb.getString(SopActionsRB.SOPCHANGEPROASSIGNTASK_0);
  request.setAttribute("pageTitle", pageTitle );

%>

<c:set var="pageTitle" value="${pageTitle}"/>

<util:wizard buttonList="DefaultWizardButtonsNoApply" title="${pageTitle}" >
 <util:wizardStep action="sopChangeProAssignTask_step" type="sopCustom"/>
</util:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>