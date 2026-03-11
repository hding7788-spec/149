<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="util" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>
<%@ page errorPage="/netmarkets/jsp/util/error.jsp"
%>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="ext.casc.ui.actionsRB" %>
<jsp:useBean id="localeBean2" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>


<%!
    private static final String RESOURCE = "ext.casc.ui.actionsRB";
%>
<%


    ResourceBundle rb = ResourceBundle.getBundle(RESOURCE, localeBean2.getLocale());

    String pageTitle = rb.getString(actionsRB.CUSTOM_GONGSHIDINGEMANAGE_TITLE);

    request.setAttribute("pageTitle", pageTitle);

%>

<c:set var="pageTitle" value="${pageTitle}"/>

<util:wizard buttonList="WizardButtonClose" title="${pageTitle}">
    <util:wizardStep action="gongshiDingeManage_step" type="custom"/>
</util:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>