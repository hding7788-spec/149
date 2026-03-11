<%
    if(ext.casc.workflow.WorkflowHelper.isPackagedPart(request.getParameter("oid"))) {
%>
	<jsp:include page="/netmarkets/jsp/ext/workflow/tree/setTreeSignature2.jsp"/>
<%
} else {
%>
	<jsp:include page="/netmarkets/jsp/ext/workflow/tree/setListSignature2.jsp"/>
<%
    }
%>