<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.glaway.mpm.task.util.TaskUtil"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"
	prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="p" uri="http://www.ptc.com/windchill/taglib/picker"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.glaway.mpm.pbom.ui.PBOMResource"%>

<fmt:setLocale value="${localeBean.locale}" />
<fmt:setBundle basename="com.glaway.mpm.pbom.ui.PBOMResource" />
<fmt:message var="mpmTaskType" key="<%=PBOMResource.TASKTYPE %>" />
<fmt:message var="mpmTaskName" key="<%=PBOMResource.TASKNAME %>" />
<fmt:message var="mpmResponser" key="<%=PBOMResource.RESPONSER %>" />
<fmt:message var="mpmProfessionalGroup"
	key="<%=PBOMResource.PROFESSIONAL_GROUP %>" />
<fmt:message var="mpmPlanner" key="<%=PBOMResource.PLANNER %>" />
<fmt:message var="mpmAllocateTime" key="<%=PBOMResource.ALLOCATETIME%>" />
<fmt:message var="mpmProfessionalGroupSetTime"
	key="<%=PBOMResource.PROFESSIONALGROUPSETTIME %>" />
<fmt:message var="mpmComments" key="<%=PBOMResource.COMMENTS %>" />
<fmt:message var="mpmDeliverable" key="<%=PBOMResource.DELIVERABLE %>" />

<script type="text/javascript">
	var xmlHttpRequest;
	function getUserOfTechGroup(){
		var group = document.getElementById("group");
		search(group.value);
	}
	
	function createXMLHttpRequest(){
		if(window.ActiveXObject){
			return new ActiveXObject("Microsoft.XMLHTTP");
		}else if(window.XMLHttpRequest){
			return new XMLHttpRequest();
		}
	}
	
	function search(para){
		var url="netmarkets/jsp/glaway/mpm/task/search/searchItemResponsor.jsp?para=" + encodeURIComponent(para);
		xmlHttpRequest = createXMLHttpRequest();
		xmlHttpRequest.onreadystatechange=callbackRole;
		
		xmlHttpRequest.open("GET", url, true);
		xmlHttpRequest.setRequestHeader( "Content-Type", "text/html;charset=UTF-8" );
		xmlHttpRequest.setRequestHeader("If-Modified-Since","0");
		
		xmlHttpRequest.send(null);
	}
	
	function callbackRole(){
		if(xmlHttpRequest.readyState==4&&xmlHttpRequest.status==200){
			var result=xmlHttpRequest.responseText;
			var responsor = document.getElementById("responsor");
			
			if(result.trim() == ""){
				alert("此组织下无任何人员，请联系管理员!");
				responsor.length = 0;
				responsor.options.add("", "");
			}else{
				var allUser = result.split("|");
				var leaderCount = allUser.length;
				responsor.length = 0;
				responsor.options.add(new Option("---选择人员---", ""));
				
				for(var i = 0; i < leaderCount; i++){
					var leaderM = allUser[i];
					responsor.options.add(new Option(leaderM.split("#")[1], leaderM.split("#")[0]));
				}
			}
		}
	}
	
</script>

<%
	ArrayList<String> itemTaskIn = TaskUtil.getItemType().get(0);
	ArrayList<String> itemTaskOut = TaskUtil.getItemType().get(1);
	request.setAttribute("itemTaskIn", itemTaskIn);
	request.setAttribute("itemTaskOut", itemTaskOut);

	ArrayList<String> responsorIn = new ArrayList<String>();
	ArrayList<String> responsorOut = new ArrayList<String>();
	request.setAttribute("responsorIn", responsorIn);
	request.setAttribute("responsorOut", responsorOut);

	ArrayList<String> groupIn = TaskUtil.getTechnicGroups().get(0);
	ArrayList<String> groupOut = TaskUtil.getTechnicGroups().get(1);
	request.setAttribute("groupIn", groupIn);
	request.setAttribute("groupOut", groupOut);

	WTPrincipal prin = SessionHelper.manager.getPrincipal();
	String currentUser = ((WTUser) prin).getFullName();
	String user = ((WTUser) prin).getName().trim();
	request.setAttribute("currentUser", currentUser);
	System.out.println("currentUser====>" + currentUser);
%>

<body bgcolor="#E6E6FA">
	<p align="center" style="font-size: 30px; font-weight: bold">
		新建条目任务
	</p>

	<jca:renderPropertyPanel>
		<w:comboBox propertyLabel="${mpmTaskType}" id="taskType"
			name="taskType" required="true" internalValues="${itemTaskIn}"
			displayValues="${itemTaskOut}" />
			
		<w:textBox propertyLabel="${mpmTaskName}" id="taskName"
			name="taskName" required="true" maxlength="20"></w:textBox>
			
		<w:comboBox propertyLabel="${mpmProfessionalGroup}" id="group"
			name="group" required="true" internalValues="${groupIn}"
			displayValues="${groupOut}" onchange="getUserOfTechGroup()"/>

		<w:comboBox propertyLabel="${mpmResponser}" id="responsor"
			name="responsor" internalValues="${responsorIn}"
			displayValues="${responsorOut}" required="true"></w:comboBox>
			
		<w:radioButton propertyLabel="${mpmDeliverable}" name="deliverable"
			label="PDS归档" value="PDSRelease" checked="true" />
		<w:radioButton name="deliverable" label="不归档" value="NORelease" />
		<w:radioButton name="deliverable" label="无" value="NO" />

		<w:label propertyLabel="${mpmPlanner}" value="${currentUser}" />
		
		<w:dateInputComponent propertyLabel="${mpmAllocateTime}"
			id="allocateTime" name="allocateTime" dateValueType="DATE_ONLY"></w:dateInputComponent>
			
		<w:dateInputComponent propertyLabel="${mpmProfessionalGroupSetTime}"
			id="setTime" name="setTime" dateValueType="DATE_ONLY"></w:dateInputComponent>
			
		<w:textArea propertyLabel="${mpmComments}" id="comments"
			name="comments" value="" cols="39" rows="5" required="false" />
	</jca:renderPropertyPanel>
</body>
