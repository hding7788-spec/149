<%@ taglib prefix="util" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>
<%@ page errorPage="/netmarkets/jsp/util/error.jsp" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="ext.ases.techMaterial.mvc.builder.TechnicsMaterialEntriesReourceRB" %>
<jsp:useBean id="localeBean2" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>

<%@page language="java" pageEncoding="UTF-8"
        contentType="text/html; charset=UTF-8"%>



<c:set var="pageTitle" value="导入工艺知识数据"/>

<util:wizard buttonList="DefaultWizardButtonsNoApply" title="${pageTitle}">
    <util:wizardStep action="importProcessKnowledgeDataStep" type="custom"/>
</util:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>
