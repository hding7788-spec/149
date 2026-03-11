<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="java.util.ArrayList"%>
<%@page import="ext.casc.process.ProcessTask"%>
<%@page import="ext.casc.util.IBAUtility"%>
<%@page import="wt.part.WTPart"%>
<%@page import="ext.casc.process.SearchProcessByPart"%>
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
<%@page import="ext.casc.process.ProcessTaskItem"%>
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
<%@ page import="ext.casc.sop.constants.SopConstants" %>
<%@ page import="cn.hutool.core.util.StrUtil" %>
<%@ page import="wt.change2.WTAnalysisActivity" %>
<%@ page import="ext.casc.changeRequest.Change2Util" %>
<%@ page import="ext.casc.analysisActivity.bean.AnalysisToSourceLink" %>
<%@ page import="ext.ases.changepackaged.ChangePackaged" %>
<%@ page import="wt.fc.*" %>
<%@ page import="ext.ases.envelope.ProcessEnvelope" %>
<%@ page import="ext.casc.analysisActivity.helper.AnalysisConstant" %>
<%@ page import="ext.casc.analysisActivity.helper.AnalysisUtil" %>


<%
	String contextPath = request.getContextPath();
	String oid = request.getParameter("oid");
	//session.removeAttribute("taskOid");
	//session.setAttribute("taskOid", oid);
	ProcessTaskItem taskItem = null;
	String state = "";
	List list = new ArrayList();
	ReferenceFactory referencefactory = new ReferenceFactory();
	Persistable persistable = referencefactory.getReference(oid).getObject();
	if(persistable instanceof ProcessTaskItem){
	    taskItem = (ProcessTaskItem)persistable;
	}
	System.out.println("processTaskItemInfoPage");
	//session.setAttribute("taskState", taskItem.getTaskItemState());
	//session.setAttribute("taskType", taskItem.getTaskType());

   if(!ProcessConstants.TASK_TYPE_FEIGONGYISHEJI.equals(taskItem.getTaskType())){
      WTPart part=	ProcessUtil.getWtPartByProcessTask(taskItem.getProcessTaskId());
      if(part!=null){
    	  list=  SearchProcessByPart.getProcess(part);
      }

    }


	//如果是工艺设计任务，处理顶层part的oid
	System.out.println("--------task type---"+taskItem.getTaskType());
	String TechnicsReportStyle=null;
	if(ProcessConstants.TASK_TYPE_GONGYISHEJI.equals(taskItem.getTaskType())) {
		ProcessTask processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
		if(processTask != null) {
			IBAUtility ibaUtil = new IBAUtility(processTask);
			String topOid = ibaUtil.getIBAValue("TOPOID");
			session.setAttribute("topOid", topOid);
		}
	}else if(ProcessConstants.TASK_TYPE_BBLGY.equals(taskItem.getTaskType())){
		ProcessTask processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
		if(processTask != null) {
			IBAUtility ibaUtil = new IBAUtility(taskItem);
			String topOid = ibaUtil.getIBAValue("TOPOID");
			session.setAttribute("topOid", topOid);
	         TechnicsReportStyle = ibaUtil.getIBAValue("TECHNICSREPORTSTYLE");
	         System.out.println("------------TechnicsReportStyle-------->>>>"+TechnicsReportStyle);
		}
	}
	if(TechnicsReportStyle==null){
		TechnicsReportStyle="";
	}

	WTContainer wtContainer = taskItem.getContainer();
	String startDate = ProcessUtil.formatTime(taskItem.getCreateTimestamp().getTime());

	String iszhuzhi = ProcessConstants.TASK_ISZHUZHI_FOU;
	Object oflag =  taskItem.getIszhuzhi();
	if(oflag!=null){
		boolean flag =(Boolean)oflag;
		if(flag){
		    iszhuzhi = ProcessConstants.TASK_ISZHUZHI_SHI;
		}
	}else{
		iszhuzhi ="";
	}

	String taskstate = taskItem.getTaskItemState();
	String executorRole = taskItem.getExecutorRole();
	System.out.println("executorRole" + executorRole);
	String renwuyaoqiuValue = taskItem.getRenwuyaoqiu();
	String renwuyijuValue = taskItem.getRenwuyiju();
	if(renwuyaoqiuValue==null){
	    renwuyaoqiuValue = "";
	}

	if(renwuyijuValue==null){
		renwuyijuValue = "";
	}
	//Map<String, List<WTUser>> map = ProcessUtil.getGroupAndUsersInOrgContainer();
	//Map<String, List<WTUser>> usersmap = ProcessUtil.getCheJianGroupAndUsers();

	IBAUtility ibaUtility = new IBAUtility(taskItem);
	String zzgynumber = ibaUtility.getIBAValue("PPNUMBER");
	if(zzgynumber == null) {
		zzgynumber = "";
	}
	String zzgyname = ibaUtility.getIBAValue("PPNAME");
	if(zzgyname == null) {
		zzgyname = "";
	}
	String zftype = ibaUtility.getIBAValue("PPTASKTYPE");
	if(zftype == null) {
		zftype = "";
	}
	String taskCreator = ibaUtility.getIBAValue("PPTASKCREATOR");
	if(taskCreator == null) {
		taskCreator = taskItem.getCreatorFullName();
	}
	String cldePlanTime = ibaUtility.getIBAValue("cldePlanTime");
	if(cldePlanTime == null) {
		cldePlanTime = "";
	}
	if (!cldePlanTime.isEmpty()) {
		cldePlanTime = ProcessUtil.formatTime3(cldePlanTime);
	}
	System.out.println("cldePlanTime=============" + cldePlanTime);
	String cldeEndTime = ibaUtility.getIBAValue("cldeEndTime");
	if(cldeEndTime == null) {
		cldeEndTime = "";
	}
	String PROCESSDOCNUM = ibaUtility.getIBAValue("PROCESSDOCNUM");

	if(PROCESSDOCNUM==null) PROCESSDOCNUM="";

	String analysis = "";
	String source = "";
	String analysisNumber = ibaUtility.getIBAValue("ANALYSISNUMBER");
	if(StrUtil.isNotEmpty(analysisNumber)){
		WTAnalysisActivity activity = AnalysisUtil.getWTAnalysisActivityByNumber(analysisNumber);
		if(activity != null) {
			ReferenceFactory rf = new ReferenceFactory();
			String url = " app/#ptc1/tcomp/infoPage?oid=" + rf.getReferenceString(activity);
			analysis = "<a href=\"" + url + "\"  target=_blank>" + activity.getNumber() + " </a>";
			QueryResult qr = PersistenceHelper.manager.navigate(activity, "sourceObject", AnalysisToSourceLink.class);
			if(qr.hasMoreElements()) {
				WTObject object = (WTObject) qr.nextElement();
				if(object instanceof ChangePackaged) {
					ChangePackaged cp = (ChangePackaged) object;
					String sourceUrl = " app/#ptc1/tcomp/infoPage?oid=" + rf.getReferenceString(cp);
					source = "<a href=\"" + sourceUrl + "\"  target=_blank>" + cp.getNumber() + " </a>";
				} else if(object instanceof ProcessEnvelope) {
					ProcessEnvelope pe = (ProcessEnvelope) object;
					String sourceUrl = " app/#ptc1/tcomp/infoPage?oid=" + rf.getReferenceString(pe);
					source = "<a href=\"" + sourceUrl + "\"  target=_blank>" + pe.getNumber() + " </a>";
				}
			}
		}
	}
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
<fmt:message var="cldePlanTime" key="<%=processRB.PROCESSTASKITEM_CLDEPLANTIME%>" />
<fmt:message var="cldeEndTime" key="<%=processRB.PROCESSTASKITEM_CLDEENDTIME%>" />
<fmt:message var="iszhuzhi" key="<%=processRB.PROCESSTASKITEM_ISZHUZHI%>" />
<fmt:message var="executorrole" key="<%=processRB.PROCESSTASKITEM_EXECUTORROLE%>" />
<fmt:message var="renwuyaoqiu" key="<%=processRB.PROCESSTASKITEM_RENWUYAOQIU%>" />
<fmt:message var="beizhu" key="<%=processRB.PROCESSTASKITEM_BEIZHU%>" />
<fmt:message var="zzgynumber" key="<%=processRB.PROCESSTASKITEM_ZZGYNUMBER%>" />
<fmt:message var="zzgyname" key="<%=processRB.PROCESSTASKITEM_FZGYNUMBER%>" />
<fmt:message var="zftype" key="<%=processRB.PROCESSTASKITEM_ZFTYPE%>" />
<fmt:message var="taskCreator" key="<%=processRB.PROCESSTASKITEM_TASKCREATOR%>" />
<fmt:message var="renwuyiju" key="<%=processRB.PROCESSTASKITEM_RENWUYIJU%>" />
<fmt:message var="baobiaoleixing" key="<%=processRB.PROCESSTASKITEM_BBLX%>" />
<fmt:message var="analysis" key="<%=processRB.PROCESSTASKITEM_ANALYSIS%>" />
<fmt:message var="source" key="<%=processRB.PROCESSTASKITEM_SOURCE%>" />
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
		<td><h4>${taskCreator}</h4></td>
		<td><%=taskCreator %></td>
	</tr>
	<tr>
		<td><h4>${startdate}</h4></td>
		<td><%=startDate %></td>
	</tr>

	<%if(ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZHIPAI_REFUSE.equals(taskItem.getTaskItemName())||ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZHIPAI.equals(taskItem.getTaskItemName())||ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZUOFEI.equals(taskItem.getTaskItemName())) {%>
     <%if("".equals(taskItem.getEndDate())||taskItem.getEndDate()==null||ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZHIPAI_REFUSE.equals(taskItem.getTaskItemName())) {%>
	<tr>
		<td><h4>*${enddate}</h4></td>
		<td id="endDate"><w:dateInputComponent  name="endDate" required="true"  dateValueType="DATE_ONLY"/></td>
	</tr>
     <%}else{ %>
     <%String endDate = ProcessUtil.formatTime(taskItem.getEndDate().getTime()); %>
     <tr>
		<td><h4>${enddate}</h4></td>
		<td><%=endDate %></td>
	</tr>
     <%} %>
	<%}else{ %>
	<%String endDate = ProcessUtil.formatTime(taskItem.getEndDate().getTime()); %>
	<tr>
		<td><h4>${enddate}</h4></td>
		<td><%=endDate %></td>

	</tr>
	<%} %>
	<%
		if(!SopConstants.SOP_TASK_TASKTYPE.equals(taskItem.getTaskType()) && !SopConstants.SOP_TASK_TASKTYPE_CHANGE.equals(taskItem.getTaskType())){
	%>
	<tr>
		<td><h4>${cldePlanTime}</h4></td>
		<td><%=startDate %></td>
	</tr>
	<tr>
		<td><h4>${cldeEndTime}</h4></td>
		<td><%=cldePlanTime %></td>
	</tr>
	<% if(ProcessConstants.TASK_TYPE_BBLGY.equals(zftype)){%>
	<tr>
		<td><h4>${zzgynumber}</h4></td>
		<td><%=zzgynumber %></td>
	</tr>
	<tr>
		<td><h4>${zzgyname}</h4></td>
		<td><%=zzgyname %></td>
	</tr>
	<% }%>

	<%if(!taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_BAOBIAOLEI)){ %>
	<tr>
		<td><h4>${zftype}</h4></td>
		<td><%=zftype %></td>
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
			<%} %>
	<%
		}
	%>




