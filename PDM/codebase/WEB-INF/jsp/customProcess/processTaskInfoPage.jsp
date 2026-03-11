<%@page import="ext.casc.util.IBAUtility" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ page import="wt.util.WTProperties" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<jca:tabToHighlight actionName="propertyPanel" objectType="object"/>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb" %>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@page import="ext.casc.process.resource.processRB,ext.casc.process.ProcessConstants" %>
<%@page import="wt.fc.ReferenceFactory,wt.fc.Persistable,ext.casc.process.ProcessTask" %>
<%@ page import="java.util.HashMap,
				 com.ptc.netmarkets.util.misc.NmAction,
				 com.ptc.netmarkets.util.misc.NmActionServiceHelper,
				 com.ptc.netmarkets.work.NmWorkItem,
				 com.ptc.netmarkets.work.NmWorkItemCommands,
				 com.ptc.netmarkets.model.NmNamedObject,
				 wt.workflow.worklist.worklistResource,
				 wt.util.WTMessage,
				 java.util.Locale,
				 java.util.ResourceBundle,
				 com.ptc.netmarkets.util.misc.NmHTMLActionModel,
				 com.ptc.netmarkets.util.misc.NmContext,
				 wt.session.SessionHelper,
				 java.util.Map,
				 ext.casc.process.util.ProcessUtil,
				 java.util.List,
				 wt.org.WTUser,
				 java.util.Set,
				 java.util.TreeSet"
%>

<%

    String oid = request.getParameter("oid");
    ProcessTask task = null;
    String state = "";

    ReferenceFactory referencefactory = new ReferenceFactory();
    Persistable persistable = referencefactory.getReference(oid).getObject();
    if (persistable instanceof ProcessTask) {
        task = (ProcessTask) persistable;
    }
    String startDate = ProcessUtil.formatTime(task.getCreateTimestamp().getTime());
    String endDate = "";
    if (task.getEndDate() != null) {
        endDate = ProcessUtil.formatTime(task.getEndDate().getTime());
    }


    IBAUtility ibaUtility = new IBAUtility(task);
    String zzgynumber = ibaUtility.getIBAValue("PPNUMBER");
    if (zzgynumber == null) {
        zzgynumber = "";
    }
    String zzgyname = ibaUtility.getIBAValue("PPNAME");
    if (zzgyname == null) {
        zzgyname = "";
    }
    String zftype = ibaUtility.getIBAValue("PPTASKTYPE");
    if (zftype == null) {
        zftype = "";
    }
    String taskCreator = ibaUtility.getIBAValue("PPTASKCREATOR");
    if (taskCreator == null) {
        taskCreator = task.getCreatorFullName();
    }
    String cldePlanTime = ibaUtility.getIBAValue("cldePlanTime");
    if (cldePlanTime == null) {
        cldePlanTime = "";
    }
    if (!cldePlanTime.isEmpty()) {
        cldePlanTime = ProcessUtil.formatTime3(cldePlanTime);
    }
    String cldeEndTime = ibaUtility.getIBAValue("cldeEndTime");
    if (cldeEndTime == null) {
        cldeEndTime = "";
    }
