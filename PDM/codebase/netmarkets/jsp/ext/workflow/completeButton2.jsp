<%@ page import="java.util.HashMap,
                 com.ptc.netmarkets.util.misc.NmAction,
                 com.ptc.netmarkets.util.misc.NmActionServiceHelper,
                 com.ptc.netmarkets.work.NmWorkItem ,
				 com.ptc.netmarkets.work.NmWorkItemCommands,
				 com.ptc.netmarkets.model.NmNamedObject,
				 wt.workflow.worklist.worklistResource,
				 wt.util.WTMessage,
				 java.util.Locale,
				 com.ptc.netmarkets.util.misc.NmHTMLActionModel,
				 com.ptc.netmarkets.util.misc.NmContext" 
%>
<%@ page import="ext.casc.workflow.TaskConfigrationHelper"%>

<%
	String workflowOid = request.getParameter("oid");
	Boolean flag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(workflowOid, "setupParticipants");
	boolean isSetupParticipant = false;
	boolean needComments = false;
	if(flag != null){
		isSetupParticipant = flag;
	}else{
		isSetupParticipant = false;
	}
	
	 flag = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(workflowOid, "needComments");
	 if(flag != null){
		needComments = flag;
	}else{
		needComments = false;
	}
%>

<jsp:directive.page import="com.ptc.core.components.rendering.GuiComponent"/>
<jsp:directive.page import="ext.ases.workflow.workflowtask.CmWfTaskProcessorCommands"/>
<jsp:directive.page import="com.ptc.core.components.rendering.RenderingContext"/>

<SCRIPT LANGUAGE="JavaScript">
<!--
function validateAdhocAssignee() {
    var shouldSubmitResponse = false;
	var pathname = window.location.pathname;
    var splitPathname = pathname.split("/");
    var wcHome = window.location.protocol + "//" + window.location.host + "/" + splitPathname[1] + "/";
    var jspName = 'netmarkets/jsp/workitem/validateAdhocAssignee.jsp';
    var fullURL = wcHome + jspName;

    // want to get no. of elements from the tbody rows
    var tbody = document.getElementById('tb__Adhoc_Act_Id');
    var noOfAssignee = tbody.getElementsByTagName('tr').length;
    var selection = '';
	var adhocAssigneeCount = 0;
    
	for (var i = 0; i < noOfAssignee; i++)
    {
        var selectedAssignee = document.getElementsByName("___AD_HOC_ASSIGNEE" + i +"___textbox")[0].value;
        if (selectedAssignee.length > 0) {
        	adhocAssigneeCount++;
            selection += selectedAssignee;
			
			if(selection.length > 0 && i < noOfAssignee)
				selection += ',';
        }
    }
    
	if(adhocAssigneeCount > 0)
    {
    	selection = selection.substring(0, selection.length-1);
    }

	if(adhocAssigneeCount==0)
	{	
		alert('Atleast 1 Assignee is needed to start activity.');
		return shouldSubmitResponse;
	}

	var loader = new ajax.ContentLoader(fullURL, null, null, 'POST', '&adhocAssignees='+selection+'&oid='+getParamFromQueryString('oid'));
    var resText = null;
	if (loader.req.readyState == 4 && loader.req.status == 200) {
        resText = loader.req.responseText;
		
		if(resText == null || resText.length <= 0){
			shouldSubmitResponse = true;
		}
		else {
			alert(resText); 
		}
    }
    return shouldSubmitResponse;
}

function getParamFromQueryString(paramToGet) {
	var hostUrl = window.location.search.substring(1);
	var paramValue = new Array();
	var paramValuePairs = hostUrl.split("&");

	for (i=0;i<paramValuePairs.length;i++) {
		paramValue = paramValuePairs[i].split("=");
		if (paramValue[0] == paramToGet) {
			var valueToReturn = paramValue[1];
			valueToReturn = valueToReturn.replace(/%3A/g, ':');
			return valueToReturn;
		}
	}
}
//-->
</SCRIPT>
	
<jsp:useBean id="modelBean" class="com.ptc.netmarkets.util.beans.NmModelBean" scope="request"/>

<jsp:useBean id="localeBean" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>
<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request"/>
<jsp:useBean id="urlFactoryBean" class="com.ptc.netmarkets.util.beans.NmURLFactoryBean"  scope="request"/>


<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean" scope="request"/>
<jsp:useBean id="actionBean" class="com.ptc.netmarkets.util.beans.NmActionBean" scope="request"/>
<jsp:useBean id="objectBean" class="com.ptc.netmarkets.util.beans.NmObjectBean" scope="request"/>

