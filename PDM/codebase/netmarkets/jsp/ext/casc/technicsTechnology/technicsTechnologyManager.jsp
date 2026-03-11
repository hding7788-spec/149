<%@page import="ext.casc.fileprint.FilePrintUtil2"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.util.Map"%>
<%@page pageEncoding="GBK" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"	prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@include file="/netmarkets/jsp/util/begin.jspf"%>


<style type="text/css">
button{
	width: 34px;
	height: 19px;

	text-align: left;

	padding-left:2px;
	font-size: 10px;
	font-style: normal;
	font-weight: normal;
	color: #464646;


	display: inline-block;
}
</style>

<script language="javascript" type="text/javascript">

function preSubmit(){

	PTC.jca.table.Utils.reload('ext.casc.doc.mvc.builder.TechnicsTechnologyBuilder', {}, true);
}
function confirmDelete(){
	var subTypeNumber;
	debugger;
	var flag = false;
	var divs = document.getElementsByTagName("div");
	for (i = 0; i < divs.length; i++) {
		if(divs[i].className.indexOf('row-selected') > -1) {
			debugger;
			var divSelected = divs[i];
			var tableSelected  = divSelected.childNodes[0];
			subTypeNumber = tableSelected.rows[0].cells[1].getElementsByTagName("div")[0].getElementsByTagName("input")[0].value;
			document.getElementById("hiddenValue").value = subTypeNumber;
			flag = true;
			break;
		}
	}
	if(!flag){
		alert("未作任何选择。");
		return false;
	}
	return true;

}
function selectType() {
	preSubmit();
}

</script>
<%

String basePath = WTProperties.getLocalProperties().getProperty(
		"wt.server.codebase", null);
Map<String, String> technologyType = FilePrintUtil2.getTechnologyDocType();
%>

<div
	style="margin: 0pt 8px 0pt 0pt; padding: 0pt; border-right-width: 8px; border: 1px solid #B5B8C8;">
<div class="x-toolbar x-small-editor x-panel-header wizard-title-text x-toolbar-layout-ct">
	<input type="hidden" name="hiddenValue" id="hiddenValue" onclick="" />
<table class="x-toolbar-ct" cellspacing="0">
	<tbody>
		<tr>
			<td class="x-toolbar-left" align="left">
			<table cellspacing="0">
				<tbody>
					<tr class="x-toolbar-left-row">
						<td id="report-1" class="x-toolbar-cell">
						<div id="report-2" class="xtb-text">工艺技术文档配置</div>
						</td>
					</tr>
				</tbody>
			</table>
			</td>
		</tr>
	</tbody>
</table>
</div>
<div id="report-3" class="wizardPanel-body wizardPanel-body-noheader">
<div id="report-4" class=" x-panel stepHeader x-panel-noborder">
<div id="report-5" class="stepPanel">
<div id="report-6" class="x-header-strip-wrap" style="left: 0px;">
<table >
<tr>
    <td>
	  <w:label value="大类名称"/>
	</td>
	<td align="center">
	  <select name="null___dalei___textbox" id="dalei" onchange="selectType()">
	  	<%-- <% for(Map.Entry<String, String> entry : technologyType.entrySet()){%>
	  		<option value=<%=entry.getKey() %>><%=entry.getKey() %></option>
	  		<% }%> --%>
	  		<option value=""></option>
	  		<option value="工艺分析策划总结">工艺分析策划总结</option>
	  		<option value="工艺定型">工艺定型</option>
	  		<option value="工艺鉴定">工艺鉴定</option>
	  		<option value="技术课题">技术课题</option>
	  		<option value="总体测发报告">总体测发报告</option>

	  </select>
    </td>

     <%-- <td><jca:action actionName="technologySearch" actionType="custom" button="true"/> --%>

	</tr>
    </td>
</tr>

</table>
</div>
</div>
</div>
</div>

<jsp:include page="${mvc:getComponentURL('ext.casc.doc.mvc.builder.TechnicsTechnologyBuilder')}" flush="true"></jsp:include>
<%--<%} %> --%>
</div>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>