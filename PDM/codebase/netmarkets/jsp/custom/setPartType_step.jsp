<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ page import="wt.util.WTProperties"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />	
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<%@ page import="com.ptc.netmarkets.model.NmOid" %>
<script type="text/javascript">
<!--
	function setPartsType(object){
		var grid = Ext.getCmp("set_part_type");
		var selModel = grid.getSelectionModel();
		var selectedRows = selModel.getSelections();
		for(var i=0;i<selectedRows.length;i++){
			//var ss= selectedRows[i].get("partType");
			var temp = selectedRows[i].id;
			var n = temp.lastIndexOf("$");
			var selectx = temp.substring(n + 4, temp.length - 2);
			if(selectx.indexOf("^VR:") > -1){
				var ojbs = selectx.split("^VR:");
				selectx = ojbs[ojbs.length-1];
			}
			document.getElementById(selectx+"_select").value=object.value;
		}
		selModel.clearSelections();
	}
//-->
</script>
<jsp:include page="${mvc:getComponentURL('ext.casc.part.mvc.builder.SetPartTypeBuilder')}" />


<%@ include file="/netmarkets/jsp/util/end.jspf"%>