<%-- <% if(ProcessConstants.TASK_TYPE_BBLGY.equals(taskItem.getTaskType())){
 %>

 <%if(ProcessConstants.REPORT_TASK_TYPE_GONGYIGENGGAI.equals(taskItem.getTaskItemName())){
 %>
<tr>
			<td><h4>${baobiaoleixing}</h4></td>
			<td><%=TechnicsReportStyle %></td>
</tr>
 <%}else if(ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZHIPAI.equals(taskItem.getTaskItemName())
		||ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZHIPAI_REFUSE.equals(taskItem.getTaskItemName())){ %>

      <%if(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN.equals(taskItem.getTaskItemState())){ %>
		<tr>
			<td><h4>${baobiaoleixing}</h4></td>
			<td><select id="Print" name="Print">

		 <option style="width:250px"></option>
         <option style="width:250px"><%=ProcessConstants.GYLXB %></option>
         <option style="width:250px"><%=ProcessConstants.YQYBMXB %></option>
         <option style="width:250px"><%=ProcessConstants.FYQYBMXB %></option>
         <option style="width:250px"><%=ProcessConstants.BZDLJMXB %></option>
         <option style="width:250px"><%=ProcessConstants.CLXHDEMXB %></option>
         <option style="width:250px"><%=ProcessConstants.WXJMXB %></option>
         <option style="width:250px"><%=ProcessConstants.FZCLDEB %></option>
         <option style="width:250px"><%=ProcessConstants.FZCLDEHZB %></option>
         <option style="width:250px"><%=ProcessConstants.GYZBMXB %></option>
         <option style="width:250px"><%=ProcessConstants.FZCLDEHZB %></option>
         <option style="width:250px"><%=ProcessConstants.XHGYDEHZB %></option>
         <option style="width:250px"><%=ProcessConstants.GYWJML %></option>
         <%for(int i=0;i<list.size();i++){ %>
         <option style="width:250px"><%=list.get(i) %></option>
         <%} %>
         </select>
         </td>
		</tr>
	 <%} else if(ProcessConstants.TASKITEM_STATE_YIWANCHENG.equals(taskItem.getTaskItemState())){%>
<tr>
			<td><h4>${baobiaoleixing}</h4></td>
			<td><%=TechnicsReportStyle %></td>
</tr>
<%} %>
<%} %>
<%} %> --%>


	<tr>
		<td><h4>${renwuyaoqiu}</h4></td>
		<td><%= renwuyaoqiuValue%></td>
	</tr>
	<%if(!taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_BAOBIAOLEI) && (!SopConstants.SOP_TASK_TASKTYPE.equals(taskItem.getTaskType()) && !SopConstants.SOP_TASK_TASKTYPE_CHANGE.equals(taskItem.getTaskType()))){ %>
	<tr>
		<td><h4>${renwuyiju}</h4></td>
		<td><%=renwuyijuValue %></td>
	</tr>
	<tr>
	<%} %>
		<td><h4>${beizhu}</h4></td>
		<%if(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN) ||
				ProcessConstants.TASK_STATE_FEIGONGZHIPAIZHONG.equals(taskstate) || ProcessConstants.TASK_STATE_FEIGONGYIZUZHANGJUJUE.equals(taskstate)){ %>
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

	<%if(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN) || taskstate.equals(ProcessConstants.TASK_STATE_FEIGONGZHIPAIZHONG) ||
			taskstate.equals(ProcessConstants.TASK_STATE_FEIGONGYIZUZHANGJUJUE)){
	%>

		<%if(executorRole.equals(ProcessConstants.ROLE_GONGYIZUZHANG)){%>
		<%if(ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZHIPAI.equals(taskItem.getTaskItemName())){%>
		<tr>
			<td><h4>*<%=ProcessConstants.TASKITEM_JSP_ZHIPAIGONGYIYUAN %>:</h4></td>
			<td>
				<input type="text" readonly id="selectedUser1" style="width:250px;" name="selectedUser1"/>
				<input type="button" onclick="showHQRY1();" value="<%=ProcessConstants.JSP_BUTTON_SELECT %>">
			</td>
		</tr>
		<%}else{%>
		<tr>
			<td><h4>*<%=ProcessConstants.TASKITEM_JSP_ZHIPAIGONGYIYUAN %>:</h4></td>
			<td>
				<input type="text" readonly id="selectedUser" style="width:250px;" name="selectedUser"/>
				<input type="button" onclick="showHQRY();" value="<%=ProcessConstants.JSP_BUTTON_SELECT %>">
			</td>
		</tr>
		<%}%>
	<%}else if(executorRole.equals(SopConstants.SOP_ROLE_BZHS)){%>
	<tr>
		<td><h4>*<%=ProcessConstants.TASKITEM_JSP_ZHIPAIGONGYIYUAN %>:</h4></td>
		<td>
			<input type="text" readonly id="selectSopUser" style="width:250px;" name="selectSopUser"/>
			<input type="button" onclick="selectSopGyy();" value="<%=ProcessConstants.JSP_BUTTON_SELECT %>">
		</td>
	</tr>
	<%}%>


		<%if(executorRole.equals(ProcessConstants.ROLE_ZHURENGONGYISHI)){
			  ArrayList<String> set = ProcessUtil.getAllCheJian();
		%>
		<tr>
	 	<td><h4><%=ProcessConstants.TASKITEM_JSP_XUANZHEZHUZHICHEJIAN %></h4></td>
	 	<td>
	 	<% if(ProcessConstants.TASK_TYPE_FEIGONGYISHEJI.equals(taskItem.getTaskType()) && !ProcessConstants.TASK_NAME_ZHIPAIGONGYIZUZHANG_JUJUE.equals(taskItem.getTaskItemName())){%>
	 	<input id="zhuzhichejian" name="zhuzhichejian" style="width:100px;" value="<%=taskItem.getChejian()%>" readonly="true"/>
	 	<%}else{%>
	 	<select id="zhuzhichejian" name="zhuzhichejian" style="width:100px;">
	 	<%for (String chejian : set) {%>
			<option value="<%=chejian %>"><%=chejian %></option>
		<%}%>
	 		</select>
	 	<% }%>

		</td>
	</tr>
		<%}%>
	<%}else {//任务已经完成，则在打开时显示填写的内容
	     if(executorRole.equals(ProcessConstants.ROLE_GONGYIZUZHANG)){
	%>
	<tr>
		<td><h4><%=ProcessConstants.TASKITEM_JSP_ZHIPAIGONGYIYUAN %></h4></td>
		<td><%=taskItem.getGongyiyuan()%></td>
	</tr>
	<%}%>
	<%}%>
	<tr>
		<td><h4><%="关联工艺文件:"%></h4></td>
		<td><input type="text" readonly id="PROCESSDOCNUM" style="width:250px;" name="PROCESSDOCNUM" value="<%=PROCESSDOCNUM %>"/></td>
	</tr>
	<% if(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWU.equals(taskItem.getTaskItemName())
			&& ProcessConstants.TASK_TYPE_LINSHIGONGYI.equals(taskItem.getTaskType())
			&& ProcessConstants.ROLE_GONGYIYUAN.equals(executorRole)
			&& taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)) {%>
	<tr>
		<td><input type="button" onclick="selectTechnics();" value="<%=AnalysisConstant.SEARCH_SELECTDOC %>"></td>
	</tr>
	<% }%>

	<% if(AnalysisConstant.RENWUYIJV_ZAIZHIPIN.equals(renwuyijuValue) || AnalysisConstant.RENWUYIJV_YIZHIPIN.equals(renwuyijuValue)){%>
	<tr>
		<td><h4>${analysis}</h4></td>
		<td><%=analysis %></td>
	</tr>
	<tr>
		<td><h4>${source}</h4></td>
		<td><%=source %></td>
	</tr>
	<% }%>

