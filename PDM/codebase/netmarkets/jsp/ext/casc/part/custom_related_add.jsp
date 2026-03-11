<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@ page import="com.ptc.windchill.enterprise.util.PartManagementHelper"%>
<%@ page import="com.ptc.windchill.enterprise.part.partResource" %>
<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.ptc.windchill.enterprise.part.partResource" />
<fmt:message var="describedByDocTableHeader" key="<%= partResource.DESCRIBED_BY_DOC_TABLE_HEADER%>" />

<%
    NmCommandBean cb = new NmCommandBean();
    cb.setCompContext(nmcontext.getContext().toString());
    cb.setRequest(request);

	String containerReferenceString =  PartManagementHelper.getContainersToSearch(cb.getContainerRef());
	boolean isPDMMethod = PartManagementHelper.getWcPDMMethodPref();
	String excludedTypes = "";
	String tableLabel = (String) pageContext.findAttribute("describedByDocTableHeader");

	if (isPDMMethod) {
		excludedTypes = "";
	}
	else {
		excludedTypes = "com.ptc.ReferenceDocument";
	}
	request.setAttribute("tableLabel",tableLabel);
	request.setAttribute("excludedTypes",excludedTypes);
%>

<wctags:genericPicker id="custom_related_add_described" multiSelect="true" inline="true"
                   pickerCallback="doNothing" pickerTitle="${tableLabel}"
                   objectType="wt.doc.WTDocument"
                   excludeSubTypes="${excludedTypes}"
                   componentId="RelatedObjectAddAssociation"
                   containerRef="<%=containerReferenceString%>"
                   baseWhereClause="(template.templated='false')" />
<%@ include file="/netmarkets/jsp/util/end.jspf"%>