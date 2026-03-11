<%@page import="ext.ases.workflow.TaskConfigrationHelper"%>
<%
	String sign_path = request.getParameter("sign_path");
	sign_path = java.net.URLDecoder.decode(sign_path,"UTF-8");
	String oid = request.getParameter("oid");
	TaskConfigrationHelper.setProcessVariableByVarName(oid,"reviewPath",sign_path);
	String pathValue = (String)TaskConfigrationHelper.getProcessVariableValue(oid,"reviewPath");
	System.out.println("pathValue is "+pathValue);
%>