</table>

<br>
<br>

<%if(executorRole.equals(ProcessConstants.ROLE_GONGYIYUAN)&&taskItem.getTaskItemName().endsWith(ProcessConstants.TASK_NAME_END_JUJUE)){
	NmHTMLActionModel am = NmActionServiceHelper.service.getActionModel("custom_process_task_delete_actions",taskItem);
	NmAction rejectTask = (NmAction)am.getActions().get(0);//废弃工艺任务按钮
	rejectTask.setButton(true);
	//completeTask.setEnabled(true);
	rejectTask.setEnabled(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN));
	actionBean.setAction(rejectTask);
	NmContext context = nmcontext.getContext();
	//context.pushElement(taskItem);
	NmAction.actionjsp( actionBean,  linkBean,  objectBean,  localeBean,  urlFactoryBean,nmcontext,  sessionBean,  out,  request,  response);
	context.popElement();
	actionBean.setAction(null);
}else if(executorRole.equals(ProcessConstants.ROLE_ZHURENGONGYISHI)){
		NmHTMLActionModel am = NmActionServiceHelper.service.getActionModel("custom_process_task_delete_actions",taskItem);
		NmAction rejectTask = (NmAction)am.getActions().get(0);//废弃工艺任务
		rejectTask.setButton(true);
		//completeTask.setEnabled(true);
		rejectTask.setEnabled(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN) || taskstate.equals(ProcessConstants.TASK_STATE_FEIGONGYIZUZHANGJUJUE));
		if(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
				&& !ProcessConstants.TASK_NAME_ZHIPAIGONGYIZUZHANG_JUJUE.equals(taskItem.getTaskItemName()) &&
				ProcessConstants.TASK_TYPE_FEIGONGYISHEJI.equals(taskItem.getTaskItemName())){
			rejectTask.setEnabled(false);
		}
		actionBean.setAction(rejectTask);
		NmContext context = nmcontext.getContext();
		//context.pushElement(taskItem);
		NmAction.actionjsp( actionBean,  linkBean,  objectBean,  localeBean,  urlFactoryBean,nmcontext,  sessionBean,  out,  request,  response);
		context.popElement();
		actionBean.setAction(null);



	}else{
	if (!SopConstants.SOP_TASK_TASKTYPE.equals(taskItem.getTaskType()) && !SopConstants.SOP_TASK_TASKTYPE_CHANGE.equals(taskItem.getTaskType())) {
		NmHTMLActionModel am = NmActionServiceHelper.service.getActionModel("custom_process_task_reject_actions", taskItem);
		NmAction rejectTask = (NmAction) am.getActions().get(0);//拒绝任务
		rejectTask.setButton(true);
		//completeTask.setEnabled(true);
		//rejectTask.setEnabled(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN));

		if (executorRole.equals(ProcessConstants.ROLE_GONGYIYUAN)
				&& taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
				&& taskItem.getTaskItemName().equals(ProcessConstants.TASK_NAME_WUXUBIANZHIGONGY)) {
			rejectTask.setEnabled(false);
		}

		if (!taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)) {
			rejectTask.setEnabled(false);
		}

		if (ProcessConstants.TASK_NAME_FEIGONGYISHEJIZHIPAI.equals(taskItem.getTaskItemName()) && taskstate.equals(ProcessConstants.TASK_STATE_FEIGONGZHIPAIZHONG)) {
			rejectTask.setEnabled(true);
		}

		if (ProcessConstants.TASK_TYPE_FEIGONGYISHEJI.equals(taskItem.getTaskItemName()) && ProcessConstants.TASK_STATE_JINGXINZHONG.equals(taskstate) && ProcessConstants.ROLE_GONGYIYUAN.equals(executorRole)) {
			rejectTask.setEnabled(true);
		}

		if (ProcessConstants.TASK_NAME_FEIGONGYISHEFANKUI.equals(taskItem.getTaskItemName())
				&& ProcessConstants.TASK_TYPE_FEIGONGYISHEJI.equals(taskItem.getTaskType())) {
			rejectTask.setEnabled(false);
		}
		actionBean.setAction(rejectTask);
		NmContext context = nmcontext.getContext();
		//context.pushElement(taskItem);
		NmAction.actionjsp(actionBean, linkBean, objectBean, localeBean, urlFactoryBean, nmcontext, sessionBean, out, request, response);
		context.popElement();
		actionBean.setAction(null);
	}
	}

