<%@page import="wt.workflow.engine.WfActivity"%>
<%@page import="wt.workflow.engine.WfProcess"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.util.WTRuntimeException"%>
<%@page import="wt.util.WTException"%>
<%@page import="wt.workflow.work.WorkItem"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/workItem" prefix="workItem"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="ext.casc.workflow.TaskConfigrationHelper"%>
<%@ page import="ext.casc.workflow.WorkflowHelper"%>
<%@ page import="wt.org.WTUser, wt.session.SessionHelper"%>
<%@ page import="wt.fc.Persistable" %>
<%@ page import="ext.ases.envelope.ProcessEnvelope" %>
<%@ page import="ext.casc.workflow.tree.GenerateJson" %>
<%
	String oid = request.getParameter("oid");
	WTUser user = (WTUser)SessionHelper.getPrincipal();
	String currentUserName = user.getName();
	boolean isComplete = false;
    boolean isPreview = false;
	ReferenceFactory ref = new ReferenceFactory();
	try {
		WorkItem wi = (WorkItem) ref.getReference(oid).getObject();
		isComplete = wi.isComplete();
        Persistable persistable = wi.getPrimaryBusinessObject().getObject();
		if(persistable != null && persistable instanceof ProcessEnvelope) {
			if(GenerateJson.isPreviewPkg(persistable)){
				isPreview = true;
			}
		}
	} catch (WTRuntimeException e) {
		e.printStackTrace();
	} catch (WTException e) {
		e.printStackTrace();
	}

	Boolean configFlag = (Boolean) TaskConfigrationHelper.getActivityVariableByVarName(oid, "isShowSignature");
	boolean isShowSignature = false;
	if (configFlag != null) {
		isShowSignature = configFlag;
	}

	configFlag = (Boolean) TaskConfigrationHelper.getActivityVariableByVarName(oid, "setSignature");
	boolean setSignature = false;
	boolean setSignature2 = false;
	if (configFlag != null) {
		setSignature = configFlag;
	} else {
		configFlag = (Boolean) TaskConfigrationHelper.getActivityVariableByVarName(oid, "setSignature2");
		if (configFlag != null) {
			setSignature2 = configFlag;
		}
	}

	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid,"setSignatureImplement");
    boolean setSignatureImplement = false;
    if(configFlag != null){
		setSignatureImplement = configFlag;
	}

	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid,"zpGYY");
    boolean zpGYY = false;
    if(configFlag != null){
		zpGYY = configFlag;
	}

	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid,"isShowEcnChangeBeforeBuilder");
    boolean isShowEcnChangeBeforeBuilder = false;
    if(configFlag != null){
		isShowEcnChangeBeforeBuilder = configFlag;
	}

	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid,"isShowSignatureImplement");
    boolean isShowSignatureImplement = false;
    if(configFlag != null){
		isShowSignatureImplement = configFlag;
	}

	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid,"showPreviewPage");
    boolean showPreviewPage = false;
    if(configFlag != null){
		showPreviewPage = configFlag;
	}

	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid,"setReleaseSignature");
    boolean setReleaseSignature = false;
    if(configFlag != null){
		setReleaseSignature = configFlag;
	}

	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid,"showReleaseSignature");
    boolean showReleaseSignature = false;
    if(configFlag != null){
		showReleaseSignature = configFlag;
	}

	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid, "setupParticipants");
	boolean isSetupParticipant = false;
	if(configFlag != null){
		isSetupParticipant = configFlag;
	}

	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid, "isOutSign");
	boolean isOutSign = false;
	if(configFlag != null){
		isOutSign = configFlag;
	}

	String selectUsersValue = (String)TaskConfigrationHelper.getActivityVariableByVarName(oid, "selectUsersValue");


	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid, "showPBOAttachment");
	boolean showPBOAttachment = false;
	if(configFlag != null){
		showPBOAttachment = configFlag;
	}

	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid, "showReivewObj");
	boolean showReivewObj = false;
	if(configFlag != null){
		showReivewObj = configFlag;
	}

	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid, "setGongShiDingE");
	boolean setGongShiDingE = false;
	if(configFlag != null){
		setGongShiDingE = configFlag;
	}

	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid,"setPrintDistributionture");
	boolean setPrintDistributionture = false;
	if(configFlag != null){
		setPrintDistributionture = configFlag;
	}

	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid,"showChangeRecoverInfo");
	boolean showChangeRecoverInfo = false;
	if(configFlag != null){
		showChangeRecoverInfo = configFlag;
	}

	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid,"showReceiveInfo");
	boolean showReceiveInfo = false;
	if(configFlag != null){
		showReceiveInfo = configFlag;
	}

	configFlag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid,"isNotice");
	boolean isNotice = false;
	if(configFlag != null){
		isNotice = configFlag;
	}
