<%@page import="com.glaway.mpm.intf.ProcessEditorToWCIntfRMI"%>
<%@page import="com.glaway.mpm.wcIntf.TechnicsIntf"%>
<%@page import="java.util.Vector"%>
<%@page import="wt.inf.container.WTContainer"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.util.List"%>
<%@page pageEncoding="GBK" %>
<%@page import="java.util.ArrayList"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core"   prefix="c" %>
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

	PTC.jca.table.Utils.reload('PBOMTechnicsReportBuilder', {}, true);
}
</script>
<%

String basePath = WTProperties.getLocalProperties().getProperty(
		"wt.server.codebase", null);
String oid = request.getParameter("oid");
Object object = new ReferenceFactory().getReference(oid).getObject();
WTPart pbomPart = (WTPart) object;
WTContainer container = pbomPart.getContainer();
Vector<String> batchs = ProcessEditorToWCIntfRMI.getBatchsByProductName(container.getName());

%>

<div
	style="margin: 0pt 8px 0pt 0pt; padding: 0pt; border-right-width: 8px; border: 1px solid #B5B8C8;">
<div class="x-toolbar x-small-editor x-panel-header wizard-title-text x-toolbar-layout-ct">
<table class="x-toolbar-ct" cellspacing="0">
	<tbody>
		<tr>
			<td class="x-toolbar-left" align="left">
			<table cellspacing="0">
				<tbody>
					<tr class="x-toolbar-left-row">
						<td id="report-1" class="x-toolbar-cell">
						<div id="report-2" class="xtb-text">工艺文件完成状态表</div>
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
<!-- <tr>
    <tr>
		<td><h4>阶段标记</h4></td>
		<td>
			<select name="phase" id="phase" >
				<option value="" selected></options>
				<option value="Y">Y</option>
				<option value="M">M</option>
				<option value="M1">M1</option>
				<option value="M2">M2</option>
				<option value="C">C</option>
				<option value="S">S</option>
				<option value="Z">Z</option>
				<option value="Z1">Z1</option>
				<option value="Z2">Z2</option>
				<option value="Z3">Z3</option>
				<option value="D">D</option>
				<option value="D1">D1</option>
				<option value="D2">D2</option>
				<option value="D3">D3</option>
				<option value="G">G</option>
				<option value="P">P</option>
			</select>
      </tr>
	<tr>

</tr>
<tr>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align=center><h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" value="查询" onclick="preSubmit();"/></h4></td>

	</tr> -->
	<tr>
    	<tr>
			<td><h4>批次号</h4></td>
			<td>
				<select name="null___batch___textbox" id="batch">
				<%for(int i = 0; i < batchs.size(); i++){ %>
				<option value="<%=batchs.get(i) %>"><%=batchs.get(i) %></option>
				<%} %>
				</select>
			</td>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<!-- <td align=center><h4><input type="button" value="   查询   " onclick="preSubmit()"/></h4></td> -->
			<td><jca:action actionName="searchBOMbyBatch" actionType="pbom" button="true"/>
      </tr>
	<tr>

</table>
</div>
</div>
</div>
</div>

<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.pbom.mvc.builder.PBOMTechnicsReportBuilder')}" flush="true"></jsp:include>
<%--<%} %> --%>
</div>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>