%>
<%
	/**SOP编制任务定制按钮*/
	if(SopConstants.SOP_TASK_TASKTYPE.equals(taskItem.getTaskType()) || SopConstants.SOP_TASK_TASKTYPE_CHANGE.equals(taskItem.getTaskType())){
	    if(SopConstants.SOP_ROLE_BZHS.equals(executorRole)){
			System.out.println("taskState:" + taskstate);
			NmHTMLActionModel am = NmActionServiceHelper.service.getActionModel("custom_soptask_actions", taskItem);
            System.out.println("actions:" + am.getActions());
			NmAction deleteTask = (NmAction) am.getActions().get(1);//作废任务
			deleteTask.setButton(true);
			if (!taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)) {
				deleteTask.setEnabled(false);
			}
			actionBean.setAction(deleteTask);
			NmContext context = nmcontext.getContext();
			NmAction.actionjsp(actionBean, linkBean, objectBean, localeBean, urlFactoryBean, nmcontext, sessionBean, out, request, response);


			NmAction completeTask = (NmAction) am.getActions().get(2);//完成任务
			completeTask.setButton(true);
			if (!taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)) {
				completeTask.setEnabled(false);
			}
			actionBean.setAction(completeTask);
			context = nmcontext.getContext();
            NmAction.actionjsp(actionBean, linkBean, objectBean, localeBean, urlFactoryBean, nmcontext, sessionBean, out, request, response);
			context.popElement();
			actionBean.setAction(null);
		}else if(SopConstants.SOP_ROLE_GYY.equals(executorRole)){
			NmHTMLActionModel am = NmActionServiceHelper.service.getActionModel("custom_soptask_actions", taskItem);
			NmAction rejectTask = (NmAction) am.getActions().get(0);//拒绝任务
			rejectTask.setButton(true);
			if (!taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)) {
				rejectTask.setEnabled(false);
			}

			actionBean.setAction(rejectTask);
			NmContext context = nmcontext.getContext();
			NmAction.actionjsp(actionBean, linkBean, objectBean, localeBean, urlFactoryBean, nmcontext, sessionBean, out, request, response);
			context.popElement();
			actionBean.setAction(null);
		}
	}else{
		//完成任务按钮
		NmHTMLActionModel am = NmActionServiceHelper.service.getActionModel("custom_process_task_complete_actions", taskItem);
		NmAction completeTask = (NmAction) am.getActions().get(0);//完成任务
		completeTask.setButton(true);
		//completeTask.setEnabled(!executorRole.equals(ProcessConstants.ROLE_GONGYIYUAN)&&taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN));
		//completeTask.setEnabled(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN));
		//completeTask.setEnabled(false);

		if (executorRole.equals(ProcessConstants.ROLE_GONGYIYUAN)
				&& taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
				&& taskItem.getTaskItemName().equals(ProcessConstants.TASK_NAME_WUXUBIANZHIGONGY)) {
			completeTask.setEnabled(true);
		} else if (executorRole.equals(ProcessConstants.ROLE_GONGYIYUAN)) {
			completeTask.setEnabled(false);
		}

		if (taskItem.getIszhuzhi() != null
				&& !taskItem.getIszhuzhi()
				&& taskItem.getTaskItemName().equals(ProcessConstants.TASK_NAME_ZHIPAIGONGYIZUZHANG_JUJUE)) {
			completeTask.setEnabled(false);
		}

		if (!taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)) {
			completeTask.setEnabled(false);
		}

		//主治车间指派工艺组长_拒绝任务  屏蔽完成任务按钮
		if (taskstate.equals(ProcessConstants.TASK_STATE_FEIGONGYIZUZHANGJUJUE)
				&& executorRole.equals(ProcessConstants.ROLE_ZHURENGONGYISHI)
				&& taskItem.getTaskItemName().endsWith(ProcessConstants.TASK_NAME_END_JUJUE)) {
			completeTask.setEnabled(true);
		}

		if (ProcessConstants.TASK_NAME_FEIGONGYISHEJIZHIPAI.equals(taskItem.getTaskItemName()) && taskstate.equals(ProcessConstants.TASK_STATE_FEIGONGZHIPAIZHONG)) {
			completeTask.setEnabled(true);
		}

		if (ProcessConstants.TASK_TYPE_FEIGONGYISHEJI.equals(taskItem.getTaskItemName()) && ProcessConstants.TASK_STATE_FEIGONGZHENGZAIJINXING.equals(taskstate) && ProcessConstants.ROLE_GONGYIYUAN.equals(executorRole)) {
			completeTask.setEnabled(true);
		}

		if (taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
				&& executorRole.equals(ProcessConstants.ROLE_ZHURENGONGYISHI)
				&& !ProcessConstants.TASK_NAME_ZHIPAIGONGYIZUZHANG_JUJUE.equals(taskItem.getTaskItemName())) {
			completeTask.setEnabled(false);
		}

		if (ProcessConstants.TASK_NAME_FEIGONGYISHEFANKUI.equals(taskItem.getTaskItemName()) && taskstate.equals(ProcessConstants.TASK_STATE_FEIGONGZHENGZAIJINXING)) {
			completeTask.setEnabled(true);
		}
		if (ext.casc.util.WCUtil.isAdmin()) {
			completeTask.setEnabled(true);
		}

		if (executorRole.equals(ProcessConstants.ROLE_GONGYIYUAN)
				&& taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
				&& taskItem.getTaskItemName().equals(ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWU)
				&& !taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_BAOBIAOLEI)) {
			completeTask.setEnabled(true);
		}

		//制品返修任务放开完成按钮
		if (executorRole.equals(ProcessConstants.ROLE_GONGYIYUAN)
				&& taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
				&& taskItem.getTaskItemName().equals(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWU)
				&& (AnalysisConstant.RENWUYIJV_ZAIZHIPIN.equals(renwuyijuValue) || AnalysisConstant.RENWUYIJV_YIZHIPIN.equals(renwuyijuValue))) {
			completeTask.setEnabled(true);
		}

		actionBean.setAction(completeTask);
		NmContext context = nmcontext.getContext();
		//context.pushElement(taskItem);
		NmAction.actionjsp(actionBean, linkBean, objectBean, localeBean, urlFactoryBean, nmcontext, sessionBean, out, request, response);
		context.popElement();
		actionBean.setAction(null);
	}