%>


<workItem:MyWorkItem />
<c:if test="${myWorkItem != null}">

	<%-- There are two steps to render a custom activity variable on the task page.

1) Place the name of the activity variable that you want to display on the
page in the comma delimited custom_variables list.

Height and/or width can be specified using CSS style syntax.
For example "variable_name{height:1;width:2}".
Specifying "all_activity_variables" will display all visible activity
variables not named "special_instructions", or "instructions".

2) To specify where to render the particular variable on the page you must add
the following line.

<tags:taskPanelValue propertyModel="${propertyModel}" attrs="variable_name"/>

Note: by default all_activty_variables are rendered.  If you add a custom
activity variable and have all_activity_variables the variable will show up twice.

--%>

	<tags:workItemInfo custom_variables="all_activity_variables" />

	<BR>
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<td rowspan="3" width="12"><IMG SRC="netmarkets/images/sp.gif" height="1" width="12"></td>
			<td rowspan="3" class="tableborderbg" width="1"><IMG SRC="netmarkets/images/sp.gif" height="1" width="1"></td>
			<td class="tableborderbg"><IMG SRC="netmarkets/images/sp.gif" height="1" width="1"></td>
			<td rowspan="3" class="tableborderbg" width="1"><IMG SRC="netmarkets/images/sp.gif" height="1" width="1"></td>
			<td rowspan="3" width="12"><IMG SRC="netmarkets/images/sp.gif" height="1" width="12"></td>
		</tr>
		<tr class="detailsboxbg">
			<td>

				<table cellpadding="0" cellspacing="0" border="0" align="right" width=100%>
					<tr>
						<td>&nbsp;</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr class="detailsboxbg">
			<td>
				<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td><tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_detailaction" /></td>
						<td width="12">
                            <IMG SRC="netmarkets/images/sp.gif" height="1" width="12">
                        </td>
						<td>
                            <font class="projectnamefont">
                            <IMG SRC="netmarkets/images/open_work.gif">
                            <tags:taskPanelValue propertyModel="${propertyModel}" attrs="workitem_activityname" />
                        </td>
					</tr>
				</table>
				<div align="left">
					<table border="0" cellpadding="1" width=100%>
						<!-- Task Information Section -->
						<tr>
							<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_instructions" />
						</tr>
						<tr>
						<tr>
							<td align="right" valign="top" nowrap>
                                <FONT class=tabledatafont>
                                    <tags:taskPanelLabel propertyModel="${propertyModel}" attrs="workitem_processname" />
							    </FONT>
                            </td>
							<td valign="top">
                                <FONT class=tabledatafont>
                                        <tags:taskPanelValue propertyModel="${propertyModel}" attrs="workitem_lightweightprocessmonitor" />
                                        <tags:taskPanelValue propertyModel="${propertyModel}" attrs="workitem_processname" />
                                </FONT>
                            </td>
						</tr>

						<tr>
							<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_processinitiator" />
						</tr>


						<tr>
							<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_assignee" />
						</tr>
						<tr>
							<td align="right" valign="top" nowrap>
                                <FONT class=tabledatafont>
                                    <tags:taskPanelLabel propertyModel="${propertyModel}" attrs="workitem_role" />
							    </FONT>
                            </td>
							<td valign="top">
                                <FONT class=tabledatafont>
                                    <tags:taskPanelValue propertyModel="${propertyModel}" attrs="workitem_role" />
							    </FONT>
                            </td>
						</tr>
						<tr>
							<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_priority" />
						</tr>
						<tr>
							<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_deadline" />
						</tr>
						<tr>
							<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_pbolink" />
						</tr>
