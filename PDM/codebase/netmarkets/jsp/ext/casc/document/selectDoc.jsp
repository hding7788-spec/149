<%@page import="java.util.List"%>
<%@page import="ext.casc.report.mvc.builders.GYWDHZBuilder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
 <%@page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@page import="wt.util.WTProperties"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<%@ page import="java.util.Map,java.util.Locale"%>
<%@ page import="wt.org.WTUser, wt.session.SessionHelper"%>
<%@ page import="java.util.Calendar,java.text.SimpleDateFormat,java.util.GregorianCalendar"%>
<%@ page import="java.net.*"%>
<%@ page import="ext.casc.process.ProcessConstants" %>
<%@page import="ext.casc.process.util.ProcessUtil"%>
<%
NmCommandBean cb = new NmCommandBean();
/* cb.setCompContext(NmContext.getContext().toString()); */
cb.setRequest(request);
NmOid nmOid = cb.getPrimaryOid();


%>
<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>
<script language="javascript">

function search(){

	setworktype("search");
	PTC.jca.table.Utils.reload('ext.casc.doc.mvc.builder.SearchDocBuilder', {}, true);
	function setworktype(worktype){
    	var ele=document.getElementById("work");
          	ele.value=worktype;
    }
}

function preSubmit(){
	var grid = Ext.getCmp("ext.casc.doc.mvc.builder.SearchDocBuilder");
	window.opener.document.getElementById('REPLACE').value = "";
	var selModel = grid.getSelectionModel();
	var selectedRows = selModel.getSelections();
	if (selectedRows.length > 0) {
		for (var i = 0; i < selectedRows.length; i++) {
			//var ss= selectedRows[i].get("partType");
			var temp = selectedRows[i].get("number");
        	window.opener.document.getElementById('REPLACE').value = temp;
		}
	}
	window.close();
}



</script>
<input type="hidden" id="work" name="work" value=""/>
<table>
<tr>
<td><%= ProcessConstants.NUMBER%></td>
<td><input id="number" name="number" type="text" style="width:200px"></td>
</tr>
<tr>
<td><%= ProcessConstants.NAME%></td>
<td><input id="name" name="name" type="text" style="width:200px"></td>
</tr>
<tr>
<td><h4><%=ProcessConstants.SHOUKONGGCONG%></h4></td>
      	<td>
			<input id="startDate" type="text" name="startDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
			<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('startDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
			<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>
			<td ><h4><%=ProcessConstants.SHOUKONGZHI%></h4></td>
		<td>
			<input id="endDate" type="text" name="endDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
			<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('endDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
			<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>
		</td>
</tr>
<tr>
<td><h4><input type="button" value="<%=ProcessConstants.JSP_SEARCH_SEARCHBUTTON%>" onclick="search();"/></h4></td>
<td><h4><input type="button" value="<%=ProcessConstants.JSP_BUTTON_OK%>" onclick="preSubmit();"/></h4></td>

</tr>
<tr>
</table>
<jsp:include page="${mvc:getComponentURL('ext.casc.doc.mvc.builder.SearchDocBuilder')}" />
<%@ include file="/netmarkets/jsp/util/end.jspf"%>