<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%

%>
<style>
.comboxStyle{
	width:100px;
}
</style>
<input type="hidden" name="selectedRowValues" id="selectedRowValues" value="" />
<div>
	<jsp:include page="${mvc:getComponentURL('ext.sast.center.productModel.mvc.builder.DocSubdivisionTableBuilder')}" />
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
function setLocalInnerName(obj){
	var id = obj.id;
	document.getElementById(id+'_LOCALDOCTYPEINNERNAME').value= obj.value ;
}
function setSiteName(obj){
	var id = obj.id;
	document.getElementById(id+'_SITENAME').value= obj.value ;
}
function preSumbit(){
	var table = PTC.jca.table.Utils.getTable('table__ext.sast.center.productModel.mvc.builder.DocSubdivisionTableBuilder_TABLE');
	var allSelection = table.getSelectionModel().getSelections();
	if(allSelection.length<=0){
		alert("请选择需要保存的行");
		return false;
	}
	var row = allSelection[0];
	var id = row.data.innerId;
	
	var siteName = document.getElementById(id+'_SITENAME').value;
	if(siteName == null || siteName.length<= 0 || siteName == ''){
		alert("请选择来源域");
		return false;
	}
	var doctypename = document.getElementById(id+'_DOCTYPENAME').value;
	if(doctypename == null || doctypename.length<= 0 || doctypename == ''){
		alert("请填写源文件类型");
		return false;
	}
	var doctypeinnername = document.getElementById(id+'_DOCTYPEINNERNAME').value;
	if(doctypeinnername == null || doctypeinnername.length<= 0 || doctypeinnername == ''){
		alert("请填写源文件类型标识");
		return false;
	}
	var localdoctypeinnername = document.getElementById(id+'_LOCALDOCTYPEINNERNAME').value;
	if(localdoctypeinnername == null || localdoctypeinnername.length<= 0 || localdoctypeinnername == ''){
		alert("请选择本地类型");
		return false;
	}
	var selectRow = id +","+ siteName+","+doctypename+","+doctypeinnername+","+localdoctypeinnername;
	document.getElementById('selectedRowValues').value = selectRow;
	alert(selectRow);
	return true;
}
function preDelete(){
	var table = PTC.jca.table.Utils.getTable('table__ext.sast.center.productModel.mvc.builder.DocSubdivisionTableBuilder_TABLE');
	var allSelection = table.getSelectionModel().getSelections();
	if(allSelection.length<=0){
		alert("请选择需要删除的行");
		return false;
	}
	var row = allSelection[0];
	var id = row.data.innerId;
	selectRow = id;
	document.getElementById('selectedRowValues').value = selectRow;
	return true;
}
function reshTableBuilder(){
	window.location.reload();
}
</script>