%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.casc.process.resource.processRB"/>
<fmt:message var="name" key="<%=processRB.PROCESSTASK_NAME%>"/>
<fmt:message var="number" key="<%=processRB.PROCESSTASK_NUMBER%>"/>
<fmt:message var="version" key="<%=processRB.PROCESSTASK_VERSION%>"/>
<fmt:message var="tasktype" key="<%=processRB.PROCESSTASK_TASKTYPE%>"/>
<fmt:message var="taskstate" key="<%=processRB.PROCESSTASK_TASKSTATE%>"/>
<fmt:message var="startdate" key="<%=processRB.PROCESSTASK_STARTDATE%>"/>
<fmt:message var="enddate" key="<%=processRB.PROCESSTASK_ENDDATE%>"/>
<fmt:message var="cldePlanTime" key="<%=processRB.PROCESSTASK_CLDEPLANTIME%>"/>
<fmt:message var="cldeEndTime" key="<%=processRB.PROCESSTASK_CLDEENDTIME%>"/>
<fmt:message var="renwuyaoqiu" key="<%=processRB.PROCESSTASK_RENWUYAOQIU%>"/>
<fmt:message var="zhuzhichejian" key="<%=processRB.PROCESSTASK_ZHUZHICHEJIAN%>"/>
<fmt:message var="fuzhichejian" key="<%=processRB.PROCESSTASK_FUZHICHEJIAN%>"/>
<fmt:message var="zzgynumber" key="<%=processRB.PROCESSTASKITEM_ZZGYNUMBER%>"/>
<fmt:message var="zzgyname" key="<%=processRB.PROCESSTASKITEM_FZGYNUMBER%>"/>
<fmt:message var="zftype" key="<%=processRB.PROCESSTASKITEM_ZFTYPE%>"/>
<fmt:message var="taskCreator" key="<%=processRB.PROCESSTASKITEM_TASKCREATOR%>"/>
<fmt:message var="renwuyiju" key="<%=processRB.PROCESSTASKITEM_RENWUYIJU%>"/>
<fmt:message var="baobiaoleixing" key="<%=processRB.PROCESSTASKITEM_BBLX%>"/>
<table cellSpacing="7">
    <tr>
        <td><h4>${name}</h4></td>
        <td><%=task.getName() %>
        </td>
    </tr>
    <tr>
        <td><h4>${number}</h4></td>
        <td><%=task.getNumber() %>
        </td>
    </tr>
    <tr>
        <td><h4>${version}</h4></td>
        <td><%=task.getVersion() %>
        </td>
    </tr>
    <tr>
        <td><h4>${zhuzhichejian}</h4></td>
        <td><%=task.getZhuzhichejian() %>
        </td>
    </tr>
    <tr>
        <td><h4>${fuzhichejian}</h4></td>
        <td><%=task.getFuzhichejian() %>
        </td>
    </tr>

    <% if (ProcessConstants.TASK_TYPE_FZGYRW.equals(zftype)) {%>
    <tr>
        <td><h4>${zzgynumber}</h4></td>
        <td><%=zzgynumber %>
        </td>
    </tr>
    <tr>
        <td><h4>${zzgyname}</h4></td>
        <td><%=zzgyname %>
        </td>
    </tr>
    <% }%>

    <tr>
        <td><h4>${zftype}</h4></td>
        <td><%=zftype %>
        </td>
    </tr>
    <tr>
        <td><h4>${taskCreator}</h4></td>
        <td><%=taskCreator %>
        </td>
    </tr>

    <tr>
        <td><h4>${tasktype}</h4></td>
        <td><%=task.getTaskType() %>
        </td>
    </tr>
    <tr>
        <td><h4>${taskstate}</h4></td>
        <td><%=task.getTaskState() %>
        </td>
    </tr>
    <tr>
        <td><h4>${startdate}</h4></td>
        <td><%=startDate %>
        </td>
    </tr>
    <tr>
        <td><h4>${enddate}</h4></td>
        <td><%=endDate %>
        </td>
    </tr>
    <tr>
        <td><h4>${cldePlanTime}</h4></td>
        <td><%=startDate %>
        </td>
    </tr>
    <tr>
        <td><h4>${cldeEndTime}</h4></td>
        <td><%=cldePlanTime %>
        </td>
    </tr>

    <tr>
        <td><h4>${renwuyaoqiu}</h4></td>
        <td><%=task.getRenwuyaoqiu() %>
        </td>
    </tr>
    <tr>
        <td><h4>${renwuyiju}</h4></td>
        <td><%=task.getRenwuyiju() %>
        </td>
    </tr>
</table>

<%
    NmHTMLActionModel am = NmActionServiceHelper.service.getActionModel("custom_process_task_delete_actions", task);
    NmAction rejectTask = (NmAction) am.getActions().get(0);
    rejectTask.setButton(true);
    //completeTask.setEnabled(true);
    rejectTask.setEnabled(task.getTaskState().equals(ProcessConstants.TASK_STATE_JINGXINZHONG));
    actionBean.setAction(rejectTask);
    NmContext context = nmcontext.getContext();
    //context.pushElement(taskItem);
    NmAction.actionjsp(actionBean, linkBean, objectBean, localeBean, urlFactoryBean, nmcontext, sessionBean, out, request, response);
    context.popElement();
    actionBean.setAction(null);
%>

<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.ListPartForProcessTaskItemBuilder')}"/>

<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.ListAttachmentsTableBuilder')}"/>

<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.TaskItemRouteAndHistoryBuilder')}"/>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>