<%@page import="ext.casc.workflow.CwbmDBController"%>
<%@page import="ext.casc.constants.Constants"%>
<%@page import="wt.httpgw.URLFactory"%>
<%@page import="ext.casc.process.ProcessConstants"%>
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
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%!private static final String RESOURCE = "ext.casc.workflow.workflowResource";%><%
//

java.util.ResourceBundle rb = java.util.ResourceBundle.getBundle(RESOURCE, localeBean.getLocale());
commandBean.setCompContext(nmcontext.getContext().toString());
commandBean.setRequest(request);
commandBean.getMap().put("UsersMultiSelect", "true");

GuiComponent guiComponentGroups = CmWfTaskProcessorCommands.getContainerTeamRoles(commandBean);
GuiComponent guiComponentUsers = CmWfTaskProcessorCommands.getUsers(commandBean);
GuiComponent guiComponentRolesAndParticipators = CmWfTaskProcessorCommands.getRolesAndParticipators(commandBean);
GuiComponent guiComponentJSActions = CmWfTaskProcessorCommands.getJSActions(commandBean);
RenderingContext cmRC = new RenderingContext();

guiComponentJSActions.draw(out, cmRC);
String baseUrl = urlFactoryBean.getFullyQualifiedHREF("");
String searchUserKey = request.getParameter("searchUserKey");
if (searchUserKey == null || "null".equals(searchUserKey))
   searchUserKey = "";

String flag=CwbmDBController.getWorkItemStatuByid(commandBean);

String message = new String("目前选人模板只支持IE浏览器，火狐浏览器不能使用的bug正在修复中");
message  = new String(message.getBytes("iso-8859-1"),"UTF-8");
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
function cwmb1(){
	var iWidth=400; //弹出窗口的宽度;
	var iHeight=300;//弹出窗口的高度;
	var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
	var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
	var dizhi=window.location.href;
	dizhi=dizhi.split("WorkItem%3A")[1].split("&u8=1")[0];
	window.open("<%=baseUrl%>netmarkets/jsp/ext/workflow/cwmb.jsp?dizhi="+dizhi+"","","height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft);
}
function xymb1(){

	if(navigator.userAgent.indexOf("Firefox")>0){
		//alert("<%=message%>");
		//return;
	}
	var dizhi=window.location.href;
	dizhi=dizhi.split("WorkItem%3A")[1].split("&u8=1")[0];
	var iWidth=400; //弹出窗口的宽度;
	var iHeight=300; //弹出窗口的高度;
	var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
	var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
	window.open("<%=baseUrl%>netmarkets/jsp/ext/workflow/xymb.jsp?dizhi="+dizhi+"","","height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft);
}


function scmb1(){
	var dizhi=window.location.href;
	dizhi=dizhi.split("WorkItem%3A")[1].split("&u8=1")[0];
	var iWidth=400; //弹出窗口的宽度;
	var iHeight=300; //弹出窗口的高度;
	var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
	var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
	window.open("<%=baseUrl%>netmarkets/jsp/ext/workflow/scmb.jsp?dizhi="+dizhi+"","","height="+iHeight+", width="+iWidth+", top="+iTop+", left="+iLeft);
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

<table width=80% border=0>
	<tr>
		<td width=40% align=right>
			<table border=0 width=100%>
			<tr>
			<td>
		<% 	if("COMPLETED".equals(flag)){%>
		<%}else{%>
			 <input id="xymb" type="button" value=<%=ProcessConstants.XZXYDMB %> onclick="xymb1()">
		<%} %>

			<input id="cwmb" type="button" value=<%=ProcessConstants.CUNWEIMUBAN %> onclick="cwmb1()">
			<input id="scmb" type="button" value=<%=ProcessConstants.SHANCHUMUBAN %> onclick="scmb1()">
			</td>
			</tr>
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
		<td width=60% align=left class=tabledatafont valign=top><br><br><br><br>
			<%guiComponentRolesAndParticipators.draw(out, cmRC); %>
		</td>
	</tr>
</table>
<%@ include file="/netmarkets/jsp/util/end.jspf"
%>