%>

<%
if(ProcessConstants.TASK_TYPE_FZGYRW.equals(zftype)) {
	//无需编制工艺并完成任务按钮
	NmHTMLActionModel am2 = NmActionServiceHelper.service.getActionModel("custom_process_task_complete2_actions",taskItem);
	NmAction completeTask2 = (NmAction)am2.getActions().get(0);
	completeTask2.setButton(true);
	if(executorRole.equals(ProcessConstants.ROLE_GONGYIYUAN)
			&& taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
			&& taskItem.getTaskItemName().equals(ProcessConstants.TASK_NAME_WUXUBIANZHIGONGY)) {
		completeTask2.setEnabled(true);
	} else {
		completeTask2.setEnabled(false);
	}

	if(executorRole.equals(ProcessConstants.ROLE_GONGYIYUAN)
				&& taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
				&& taskItem.getTaskItemName().endsWith(ProcessConstants.TASK_NAME_END_JUJUE)) {
			completeTask2.setEnabled(false);
	}

	//completeTask.setEnabled(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN));
	actionBean.setAction(completeTask2);
	NmContext context2 = nmcontext.getContext();
	//context.pushElement(taskItem);
	NmAction.actionjsp( actionBean, linkBean, objectBean, localeBean, urlFactoryBean,nmcontext, sessionBean, out, request, response);
	context2.popElement();
	actionBean.setAction(null);
}

