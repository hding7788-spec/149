<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<input type="hidden" name="selectedRowValues" id="selectedRowValues" value="" />
<style>
.comboxStyle{
	width:100px;
}
</style>
<div>
<jsp:include page="${mvc:getComponentURL('ext.sast.center.productModel.mvc.builder.ReceiveScopeModelTypeBuilder')}" />
</div>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
<script type="text/javascript">
function preSumbit(){
	debugger;
	var selectRow = "";
	var table = PTC.jca.table.Utils.getTable('table__ext.sast.center.productModel.mvc.builder.ReceiveScopeModelTypeBuilder_TABLE');
	var allSelection = table.getSelectionModel().getSelections();
	for(var i=0;i<allSelection.length;i++){
		var row = allSelection[i];
		var model_type_id = row.data.model_type_id;
		if(selectRow.length>0){
			selectRow = selectRow+","+model_type_id;
		}else{
			selectRow = model_type_id;
		}
	}
	document.getElementById("selectedRowValues").value=selectRow;
	return true;
}
</script>