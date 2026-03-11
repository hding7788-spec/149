<%@ page import="ext.casc.workflow.TaskConfigrationHelper"%>
<%
	//Locale locale = WTContext.getContext().getLocale();
	String SINGATURE_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","SINGATURE",null,locale);
	//boolean isDispalyReviewPath = (Boolean)TaskConfigrationHelper.getProcessVariableValue(oid,"isDispalyReviewPath");
	if(isDispalyReviewPath){
		String reviewPath = (String)TaskConfigrationHelper.getProcessVariableValue(oid,"reviewPath");
		if(reviewPath != null){
%>

			<td align="right"><font size="3"><B><%=SINGATURE_MESSAGE%>:</B></font></td>
			<td>
			<table><tr>	
		<%
				String pathArray[] = reviewPath.split(",");
				for(int i = 0 ; i < pathArray.length ; i++){
					String path = pathArray[i];
		%>
					<td><font size="2"><%=path%></font></td>
		<%
				}
		%>
			</tr></table></td>

<%
		}
	}
%>