%>
<%
if(ProcessConstants.TASK_TYPE_FZGYRW.equals(zftype)) {
	//无需编制工艺并完成任务按钮
	NmHTMLActionModel am4 = NmActionServiceHelper.service.getActionModel("custom_process_task_complete4_actions",taskItem);
	NmAction completeTask4 = (NmAction)am4.getActions().get(0);
	completeTask4.setButton(true);
	if(executorRole.equals(ProcessConstants.ROLE_GONGYIYUAN)
			&& taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
			&& taskItem.getTaskItemName().equals(ProcessConstants.TASK_NAME_WUXUBIANZHIGONGY)) {
	    completeTask4.setEnabled(true);
	} else {
	    completeTask4.setEnabled(false);
	}

	if(executorRole.equals(ProcessConstants.ROLE_GONGYIYUAN)
			&& taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
			&& taskItem.getTaskItemName().equals(ProcessConstants.TASK_NAME_FUZHIGONGYIGENGGAIRENWU)) {
	    completeTask4.setEnabled(true);
	} else {
	    completeTask4.setEnabled(false);
	}

	if(executorRole.equals(ProcessConstants.ROLE_GONGYIYUAN)
				&& taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
				&& taskItem.getTaskItemName().endsWith(ProcessConstants.TASK_NAME_END_JUJUE)) {
	    completeTask4.setEnabled(false);
	}

	//completeTask.setEnabled(taskstate.equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN));
	actionBean.setAction(completeTask4);
	NmContext context4 = nmcontext.getContext();
	//context.pushElement(taskItem);
	NmAction.actionjsp( actionBean, linkBean, objectBean, localeBean, urlFactoryBean,nmcontext, sessionBean, out, request, response);
	context4.popElement();
	actionBean.setAction(null);
}

%>
<%
if(ProcessConstants.TASK_TYPE_FEIGONGYISHEJI.equals(taskItem.getTaskType()) &&
		!ProcessConstants.TASK_NAME_ZHIPAIGONGYIZUZHANG_JUJUE.equals(taskItem.getTaskItemName()) &&
		ProcessConstants.ROLE_ZHURENGONGYISHI.equals(executorRole)
		){
	NmHTMLActionModel am3 = NmActionServiceHelper.service.getActionModel("custom_process_task_complete3_actions",taskItem);
	NmAction completeTask3 = (NmAction)am3.getActions().get(0);
	completeTask3.setButton(true);
	if(ProcessConstants.TASK_STATE_FEIGONGYIWANCHENG.equals(taskItem.getTaskItemState())){
		completeTask3.setEnabled(false);
	}
	actionBean.setAction(completeTask3);
	NmContext context2 = nmcontext.getContext();
	//context.pushElement(taskItem);
	NmAction.actionjsp( actionBean, linkBean, objectBean, localeBean, urlFactoryBean,nmcontext, sessionBean, out, request, response);
	context2.popElement();
	actionBean.setAction(null);

}%>

 <%if(ProcessConstants.TASK_TYPE_FEIGONGYISHEJI.equals(taskItem.getTaskType()) &&
		!ProcessConstants.TASK_NAME_ZHIPAIGONGYIZUZHANG_JUJUE.equals(taskItem.getTaskItemName()) &&
		ProcessConstants.ROLE_ZHURENGONGYISHI.equals(executorRole)
		){
	NmHTMLActionModel am3 = NmActionServiceHelper.service.getActionModel("custom_process_task_reject3_actions",taskItem);
	NmAction rejectTask3 = (NmAction)am3.getActions().get(0);
	rejectTask3.setButton(true);
	if(ProcessConstants.TASK_STATE_FEIGONGYIWANCHENG.equals(taskItem.getTaskItemState())){
		rejectTask3.setEnabled(false);
	}
	actionBean.setAction(rejectTask3);
	NmContext context2 = nmcontext.getContext();
	//context.pushElement(taskItem);
	NmAction.actionjsp( actionBean, linkBean, objectBean, localeBean, urlFactoryBean,nmcontext, sessionBean, out, request, response);
	context2.popElement();
	actionBean.setAction(null);

}


%>

<%
    //完成报表类工艺编制任务
    if (executorRole.equals(ProcessConstants.ROLE_GONGYIYUAN) && taskItem.getTaskItemState().equals(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN)
            && taskItem.getTaskItemName().equals(ProcessConstants.REPORT_TASK_TYPE_GONGYIGENGGAI)) {
        NmHTMLActionModel reportActionModel= NmActionServiceHelper.service.getActionModel("custom_report_process_task_complete_actions", taskItem);
        NmAction reportComplete = (NmAction) reportActionModel.getActions().get(0);
        reportComplete.setButton(true);
        //completeTask.setEnabled(true);
        reportComplete.setEnabled(true);
        actionBean.setAction(reportComplete);
        NmContext reportContext = nmcontext.getContext();
        NmAction.actionjsp(actionBean, linkBean, objectBean, localeBean, urlFactoryBean, nmcontext, sessionBean, out, request, response);
        reportContext.popElement();
        actionBean.setAction(null);
    }
%>

<br>
<br>
<%if(!ProcessConstants.TASK_TYPE_FEIGONGYISHEJI.equals(taskItem.getTaskType())){%>
<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.ListPartForProcessTaskItemBuilder')}" />
<%} %>
<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.ListAttachmentsTableBuilder')}" />
<%if(SopConstants.SOP_TASK_TASKTYPE.equals(taskItem.getTaskType()) || SopConstants.SOP_TASK_TASKTYPE_CHANGE.equals(taskItem.getTaskType())){%>
	<jsp:include page="${mvc:getComponentURL('ext.casc.sop.mvc.builders.SopTaskItemRouteAndHistoryBuilder')}" />
<%
}else{%>
	<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.TaskItemRouteAndHistoryBuilder')}" />
<%}%>

