<%@page import="ext.ases.workflow.TaskConfigrationHelper"%>
<%
	String index = request.getParameter("index");
	String oid = request.getParameter("oid");
	
	String selIndex = (String)TaskConfigrationHelper.getProcessVariableValue(oid,"selectPathIndex");
	String reSetIndex = "";
	if(selIndex != null && ! selIndex.equals("") ){
		String indexArray[] = selIndex.split(",");
		for(int i = 0 ; i < indexArray.length ; i++){
			String temp = indexArray[i];
			if(!index.equals(temp)){
				reSetIndex = reSetIndex + temp + ",";
			}
		}
	}
	TaskConfigrationHelper.setProcessVariableByVarName(oid,"selectPathIndex",reSetIndex);
	System.out.println("reSetIndex is "+reSetIndex);
%>