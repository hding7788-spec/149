<%@ page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />	
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<%@ page import="ext.casc.ui.actionsRB,java.util.List"%>
<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean" %>
<%@ page import="com.ptc.netmarkets.model.NmOid" %>
<%@ page import="wt.part.WTPart" %>
<%@ page import="ext.casc.process.mvc.builder.RefreshBatchesProcessor" %>
<%@ page import="java.util.ArrayList" %>
<style type="text/css">
	h1{
		font-size: 16px;
		font-weight: 500;
		margin:0;
		padding:0;
	}
	h2{
		font-weight: 500;
	}
	fieldset {
		font-size: 15px;
		padding:10px;
		margin:10px;
		width:270px;
		color:#000000;
		border:#000000 solid 2px;
	}
	select {
		width: 160px;
	}
</style>
<script language="JavaScript" src='netmarkets/javascript/util/revisionLabelPicker.js'></script>
<%
	NmCommandBean cb = new NmCommandBean();
	cb.setRequest(request);
	NmOid nmOid = cb.getPrimaryOid();
	Object object = nmOid.getRefObject();
	String number="";
	ArrayList<String> list = null;
	String pbomBatch = "";
	if(object instanceof WTPart){
		number= RefreshBatchesProcessor.getPartNumberByoid(nmOid);
		list = RefreshBatchesProcessor.getProductBatchesByoid(nmOid);
		pbomBatch = RefreshBatchesProcessor.getPbomBatchByoid(nmOid);
		session.setAttribute("number",number);
	}
%>
<script type="text/javascript">
	function preSubmit() {
		var myselect = document.getElementById("batch1")
		var index = myselect.selectedIndex ;
		var value = myselect.options[index].value.trim();
		if(value==""){
			JCAAlert("请按顺序且至少定义批次号1的值");
			return false;
		}
		setValue();
		return true;
	}
	function setValue() {
		var batch = "";
		for (var i = 1; i < 11; i++) {
			var myselect = document.getElementById("batch"+i);
			var index = myselect.selectedIndex;
			var value = myselect.options[index].value.trim();
			if(value!=""){
				batch = batch + value + "&nbsp;&nbsp;&nbsp;";
			}else{
				break;
			}
		}
		var h1 = document.getElementById('h1');
		h1.innerHTML = batch;
	}
</script>
<table>
	<tr>
		<td><h1>&nbsp;&nbsp;PBOM编号&nbsp;:&nbsp;</h1></td>
		<td><h1>&nbsp;<%=number%></h1></td>
	</tr>
	<tr></tr>
	<tr></tr>
</table>
<fieldset style="width:400px; height:240px;">
	<legend>&nbsp;批次号</legend>
	<div>
		<tr><td>
			<h2>选择批次号1&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;
				<select name="batch1" id="batch1">
					<option value=""></option>
					<%for(int i=0;i<list.size();i++){%>
					<option value="<%=list.get(i) %>"><%=list.get(i) %></option>
					<%}%>
				</select>
			</h2>
		</td></tr>
		<tr><td>
			<h2>选择批次号2&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;
				<select name="batch2" id="batch2">
					<option value=""></option>
					<%for(int i=0;i<list.size();i++){%>
					<option value="<%=list.get(i) %>"><%=list.get(i) %></option>
					<%}%>
				</select>
			</h2>
		</td></tr>
		<tr><td>
			<h2>选择批次号3&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;
				<select name="batch3" id="batch3">
					<option value=""></option>
					<%for(int i=0;i<list.size();i++){%>
					<option value="<%=list.get(i) %>"><%=list.get(i) %></option>
					<%}%>
				</select>
			</h2>
		</td></tr>
		<tr><td>
			<h2>选择批次号4&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;
				<select name="batch4" id="batch4">
					<option value=""></option>
					<%for(int i=0;i<list.size();i++){%>
					<option value="<%=list.get(i) %>"><%=list.get(i) %></option>
					<%}%>
				</select>
			</h2>
		</td></tr>
		<tr><td>
			<h2>选择批次号5&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;
				<select name="batch5" id="batch5">
					<option value=""></option>
					<%for(int i=0;i<list.size();i++){%>
					<option value="<%=list.get(i) %>"><%=list.get(i) %></option>
					<%}%>
				</select>
			</h2>
		</td></tr>
		<tr><td>
			<h2>选择批次号6&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;
				<select name="batch6" id="batch6">
					<option value=""></option>
					<%for(int i=0;i<list.size();i++){%>
					<option value="<%=list.get(i) %>"><%=list.get(i) %></option>
					<%}%>
				</select>
			</h2>
		</td></tr>
		<tr><td>
			<h2>选择批次号7&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;
				<select name="batch7" id="batch7">
					<option value=""></option>
					<%for(int i=0;i<list.size();i++){%>
					<option value="<%=list.get(i) %>"><%=list.get(i) %></option>
					<%}%>
				</select>
			</h2>
		</td></tr>
		<tr><td>
			<h2>选择批次号8&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;
				<select name="batch8" id="batch8">
					<option value=""></option>
					<%for(int i=0;i<list.size();i++){%>
					<option value="<%=list.get(i) %>"><%=list.get(i) %></option>
					<%}%>
				</select>
			</h2>
		</td></tr>
		<tr><td>
			<h2>选择批次号9&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;
				<select name="batch9" id="batch9">
					<option value=""></option>
					<%for(int i=0;i<list.size();i++){%>
					<option value="<%=list.get(i) %>"><%=list.get(i) %></option>
					<%}%>
				</select>
			</h2>
		</td></tr>
		<tr><td>
			<h2>选择批次号10&nbsp;:&nbsp;&nbsp;&nbsp;
				<select name="batch10" id="batch10">
					<option value=""></option>
					<%for(int i=0;i<list.size();i++){%>
					<option value="<%=list.get(i) %>"><%=list.get(i) %></option>
					<%}%>
				</select>
			</h2>
		</td></tr>
	</div>
</fieldset>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>