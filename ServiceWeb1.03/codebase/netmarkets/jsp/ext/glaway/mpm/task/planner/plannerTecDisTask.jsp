<%@ page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.glaway.mpm.task.util.TaskUtil"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"
	prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>

<link href="netmarkets/jsp/ext/numbergen/css/nmstyles.css" rel="stylesheet" type="text/css">

<%@ include file="/netmarkets/jsp/util/begin.jspf" %>

<script type="text/javascript">
	var xmlHttpRequest;
	
	function getLeader(){
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
	
		var url="netmarkets/jsp/glaway/mpm/task/search/searchResponsor.jsp?para=" + encodeURIComponent(para);
		xmlHttpRequest = createXMLHttpRequest();
		
		xmlHttpRequest.onreadystatechange=callbackRole;
		
		xmlHttpRequest.open("GET",url,true);
		xmlHttpRequest.setRequestHeader( "Content-Type", "text/html;charset=UTF-8" );
		xmlHttpRequest.setRequestHeader("If-Modified-Since","0");
		
		xmlHttpRequest.send(null);
	}
	
	function callbackRole(){
		if(xmlHttpRequest.readyState==4&&xmlHttpRequest.status==200){
         	var result=xmlHttpRequest.responseText;
         	var leader = document.getElementById("respon");
         	var member = document.getElementById("personer");
         	
         	if(result.trim() == ""){
         		alert("此组织下无任何人员，请联系管理员!");
         		leader.length = 0;
         		leader.options.add("","");
         		
         		member.length = 0;
         		member.options.add("", "");
         	}else{
         		var everyUserObj = result.split(";");

         		leader.length = 0;
         		member.length = 0;
         		
         		var leaderUser = everyUserObj[0].split("|");
         		var memberUser = everyUserObj[1].split("|");
         		
         		var leaderCount = leaderUser.length;
         		var memberCount = memberUser.length;
         		
         		leader.options.add(new Option("---选择人员---", ""));
         		member.options.add(new Option("---选择人员---", ""));
         		
         		for(var i = 0; i < leaderCount; i++){
         			var everyLeader = leaderUser[i];
         			leader.options.add(new Option(everyLeader.split("#")[1], everyLeader.split("#")[0]));
         		}
         		
         		for(var j = 0; j < memberCount; j++){
         			var everyMember = memberUser[j];
         			member.options.add(new Option(everyMember.split("#")[1], everyMember.split("#")[0]));
         		}
         	}
         }
	}

	function selectTechnic(){
		PTC.jca.table.Utils.reload("plannertechTaskSearchResult", {}, true);		
	}
</script>

<%
	ArrayList<String> groupin = TaskUtil.getTechnicGroups().get(0);
	ArrayList<String> groupout = TaskUtil.getTechnicGroups().get(1);
	request.setAttribute("groupin", groupin);
	request.setAttribute("groupout", groupout);
	
	ArrayList<String> responorin = new ArrayList<String>();
	ArrayList<String> responorout = new ArrayList<String>();
	request.setAttribute("responorin", responorin);
	request.setAttribute("responorout", responorout);
%>

	<table>
		<tr>
			<td>
				筛选：<select id="selecteid" name="selecteid" onchange="selectTechnic();">
					<option value="ALL">所有</option>
					<option value="UNDISPATCH">未派工</option>
					<option value="PLANNERDISPATCH">计划员派工</option>
					<option value="LEADERDISPATCH">组长派工</option>
				</select>
			</td>
		</tr>
		<tr>
			<td>
				<jca:renderPropertyPanel>
					<w:comboBox propertyLabel="专业组" id="group" name="group" internalValues="${groupin}" displayValues="${groupout}" onchange="getLeader()"></w:comboBox>
				</jca:renderPropertyPanel>
			</td>
			<td>
				<jca:renderPropertyPanel>
					<w:comboBox propertyLabel="组长" id="respon" name="respon" internalValues="${responorin}" displayValues="${responorout}"></w:comboBox>
				</jca:renderPropertyPanel>
			</td>
			<td>
				<jca:renderPropertyPanel>
					<w:comboBox propertyLabel="组员" id="personer" name="personer" internalValues="${responorin}" displayValues="${responorout}"></w:comboBox>
				</jca:renderPropertyPanel>
			</td>
			<td>
				<jca:renderPropertyPanel>
					<w:dateInputComponent propertyLabel="完成时间" name="approve_time" dateValueType="DATE_ONLY" id="approve_time"></w:dateInputComponent>
				</jca:renderPropertyPanel>
			</td>
			<td>
				<jca:renderPropertyPanel>
					<w:textBox propertyLabel="备注" id="comment" name="comment"/>
				</jca:renderPropertyPanel>
			</td>
		</tr>
	</table>
<div>
	<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.task.PlannerTechnicTaskBuilder')}" flush="true" ></jsp:include>
</div>
		
<%@ include file="/netmarkets/jsp/util/end.jspf" %>