<script>
var zpGYY = '<%=ProcessConstants.TASKITEM_JSP_SELECTUSER%>';
var taskOid = '<%=oid%>';
function showHQRY(){
	var win;
	var nav;
	var loader;
	var root;
	var categoryid;

	if(!loader){
	    loader = new Ext.tree.TreeLoader({
	        url :  'netmarkets/jsp/customProcessTask/loadGYYData.jsp?taskOid='+taskOid
	    });
	    loader.on('beforeload', function(treeloader, node) {
	        treeloader.baseParams = {
	            id : node.id,
	            text:node.text,
	            method : 'tree'
	        };
	    }, this);
	}

	if(!root){
	    root = new Ext.tree.AsyncTreeNode({
	        id : '0',
	        text :'<%=ProcessConstants.TASKITEM_JSP_ZHIPAIGONGYIYUAN %>',
	        icon: ""
	    });
	}

	if(!nav){
	    nav = new Ext.tree.TreePanel({
	        autoScroll:true,
	        animate : true,
	        height:300,
	        border:false,
	        loader : loader,
	        root : root,
	        enableDD : false,
	        rootVisible:false
	    });
	}
	nav.expandAll();
	if(!win){
		win = new Ext.Window({
			title: zpGYY,
	    	id:"GYHQ_Window",
	    	width:450,
	   	 	minWidth: 200,
	    	minHeight: 300,
	    	layout: 'fit',
	    	//plain:true,
	    	closeAction:'hide',
	    	bodyStyle:'padding:5px;',
	    	buttonAlign:'center',
	    	items:[nav],
	    	modal:true,
	    	  buttons : [{
	            text:'<%=ProcessConstants.JSP_BUTTON_OK %>',
	            cls : "x-btn-text-icon",
	        	icon : "netmarkets/images/save.png",
	            handler:function(){
	            	var arr = nav.getChecked();
	           		if(arr.length>0){
	           			var str1;
	           			var str2;
	           			for(var i=0;i<arr.length;i++){
	           				if(i == 0) {
	           					str1 = arr[i].id;
	           					str2 = arr[i].text;
	           				} else {
	           					str1 = str1+";"+arr[i].id;
	           					str2 = str2+";"+arr[i].text;
	           				}
	           			}
	           			zpGYZZ(str1,str2);
	           		}else{
	           			zpGYZZ("","");
	           		}

	           		win.close();
	            }
	        },{
	            text : '<%=ProcessConstants.JSP_BUTTON_CANCEL %>',
	            cls : "x-btn-text-icon",
	        	icon : "netmarkets/images/cancel.png",
	            handler:function(){
	                win.close();
	            }
	        }]

		});
	}
	win.show();
}

function showHQRY1(){
	var win;
	var nav;
	var loader;
	var root;
	var categoryid;

	if(!loader){
	    loader = new Ext.tree.TreeLoader({
	        url :  'netmarkets/jsp/customProcessTask/loadGYYData1.jsp?taskOid='+taskOid
	    });
	    loader.on('beforeload', function(treeloader, node) {
	        treeloader.baseParams = {
	            id : node.id,
	            text:node.text,
	            method : 'tree'
	        };
	    }, this);
	}

	if(!root){
	    root = new Ext.tree.AsyncTreeNode({
	        id : '0',
	        text :'<%=ProcessConstants.TASKITEM_JSP_ZHIPAIGONGYIYUAN %>',
	        icon: ""
	    });
	}

	if(!nav){
	    nav = new Ext.tree.TreePanel({
	        autoScroll:true,
	        animate : true,
	        height:300,
	        border:false,
	        loader : loader,
	        root : root,
	        enableDD : false,
	        rootVisible:false
	    });
	}
	nav.expandAll();
	if(!win){
		win = new Ext.Window({
			title: zpGYY,
	    	id:"GYHQ_Window",
	    	width:450,
	   	 	minWidth: 200,
	    	minHeight: 300,
	    	layout: 'fit',
	    	//plain:true,
	    	closeAction:'hide',
	    	bodyStyle:'padding:5px;',
	    	buttonAlign:'center',
	    	items:[nav],
	    	modal:true,
	    	tbar:[' ',
	              new Ext.form.TextField({
	                  width:350,
	                  emptyText:'<%="请输入关键字检索"%>',
	                  enableKeyEvents: true,
	                  listeners:{
	                      keyup:function(node, event) {
	                          findByKeyWordFiler(node, event);
	                      },
	                      scope: this
	                  }
	              })
	    	],
	    	  buttons : [{
	            text:'<%=ProcessConstants.JSP_BUTTON_OK %>',
	            cls : "x-btn-text-icon",
	        	icon : "netmarkets/images/save.png",
	            handler:function(){
	            	var arr = nav.getChecked();
	           		if(arr.length>0){
	           			var str1;
	           			var str2;
	           			for(var i=0;i<arr.length;i++){
	           				if(i == 0) {
	           					str1 = arr[i].id;
	           					str2 = arr[i].text;
	           				} else {
	           					str1 = str1+";"+arr[i].id;
	           					str2 = str2+";"+arr[i].text;
	           				}
	           			}
	           			zpGYZZ1(str1,str2);
	           		}else{
	           			zpGYZZ1("","");
	           		}

	           		win.close();
	            }
	        },{
	            text : '<%=ProcessConstants.JSP_BUTTON_CANCEL %>',
	            cls : "x-btn-text-icon",
	        	icon : "netmarkets/images/cancel.png",
	            handler:function(){
	                win.close();
	            }
	        }]

		});


		var treeFilter = new Ext.tree.TreeFilter(nav, {
		    clearBlank : true,
		    autoClear : true
		});

		var timeOutId  = null;
		var hiddenPkgs = [];

		var findByKeyWordFiler = function(node, event) {
		    //clearTimeout(timeOutId);// 清除timeOutId
		    nav.expandAll();// 展开树节点
		    // 为了避免重复的访问后台，给服务器造成的压力，采用timeOutId进行控制，如果采用treeFilter也可以造成重复的keyup
		    timeOutId = setTimeout(function() {
		        // 获取输入框的值
		        var text = node.getValue();
		        // 根据输入制作一个正则表达式，'i'代表不区分大小写
		        var re = new RegExp(Ext.es0capeRe(text), 'i');
		        // 先要显示上次隐藏掉的节点
		        Ext.each(hiddenPkgs, function(n) {
		            n.ui.show();
		        });
		        hiddenPkgs = [];
		        if (text != "") {
		            treeFilter.filterBy(function(n) {
		                // 只过滤叶子节点，这样省去枝干被过滤的时候，底下的叶子都无法显示
		                return !n.isLeaf() || re.test(n.text);
		            });
		            // 如果这个节点不是叶子，而且下面没有子节点，就应该隐藏掉
		            nav.root.cascade(function(n) {
		                if(n.id!='0'){
		                    if(!n.isLeaf() &&judge(n,re)==false&& !re.test(n.text)){
		                        hiddenPkgs.push(n);
		                        n.ui.hide();
		                    }
		                }
		            });
		        } else {
		            treeFilter.clear();
		            return;
		        }
		    }, 500);
		}

		// 过滤不匹配的非叶子节点或者是叶子节点
		var judge =function(n,re){
		    var str=false;
		    n.cascade(function(n1){
		        if(n1.isLeaf()){
		            if(re.test(n1.text)){ str=true;return; }
		        } else {
		            if(re.test(n1.text)){ str=true;return; }
		        }
		    });
		    return str;
		};



	}
	win.show();
}

