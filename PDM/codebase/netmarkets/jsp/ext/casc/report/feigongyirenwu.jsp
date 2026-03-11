<%@page import="java.util.ArrayList"%>
<%@page import="ext.casc.constants.Constants"%>
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
ArrayList list=null;
list=ProcessUtil.getAllCheJian();
%>
<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>

<script language="javascript">

function preSubmit(){

	setworktype("search");
	PTC.jca.table.Utils.reload('SearchGONGYIRENWU_table_id_fgyrw', {}, true);
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
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</td>
</tr>
<tr>
<tr>
<wctags:contextPicker id="containerTypeList" pickerTitle="" label="上下文:" customAccessController="com.ptc.windchill.enterprise.preference.PreferenceContextAccessController" multiSelect="false" pickerTextBoxLength="15" pickerType="search" readOnlyPickerTextBox="true"/>
<td>
<input type="button" value="清除"  onclick="changeContainer()">
</td>
</tr>
</tr>
<tr>
<td>
			<b>计划主题:</b>
		</td>

		<td>
		<select name="zhuti" id="zhuti" style='width:150px'>
				<option value="" selected></options>
			    <%for(String title:ProcessConstants.titles){%>
			    	<option value=<%=title%>><%=title%></options>
			    <%}%>
         </select>
		</td>
</tr>
<tr>
 <td>
	  <w:label value="开始时间范围"/>
	</td>
    <td >
     <%-- <w:dateInputComponent name="startdate" required="true" dateValueType="DATE_ONLY"/>
<w:dateInputComponent name="enddate" required="true" dateValueType="DATE_ONLY"/> --%>
			<input id="startDate" type="text" name="startDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
			<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('startDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
			<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>
			<input id="endDate" type="text" name="endDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
			<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('endDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
			<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>
    </td>
</tr>
<!-- <tr>
<td>
			<b>项目部:</b>
		</td>
		<td>
			<input type="text" name="xiangmubu" id="xiangmubu"/>
		</td>
	</tr> -->
 <tr>
<td>
			<b>项目部:</b>
		</td>

		<td>
		<select name="xiangmubu" id="xiangmubu" style='width:150px'>
				<option value="" selected></options>
			    <%
                    for(String bumen:Constants.allBumen){%>
                      <option value=<%=bumen %>><%=bumen%></options>
                    <%}
			    %>
         </select>
		</td>
</tr>
<tr>
<td>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
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
<jsp:include page="${mvc:getComponentURL('SearchGONGYIRENWU_table_id_fgyrw')}" />
<%@ include file="/netmarkets/jsp/util/end.jspf"%>