<%
		Object obj=new ReferenceFactory().getReference(oid).getObject();
		if(obj instanceof WorkItem){
			WorkItem item=(WorkItem)obj;
			WfProcess process=null;
			WfActivity activity=(WfActivity)item.getSource().getObject();
			process=(WfProcess)activity.getParentProcessRef().getObject();
			//WTContainer wfcont=(WTContainer)activity.getParentProcessRef().getObject();
			//if(wfcont instanceof WfBlock){
			//	process=((WfBlock)wfcont).getParentProcess();
			//}else if(){
			//	
			//}else{
			//	process=(WfProcess)wfcont;
			//}
			String templateName=process.getTemplate().getName();
			if(templateName.contains("149���ݽ������뵥��������")){
				%>
				<tr>
					<td></td><td><jca:action actionName="view" actionType="borrow" button="true" /></td>
				</tr>
				<% 
			}
		}
	
	%>
						<tr>
							<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_state" />
						</tr>

						<tags:taskPanelValue propertyModel="${propertyModel}" attrs="all_activity_variables" />

						<tr>
							<td valign="middle" colspan="3">
                                <FONT class=wizardbuttonfont><tags:adhocAct /></FONT>
                            </td>
						</tr>

						<tr>
							<td valign="middle" colspan="3"><hr size="1" width="100%"></td>
						</tr>
						<%if(isSetupParticipant) {%>
						<tags:customWorkItemActions />
						<%}else{ %>
						<tags:workItemActions />
						<%} %>

						<%
					    	if(isOutSign && !isComplete){
					    %>
						<tags:custSetOutSignInfo />
						<%
							}
						%>
						<tr>
							<td valign="middle" colspan="3"><hr size="1" width="100%"></td>
						</tr>
						<!-- task completion section -->

						<tr>
							<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_esignature" />
						</tr>

						<tr>
							<td></td>
							<td></td>
							<%if(isSetupParticipant) {%>
							<td valign="middle" align="left">
                                <FONT class=wizardbuttonfont>
                                    <jsp:include page="/netmarkets/jsp/ext/workflow/completeButton.jsp" />
                                </FONT>
                            </td>
							<%}else{%>
							<td valign="middle" align="left">
                                <FONT class=wizardbuttonfont>
                                    <jsp:include page="/netmarkets/jsp/customtemplates/completeButton.jsp" />
                                </FONT>
                            </td>
							<%} %>

						</tr>
					</table>


					<%
	 					if (isNotice) {
					%>
					<table>
						<tr>
							<td><%@ include file="/netmarkets/jsp/ext/ases/envelope/NewenvelopeObjects.jsp"%></td>
						</tr>
					</table>
					<%
						}
					%>
				</div>
			</td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td class="tableborderbg"><IMG SRC="netmarkets/images/sp.gif" height="1" width="1"></td>
		</tr>
	</table>
	<%
    	if(isOutSign){
    %>
	<table>
		<tr>
			<td><jsp:include page="${mvc:getComponentURL('ext.casc.workflow.tree.mvc.builder.ViewOutSignTableBuilder')}" /></td>
		</tr>
	</table>
	<%
		}
	%>


	<%
		if (setSignature) {
	%>
	<table>
		<tr>
			<td><%@ include file="/netmarkets/jsp/ext/workflow/tree/setSignature.jsp"%></td>
		</tr>
	</table>
	<%
		}
	 if (setSignature2) {
	%>
	<table>
		<tr>
			<td><%@ include file="/netmarkets/jsp/ext/workflow/tree/setSignature2.jsp"%></td>
		</tr>
	</table>
	<%
		}
	%>

    <%
        if(setSignatureImplement) {
            if(isPreview) {
    %>
    <table>
        <tr>
            <td>
            <td>
                <jsp:include page="/netmarkets/jsp/ext/workflow/tree/setPreviewImplement.jsp"/>
            </td>
            </td>
        </tr>
    </table>
    <%
    } else {
    %>
    <table>
        <tr>
            <td>
            <td>
                <%@ include file="/netmarkets/jsp/ext/workflow/tree/setSignatureImplement.jsp" %>
            </td>
            </td>
        </tr>
    </table>
    <%
            }
        }
    %>

    <%
        if(zpGYY) {
            if(isPreview) {
    %>
    <table>
        <tr>
            <td>
            <td>
                <jsp:include page="/netmarkets/jsp/ext/workflow/tree/setListPreviewZP.jsp"/>
            </td>
            </td>
        </tr>
    </table>
    <%
    } else {
    %>
    <table>
        <tr>
            <td>
            <td>
                <%@ include file="/netmarkets/jsp/ext/workflow/tree/setListSignatureZP.jsp" %>
            </td>
            </td>
        </tr>
    </table>
    <%
            }
        }
    %>


	<%
		if (isShowSignature) {
	%>
	<table>
		<tr>
			<td><%@ include file="/netmarkets/jsp/ext/workflow/tree/showSignature.jsp"%></td>
		</tr>
	</table>
	<%
		}
	%>

	<%
	if(isShowSignatureImplement){
%>
	<table>
		<tr>
			<td><%@ include file="/netmarkets/jsp/ext/workflow/tree/showSignatureImplement.jsp"%></td>
		</tr>
	</table>
	<%
	}
