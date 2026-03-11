<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.glaway.mpm.task.util.TaskUtil"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"
	prefix="jca"%>
	<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>

<link href="netmarkets/jsp/ext/numbergen/css/nmstyles.css" rel="stylesheet" type="text/css">

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

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
			var leader = document.getElementById("leader");
			var reviewer = document.getElementById("reviewer");
			
			if(result.trim() == ""){
				alert("此组织下无任何人员，请联系管理员!");
				design.length = 0;
				leader.length = 0;
				reviewer.length = 0;
				design.options.add("", "");
				leader.options.add("", "");
				reviewer.options.add("", "");
			}else{
				var allUser = result.split(";");
				var allLeader = allUser[0];
				var allMember = allUser[1];
				
				
				design.length = 0;
				leader.length = 0;
				reviewer.length = 0;
				
				var everyLeader = allLeader.split("|");
				var everyMember = allMember.split("|");
				
				var leaderCount = everyLeader.length;
				var MemberCount = everyMember.length;
				
				leader.options.add(new Option("---选择人员---", ""));
				design.options.add(new Option("---选择人员---", ""));
				reviewer.options.add(new Option("---选择人员---", ""));
				
				for(var i = 0; i < leaderCount; i++){
					var leaderM = everyLeader[i];
					leader.options.add(new Option(leaderM.split("#")[1], leaderM.split("#")[0]));
				}
				
				for(var j = 0; j < MemberCount; j++){
					var memberM = everyMember[j];
					design.options.add(new Option(memberM.split("#")[1], memberM.split("#")[0]));
					reviewer.options.add(new Option(memberM.split("#")[1], memberM.split("#")[0]));
				}
			}
		}
	}

	function selectDetailTechnic(){
		PTC.jca.table.Utils.reload("plannerdetailallocateTable", {}, true);		
	}
</script>

	<%
		ArrayList<String> groupin = TaskUtil.getTechnicGroups().get(0);
		ArrayList<String> groupout = TaskUtil.getTechnicGroups().get(1);
		request.setAttribute("groupin", groupin);
		request.setAttribute("groupout", groupout);
		
		ArrayList<String> designin = new ArrayList<String>();
		ArrayList<String> designout = new ArrayList<String>();
		request.setAttribute("designin", designin);
		request.setAttribute("designout", designout);
	%>

	<div>
		<p align="center" style="font-size:30px;font-weight:bold">详细任务派工</p>
	</div>
	
	<table>
		<tr>
			<td>
				筛选：<select id="selecteid" name="selecteid" onchange="selectDetailTechnic();">
					<option value="ALL">所有任务</option>
					<option value="UNDISPATCH">未派工</option>
					<option value="PLANNERDISPATCH">计划员已派工</option>
					<option value="LEADERDISPATCH">组长已派工</option>
				</select>
			</td>
		</tr>
		<tr>
			<td>
				<jca:renderPropertyPanel>
					<w:comboBox propertyLabel="专业组" id="group" name="group" internalValues="${groupin}" displayValues="${groupout}" onchange="getUserOfTechGroup()"></w:comboBox>
				</jca:renderPropertyPanel>
			</td>
			
			<td>
				<jca:renderPropertyPanel>
					<w:comboBox propertyLabel="组长" id="leader" name="leader" internalValues="${designin}" displayValues="${designout}"></w:comboBox>
				</jca:renderPropertyPanel>
			</td>
			
			<td>
				<jca:renderPropertyPanel>
					<w:comboBox propertyLabel="设计" id="design" name="design" internalValues="${designin}" displayValues="${designout}"></w:comboBox>
				</jca:renderPropertyPanel>
			</td>
			
			<td>
				<jca:renderPropertyPanel>
					<w:comboBox propertyLabel="审核" id="reviewer" name="reviewer" internalValues="${designin}" displayValues="${designout}"></w:comboBox>
				</jca:renderPropertyPanel>
			</td>
			
			<td>
				<jca:renderPropertyPanel>
					<w:dateInputComponent propertyLabel="预定批准时间" name="approve_time" dateValueType="DATE_ONLY" id="approve_time"></w:dateInputComponent>
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
		<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.task.PlannerDetailAllocateBuilder')}" flush="true" ></jsp:include>
	</div>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>