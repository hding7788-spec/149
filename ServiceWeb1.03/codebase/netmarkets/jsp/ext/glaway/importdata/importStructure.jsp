<%@ taglib prefix="util" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page errorPage="/netmarkets/jsp/util/error.jsp"
%><%@ page import="com.glaway.mpm.importdata.productRB,java.util.ResourceBundle"
%>
<jsp:useBean id="localeBean2" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>

<%!
  private static final String PART_RESOURCE = "com.glaway.mpm.importdata.productRB";
%><%
  ResourceBundle rb = ResourceBundle.getBundle(PART_RESOURCE, localeBean2.getLocale());

  String pageTitle = rb.getString(productRB.import_title);

  request.setAttribute("pageTitle", pageTitle );

%>



<c:set var="pageTitle" value="${pageTitle}"/>

<util:wizard buttonList="DefaultWizardButtonsNoApply" title="${pageTitle}" >
 <util:wizardStep action="importStructureStep" type="pbom"/>
</util:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
