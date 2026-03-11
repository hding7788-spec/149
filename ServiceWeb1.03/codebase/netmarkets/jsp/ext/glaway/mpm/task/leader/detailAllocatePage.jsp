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
			var approve = document.getElementById("approve");
			
			if(result.trim() == ""){
				alert("此组织下无任何人员，请联系管理员!");
				design.length = 0;
				design.options.add("", "");
				
				approve.length = 0;
				approve.options.add("", "");
			}else{
				var everyUserObj = result.split("|");
				design.length = 0;
				approve.length = 0;
				var count = everyUserObj.length;
				for(var i = 0; i < count; i++){
					var everUser = everyUserObj[i];
					design.options.add(new Option(everUser.split("#")[1], everUser.split("#")[0]));
					approve.options.add(new Option(everUser.split("#")[1], everUser.split("#")[0]));
				}
			}
		}
	}

	function selectDetailTechnic(){
		PTC.jca.table.Utils.reload("leaderdetailallocateTable", {}, true);		
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
					<w:comboBox propertyLabel="设计" id="design" name="design" internalValues="${designin}" displayValues="${designout}"></w:comboBox>
				</jca:renderPropertyPanel>
			</td>
			
			<td>
				<jca:renderPropertyPanel>
					<w:comboBox propertyLabel="审核" id="approve" name="approve" internalValues="${designin}" displayValues="${designout}"></w:comboBox>
				</jca:renderPropertyPanel>
			</td>
			
			<td>
				<jca:renderPropertyPanel>
					<w:dateInputComponent propertyLabel="预定批准时间" name="approve_time" dateValueType="DATE_ONLY" id="finish_time"></w:dateInputComponent>
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
		<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.task.LeaderDetailAllocateBuilder')}" flush="true" ></jsp:include>
	</div>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>