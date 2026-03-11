<%@page import="ext.ases.workflow.TaskConfigrationHelper"%>
<%
	String addOrRemove = request.getParameter("addOrRemove");
	String oid = request.getParameter("oid");
	
	TaskConfigrationHelper.setProcessVariableByVarName(oid,"huiqian149",Boolean.parseBoolean(addOrRemove));
	System.out.println("==========================~~~~~~~~~~~~~~~~~~~~ " + addOrRemove);
%>