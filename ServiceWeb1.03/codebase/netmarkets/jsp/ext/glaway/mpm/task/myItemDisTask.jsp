<%@ page language="java" pageEncoding="UTF-8" %>
<%@page import="java.util.ArrayList"%>
<%@page import="com.glaway.mpm.task.util.TaskUtil"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<link href="netmarkets/jsp/ext/numbergen/css/nmstyles.css" rel="stylesheet" type="text/css">
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

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
	
		var url="netmarkets/jsp/glaway/mpm/task/search/searchItemResponsor.jsp?para=" + encodeURIComponent(para);
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
         	var user = document.getElementById("respon");
         	
         	if(result.trim() == ""){
         		alert("此组织下无任何人员，请联系管理员!");
         		user.length = 0;
         		user.options.add("","");
         	}else{
         		var everyUserObj = result.split("|");
         		user.length = 0;
         		var count = everyUserObj.length;
         		for(var i = 0; i < count; i++){
         			var everUser = everyUserObj[i];
         			user.options.add(new Option(everUser.split("#")[1], everUser.split("#")[0]));
         		}
         	}
         }
	}
	
	function selectTechnic(){
		var selectCritia = document.getElementById("selecteid").value;
		if(selectCritia == "0"){
			return;
		}
		PTC.jca.table.Utils.reload("itemTaskSearchResult", {}, true);		
	}
	
	function skimDoc(oid ,url){
		if(confirm('是否浏览?')){
			var left=parseInt((screen.availWidth   -   300)/2);
			var top=parseInt((screen.availHeight   -   200)/2);
			window.open(url + "?oid=" + oid, "浏览交付物", "width=400,height=250,toolbar=no,menubar=no,top="+top+",left="+left);
		}
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
					<option value="ALL">--筛选条件--</option>
					<option value="DISPATCH">已派工</option>
					<option value="UNDISPATCH">未派工</option>
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
					<w:comboBox propertyLabel="责任人" id="respon" name="respon" internalValues="${responorin}" displayValues="${responorout}"></w:comboBox>
				</jca:renderPropertyPanel>
			</td>
			<td>
				<jca:renderPropertyPanel>
					<w:dateInputComponent propertyLabel="完成时间" name="finish_time" dateValueType="DATE_ONLY" id="finish_time"></w:dateInputComponent>
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
	<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.task.ItemTaskBuilder')}" flush="true"></jsp:include>
</div>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>