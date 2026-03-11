<%@page import="ext.ases.workflow.TaskConfigrationHelper"%>
<%
	String index = request.getParameter("index");
	String oid = request.getParameter("oid");
	
	String selIndex = (String)TaskConfigrationHelper.getProcessVariableValue(oid,"selectPathIndex");
	if(selIndex != null && ! selIndex.equals("") ){
		selIndex = selIndex+","+index;
	}else{
		selIndex = index;
	}
	TaskConfigrationHelper.setProcessVariableByVarName(oid,"selectPathIndex",selIndex);
	System.out.println("selIndex is "+selIndex);
%>