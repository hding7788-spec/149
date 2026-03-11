<%@ taglib prefix="util" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page errorPage="/netmarkets/jsp/util/error.jsp"
%><%@ page import="ext.casc.process.resource.processRB,java.util.ResourceBundle"
%>
<jsp:useBean id="localeBean2" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>

<%!
  private static final String PART_RESOURCE = "ext.casc.process.resource.processRB";
%>
<%
String flag = (String)session.getAttribute("flag");
if("1".equals(flag)) {
	//session.setAttribute("flag", "0");
	session.removeAttribute("tempDeleteList");
	session.removeAttribute("addObject");
}

  ResourceBundle rb = ResourceBundle.getBundle(PART_RESOURCE, localeBean2.getLocale());

  String pageTitle = rb.getString(processRB.PBOMPROASSIGNTASK_TITLE);

  request.setAttribute("pageTitle", pageTitle );

%>

<c:set var="pageTitle" value="${pageTitle}"/>

<util:wizard buttonList="DefaultWizardButtonsNoApply" title="${pageTitle}" >
 <util:wizardStep action="PBOMProAssignTaskNew_step" type="customProcessTask"/>
</util:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>