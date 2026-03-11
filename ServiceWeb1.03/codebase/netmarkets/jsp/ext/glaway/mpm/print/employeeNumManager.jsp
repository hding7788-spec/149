<%@page import="wt.util.WTProperties"%>
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
	PTC.jca.table.Utils.reload('com.glaway.mpm.print.builder.EmployeeNumManagerBuilder', {}, true);
}

</script>
<%

String basePath = WTProperties.getLocalProperties().getProperty(
		"wt.server.codebase", null);
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
						<div id="report-2" class="xtb-text">人员工号配置</div>
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
	  <w:label value="用户名："/>
	</td>
	<td align="center">
	  <input type="text"  value="${userName_default}" name="null___userName___textbox" id="userName"  maxlength="100" required="" />

    </td>


		<td align=center><h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" value="查询" onclick="preSubmit();"/></h4></td>
	</tr>
    </td>
</tr>

</table>
</div>
</div>
</div>
</div>
<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.print.builder.EmployeeNumManagerBuilder')}" flush="true"></jsp:include>
<%--<%} %> --%>
</div>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>