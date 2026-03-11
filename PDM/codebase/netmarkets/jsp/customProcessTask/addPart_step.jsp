<%@page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@page import="wt.util.WTProperties"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<%@ page import="java.util.Map,java.util.Locale"%>
<%@ page import="wt.org.WTUser, wt.session.SessionHelper"%>
<%@ page import="java.util.Calendar,java.text.SimpleDateFormat,java.util.GregorianCalendar"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="ext.casc.process.ProcessConstants" %>
<%@page import="ext.casc.process.util.ProcessUtil"%>

<script language="javascript">
function addParts(){
	var grid = Ext.getCmp("ext.casc.process.mvc.builder.SearchPartResultBilder");
	var selModel = grid.getSelectionModel();
	var selectedRows = selModel.getSelections();
	if (selectedRows.length > 0) {
		var grid2 = window.opener.Ext.getCmp('TEMP_ASSIGN_TASK');
		if(!grid2){
			  grid2 = window.opener.Ext.getCmp('PBOM_ASSIGN_TASK');
		 }
		for (var i = 0; i < selectedRows.length; i++) {
			  grid2.getStore().insert(0, selectedRows[i]);
		}
	}
}
	function preSubmit(){
		setworktype("search");
		PTC.jca.table.Utils.reload('ext.casc.process.mvc.builder.SearchPartResultBilder', {}, true);
    }

	function setworktype(worktype){
    	var ele=document.getElementById("work");
          	ele.value=worktype;
    }

</script>

<input type="hidden" id="work" name="work" value=""/>

<table>
	<tr>
		<td><h4><%=ProcessConstants.JSP_SEARCH_NUMBER%></h4></td>
		<td><input type="text" name="number" id="number"/></td>
	</tr>
	<tr>
		<td><h4><%=ProcessConstants.JSP_SEARCH_NAME%></h4></td>
		<td><input type="text" name="name" id="name"/></td>
	</tr>
	<tr>
		<td><h4><%=ProcessConstants.JSP_SEARCH_VERSION%></h4></td>
		<td><input type="text" name="version" id="version"/></td>
	</tr>
<br>
	<tr>
	</tr>
	<td></td>
	<tr>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align=center><h4><input type="button" value="<%=ProcessConstants.JSP_SEARCH_SEARCHBUTTON%>" onclick="preSubmit();"/></h4></td>
	</tr>
</table>

<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.SearchPartResultBilder')}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>