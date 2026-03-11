<%@page language="java" session="true" pageEncoding="GBK"%>
 <%@page import="java.util.Iterator"%>
<%@page import="ext.casc.util.WCUtil,java.util.Set,ext.casc.constants.Constants"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />

<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<%@page import="wt.util.WTProperties"%>
<%@page import="java.util.List"%>
<%@page import="ext.casc.process.ProcessConstants"%>
<%@page import="ext.casc.process.util.ProcessUtil"%>
<jsp:include page="${mvc:getComponentURL('ext.casc.product.mvc.builder.ProductRoleListBuilder')}" />
<script type="text/javascript">
Ext.override(Ext.ux.grid.BufferView,{cacheSize:1000});
/***
 *author:chenming
 *data:2015-08-26
 * 修改所选择产品的角色的角色全选删除
 */
function selectAll(object){
	var a1=object.id.split(".PDMLinkProduct:")[0]+".PDMLinkProduct:";
	var a2="_"+object.id.split(".PDMLinkProduct:")[1].split("_")[1];
	var grid = Ext.getCmp("ProductRoleListBuilder");
	var selModel = grid.getSelectionModel();
	var selectedRows = selModel.getSelections();
	if (selectedRows.length > 0) {
		for (var i = 0; i < selectedRows.length; i++) {
			var temp = selectedRows[i].id;
			var a3=temp.split(".PDMLinkProduct:")[1].split("!")[0];
			var inputid = a1+a3+a2;
			if(document.getElementById(inputid)){
				document.getElementById(inputid).checked = object.checked;
			}
		}
	}
	selModel.clearSelections();
}


</script>



<%@ include file="/netmarkets/jsp/util/end.jspf"%>