function zpGYZZ(userOid ,userName){
	var el = document.getElementById("selectedUser");
	if(el!=null){
		el.value=userName;
		el.title=userName;
	}
}

function validateValue(){
	var el = document.getElementById("selectedUser");
	if(el == null) {
		alert("sdfsfsfsf");
		return;
	}
}
function zpGYZZ1(userOid ,userName){
	var el = document.getElementById("selectedUser1");
	if(el!=null){
		el.value=userName;
		el.title=userName;
	}
}

function validateValue1(){
	var el = document.getElementById("selectedUser1");
	if(el == null) {
		alert("sdfsfsfsf");
		return;
	}
}



function selectSopGyy() {
    var win;
    var nav;
    var loader;
    var root;
    var categoryid;

    if(!loader){
        loader = new Ext.tree.TreeLoader({
            url :  'netmarkets/jsp/ext/casc/sop/task/loadGyyInfo.jsp?taskOid='+taskOid
        });
        loader.on('beforeload', function(treeloader, node) {
            treeloader.baseParams = {
                id : node.id,
                text:node.text,
                method : 'tree'
            };
        }, this);
    }

    if(!root){
        root = new Ext.tree.AsyncTreeNode({
            id : '0',
            text :'<%=ProcessConstants.TASKITEM_JSP_ZHIPAIGONGYIYUAN %>',
            icon: ""
        });
    }

    if(!nav){
        nav = new Ext.tree.TreePanel({
            autoScroll:true,
            animate : true,
            height:300,
            border:false,
            loader : loader,
            root : root,
            enableDD : false,
            rootVisible:false,
            listeners: {
                checkchange: function (node, checked) {
                    //当前选中的设置选中状态
                    node.attributes.checked = checked;
                    //获取该树下的所有已选中节点，设置为非选中
                    var chs = nav.getChecked();
                    //循环处理，如果不等于当前选择的节点，则通过toggleCheck方法设置非选中状态
                    for(var i = 0; i < chs.length; i++) {
                        if(chs[i].attributes.id != node.attributes.id) {
                            chs[i].ui.toggleCheck(!checked);
                        }
                    }
                }
            }
        });
    }
    nav.expandAll();
    if(!win){
        win = new Ext.Window({
            title: zpGYY,
            id:"GYHQ_Window",
            width:450,
            minWidth: 200,
            minHeight: 300,
            layout: 'fit',
            //plain:true,
            closeAction:'hide',
            bodyStyle:'padding:5px;',
            buttonAlign:'center',
            items:[nav],
            modal:true,
            buttons : [{
                text:'<%=ProcessConstants.JSP_BUTTON_OK %>',
                cls : "x-btn-text-icon",
                icon : "netmarkets/images/save.png",
                handler:function(){
                    var arr = nav.getChecked();
                    if(arr.length>0){
                        var str1;
                        var str2;
                        for(var i=0;i<arr.length;i++){
                            if(i == 0) {
                                str1 = arr[i].id;
                                str2 = arr[i].text;
                            } else {
                                str1 = str1+";"+arr[i].id;
                                str2 = str2+";"+arr[i].text;
                            }
                        }

                        document.getElementById("selectSopUser").value = str2;
                    }else{
                        document.getElementById("selectSopUser").value = "";
                    }

                    win.close();
                }
            },{
                text : '<%=ProcessConstants.JSP_BUTTON_CANCEL %>',
                cls : "x-btn-text-icon",
                icon : "netmarkets/images/cancel.png",
                handler:function(){
                    win.close();
                }
            }]

        });
    }
    win.show();
}

function selectTechnics() {
	var winObj = window.open("<%=contextPath%>/ptc1/ext/casc/analysisActivity/addRelatedObj?objType=tempTechnics&AjaxEnabled=component&wizardActionClass=ext.casc.analysisActivity.process.ExtRelatedTechnicsForTaskProcessor&wizardActionMethod=execute&portlet=poppedup&context=analysisActivity%24relatedObjects%24<%=oid%>24&oid=<%=oid%>", '关联受控临时工艺文件', 'height=600, width=650, top=150, left=300');
	var loop = setInterval(function () {
		if (winObj.closed) {
			clearInterval(loop);
		}
	}, 3);
}

function renderNumber(value) {
	if(value.length > 0){
		var splits = value.split("@");
		var num = splits[0];
		var oid = splits[1];
		var url = " app/#ptc1/tcomp/infoPage?oid=" + oid
		value = "<a href=\"" + url + "\"  target=_blank>" + num + "</a>";
	}
	return value;
}

</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>