%>


	<%
	if(isShowEcnChangeBeforeBuilder){
%>
	<table>
		<tr>
			<td><%@ include file="/netmarkets/jsp/ext/workflow/tree/showECNChangeBeforeBuilder.jsp"%></td>
		</tr>
	</table>
	<%
}
%>

	<%
	if(showPreviewPage){
%>
	<table>
		<tr>
			<td><%@ include file="/netmarkets/jsp/ext/workflow/tree/setPreviewSignature.jsp"%></td>
		</tr>
	</table>
	<%
}
%>


	<%
	if (setReleaseSignature) {
%>
	<table>
		<tr>
			<td><%@ include file="/netmarkets/jsp/ext/workflow/tree/setReleaseListSignature.jsp"%></td>
		</tr>
	</table>
	<%
	}
%>

	<%
	if(showReleaseSignature) {
%>
	<table>
		<tr>
			<td><jsp:include page="${mvc:getComponentURL('ext.casc.workflow.tree.mvc.builder.ShowReleaseListSignatureBuilder')}" /></td>
		</tr>
	</table>

	<%
	}
%>

	<!-- add by lkc -->
	<%
		if(showReceiveInfo){
	%>
	<table>
		<tr>
			<td><jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.print.ShowReceiveInfoBuilder')}" /></td>
		</tr>
	</table>
	<%
		}
	%>

	<!-- add by lkc -->
	<%
		if(showChangeRecoverInfo){
	%>
	<table>
		<tr>
			<td><jsp:include page="${mvc:getComponentURL('ext.casc.workflow.doc.mvc.builder.ShowChangeRecoverInfoBuilder')}" /></td>
		</tr>
	</table>
	<%
		}
	%>

	<%
if(showReivewObj){
%>
	<table>
		<tr>
			<td><%@ include file="/netmarkets/jsp/ext/workflow/doc/releaseSetListSignature.jsp"%></td>
		</tr>
	</table>
	<% }%>

	<%if(setGongShiDingE){ %>
	<table>
		<tr>
			<td><jsp:include page="${mvc:getComponentURL('ext.casc.workflow.tree.mvc.builder.SetListGongShiDingEBuilder')}" /></td>
		</tr>
	</table>
	<%} %>

	<%
		if (setPrintDistributionture) {
	%>
	<table>
		<tr>
			<td><%@ include file="/netmarkets/jsp/ext/workflow/tree/setPrintDistributiontrue.jsp"%></td>
		</tr>
	</table>
	<%
		}
	%>

	<table>
		<tr>
			<td><jsp:include page="${mvc:getComponentURL('ext.casc.workflow.tree.mvc.builder.WorkflowPboAttachmentsTableBuilder')}" /></td>
		</tr>
	</table>

	<!-- PBO Info -->

	<table border="0" cellpadding="3" width=100%>
		<tr>
			<td colspan="3">&nbsp; <!-- show the entire routing history & reassignment history tables -->
				<tags:routingStatus dispProcess="ALL" /> <!-- show the reassignment history within a table -->
				<!-- tags:reassignHistory showRH="table"/ --> <!-- displayType options are "table" or "link".  This tag only works when PBO implements interface SubjectOfNotebook -->
				<workItem:notebook displayType="table" /> <!-- displayType options are "table" or "link".  This tag only works when PBO implements interface SubjectOfForum -->
				<workItem:discussions displayType="table" /></td>
		</tr>
	</table>

	<BR>
</c:if>
<script>
	<%
		if(selectUsersValue!=null&&!currentUserName.equals("805")){
	%>
    var bohui =  document.getElementsByName("workitem$taskFormTemplate$<%=oid%>$___WfUserEvent0___radio");
	if (bohui[2]) {
		bohui[2].disabled = true;
	}
<%}
	%>

</script>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
