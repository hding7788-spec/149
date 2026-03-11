<%@page import="java.util.ArrayList"%>
<%@page import="ext.casc.process.mvc.builder.SearchDownloadPrintBuilder"%>
<%@page import="wt.part.WTPart"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@page import="wt.util.WTProperties"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />
<%-- <%@ include file="/netmarkets/jsp/util/begin.jspf"%> --%>
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
Object object = nmOid.getRefObject();
	ArrayList list=null;
	ArrayList list1=null;
if(object instanceof WTPart){
	list=SearchDownloadPrintBuilder.getPiCi(nmOid);
	list1=SearchDownloadPrintBuilder.getDocStyle(nmOid);
}

%>
<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>

<script language="javascript">

function preSubmit(){

	setworktype("search");
	PTC.jca.table.Utils.reload('SearchProcessTask_table_id_downloadPrint', {}, true);


	function setworktype(worktype){
    	var ele=document.getElementById("work");
          	ele.value=worktype;
    }
}
</script>
<input type="hidden" id="work" name="work" value=""/>
<table>
<br>
<br>
<br>
<br>
<br>
	<tr>
		<td><h4><%=ProcessConstants.PICI%></h4></td>
		<td>
		<select name="pici" id="pici" >
		<%for(int i=0;i<list.size();i++){ %>
         <option style="width:200px"><%=list.get(i) %></option>
         <%} %>
         </select>
         </td>
	</tr>
	<tr>
	<tr>
	<tr>
	<tr>
	<td><h4><%=ProcessConstants.DOC_STYLE%></h4></td>
	<td>
	<select name="docStyle" id="docStyle" >
			 <%for(int i=0;i<list1.size();i++){ %>
         <option style="width:200px"><%=list1.get(i) %></option>
         <%} %>

	</select>

	</td>
	</tr>
    <tr>
    <tr>
	<tr>
	</tr>
	<td></td>
	<tr>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align=center><h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" value="<%=ProcessConstants.JSP_SEARCH_SEARCHBUTTON%>" onclick="preSubmit();"/></h4></td>
	</tr>
</table>
<jsp:include page="${mvc:getComponentURL('SearchProcessTask_table_id_downloadPrint')}" />
<%@ include file="/netmarkets/jsp/util/end.jspf"%>