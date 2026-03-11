<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="ext.sast.center.productModel.bean.ReceivedModelTypeInfo"%>
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
String localHostModelType = request.getParameter("local_modeltypename");
String localHostModelType_us = request.getParameter("local_modeltypeid");
String sastModelType = request.getParameter("sast_modeltypename");
String sastModelType_US = request.getParameter("sast_modeltypeid");
String selectedRowValues = request.getParameter("selectedRowValues");
%>
<input type="hidden" id="showIBA" name="showIBA" vaule="true">
<input type="hidden" id="changModelTypeAttribute" name="changModelTypeAttribute" vaule="">
<input type="hidden" name="showEditCombox" id="showEditCombox" value="" />
<div>
	
	<w:label value="${MODELTYPEINFOBUILDER_03}"/>
	<w:textBox name="sastModelType" id="sastModelType" size="30" readonly="true"
					maxlength="100" value="<%=sastModelType %>" />
	&nbsp;&nbsp;
	<w:label value="${MODELTYPEINFOBUILDER_01}"/>
	<w:textBox name="localModelType" id="localModelType" size="30" readonly="true"
					maxlength="100" value="<%=localHostModelType %>" />
	
	<div style="display:none;">
	<w:textBox name="sastModelType_US" id="sastModelType_US" size="30" readonly="true"
					maxlength="100" value="<%=sastModelType_US %>" />
	<w:textBox name="localModelType_US" id="localModelType_US" size="30" readonly="true"
					maxlength="100" value="<%=localHostModelType_us %>" />
	</div>
</div>
<div>
	<jsp:include page="${mvc:getComponentURL('ext.sast.center.productModel.mvc.builder.ReceivedModelAttributeInfoTableBuilder')}" />
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
	PTC.jca.table.Utils.reload('ext.sast.center.productModel.mvc.builder.ReceivedModelAttributeInfoTableBuilder', {}, true);
}
function showSastModelTypeAttributes(row){
	debugger;
	var selectedValue = row.value;
	var selectedId = row.id;
	if(selectedValue == '--请选择--'){
		selectedValue = ' ';
	}
	alert(selectedValue);
	document.getElementById(selectedId+"_RECEIVEDATTR").value = selectedValue;
	document.getElementById("changModelTypeAttribute").value+=selectedId+","+selectedValue+";";
}
function reshTableBuilder(){
	window.location.reload();
}
</script>