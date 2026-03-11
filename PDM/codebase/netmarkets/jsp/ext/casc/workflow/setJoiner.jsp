<%@page import="java.util.List"%>
<%@page import="ext.casc.workflow.CustomCmWfTaskProcessorCommands"%>
<%@ taglib prefix="util" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="SETJOINER_1" key="SETJOINER_1"/>
<%
List oids = commandBean.getSelectedOidForPopup();
 Boolean flag= CustomCmWfTaskProcessorCommands.compareWorkItem(oids);

 %>
 <%if(flag==false){ %>
<div  align="center"  ><font color=red size="40" style="font-family:宋体; font-size:20px"><%=CustomCmWfTaskProcessorCommands.tips %></font></div>
 <%}else{ %>
<util:wizard helpSelectorKey="team.addRoles" title="${SETJOINER_1}">
   <util:wizardStep action="setJoiner_step" type="projmgmt" />
</util:wizard>
<%} %>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
