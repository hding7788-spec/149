﻿<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.glaway.mpm.constants.Constants"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<%@include file="/netmarkets/jsp/util/begin.jspf"%>

<link href="netmarkets/jsp/ext/numbergen/css/nmstyles.css"
	rel="stylesheet" type="text/css">
<script type="text/javascript">

	function reload(){ 
	   PTC.jca.table.Utils.reload("gzCard_searchResultTable", {}, true);
	}
	
	
	Ext.onReady(function() {
		var startsdatePicker = new Ext.form.DateField({
			fieldLabel: 'Starts',
			name: 'beginTime',
			id: 'beginTime',
			width: 90,
			allowBlank: true,
			format: 'Y/m/d',
			showWeekNumber: true,
			editable: false
		});
		startsdatePicker.render('begin_date');
		var endsdatePicker = new Ext.form.DateField({
			fieldLabel: 'Ends',
			name: 'endTime',
			id: 'endTime',
			width: 90,
			allowBlank: true,
			format: 'Y/m/d',
			showWeekNumber: true,
			editable: false
		});
		endsdatePicker.render('end_date');
	});
	
	
</script>


<%
ArrayList<String> internalValues=new ArrayList<String>();
internalValues.add("");
internalValues.add(Constants.INWORK);
internalValues.add(Constants.BOHUI);
internalValues.add(Constants.SYZ);
internalValues.add(Constants.RELEASED);
internalValues.add(Constants.YZF);
request.setAttribute("internalValues",internalValues);
ArrayList<String> displayValues=new ArrayList<String>();
displayValues.add("所有状态");
displayValues.add("拟制");
displayValues.add("驳回");
displayValues.add("审阅中");
displayValues.add("已归档");
displayValues.add("已作废");
request.setAttribute("displayValues",displayValues);
 %>
<fieldset>
	<legend>
		&nbsp;&nbsp;查询条件
	</legend>
</fieldset>
<div class="fieldsetbody">
	<table width="100%" class="layoutTable100">
		<tr>
			<td width="30">
				&nbsp;
			</td>
			<td width="100">
				&nbsp;
			</td>
			<td>
				&nbsp;
			</td>
		</tr>
		<tr height="30">
			<td>
				&nbsp;
			</td>
			<td class="STYLE3">
				<b>产品代号:</b>
			</td>
			<td class="STYLE3">
				<w:textBox name="productNumber" id="productNumber" />
			</td>
		</tr>
		<tr height="30">
			<td>
				&nbsp;
			</td>
			<td class="STYLE3">
				<b>状态:</b>
			</td>
			<td class="STYLE3">
				<w:comboBox id="state" name="state"
					internalValues="${internalValues}" displayValues="${displayValues}"></w:comboBox>
			</td>
		</tr>
		<tr height="30">
			<td>
				&nbsp;
			</td>
			<wctags:userPicker id="applicant" label="申请人" />
		</tr>
		<tr height="30">
			<td>
				&nbsp;
			</td>
			<td class="STYLE3">
				<b>起始时间:</b>
			</td>
			<td class="STYLE3">
				<div id='begin_date' style='width: 100px; float: left; clear: both'></div>
			</td>
		</tr>
		<tr height="30">
			<td>
				&nbsp;
			</td>
			<td class="STYLE3">
				<b>截止时间:</b>
			</td>
			<td class="STYLE3">
				<div id=end_date style='width: 100px; float: left; clear: both'></div>
			</td>
		</tr>
		<tr height="30">
			<td>
				&nbsp;
			</td>
			<td></td>
			<td>
				<input type="button" name="search" id="search" value="搜索"
					onclick="reload()" />
				<input type="reset" id="reset" name="reset" value="重置" />
			</td>
		</tr>
	</table>
</div>
<br>



<jsp:include
	page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.mpmresource.gz.gzcard.SearchGZCardTableBuilder')}"
	flush="true" />
<%@include file="/netmarkets/jsp/util/end.jspf"%>