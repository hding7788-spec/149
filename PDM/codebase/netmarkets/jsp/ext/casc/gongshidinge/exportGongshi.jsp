<%@ taglib prefix="util" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>
<%@ page errorPage="/netmarkets/jsp/util/error.jsp" %>
<%@page pageEncoding="GBK" %>

<jsp:useBean id="localeBean2" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>


<util:wizard buttonList="DefaultWizardButtonsNoApply" title="工时定额差异导出">
    <util:wizardStep action="exportGongshiStep" type="custom"/>
</util:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>
