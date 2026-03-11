<%@page import="ext.casc.sop.constants.SopConstants"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
 <%@page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@page import="wt.util.WTProperties"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />
 <%-- <%@ include file="/netmarkets/jsp/util/begin.jspf"%> --%>
 <%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%-- <%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%> --%>
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

function preSubmit(){

	setworktype("search");
	PTC.jca.table.Utils.reload('SearchSop_table_id_technicsQuote', {}, true);
	function setworktype(worktype){
    	var ele=document.getElementById("work");
          	ele.value=worktype;
    }
}

function changeContainer(){
	var e = document.getElementById("containerTypeList$label$");
	var obj = document.getElementById("containerTypeList");
	if(obj!=null){
	obj.value="";
	e.value="";
	}
}

</script>
<input type="hidden" id="work" name="work" value=""/>
<table>
<tr>
<td>
&nbsp;&nbsp;&nbsp;
</td>
</tr>

<tr>
<wctags:contextPicker  id="containerTypeList" pickerTitle="" label="上下文:" customAccessController="com.ptc.windchill.enterprise.preference.PreferenceContextAccessController" multiSelect="false" pickerTextBoxLength="20" readOnlyPickerTextBox="true" pickerType="search" />
<td>
<input type="button" value="清除"  onclick="changeContainer()">
</td>
</tr>
<tr>
<td><h4><%= SopConstants.JSP_SEARCH_TECHNICSNUMBER%></h4></td>
<td><input id="technicsNumber" name="technicsNumber" type="text" style="width:200px"></td>
</tr>
<tr>
<td><h4><%= SopConstants.JSP_SEARCH_TECHNICSNAME%></h4></td>
<td><input id="technicsName" name="technicsName" type="text" style="width:200px"></td>
</tr>
<tr>
<td><h4><%= SopConstants.JSP_SEARCH_SOPTECHNICSNUMBER%></h4></td>
<td><input id="sopTechnicsNumber" name="sopTechnicsNumber" type="text" style="width:200px"></td>
</tr>
<tr>
<td><h4><%= SopConstants.JSP_SEARCH_SOPTECHNICSNAME%></h4></td>
<td><input id="sopTechnicsName" name="sopTechnicsName" type="text" style="width:200px"></td>
</tr>



<tr>
<td>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</td>
</tr>
<tr>
<td>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</td>
</tr>
<tr>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align=center><h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" value="<%=ProcessConstants.JSP_SEARCH_SEARCHBUTTON%>" onclick="preSubmit();"/></h4></td>

		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="reset" value="<%=ProcessConstants.JSP_SEARCH_RESET%>" /></td>
	</tr>
</table>
<jsp:include page="${mvc:getComponentURL('SearchSop_table_id_technicsQuote')}" />
<%@ include file="/netmarkets/jsp/util/end.jspf"%>