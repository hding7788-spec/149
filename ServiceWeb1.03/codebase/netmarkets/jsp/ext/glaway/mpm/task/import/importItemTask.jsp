<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>

<%@ page import="com.glaway.mpm.task.ui.TaskResource" %>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<%@ page import="com.glaway.mpm.task.ui.TaskResource" %>
<link href="netmarkets/jsp/ext/numbergen/css/nmstyles.css" rel="stylesheet" type="text/css">
<%@include file="/netmarkets/jsp/util/begin.jspf"%>
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.glaway.mpm.task.ui.TaskResource"/>
<fmt:message var="downloadExcel" key="<%=TaskResource.PRIVATE_CONSTANT_48 %>"/>

<%
%>

<script language="javascript">
	function importTask(){
		var file = document.getElementById("file");
		var form = this.document.mainform[0];
		
		if(file.value == ""){
			alert("请选择导入的文件!");
			return false;
		}
		if(form.logBoard){
			form.logBoard.value="";
		}
		
		form.action="netmarkets/jsp/glaway/mpm/task/import/importLoader.jsp";
		form.encoding="multipart/form-data";
		form.target="_blank";
		form.method="post";
		form.submit();
		
	}
</script>

<fieldset><legend>&nbsp;&nbsp;&nbsp;&nbsp;操作说明</legend></fieldset>
<div class="fieldsetbody">
	<table width="100%" class="layoutTable">
		<tr>
			<td width="25">&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td height="30"><span class="STYLE8">1.</span></td>
			<td><span class="STYLE8">下载导入模板文件</span></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td height="30"><span class="STYLE8">2.</span></td>
			<td><span class="STYLE8">按照模板文件填写待导入数据</span></td>
		</tr>
	</table>
</div>
<br/>
<br/>
<br/>
<div class="fieldsetbody">
	<table width="100%">
		<tr>
			<td>选择导入条目任务的Excel文件：</td>
		</tr>
		<tr>
			<td><input name="file" type="file" id="file" size="40"/></td>
		</tr>
		<tr>
			<td><input name="Submit" type="button" onclick="importTask()" value="导入"/></td>
		</tr>
	</table>
</div>


<div align="center">
	<a id="downloadExcel" name="downloadExcel" href="netmarkets\jsp\glaway\mpm\task\import\modelTemplate.xls" target="_blank">${downloadExcel}</a>
</div>

<fieldset><legend>&nbsp;&nbsp;&nbsp;&nbsp;日志报告</legend></fieldset>
<div class="fieldsetbody">
	<table align="left">
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		
		<tr>
			<td width="25" class="STYLE8"><span class="STYLE9"></span></td>
			<td width="100%" class="STYLE8"><span class="STYLE9">导入日志</span></td>
		</tr>
		
		<tr>
			<td class="STYLE8"><span class="STYLE9"></span></td>
			<td class="STYLE8"><span style="font-size:14">
				<textarea rows="10" cols="50" name="logBoard" id="logBoard" style="width:90%" readonly="readonly"></textarea>
			</span>
			</td>
		</tr>
	</table>	
</div>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>