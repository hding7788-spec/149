<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="ext.sast.center.productModel.util.SyncProductHelper"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.pdmlink.PDMLinkProduct"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags" %>
<%@ page import="java.util.List"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%
List<?> list = commandBean.getSelectedOidForPopup();
if(list == null || list.size()==0){
	out.println("<SCRIPT>JCAAlert('ext.sast.center.resource.CustomResource.SYNC_MODELTYPE');window.close();</SCRIPT>");
}
NmOid nmOid = (NmOid)list.get(0);
PDMLinkProduct product = (PDMLinkProduct)nmOid.getRefObject();
String productOid = PersistenceHelper.getObjectIdentifier(product).getId()+"";
String productName = product.getName();
String sastModel = SyncProductHelper.getSastProductNameByProductOid(productOid);
request.setAttribute("sastModelStr", sastModel);
%>
<input type="hidden" id="productOid" name="productOid" value="<%=productOid%>">
<input type="hidden" id="productName" name="productName" value="<%=productName%>">
<div style="display:none">
	<w:textBox name="selectedData" id="selectedData" value="" />
</div>
<div>
	<w:label value="本地型号产品名称"/>
	<w:textBox name="localHostModel" id="localHostModel" size="30" readonly="true"
					maxlength="100" value="<%=productName %>" />
	<fieldset class=" x-fieldset x-form-label-left" style="width: auto;">
		<legend class="x-fieldset-header x-unselectable" style="-moz-user-select: none;"><span class="attributePanel-fieldset-title">筛选条件</span></legend>
		<table>
			<tr>
				<td colspan="1" align="left"><w:label value="中心域产品库名称"/></td>
				<td colspan="1" align="left" ><w:textBox name="sastModel" id="sastModel" size="30"
					maxlength="100" value="" />
				</td>
			</tr>
			<tr>
				<td align="left">
					<input type="button" name="search" value="筛选" onclick="doSearch();" />&nbsp;&nbsp;&nbsp;&nbsp;
					<input type="Reset" name="Reset" value="重 置" onclick="clearPickerData();" />
				</td>
			</tr>
		</table>
	</fieldset>

</div>
<jsp:include page="${mvc:getComponentURL('ext.sast.center.productModel.mvc.builder.SastProductInfoTableBuilder')}" />
<script type="text/javascript">
function preSumbit(){
	var selectRow = "";
	var table = PTC.jca.table.Utils.getTable('ext.sast.center.productModel.mvc.builder.SastProductInfoTableBuilder');
	var allSelection = table.getSelectionModel().getSelections();
	for(var i=0;i<allSelection.length;i++){
		var row = allSelection[i];
		var productOid = row.data.SAST_ProductOid;
		var productName = row.data.SAST_ProductName;
		var remarks = row.data.SAST_Remarks;
		selectRow = productOid+","+productName+","+remarks +";" + selectRow;
	}
	document.getElementById("selectedData").value = selectRow;
	return true;
}
function cancelSumbit(){
	window.close();
}
function myfun(){
	document.getElementById("PJL_wizard_ok").style.display="none";
	document.getElementById("PJL_wizard_cancel").style.display="none";
	var sastModelStr = "${sastModelStr}";
	var selectedData = new Array();
	var grid = PTC.jca.table.Utils.getTable('ext.sast.center.productModel.mvc.builder.SastProductInfoTableBuilder');
	var values = sastModelStr.split(",");
	for(var i=0;i<values.length;i++){
		var tepm = grid.store.find('SAST_ProductName',values[i]);
		selectedData[i] = tepm;
	}
	grid.selModel.selectRows(selectedData);
}
function clearPickerData(){
	document.getElementById("sastModel").value="";
	PTC.jca.table.Utils.reload('ext.sast.center.productModel.mvc.builder.SastProductInfoTableBuilder', {}, true);
}
function doSearch(){
	PTC.jca.table.Utils.reload('ext.sast.center.productModel.mvc.builder.SastProductInfoTableBuilder', {}, true);
}
</script>
<%@include file="/netmarkets/jsp/util/end.jspf" %>
