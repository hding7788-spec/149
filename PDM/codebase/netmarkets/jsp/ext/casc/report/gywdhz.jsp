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
 <%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%-- <%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%> --%>
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

	ArrayList<String[]> list=null;
	list=GYWDHZBuilder.getDocStyle();
	Map<String,String> docStateMap=null;
	docStateMap=ProcessConstants.map;

	ArrayList cheJianList=null;
	cheJianList=ProcessUtil.getAllCheJian();

%>
<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>
<script language="javascript">

function preSubmit(){

	setworktype("search");
	PTC.jca.table.Utils.reload('SearchWorkItem_table_id_gywdhz', {}, true);
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
function changAapplicant(){
	var e = document.getElementById("applicant$label$");
	var obj = document.getElementById("applicant");
	if(obj!=null){
	obj.value="";
	e.value="";
	}
}
</script>
<input type="hidden" id="work" name="work" value=""/>
<table>
<wctags:contextPicker id="containerTypeList" pickerTitle="" label="上下文:" customAccessController="com.ptc.windchill.enterprise.preference.PreferenceContextAccessController" multiSelect="false" pickerTextBoxLength="15" pickerType="search" readOnlyPickerTextBox="true"/>
<td>
<input type="button" value="清除"  onclick="changeContainer()">
</td>
<tr>
<wctags:userPicker id="applicant" label="创建者" />
<td>
<input type="button" value="清除"  onclick="changAapplicant()">
</td>
</tr>
<tr>
<td><%=ProcessConstants.SHENGMINGZHOUQIZHUANGTAI %></td>
<td><select name="smzqzt" id="smzqzt" >
<option style="width:200px"> </option>
      <%for(String s:docStateMap.keySet()) {%>
      <option style="width:200px" value="<%=s%>"> <%=docStateMap.get(s) %></option>
      <%} %>
         </select>
</td>
</tr>
<%-- <tr>
<td><%=ProcessConstants.CHUANGJIANSHIJIAN %></td>
<td>
<w:dateInputComponent name="startdate" required="true" dateValueType="DATE_ONLY"/>
<w:dateInputComponent name="enddate" required="true" dateValueType="DATE_ONLY"/>
</td>
</tr> --%>
<tr>
<td><%=ProcessConstants.DOC_STYLE %></td>
<td>
<select name="docStyle" id="docStyle" >
 <option style="width:200px"> </option>
<%for(int i=0;i<list.size();i++){ %>

         <option style="width:200px" value=<%=list.get(i)[1] %>> <%=list.get(i)[0] %></option>
<%} %>
         </select>
         </td>
</tr>

<%-- <tr>
<td><%=ProcessConstants.ZHUZHICHEJIAN %></td>
<td><select name="chejian" id="chejian" >
 <option style="width:200px"> </option>
<%for(int i=0;i<cheJianList.size();i++){ %>

         <option style="width:200px" value=<%=cheJianList.get(i) %>> <%=cheJianList.get(i) %></option>
<%} %>
         </select>
         </td>
</tr> --%>
<tr>
<td><%= ProcessConstants.JSP_SEARCH_NUMBER%></td>
<td><input id="partNumber" name="partNumber" type="text" style="width:200px"></td>
</tr>

<tr>
<td><%= ProcessConstants.DAYINZHUANGTAI%></td>
<td><select name="dayinFlag" id="dayinFlag" >
 <option style="width:200px"> </option>
 <option style="width:200px"><%=ProcessConstants.TASK_ISZHUZHI_SHI %> </option>
 <option style="width:200px"><%=ProcessConstants.TASK_ISZHUZHI_FOU %> </option>
 </td>
</tr>

<tr>
<td><%= ProcessConstants.GONGYIWENJIANLEIXING%></td>
<td><select name="technicsStyle" id="technicsStyle" >
 <option style="width:200px"> </option>
 <option style="width:200px"><%=ProcessConstants.ZHENGSHIGONGYIWENJIAN %> </option>
 <option style="width:200px"><%=ProcessConstants.LINSHIGONGYIWENJIAN %> </option>
 </td>
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
<jsp:include page="${mvc:getComponentURL('SearchWorkItem_table_id_gywdhz')}" />
<%@ include file="/netmarkets/jsp/util/end.jspf"%>