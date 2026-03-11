<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%response.setContentType("text/html; charset=UTF-8");%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@ page import="ext.casc.ui.actionsRB,
                 com.ptc.windchill.enterprise.util.PartManagementHelper,
                 com.ptc.netmarkets.util.beans.NmCommandBean "%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>

<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="ecr" key="<%= actionsRB.ANALYSISACTIVITY_ADDRELATEDCHANGEREQUEST_TITLE%>" />

<%
    NmCommandBean cb = new NmCommandBean();
    cb.setCompContext(nmcontext.getContext().toString());
    cb.setRequest(request);

    String containerReferenceString =  PartManagementHelper.getContainersToSearch(cb.getContainerRef());

    String tableHeader = (String) pageContext.findAttribute("ecr");
    String objType = "WCTYPE|wt.change2.WTChangeRequest2|casc.sast.149.PROCESS_ECR";

    request.setAttribute("objectType",objType);
	request.setAttribute("tableLabel",tableHeader);

%>

<head>
    <script>
        function setValue(objects) {
            var number = objects.pickedObject[0].number;
            window.opener.document.getElementById('ECRNUMBER').value = number;
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