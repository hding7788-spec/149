<%
	if(ext.casc.workflow.WorkflowHelper.isPackagedPart(request.getParameter("oid"))){
	    //System.out.println("---------showTreeSignature.jsp");
%>
	<jsp:include page="/netmarkets/jsp/ext/workflow/tree/showTreeSignature.jsp"/>
<%
	}else{
	    //System.out.println("---------showListSignature.jsp");
%>
	<jsp:include page="/netmarkets/jsp/ext/workflow/tree/showListSignature.jsp"/>
<%	
	}
%>