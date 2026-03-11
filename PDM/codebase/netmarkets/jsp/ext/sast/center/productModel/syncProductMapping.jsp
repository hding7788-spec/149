<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/fmt"  prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.sast.center.resource.CustomResource" />
<fmt:message var="PRODUCTMODELLISTTABLEBUILDER_01"       key="PRODUCTMODELLISTTABLEBUILDER_01"/>
<fmt:message var="PRODUCTMODELLISTTABLEBUILDER_03"       key="PRODUCTMODELLISTTABLEBUILDER_03"/>

<div style="width:100%;hieght:20px;background:#0080C0" align="center"><font size="5" style="color:#FFFFFF"><B>协同型号映射</B></font></div>
<div id = "serachCondation">
	<fieldset class=" x-fieldset x-form-label-left" style="width: auto;">
		<legend class="x-fieldset-header x-unselectable" style="-moz-user-select: none;"><span class="attributePanel-fieldset-title">筛选条件</span></legend>
		<table>
			<tr>
				<td colspan="1" align="left"><w:label value="${PRODUCTMODELLISTTABLEBUILDER_01 }"/></td>
				<td colspan="1" align="left" ><w:textBox name="localHostModel" id="localHostModel" size="30"
					maxlength="100" value="" />
				</td>
				<td>&nbsp;&nbsp;</td>
				<td colspan="1" align="left"><w:label value="${PRODUCTMODELLISTTABLEBUILDER_02}"/></td>
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
<div id="productList">
	<jsp:include page="${mvc:getComponentURL('ext.sast.center.productModel.mvc.builder.ProductInfoTableBuilder')}" />
</div>
<script type="text/javascript">
function clearPickerData(){
	document.getElementById("sastModel").value="";
	document.getElementById("localHostModel").value="";
	PTC.jca.table.Utils.reload('ext.sast.center.productModel.mvc.builder.ProductInfoTableBuilder', {}, true);
}
function doSearch(){
	PTC.jca.table.Utils.reload('ext.sast.center.productModel.mvc.builder.ProductInfoTableBuilder', {}, true);
}
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>