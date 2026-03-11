<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
request.setAttribute("basePath", basePath);
%>
<input type="hidden" name="showEditCombox" id="showEditCombox" value="" />
<input type="hidden" name="selectedRowValues" id="selectedRowValues" value="" />
<input type="hidden" name="changModelType" id="changModelType" value="" />
<div style="width:100%;hieght:20px;background:#0080C0" align="center"><font size="5" style="color:#FFFFFF"><B>协同模型映射</B></font></div>
<div>
<jsp:include page="${mvc:getComponentURL('ext.sast.center.productModel.mvc.builder.ModelTypeInfoBuilder')}" />
</div>
<div>
<jsp:include page="${mvc:getComponentURL('ext.sast.center.productModel.mvc.builder.ReceivedModelTypeListBuilder')}" />
</div>
<style>
.comboxStyle{
	width:100px;
}
</style>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
<script type="text/javascript">
function showEditCombox(){
	debugger;
	document.getElementById("showEditCombox").value="true";
	document.getElementById("changModelType").value="";
	PTC.jca.table.Utils.reload('ext.sast.center.productModel.mvc.builder.ModelTypeInfoBuilder', {}, true);
}
function hiddenCombox(){
	if(JCAConfirm("ext.sast.center.resource.CustomActionRB.ISSUBMIT")){
		document.getElementById("showEditCombox").value="";
		PTC.jca.table.Utils.reload('ext.sast.center.productModel.mvc.builder.ModelTypeInfoBuilder', {}, true);
	}
} 
function showSastModelTypeUs(row){
	debugger;
	var selectedValue = row.value;
	var selectedId = row.id;
	if(selectedValue == '--请选择--'){
		selectedValue = ' ';
	}
	document.getElementById(selectedId+"_SASTTYPE_US").value = selectedValue;
	document.getElementById("changModelType").value += selectedId+","+selectedValue+";";
}
function getLocalValue(obj){
	var id = obj.id;
	var selectedValue = obj.value;
	if(selectedValue == '--请选择--'){
		selectedValue = ' ';
	}
	document.getElementById(id+'_LOCAL_MODELTYPEID').value = selectedValue ;
	
	
}
function getSastValue(obj){
	var id = obj.id;
	var selectedValue = obj.value;
	if(selectedValue == '--请选择--'){
		selectedValue = ' ';
	}
	document.getElementById(id+'_SAST_MODELTYPEID').value= selectedValue ;
}
var selectRow = "";
function preSumbit(){
	var table = PTC.jca.table.Utils.getTable('table__ext.sast.center.productModel.mvc.builder.ReceivedModelTypeListBuilder_TABLE');
	var allSelection = table.getSelectionModel().getSelections();
	if(allSelection.length<=0){
		alert("请选择需要保存的行");
		return false;
	}
	var row = allSelection[0];
	var id = row.data.innerId;
	
	var sastModelTypeId = document.getElementById(id+'_SAST_MODELTYPEID').value;
	if(sastModelTypeId == null || sastModelTypeId.length<= 0 || sastModelTypeId == ' '){
		alert("请选择中心域类型");
		return false;
	}
	var localModelTypeId = document.getElementById(id+'_LOCAL_MODELTYPEID').value;
	if(localModelTypeId == null || localModelTypeId.length<= 0 || localModelTypeId == ' '){
		alert("请选择本地类型");
		return false;
	}
	selectRow = id +","+ sastModelTypeId+","+localModelTypeId;
	document.getElementById('selectedRowValues').value = selectRow;
	return true;
}
function reshTableBuilder(){
	PTC.jca.table.Utils.reload('ext.sast.center.productModel.mvc.builder.ReceivedModelTypeListBuilder', {}, true);
}
function preDelete(){
	var table = PTC.jca.table.Utils.getTable('table__ext.sast.center.productModel.mvc.builder.ReceivedModelTypeListBuilder_TABLE');
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
function preEdit(){
	var table = PTC.jca.table.Utils.getTable('table__ext.sast.center.productModel.mvc.builder.ReceivedModelTypeListBuilder_TABLE');
	var allSelection = table.getSelectionModel().getSelections();
	if(allSelection.length<=0){
		alert("请选择需要映射的类型");
		return false;
	}
	var row = allSelection[0];
	var id = row.data.innerId;
	selectRow = id;
	document.getElementById('selectedRowValues').value = selectRow;
	return true;
}
function editReceivedModelAttr(){
	var table = PTC.jca.table.Utils.getTable('table__ext.sast.center.productModel.mvc.builder.ReceivedModelTypeListBuilder_TABLE');
	var allSelection = table.getSelectionModel().getSelections();
	if(allSelection.length<=0){
		alert("请选择需要映射的类型");
		return false;
	}
	var row = allSelection[0];
	var id = row.data.innerId;
	var sast_modeltypeid = row.data.sast_modeltypeid;
	if(sast_modeltypeid == ''){
		alert("请先保存类型设置");
	}
	var sast_modeltypename = row.data.sast_modeltypename;
	var local_modeltypeid = row.data.local_modeltypeid;
	var local_modeltypename = row.data.local_modeltypename;
	
	var basePath = "${basePath}";
	var url = '/netmarkets/jsp/ext/sast/center/productModel/editReceivedModelAttributeInfo.jsp?selectedRowValues='+id+"&sast_modeltypeid="+sast_modeltypeid
			+"&sast_modeltypename="+sast_modeltypename+"&local_modeltypeid="+local_modeltypeid+"&local_modeltypename="+local_modeltypename;
	window.open(basePath+url,'_blank');
}
</script>