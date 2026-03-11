<%@page import="java.util.List"%>
<%@page import="ext.casc.workflow.CustomCmWfTaskProcessorCommands"%>
<%@ taglib prefix="util" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="SETFINISH_1" key="SETFINISH_1"/>
<%
List oids = commandBean.getSelectedOidForPopup();
 String result = CustomCmWfTaskProcessorCommands.compareFinishConditions(oids);
 %>
 <%if(!"".equals(result)){ %>
<div  align="center"  ><font color=red size="40" style="font-family:宋体; font-size:20px"><%=result %></font></div>
 <%}else{
	CustomCmWfTaskProcessorCommands.finishWorkitem(oids);
	 out.print("<script type='text/javascript'>close()</script>");
} %>
<script type="text/javascript">
	function close(){
		window.close();
	}
</script>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
