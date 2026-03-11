
<%@page import="com.glaway.mpm.util.WTContainerUtil"%><%@ taglib
	uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"
	prefix="jca"%>
<%@ taglib prefix="Component"
	uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@page language="java" pageEncoding="GBK"
	contentType="text/html; charset=GBK"%>
<%@ page import="wt.util.WTProperties"%>
<%@ page import="java.util.*"%>
<%@ page import="wt.pdmlink.PDMLinkProduct"%>
<%@ page import="wt.inf.library.WTLibrary"%>
<script type="text/javascript">
PTC.navigation.loadScript("netmarkets/javascript/nriet/jquery16.js");
var $j = jQuery.noConflict();
</script>
<script type="text/javascript">

	function getTable(action) {
		$j("#tableDiv").load("netmarkets/jsp/glaway/mpm/processplan/workTimeReportTable.jsp", {number: $j("#number").val()}, function (responseText, textStatus, XMLHttpRequest){
		});
	}
</script>
<div
	style="margin: 0pt 8px 0pt 0pt; padding: 0pt; border-right-width: 8px; border: 1px solid #B5B8C8;">
	<div id="reportTitleBar"
		class="x-toolbar x-small-editor x-panel-header wizard-title-text x-toolbar-layout-ct">
		<table class="x-toolbar-ct" cellspacing="0">
			<tbody>
				<tr>
					<td class="x-toolbar-left" align="left">
						<table cellspacing="0">
							<tbody>
								<tr class="x-toolbar-left-row">
									<td id="report-1" class="x-toolbar-cell">
										<div id="report-2" class="xtb-text">
											工艺辅料汇总
										</div>
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
					<table id="report-7" class="header-strip">
						<tbody>
							<tr>
								<td class="attributePanel-label" valign="top" align="left">
									<b>工艺编号</b>
								</td>
								<td class="STYLE3">
									<Component:textBox id="number" name="number" required="true" />
								</td>
							</tr>
							<tr>
								<td align="left" valign="top" colspan="4"
									class="attributePanel-label">
									<input type="button" id="queryButton"
										onclick="getTable('query')" value="查询">
									&nbsp;
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>

	<div id="tableDiv">
	</div>

</div>