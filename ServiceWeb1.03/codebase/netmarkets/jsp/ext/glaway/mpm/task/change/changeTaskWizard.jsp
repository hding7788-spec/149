<%@ page language="java" pageEncoding="UTF-8" %>
<%@ page import="com.glaway.mpm.task.ui.TaskResource" %>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="com.glaway.mpm.task.util.TaskUtil"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.glaway.mpm.task.ui.TaskResource"/>
<fmt:message var="option" key="<%=TaskResource.PRIVATE_CONSTANT_54 %>"/>
<fmt:message var="oriChangeTime" key="<%=TaskResource.PRIVATE_CONSTANT_55 %>"/>
<fmt:message var="newChangeTime" key="<%=TaskResource.PRIVATE_CONSTANT_56 %>"/>
<fmt:message var="comments" key="<%=TaskResource.PRIVATE_CONSTANT_57%>"/>
<fmt:message var="worker" key="<%=TaskResource.PRIVATE_CONSTANT_58%>"/>
<fmt:message var="approver" key="<%=TaskResource.PRIVATE_CONSTANT_59%>"/>
<fmt:message var="group" key="<%=TaskResource.PRIVATE_CONSTANT_60%>"/>

<script type="text/javascript" src="netmarkets/javascript/components.js"></script>
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
		var url="netmarkets/jsp/glaway/mpm/task/search/searchUserOfTechGroup.jsp?para=" + encodeURIComponent(para);
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
			var design = document.getElementById("design");
			var reviewer = document.getElementById("reviewer");
			
			if(result.trim() == ""){
				alert("此组织下无任何人员，请联系管理员!");
				design.length = 0;
				reviewer.length = 0;
				design.options.add("", "");
				reviewer.options.add("", "");
			}else{
				var allUser = result.split(";");
				var allMember = allUser[1];
				
				design.length = 0;
				reviewer.length = 0;
				
				var everyMember = allMember.split("|");
				
				var MemberCount = everyMember.length;
				
				design.options.add(new Option("---选择人员---", ""));
				reviewer.options.add(new Option("---选择人员---", ""));
				
				for(var j = 0; j < MemberCount; j++){
					var memberM = everyMember[j];
					design.options.add(new Option(memberM.split("#")[1], memberM.split("#")[0]));
					reviewer.options.add(new Option(memberM.split("#")[1], memberM.split("#")[0]));
				}
			}
		}
	}
</script>

<%
	
	ArrayList<String> changeIn = new ArrayList<String>();
	ArrayList<String> changeOut = new ArrayList<String>();
	changeIn.add("");
	changeIn.add("time");
	changeIn.add("responsor");
	changeIn.add("stop");
		
	changeOut.add("-----请选择一个任务类型-----");
	changeOut.add("时间修改");
	changeOut.add("负责人修改");
	changeOut.add("终止任务");
		
	request.setAttribute("changeIn", changeIn);
	request.setAttribute("changeOut", changeOut);
	
	String oriTimeStr = "2013-3-2";
	pageContext.setAttribute("oriTimeStr", oriTimeStr);
	
	ArrayList<String> groupIn = TaskUtil.getTechnicGroups().get(0);
	ArrayList<String> groupOut = TaskUtil.getTechnicGroups().get(1);
	
	request.setAttribute("groupIn", groupIn);
	request.setAttribute("groupOut", groupOut);
	
	ArrayList<String> workerIn = new ArrayList<String>();
	ArrayList<String> workerOut = new ArrayList<String>();
	
	request.setAttribute("workerIn", workerIn);
	request.setAttribute("workerOut", workerOut);
	
	ArrayList<String> approverIn = new ArrayList<String>();
	ArrayList<String> approverOut = new ArrayList<String>();
	
	request.setAttribute("approverIn", approverIn);
	request.setAttribute("approverOut", approverOut);

%>
<table>
	<tr>
		<td>
			<div id="option">
				<jca:renderPropertyPanel>
					<w:comboBox propertyLabel="${option}" id="changeOption" name="changeOption" required="true" internalValues="${changeIn}" displayValues="${changeOut}" onchange="showdiv()"/>
				</jca:renderPropertyPanel>
			</div>
			
			<div id="timeOption" style="display:none">
				<jca:renderPropertyPanel>
					<w:textBox propertyLabel="${oriChangeTime}" id="oriTime" name="oriTime" value="${oriTimeStr}" editable="false"/>
					<w:dateInputComponent propertyLabel="${newChangeTime}" id="newTime" name="newTime" dateValueType="DATE_ONLY"></w:dateInputComponent>
					<w:textArea propertyLabel="${comments}" id="commentsTime" name="commentsTime" value="" cols="40" rows="5"/>
				</jca:renderPropertyPanel>
			</div>
			
			<div id="stopOption" style="display:none">
				<jca:renderPropertyPanel>
					<w:textArea propertyLabel="${comments}" id="commentsStop" name="commentsStop" value="" cols="40" rows="5"/>
				</jca:renderPropertyPanel>
			</div>
			
			<div id="responsorOption" style="display:none">
				<jca:renderPropertyPanel>
					<w:comboBox propertyLabel="${group}" id="group" name="group" internalValues="${groupIn}" displayValues="${groupOut}" onchange="getUserOfTechGroup();"></w:comboBox>
					<w:comboBox propertyLabel="${worker}" id="design" name="design" internalValues="${workerIn}" displayValues="${workerOut}"></w:comboBox>
					<w:comboBox propertyLabel="${approver}" id="reviewer" name="reviewer" internalValues="${approverIn}" displayValues="${approverOut}"></w:comboBox>
					<w:textArea propertyLabel="${comments}" id="commentsResponsor" name="commentsResponsor" value="" cols="40" rows="5"/>
				</jca:renderPropertyPanel>
			</div>
		</td>
	</tr>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	<tr>
		<td>
			注：所有需必填！
		</td>
	</tr>
</table>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>