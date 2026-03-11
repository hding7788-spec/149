<%@page import="ext.casc.util.IBAUtility"%>
<%@page import="wt.inf.container.WTContainer"%>
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
				 java.util.Set,
				 java.util.TreeSet"
%>


<%

	String oid = request.getParameter("oid");
	//session.removeAttribute("taskOid");
	session.setAttribute("taskOid", oid);
	ProcessTaskItem taskItem = null;
	String state = "";

	ReferenceFactory referencefactory = new ReferenceFactory();
	Persistable persistable = referencefactory.getReference(oid).getObject();
	if(persistable instanceof ProcessTaskItem){
	    taskItem = (ProcessTaskItem)persistable;
	}

	session.setAttribute("taskState", taskItem.getTaskItemState());

	WTContainer wtContainer = taskItem.getContainer();
	String startDate = ProcessUtil.formatTime(taskItem.getCreateTimestamp().getTime());
	String endDate = ProcessUtil.formatTime(taskItem.getEndDate().getTime());
	String iszhuzhi = ProcessConstants.TASK_ISZHUZHI_FOU;
	boolean flag = taskItem.getIszhuzhi();
	if(flag){
	    iszhuzhi = ProcessConstants.TASK_ISZHUZHI_SHI;
	}
	String taskstate = taskItem.getTaskItemState();
	String executorRole = taskItem.getExecutorRole();
	String renwuyaoqiuValue = taskItem.getRenwuyaoqiu();
	if(renwuyaoqiuValue==null){
	    renwuyaoqiuValue = "";
	}
	Map<String, List<WTUser>> map = ProcessUtil.getGroupAndUsersInOrgContainer();
	Map<String, List<WTUser>> usersmap = ProcessUtil.getCheJianGroupAndUsers();

	IBAUtility ibaUtility = new IBAUtility(taskItem);
	String zzgynumber = ibaUtility.getIBAValue("PPNUMBER");
	String zzgyname = ibaUtility.getIBAValue("PPNAME");
	String zftype = ibaUtility.getIBAValue("PPTASKTYPE");
	String taskCreator = taskItem.getCreatorFullName();
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
<fmt:message var="zzgynumber" key="<%=processRB.PROCESSTASKITEM_ZZGYNUMBER%>" />
<fmt:message var="zzgyname" key="<%=processRB.PROCESSTASKITEM_FZGYNUMBER%>" />
<fmt:message var="zftype" key="<%=processRB.PROCESSTASKITEM_ZFTYPE%>" />
<fmt:message var="taskCreator" key="<%=processRB.PROCESSTASKITEM_TASKCREATOR%>" />

<input type="hidden" name="taskOid" id="taskOid" value="<%=oid%>"/>

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

	<% if(ProcessConstants.TASK_TYPE_FZGYRW.equals(zftype)){%>
		<tr>
			<td><h4>${zzgynumber}</h4></td>
			<td><%=zzgynumber %></td>
		</tr>
		<tr>
			<td><h4>${zzgyname}</h4></td>
			<td><%=zzgyname %></td>
		</tr>
	<% }%>

	<tr>
		<td><h4>${zftype}</h4></td>
		<td><%=zftype %></td>
	</tr>
	<tr>
		<td><h4>${taskCreator}</h4></td>
		<td><%=taskCreator %></td>
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
		<td><%=renwuyaoqiuValue %></td>
	</tr>
	<tr>
		<td><h4>${beizhu}</h4></td>
		<%if(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)){ %>
		<td><textarea name="beizhi" cols ="40" rows = "5"></textarea></td>
		<%}else{
			String beizhuValue = taskItem.getDescription();
			if(beizhuValue == null){
			    beizhuValue = "";
			}
		%>
		<td><%=beizhuValue %></td>
		<%} %>
	</tr>
	<%if(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)){ %>
	<%if(executorRole.equals(ProcessConstants.ROLE_GONGYIZUZHANG)){
		String chejian = taskItem.getChejian();
		List<WTUser> allUsers = ProcessUtil.getUsersByChanJianNumber(wtContainer,chejian);
	%>
	<tr>
		<td><h4><%=ProcessConstants.TASKITEM_JSP_ZHIPAIGONGYIYUAN %>:</h4></td>
		<td>
			<select id="gongyiyuan" name="gongyiyuan">
	<% for(int i=0;i<allUsers.size();i++){
		    WTUser wtUser = allUsers.get(i);
	%>
			<option value="<%=wtUser.getName()+"("+wtUser.getFullName()+")" %>"><%=wtUser.getName()+"("+wtUser.getFullName()+")" %></option>
	<%
	   }
	%>
			</select>
		</td>
	</tr>
	<%}%>
	 <%
	}else {//任务已经完成，则在打开时显示填写的内容
	    if(executorRole.equals(ProcessConstants.ROLE_GONGYIZUZHANG)){
	%>
	<tr>
		<td><h4><%=ProcessConstants.TASKITEM_JSP_ZHIPAIGONGYIYUAN %></td>
		<td><%=taskItem.getGongyiyuan() %></td>
	</tr>
	<%}%>
	<%}%>
</table>

<br>
<br>

<%
	if(executorRole.equals(ProcessConstants.ROLE_ZHURENGONGYISHI)){
		NmHTMLActionModel am = NmActionServiceHelper.service.getActionModel("custom_process_task_delete_actions",taskItem);
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
	}else{
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
	}


%>
<%
	NmHTMLActionModel am = NmActionServiceHelper.service.getActionModel("custom_process_task_complete_actions",taskItem);
	NmAction completeTask = (NmAction)am.getActions().get(0);
	completeTask.setButton(true);
	completeTask.setEnabled(!executorRole.equals(ProcessConstants.ROLE_GONGYIYUAN));
	//completeTask.setEnabled(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN));
	actionBean.setAction(completeTask);
	NmContext context = nmcontext.getContext();
	//context.pushElement(taskItem);
	NmAction.actionjsp( actionBean,  linkBean,  objectBean,  localeBean,  urlFactoryBean,nmcontext,  sessionBean,  out,  request,  response);
	context.popElement();
	actionBean.setAction(null);

%>

<br>
<br>

<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.ListPartForProcessTaskItemBuilder')}" />

<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.ListAttachmentsTableBuilder')}" />

<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.TaskItemRouteAndHistoryBuilder')}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>