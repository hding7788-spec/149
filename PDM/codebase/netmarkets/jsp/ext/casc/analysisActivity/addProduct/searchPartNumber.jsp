<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%response.setContentType("text/html; charset=UTF-8");%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@ page import="ext.casc.ui.actionsRB,
                 com.ptc.windchill.enterprise.util.PartManagementHelper,
                 com.ptc.netmarkets.util.beans.NmCommandBean "%>
<%@ page import="wt.part.WTPart" %>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>

<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="addProduct" key="<%= actionsRB.ANALYSISACTIVITY_ADDRELATEDPRODUCT_TITLE%>" />

<%
    NmCommandBean cb = new NmCommandBean();
    cb.setCompContext(nmcontext.getContext().toString());
    cb.setRequest(request);

    String containerReferenceString =  PartManagementHelper.getContainersToSearch(cb.getContainerRef());

    String tableHeader = (String) pageContext.findAttribute("addProduct");
    String objType = WTPart.class.getName();

    request.setAttribute("objectType",objType);
	request.setAttribute("tableLabel",tableHeader);

%>

<head>
    <script>
        function setValue(objects) {
            var number = objects.pickedObject[0].number;
            var oid = objects.pickedObject[0].oid;
            window.opener.document.getElementById('number').value = number;
            window.opener.document.getElementById('number_value').value = oid;
        }
    </script>
</head>

<wctags:genericPicker id="setEcrNumber" inline="true" pickerCallback="setValue"
                      pickerTitle="${tableLabel}" multiSelect="false"
                      componentId="RelatedObjectAddAssociation"
                      containerRef="<%=containerReferenceString%>"
                      defaultVersionValue="LATEST"
                      baseWhereClause="(latestIteration='1')"
                      objectType="${objectType}" pickedAttributes="number" />
<%@ include file="/netmarkets/jsp/util/end.jspf"%>