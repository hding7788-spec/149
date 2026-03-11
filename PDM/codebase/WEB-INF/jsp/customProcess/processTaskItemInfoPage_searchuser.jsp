<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ page import="wt.util.WTProperties"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />	
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<%@page import="ext.casc.process.resource.processRB,ext.casc.process.ProcessConstants"%>
<%@page import="wt.fc.ReferenceFactory,wt.fc.Persistable,ext.casc.process.ProcessTaskItem"%>
<%@ page import="java.util.HashMap,
                 com.ptc.netmarkets.util.misc.NmAction,
                 com.ptc.netmarkets.util.misc.NmActionServiceHelper,
                 com.ptc.netmarkets.work.NmWorkItem ,
				 com.ptc.netmarkets.work.NmWorkItemCommands,
				 com.ptc.netmarkets.model.NmNamedObject,
				 wt.workflow.worklist.worklistResource,
				 wt.util.WTMessage,
				 java.util.Locale,
				 java.util.ResourceBundle,
				 com.ptc.netmarkets.util.misc.NmHTMLActionModel,
				 com.ptc.netmarkets.util.misc.NmContext,wt.session.SessionHelper,
				 java.util.Map,
				 ext.casc.process.util.ProcessUtil,
				 java.util.List,
				 wt.org.WTUser,
				 java.util.Iterator,
				 java.util.Set,
				 java.util.TreeSet"
%>


<%

	String oid = request.getParameter("oid");
	ProcessTaskItem taskItem = null;	
	String state = "";
	
	ReferenceFactory referencefactory = new ReferenceFactory();
	Persistable persistable = referencefactory.getReference(oid).getObject();
	if(persistable instanceof ProcessTaskItem){
	    taskItem = (ProcessTaskItem)persistable;
	}
	String startDate = taskItem.getCreateTimestamp().toLocaleString();
	String endDate = taskItem.getEndDate().toLocaleString();
	String iszhuzhi = ProcessConstants.TASK_ISZHUZHI_FOU;
	boolean flag = taskItem.getIszhuzhi();
	if(flag){
	    iszhuzhi = ProcessConstants.TASK_ISZHUZHI_SHI;
	}
	String taskstate = taskItem.getTaskItemState();
	String executorRole = taskItem.getExecutorRole();
	Map<String, List<WTUser>> map = ProcessUtil.getGroupAndUsersInOrgContainer();
	Map<String, List<WTUser>> usersmap = ProcessUtil.getCheJianGroupAndUsers();
	ArrayList refList=new ArrayList();
	ArrayList nameList=new ArrayList();
%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.casc.process.resource.processRB" />
<fmt:message var="taskname" key="<%=processRB.PROCESSTASKITEM_TASKNAME%>" />
<fmt:message var="name" key="<%=processRB.PROCESSTASKITEM_NAME%>" />
<fmt:message var="number" key="<%=processRB.PROCESSTASKITEM_NUMBER%>" />
<fmt:message var="tasktype" key="<%=processRB.PROCESSTASKITEM_TASKTYPE%>" />
<fmt:message var="taskstate" key="<%=processRB.PROCESSTASKITEM_TASKSTATE%>" />
<fmt:message var="startdate" key="<%=processRB.PROCESSTASKITEM_STARTDATE%>" />
<fmt:message var="enddate" key="<%=processRB.PROCESSTASKITEM_ENDDATE%>" />
<fmt:message var="iszhuzhi" key="<%=processRB.PROCESSTASKITEM_ISZHUZHI%>" />
<fmt:message var="executorrole" key="<%=processRB.PROCESSTASKITEM_EXECUTORROLE%>" />
<fmt:message var="renwuyaoqiu" key="<%=processRB.PROCESSTASKITEM_RENWUYAOQIU%>" />
<fmt:message var="beizhu" key="<%=processRB.PROCESSTASKITEM_BEIZHU%>" />

	<c:set var="iVals" value="<%=refList%>"/>
	<c:set var="dVals" value="<%=nameList%>"/>

