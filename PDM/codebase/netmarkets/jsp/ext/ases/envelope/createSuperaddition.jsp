<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>
<%@ page import="ext.ases.antecedency.AntecedencyUtil,
wt.fc.WTObject,
wt.lifecycle.State,
wt.fc.ReferenceFactory
"%>
<%
	String contextPath = request.getContextPath();
	String oid = request.getParameter("oid");
	ReferenceFactory rf = new ReferenceFactory();
	WTObject obj = (WTObject)rf.getReference(oid).getObject();
	AntecedencyUtil.setObjLifeCycleState(obj, State.toState("INWORK"));
	String urlInfo = "/Windchill/netmarkets/jsp/work/listAssignments.jsp"; 
%>
<script>
	window.opener.location.reload(); 
	window.close();
</script> 
<%@ include file="/netmarkets/jsp/util/end.jspf"%>