<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="util" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>
<%@ page errorPage="/netmarkets/jsp/util/error.jsp"
%>
<jsp:useBean id="localeBean2" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>

<%!
    private static final String PART_RESOURCE = "ext.ases.techMaterial.mvc.builder.TechnicsMaterialEntriesReourceRB";
%>
<%

    request.setAttribute("pageTitle", "设置参数");
%>


<script type="text/javascript">

</script>

<%

%>
<c:set var="pageTitle" value="${pageTitle}"/>
<util:wizard buttonList="DefaultWizardButtonsNoApply" title="${pageTitle}">
    <util:wizardStep action="batchSetProcessParamsStep" type="technicsMaterialEntries"/>
</util:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>