<table cellSpacing="7" >
	<tr>
		<td><h4>${taskname}</h4></td>
		<td><%=taskItem.getTaskItemName() %></td>
	</tr>
	<tr>
		<td><h4>${name}</h4></td>
		<td><%=taskItem.getName() %></td>
	</tr>
	<tr>
		<td><h4>${number}</h4></td>
		<td><%=taskItem.getNumber() %></td>
	</tr>
	<tr>
		<td><h4>${tasktype}</h4></td>
		<td><%=taskItem.getTaskType() %></td>
	</tr>
	<tr>
		<td><h4>${taskstate}</h4></td>
		<td><%=taskItem.getTaskItemState() %></td>
	</tr>
	<tr>
		<td><h4>${startdate}</h4></td>
		<td><%=startDate %></td>
	</tr>
	<tr>
		<td><h4>${enddate}</h4></td>
		<td><%=endDate %></td>
	</tr>
	<tr>
		<td><h4>${iszhuzhi}</h4></td>
		<td><%=iszhuzhi %></td>
	</tr>
	<tr>
		<td><h4>${executorrole}</h4></td>
		<td><%=taskItem.getExecutorRole() %></td>
	</tr>
	<tr>
		<td><h4>${renwuyaoqiu}</h4></td>
		<td><%=taskItem.getRenwuyaoqiu() %></td>
	</tr>
	<tr>
		<td><h4>${beizhu}</h4></td>
		<%if(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)){ %>
		<td><textarea name="beizhi" cols ="40" rows = "5"></textarea></td>
		<%}else{ %>
		<td><%=taskItem.getDescription() %></td>
		<%} %>
	</tr>
	<%if(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)){ %>
	<%if(executorRole.equals(ProcessConstants.ROLE_GONGYIZUZHANG)){ 
		String chejian = taskItem.getChejian();
		List<WTUser> allUsers = usersmap.get(chejian);
	%>
	<tr>
		<td><h4><%=ProcessConstants.TASKITEM_JSP_ZHIPAIGONGYIYUAN %>:</h4></td>
		<td>
			<w:comboBox size="1" id="assignTo" name="assignTo" internalValues="${iVals}"  displayValues="${dVals}" required="true"/>
			<%
		 	NmHTMLActionModel am = NmActionServiceHelper.service.getActionModel("reassign search action", taskItem);
			List actions = am.getActions();
			NmAction reassignSearchUserAction = null;
			for (Iterator i = actions.iterator(); i.hasNext();) {
				NmAction next = (NmAction)i.next();
				if ( next.getAction().equals("reassignSearchUser") ) {
					reassignSearchUserAction = next;
					break;
				}
			}
			reassignSearchUserAction.setButton(true);           
			reassignSearchUserAction.setEnabled(true);
			reassignSearchUserAction.setMultiselect(true);
			objectBean.setObject(reassignSearchUserAction);
			%>
			<jsp:include page="/netmarkets/jsp/util/object.jsp" flush="true"/>
						<%
						objectBean.setObject(null);  
				  %> 
		</td>
	</tr>
	<%}%>
	
	<% if(executorRole.equals(ProcessConstants.ROLE_ZHURENGONGYISHI)){
	      Set<String> set = new TreeSet<String>();
	      set.addAll(map.keySet());
	 %>
	 <tr>
	 	<td><h4><%=ProcessConstants.TASKITEM_JSP_XUANZHEZHUZHICHEJIAN %></h4></td>
	 	<td>
	 		<select id="zhuzhichejian" name="zhuzhichejian">
	 		<option value="kongzhi"></option>
	 <%
	      for (String chejian : set) {
	%>
			<option value="<%=chejian %>"><%=chejian %></option>
	<%           
	      }
	 %>
	 		</select>
		</td>
	</tr>
	 <%
	  }
	%>			

	<% if(executorRole.equals(ProcessConstants.ROLE_ZHURENGONGYISHI)){
	      Set<String> set = new TreeSet<String>();
	      set.addAll(map.keySet());
	 %>
	 <tr>
	 	<td><h4><%=ProcessConstants.TASKITEM_JSP_XUANZHEFUZHICHEJIAN %></h4></td>
	 	<td>
	 <%
	      for (String chejian : set) {
	%>
			<input type="checkbox" id="fuzhichejian" name="fuzhichejian" value="<%=chejian %>"><%=chejian %></input>
	<%           
	   	  }
	 %>
		</td>
	</tr>
	 <%
	  }
	}else {//任务已经完成，则在打开时显示填写的内容
	    if(executorRole.equals(ProcessConstants.ROLE_GONGYIZUZHANG)){
	%>
	<tr>
		<td><h4><%=ProcessConstants.TASKITEM_JSP_ZHIPAIGONGYIYUAN %></td>
		<td><%=taskItem.getGongyiyuan() %></td>
	</tr>
	<%	}else if(executorRole.equals(ProcessConstants.ROLE_ZHURENGONGYISHI)){%>
	<tr>
		<td><h4><%=ProcessConstants.TASKITEM_JSP_XUANZHEZHUZHICHEJIAN %></h4></td>
		<td><%=taskItem.getZhuzhichejian() %></td>
	</tr>
	<tr>
		<td><h4><%=ProcessConstants.TASKITEM_JSP_XUANZHEFUZHICHEJIAN %></h4></td>
		<td><%=taskItem.getFuzhichejian() %></td>
	</tr>
	<%  }
	 }%>
