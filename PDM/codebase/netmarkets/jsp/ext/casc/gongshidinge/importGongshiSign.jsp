<%@ taglib prefix="util" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>
<%@ page errorPage="/netmarkets/jsp/util/error.jsp" %>
<%@page pageEncoding="GBK" %>

<jsp:useBean id="localeBean2" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>


<util:wizard buttonList="DefaultWizardButtonsNoApply" title="手动导入工时标识">
    <util:wizardStep action="importGongshiSignStep" type="custom"/>
</util:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>
