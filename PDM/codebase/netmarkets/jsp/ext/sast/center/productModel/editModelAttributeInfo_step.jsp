<%@page import="com.ptc.core.lwc.server.LWCTypeDefinition"%>
<%@page import="com.ptc.windchill.enterprise.dsvcore.server.utils.PersistableHelper"%>
<%@page import="ext.sast.center.productModel.util.SyncModeTypeHelper"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Locale"%>
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
<fmt:message var="MODELTYPEINFOBUILDER_01"       key="MODELTYPEINFOBUILDER_01"/>
<fmt:message var="MODELTYPEINFOBUILDER_03"       key="MODELTYPEINFOBUILDER_03"/>
<style>
.comboxStyle{
	width:100px;
}
</style>
<%
String localHostModelType = "";
String sastModelType = "";
String sastModelType_US = "";
List<?> list = commandBean.getSelectedOidForPopup();
if(list == null || list.size()==0){
	out.println("<SCRIPT>JCAAlert('ext.sast.center.resource.CustomActionRB.SELECT_MODELTYPE');window.close();</SCRIPT>");
}
String oid = list.get(0).toString();
LWCTypeDefinition lwcType = (LWCTypeDefinition)PersistableHelper.findPersistable(oid);

String displayType = lwcType.getDisplayIdentifier().getLocalizedMessage(Locale.CHINA);

String[] values = SyncModeTypeHelper.getModelTypeZHByLocalHostType_US(displayType);
if(values == null || values.length <= 0){
	out.println("<SCRIPT>JCAAlert('ext.sast.center.resource.CustomResource.SYNC_MODELTYPE');window.close();</SCRIPT>");
}else{
	localHostModelType = values[0];
	sastModelType = values[1];
	sastModelType_US = values[2];
	request.setAttribute("sastModelType_US", sastModelType_US);
}
%>
<input type="hidden" id="showIBA" name="showIBA" vaule="true">
<input type="hidden" id="changModelTypeAttribute" name="changModelTypeAttribute" vaule="">
<input type="hidden" name="showEditCombox" id="showEditCombox" value="" />
<input type="hidden" name="localHostModelType_US" id="localHostModelType_US" value="<%=displayType %>" />
<input type="hidden" name="localHostModelType_oid" id="localHostModelType_oid" value="<%=displayType %>" />
<div>
	<w:label value="${MODELTYPEINFOBUILDER_01}"/>
	<w:textBox name="localHostModelType" id="localHostModelType" size="30" readonly="true"
					maxlength="100" value="<%=localHostModelType %>" />
	&nbsp;&nbsp;
	<w:label value="${MODELTYPEINFOBUILDER_03}"/>
	<w:textBox name="sastModelType" id="sastModelType" size="30" readonly="true"
					maxlength="100" value="<%=sastModelType %>" />
	<div style="display:none;">
	<w:textBox name="sastModelType_US" id="sastModelType_US" size="30" readonly="true"
					maxlength="100" value="<%=sastModelType_US %>" />
	</div>
</div>
<div>
	<jsp:include page="${mvc:getComponentURL('ext.sast.center.productModel.mvc.builder.ModelAttributeInfoTableBuilder')}" />
</div>
<%@include file="/netmarkets/jsp/util/end.jspf" %>
<script type="text/javascript">
function myfun(){
	document.getElementById("PJL_wizard_ok").parentNode.innerHTML = "<input type='button' value='关闭（C）' onclick='closeWin()'>";
	document.getElementById("PJL_wizard_cancel").style.display="none";
}
function closeWin(){
	window.close();
}
function showEditCombox(){
	document.getElementById("showEditCombox").value = "true";
	PTC.jca.table.Utils.reload('ext.sast.center.productModel.mvc.builder.ModelAttributeInfoTableBuilder', {}, true);
}
function showSastModelTypeAttributes(row){
	debugger;
	var selectedValue = row.value;
	var selectedId = row.id;
	if(selectedValue == '--请选择--'){
		selectedValue = ' ';
	}
	document.getElementById(selectedId+"_SAST_ATTRIBUTE_US").value = selectedValue;
	document.getElementById("changModelTypeAttribute").value+=selectedId+","+selectedValue+";";
	
	
}
function showIBA(){
	document.getElementById("showIBA").value ="true";
	PTC.jca.table.Utils.reload('ext.sast.center.productModel.mvc.builder.ModelAttributeInfoTableBuilder', {}, true);
}
function showOOTB(){
	document.getElementById("showIBA").value ="false";
	PTC.jca.table.Utils.reload('ext.sast.center.productModel.mvc.builder.ModelAttributeInfoTableBuilder', {}, true);
}
function reshTableBuilder(){
	window.location.reload();
}
</script>