<jsp:useBean id="checkBoxBean" class="com.ptc.netmarkets.util.beans.NmCheckBoxBean"  scope="request"/>
<jsp:useBean id="textBoxBean"  class="com.ptc.netmarkets.util.beans.NmTextBoxBean"  scope="request"/>
<jsp:useBean id="radioButtonBean" class="com.ptc.netmarkets.util.beans.NmRadioButtonBean"  scope="request"/>
<jsp:useBean id="textAreaBean" class="com.ptc.netmarkets.util.beans.NmTextAreaBean"  scope="request"/>
<jsp:useBean id="comboBoxBean" class="com.ptc.netmarkets.util.beans.NmComboBoxBean"  scope="request"/>
<jsp:useBean id="dateBean"    class="com.ptc.netmarkets.util.beans.NmDateBean"    scope="request"/>
<jsp:useBean id="stringBean" class="com.ptc.netmarkets.util.beans.NmStringBean"  scope="request"/>
<jsp:useBean id="linkBean"    class="com.ptc.netmarkets.util.beans.NmLinkBean"    scope="request"/>
<jsp:useBean id="sessionBean" class="com.ptc.netmarkets.util.beans.NmSessionBean" scope="request"/>	
	           
<%
  nmcontext.setRequest(request);
  nmcontext.setResponse(response);
  nmcontext.adjustContext(request);
  com.ptc.netmarkets.util.beans.NmCommandBean cb = new com.ptc.netmarkets.util.beans.NmCommandBean();
  cb.setCompContext(nmcontext.getContext().toString());
  cb.setRequest(request);
  cb.setSessionBean(sessionBean);
  NmWorkItem myNmWorkItem = (NmWorkItem) NmWorkItemCommands.view(cb);%>                
  
  <%!
  private static final String RESOURCE = "wt.workflow.worklist.worklistResource";
  private static final String WORK_RESOURCE = "com.ptc.netmarkets.work.workResource";

  %>
<br>               
<div id="completButtonId">                  
<table class="pp" cellspacing="1" cellpadding="1" height="5">
<tr  class="basefont" align="right" height="5">

<td align="center" height="5">
<%
Locale locale = localeBean.getLocale();
response.setCharacterEncoding("UTF-8");
	NmHTMLActionModel am = null;
	 String show= request.getParameter("showAdhocComponent");

	if(!myNmWorkItem.isAdhocActivity() || !(show !=null && show.equals("table")) )
	{
		if(myNmWorkItem.isAdhocActivity() && myNmWorkItem.isAdhocInProgress() )
		{
		
  				    out.println (new WTMessage (RESOURCE, worklistResource.AD_HOC_IN_PROGRESS, null).getLocalizedMessage (locale));
					out.println("<br></br>");
		}
		else 
		{
			am = NmActionServiceHelper.service.getActionModel("complete workitem",myNmWorkItem);
			 NmAction completeTask = (NmAction)am.getActions().get(0);
			
			completeTask.setButton(true);
			//completeTask.setEnabled(true);
			completeTask.setEnabled((!myNmWorkItem.isCompleted() && !myNmWorkItem.isSuspended() && myNmWorkItem.isMine()));//
			actionBean.setAction(completeTask);
			NmContext context = nmcontext.getContext();
			context.pushElement(myNmWorkItem);
			NmAction.actionjsp( actionBean,  linkBean,  objectBean,  localeBean,  urlFactoryBean,nmcontext,  sessionBean,  out,  request,  response);
			context.popElement();
			actionBean.setAction(null);
		}
	}
	else
	{
		
		am = NmActionServiceHelper.service.getActionModel("start Activities",myNmWorkItem);
		NmAction startTask = (NmAction)am.getActions().get(0);
		startTask.setButton(true);
		startTask.setEnabled((!myNmWorkItem.isCompleted() && !myNmWorkItem.isSuspended() && myNmWorkItem.isMine()));
		actionBean.setAction(startTask);
		NmContext context1 = nmcontext.getContext();
		context1.pushElement(myNmWorkItem);
		NmAction.actionjsp( actionBean,  linkBean,  objectBean,  localeBean,  urlFactoryBean,nmcontext,  sessionBean,  out,  request,  response);
		context1.popElement();
		actionBean.setAction(null);
	}