</table>

<br>
<br>

<%
	NmHTMLActionModel am = NmActionServiceHelper.service.getActionModel("custom_process_task_reject_actions",taskItem);
	NmAction rejectTask = (NmAction)am.getActions().get(0);
	rejectTask.setButton(true);
	//completeTask.setEnabled(true);
	rejectTask.setEnabled(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN));
	actionBean.setAction(rejectTask);
	NmContext context = nmcontext.getContext();
	//context.pushElement(taskItem);
	NmAction.actionjsp( actionBean,  linkBean,  objectBean,  localeBean,  urlFactoryBean,nmcontext,  sessionBean,  out,  request,  response);
	context.popElement();
	actionBean.setAction(null);

%>
<%
	am = NmActionServiceHelper.service.getActionModel("custom_process_task_complete_actions",taskItem);
	NmAction completeTask = (NmAction)am.getActions().get(0);
	completeTask.setButton(true);
	//completeTask.setEnabled(true);
	completeTask.setEnabled(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN));
	actionBean.setAction(completeTask);
	context = nmcontext.getContext();
	//context.pushElement(taskItem);
	NmAction.actionjsp( actionBean,  linkBean,  objectBean,  localeBean,  urlFactoryBean,nmcontext,  sessionBean,  out,  request,  response);
	context.popElement();
	actionBean.setAction(null);

%>

<SCRIPT type="text/javascript">
 
function reassignUserCallback(objects) {
	// The search picker used for the reassign task wizard is
	// single select so expect a single JSON object.
	var oid = objects.pickedObject[0].oid;
	var userName = objects.pickedObject[0].name;
	var displayName = objects.pickedObject[0].fullName;
	var principalObjectOid = objects.pickedObject[0].principalObjId;

	//alert("username: " + userName);
	//alert("displayName: " + displayName.length);
	// This is here because the user picker is currently not sending back the fullName.
	// If the fullName length is 0 then we'll fall back to displaying the username.
	if ( displayName.length == 0 ) {
		selectUser(principalObjectOid, userName);   
	}else {
		selectUser(principalObjectOid, displayName);
	}
}
 
function selectUser(userName, displayName) {
	var userList = document.forms.mainform.assignTo;
	var newOption = new Option(displayName,userName, false, true);
	var size = userList.length;
	var newList = new Array(size);
	var existingOption;

	// Save the existing options in an array.
	for (var i = 0; i < size; i++) {
		existingOption = userList.options[i];
		if ( userList.options[i] ) {
			if ( userList.options[i].value == userName ) {
				// If the user is already in the list, select the user and just return
				userList.options[i].selected = true;
				return;
			}else {
				//alert (userList.options[i].text + " " + userList.options[i].value + " " + userList.options[i].defaultSelected + " " + userList.options[i].selected);
				newList[i] = new Option(userList.options[i].text, userList.options[i].value, false, false);
			}
		}		 
	}
	
	// Empty the select list
	userList.options.length=0;

	// Add the new option to the top of the select element.
	userList.options[0]=newOption;

	// Slide the options up an index and add them to the select element
	for (var j = 0; j < newList.length; j++) {
		//alert (newList[j].text + " " + newList[j].value + " " + newList[j].defaultSelected + " " + newList[j].selected);
		userList.options[j+1]=newList[j];
	} 
} 
</SCRIPT>

<br>
<br>

<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.ListPartForProcessTaskItemBuilder')}" />

<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.TaskItemRouteAndHistoryBuilder')}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>