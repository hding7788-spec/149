<%@ taglib uri="http://java.sun.com/jsp/jstl/core"            prefix="c"
%><%@ taglib uri="http://www.ptc.com/windchill/taglib/workItem" prefix="workItem"
%><%@ taglib tagdir="/WEB-INF/tags"                             prefix="tags" 
%>

<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean"  scope="request"/>
<jsp:useBean id="localeBean" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>
<%!
	private static final String RESOURCE = "ext.casc.workflow.workflowResource";  
%>
<%
	java.util.ResourceBundle rb = java.util.ResourceBundle.getBundle(RESOURCE, localeBean.getLocale());	
 %>
      
<table>
  	<tr>
	   <td valign="top" nowrap="true">
	      <b class=tabledatafont>
    	    <div style="MARGIN-TOP: 10px;"><%= rb.getString("workflow.participants.setup") %></div>
          </b> 
    	</td>
    </tr>
    <tr>
      <td>
      	<jsp:include page="/netmarkets/jsp/ext/workflow/setupParticipants.jsp" flush="true"/>
      </td>
    </tr>
</table>