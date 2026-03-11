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
cb.setRequest(request);
NmOid nmOid = cb.getPrimaryOid();
%>
<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>

<script language="javascript">

function preSubmit(){

	setworktype("search");
	PTC.jca.table.Utils.reload('SearchSop_table_id_technicsYiJv', {}, true);
	function setworktype(worktype){
    	var ele=document.getElementById("work");
          	ele.value=worktype;
    }
}

function changeJieshouzhe(){
	var e = document.getElementById("bianzhizhe$label$");
	var obj = document.getElementById("bianzhizhe");
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
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</td>
</tr>
<tr>
<wctags:userPicker id="bianzhizhe" label="±àÖÆÕß:" />
<td>
<input type="button" value="Çå³ý"  onclick="changeJieshouzhe()">
</td>
</tr>

<tr>
<td><h4><%= SopConstants.JSP_SEARCH_GISTNUMBER%></h4></td>
<td><input id="gistNumber" name="gistNumber" type="text" style="width:200px"></td>
</tr>
<tr>
<td><h4><%= SopConstants.JSP_SEARCH_GISTNAME%></h4></td>
<td><input id="gistName" name="gistName" type="text" style="width:200px"></td>
</tr>
<tr>
<td><h4><%= SopConstants.JSP_SEARCH_TECHNICSNUMBER%></h4></td>
<td><input id="sopTechnicsNumber" name="sopTechnicsNumber" type="text" style="width:200px"></td>
</tr>
<tr>
<td><h4><%= SopConstants.JSP_SEARCH_TECHNICSNAME%></h4></td>
<td><input id="sopTechnicsName" name="sopTechnicsName" type="text" style="width:200px"></td>
</tr>
<tr>
		<td><h4><%=SopConstants.JSP_SEARCH_TECHNICSTYPE%></h4></td>
		<td>
			<select name="tectype" id="tectype" >
				<option value="" selected></options>
				<option value="<%=SopConstants.JSP_SEARCH_GISTTECHNICS %>"><%=SopConstants.JSP_SEARCH_GISTTECHNICS %></option>
				<option value="<%=SopConstants.JSP_SEARCH_SOPTECHNICS %>"><%=SopConstants.JSP_SEARCH_SOPTECHNICS %></option>
			</select>
      </tr>

<tr>


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
<jsp:include page="${mvc:getComponentURL('SearchSop_table_id_technicsYiJv')}" />
<%@ include file="/netmarkets/jsp/util/end.jspf"%>