%>
</td><td align="center" height="5">
<%
if(!myNmWorkItem.isAdhocInProgress())
{
	
if (myNmWorkItem.isProjectWorkItem() && !myNmWorkItem.isAdhocActivity()) {
		am = NmActionServiceHelper.service.getActionModel("update workitem",myNmWorkItem);
		NmAction updateTask = (NmAction)am.getActions().get(0);
		updateTask.setButton(true);
		updateTask.setIcon(null);
		updateTask.setEnabled((!myNmWorkItem.isCompleted() && !myNmWorkItem.isSuspended() && myNmWorkItem.isMine()));
		//boolean adminTrackCostOverride=;
		//if (adminTrackCostOverride) updateTask.setEnabled(true);
		actionBean.setAction(updateTask);
		NmContext context = nmcontext.getContext();
		context.pushElement(myNmWorkItem);
		NmAction.actionjsp( actionBean,  linkBean,  objectBean,  localeBean,  urlFactoryBean,nmcontext,  sessionBean,  out,  request,  response);
		context.popElement();
		actionBean.setAction(null);
	}else {
		

		am = NmActionServiceHelper.service.getActionModel("save workitem",myNmWorkItem);
		NmAction saveTask = (NmAction)am.getActions().get(0);
		saveTask.setButton(true);
		//saveTask.setEnabled((!myNmWorkItem.isCompleted() && !myNmWorkItem.isSuspended() && myNmWorkItem.isMine()));
		saveTask.setEnabled(false);
		actionBean.setAction(saveTask);
		NmContext context2 = nmcontext.getContext();
		context2.pushElement(myNmWorkItem);
		NmAction.actionjsp( actionBean,  linkBean,  objectBean,  localeBean,  urlFactoryBean,nmcontext,  sessionBean,  out,  request,  response);
		context2.popElement();
		actionBean.setAction(null);
	}
}
%>
<%
System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%  Working  %%%%%%%%%%%%%%%%%%%%%");
GuiComponent guiComponentInitJSAction = CmWfTaskProcessorCommands.initJSAction(needComments);
guiComponentInitJSAction.draw(out, new RenderingContext());
 %>
 
 
 <input type="hidden" name="completehidden" id="completehidden" onclick="" />
<script type="text/javascript">
var isSetupParticipant = '<%=isSetupParticipant%>';
// 替换原有的任务完成按钮
function replaceTaskCompleteButton3() {
		if(isSetupParticipant == "true"){
			var completeBtn = document.getElementsByName("complete")[0];
    	var completehiddenBtn = document.getElementsByName("completehidden")[0];
    	if (completeBtn&&completehiddenBtn) {    	
        //completeBtn.oldOnClick = completeBtn.onclick;
        completehiddenBtn.onclick = completeBtn.onclick;
    		completeBtn.onclick = validata;    			
    	}
		}
}      	

replaceTaskCompleteButton3();

	function replaceTaskCompleteButton4() {
		var completehidden2 = document.getElementsByName("completehidden2")[0];
    var completehidden = document.getElementsByName("completehidden")[0];
    if(completehidden2){
   		if (completehidden) {    	
    		completehidden2.onclick = completehidden.onclick;
    		completehidden.onclick = setPartContainer;    			
    	}
    }
	}      	

replaceTaskCompleteButton4();

	function replaceTaskCompleteButton5() {
		var completehidden3 = document.getElementsByName("completehidden3")[0];
    var completehidden2 = document.getElementsByName("completehidden2")[0];
    var completehiddenBtn = document.getElementsByName("completehidden")[0];
    var completeBtn = document.getElementsByName("complete")[0];
    if(completehidden3){
   		if (completehidden2) {    	
    		completehidden3.onclick = completehidden2.onclick;
    		completehidden2.onclick = setPhaseCode;
    	} else if (completehiddenBtn) {
    		completehidden3.onclick = completehiddenBtn.onclick;
    		completehiddenBtn.onclick = setPhaseCode;
    	} else if (completeBtn) {
    		completehidden3.onclick = completeBtn.onclick;
    		completeBtn.onclick = setPhaseCode;
    	}
    }
	}      	

replaceTaskCompleteButton5();

	function replaceTaskCompleteButton6() {
		var completehidden6 = document.getElementsByName("completehidden6")[0];
    var completehidden = document.getElementsByName("completehidden")[0];
    if(completehidden6){
   		if (completehidden) {    	
    		completehidden6.onclick = completehidden.onclick;
    		completehidden.onclick = setOuterdocProduct;    			
    	}
    }
	}        	

replaceTaskCompleteButton6();


</script>
</td>
</table>
</div>