<%@page import="wt.inf.container.WTContained"%>
<%@page import="ext.casc.workflow.CustomCmWfTaskProcessorCommands"%>
<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean,
                 com.ptc.netmarkets.rule.NmRuleCommands,
                 com.ptc.netmarkets.model.NmObjectHelper,
                 com.ptc.netmarkets.util.misc.NmContext,
                 com.ptc.netmarkets.util.misc.NmContextItem,
                 com.ptc.netmarkets.util.table.NmHTMLTable,
                 com.ptc.netmarkets.model.NmOid,
                 com.ptc.netmarkets.folder.NmFolderHelper,
                 com.ptc.netmarkets.lifecycle.NmLifeCycleHelper,
                 com.ptc.netmarkets.work.NmWorkItemCommands,
                 com.ptc.netmarkets.workflow.workflowResource,
                 com.ptc.netmarkets.util.table.*,
                 com.ptc.netmarkets.util.misc.*,
                 wt.util.WTMessage,
                 java.util.ResourceBundle,
                 java.util.ArrayList,
                 java.util.Iterator,
                 java.util.Locale,
                 wt.fc.ObjectIdentifier,
                 java.util.HashMap"
%>
<jsp:directive.page import="ext.casc.workflow.CmWfTaskProcessorCommands"/>
<jsp:directive.page import="com.ptc.core.components.rendering.RenderingContext"/>
<jsp:directive.page import="com.ptc.core.components.rendering.GuiComponent"/>
<%@ page import="wt.workflow.work.WorkItem" %>
<%@ page import="wt.workflow.engine.WfActivity" %>
<%-- <%@ include file="/netmarkets/jsp/util/begin.jspf"%> --%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%!private static final String RESOURCE = "ext.casc.workflow.workflowResource";%><%
//
java.util.ResourceBundle rb = java.util.ResourceBundle.getBundle(RESOURCE, localeBean.getLocale());
commandBean.setCompContext(nmcontext.getContext().toString());
commandBean.setRequest(request);
commandBean.getMap().put("UsersMultiSelect", "true");

List oids = commandBean.getSelectedOidForPopup();
 WTContained container= CustomCmWfTaskProcessorCommands.getContainer(oids);
 /* Boolean flag= CustomCmWfTaskProcessorCommands.compareWorkItem(oids);
 out.println("------>>>>"+flag); */
	String workItemName = "";
	if(oids.size() > 0) {
		Object value = (Object) oids.get(0);
		NmOid oid1 = (NmOid) value;
		WorkItem workItem = (WorkItem) oid1.getRefObject();
        if(workItem != null) {
            WfActivity activity = (WfActivity) workItem.getSource().getObject();
			workItemName = activity.getName();
        }
	}
 GuiComponent guiComponentGroups = CustomCmWfTaskProcessorCommands.getContainerTeamRoles(workItemName,container);
GuiComponent guiComponentUsers = CustomCmWfTaskProcessorCommands.getUsers(container,commandBean);
GuiComponent guiComponentRolesAndParticipators = CustomCmWfTaskProcessorCommands.getRolesAndParticipators(oids,commandBean);
GuiComponent guiComponentJSActions = CustomCmWfTaskProcessorCommands.getJSActions(commandBean);
RenderingContext cmRC = new RenderingContext();

guiComponentJSActions.draw(out, cmRC);
String baseUrl = urlFactoryBean.getFullyQualifiedHREF("");
String searchUserKey = request.getParameter("searchUserKey");
if (searchUserKey == null || "null".equals(searchUserKey))
   searchUserKey = "";
%>
<script type="text/javascript">

function searchUsers() {
	new Ajax.Request(
		"<%=baseUrl%>netmarkets/jsp/ext/workflow/searchUsers.jsp",
		{
			method: "post",
			postBody: "key=" + encodeURIComponent($('searchUserKey').value),
			onComplete: merageGroups
		}
	);
}

function merageGroups(transport, result) {
	var userFound = "<%=rb.getString("workflow.participants.usersFound") %>";
	var options = $("groups").options;

	if (options.length > 0 && (options[0].text == userFound)) {
		options[0].parentNode.removeChild(options[0]);
	}

	if (result.isSuccess == true) {
		var opt = document.createElement("option");
		opt.text = userFound;
		opt.value = decodeURIComponent(result.result);
		options.add(opt, 0);
		options.selectedIndex = 0;

		listGroupUsers();
	}
}


</script>
<table width=60% border=0>
	<tr>
		<td width=30% align=right>
			<table border=0 width=100%>
				<tr  >
					<td colspan=2 class=tabledatafont  nowrap><br><%=rb.getString("workflow.participants.searchUsers") %>
						<input type=text name='searchUserKey' id="searchUserKey" value="<%=searchUserKey %>" size=25>
						<input type="button" onclick="searchUsers()" value='&nbsp;<%=rb.getString("workflow.participants.btnSearch") %>&nbsp;'>
					</td>
				</tr>
				<tr  >
					<td class=tabledatafont><%=rb.getString("workflow.participants.groupsCombox") %><br>
						<%guiComponentGroups.draw(out, cmRC); %>
					</td>
					<td class=tabledatafont><%=rb.getString("workflow.participants.usersCombox") %><br>
						<%guiComponentUsers.draw(out, cmRC); %>
					</td>
				</tr>
			</table>
		</td>
		<td width=70% align=left class=tabledatafont valign=top><br><br><br><br>
			<%guiComponentRolesAndParticipators.draw(out, cmRC); %>
		</td>
	</tr>
</table>

<%@ include file="/netmarkets/jsp/util/end.jspf"
%>