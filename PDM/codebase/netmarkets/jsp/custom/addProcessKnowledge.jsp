<%@ taglib prefix="util" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@page language="java" pageEncoding="UTF-8"
        contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="CUSTOM_ADDBATCH" key="CUSTOM_ADDBATCH"/>

<util:wizard helpSelectorKey="team.addBatch" title="创建工艺知识">
   <util:wizardStep action="addProcessKnowledge_step" type="custom" />
</util:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
