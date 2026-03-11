<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%
	String oid = request.getParameter("oid").trim();
%>
<jsp:include page="${mvc:getComponentURL('ext.casc.workflow.tree.mvc.builder.SetOutSignTableBuilder')}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>