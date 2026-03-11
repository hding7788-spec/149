<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" 
%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@taglib prefix="cwiz" uri="http://www.ptc.com/windchill/taglib/changeWizards"
%><%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments"
%><%@taglib prefix="fmt" uri="http://www.ptc.com/windchill/taglib/fmt"
%><%@ include file="/netmarkets/jsp/components/beginWizard.jspf"
%><%@ include file="/netmarkets/jsp/zchange/includeWizBean.jspf"%>
<%@ page import="com.glaway.mpm.change.ui.changeResource" %>
<%@include file="/netmarkets/jsp/zchange/propagationConfiguration.jspf"%>

<fmt:setBundle basename="com.glaway.mpm.change.ui.changeResource"/>
<fmt:message var="newTechnicNotice" key="<%=changeResource.NEWTECHNICNOTICE%>" />

<jca:initializeItem operation="${createBean.create}" baseTypeName="wt.change2.WTChangeIssue"/>

<%@include file="/netmarkets/jsp/annotation/wizardConfig.jspf" %>
<jca:wizard title="${newTechnicNotice}">
	<jca:wizardStep action="defineTCNAttributesWizStep" type="ztechnicChangeNotice"/>

	<jca:wizardStep action="affectedDataStepZ" type="ztechnicChangeNotice" />
	
</jca:wizard>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>