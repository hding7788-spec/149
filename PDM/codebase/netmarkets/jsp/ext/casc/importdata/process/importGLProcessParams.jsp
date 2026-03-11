<%@ taglib prefix="util" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>
<%@ page errorPage="/netmarkets/jsp/util/error.jsp" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="ext.ases.techMaterial.mvc.builder.TechnicsMaterialEntriesReourceRB" %>
<jsp:useBean id="localeBean2" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>

<%!
    private static final String TechnicsMaterialReourceRB = "ext.ases.techMaterial.mvc.builder.TechnicsMaterialEntriesReourceRB";
%>
<%
    ResourceBundle rb = ResourceBundle.getBundle(TechnicsMaterialReourceRB, localeBean2.getLocale());
    String pageTitle = rb.getString(TechnicsMaterialEntriesReourceRB.TECHNICSMATERIALENTRIES_IMPORTPROCESSPARAMS_1);
    request.setAttribute("pageTitle", pageTitle);
%>


<c:set var="pageTitle" value="${pageTitle}"/>

<util:wizard buttonList="DefaultWizardButtonsNoApply" title="${pageTitle}">
    <util:wizardStep action="importGLProcessParamsStep" type="technicsMaterialEntries"/>
</util:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>
