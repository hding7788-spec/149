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
<%@ page import="ext.casc.sop.constants.SopConstants" %>

<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>

<%
WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
Map<String, List<WTUser>> map = ProcessUtil.getGroupAndUsersInOrgContainer();
Set<String> set = new TreeSet<String>();
set.addAll(map.keySet());
String msg = ProcessConstants.JSP_MSG_VALIDTE;
%>


<script language="javascript">
	
	function preSubmit(){
		//var number = document.getElementById("objnumber").value;
		//var name = document.getElementById("objname").value;
		//if((name == null && number == null)||(name == "" && number == "")){
			//alert("<%=msg%>");
			//return;
		//}
		setworktype("search");
		PTC.jca.table.Utils.reload('SearchProcessTask_table_id_gongyiyuan2', {}, true);					
    }
	
	function setworktype(worktype){
    	var ele=document.getElementById("work");
          	ele.value=worktype;  
    }
		
</script>

<input type="hidden" id="work" name="work" value=""/>
<input type="hidden" id="gongyiyuan" name="gongyiyuan" value="<%=currentUser.getName() %>"/>

<table>
	<tr>
		<td><h4><%=ProcessConstants.JSP_SEARCH_NUMBER%></h4></td>
		<td><input type="text" name="objnumber" id="objnumber"/></td>
	</tr>
	<tr>
		<td><h4><%=ProcessConstants.JSP_SEARCH_NAME%></h4></td>
		<td><input type="text" name="objname" id="objname"/></td>
	</tr>
	<tr>
		<td><h4><%=ProcessConstants.JSP_SEARCH_GONGYIYUAN%></h4></td>
		<td><input type="text" name="gongyiyuan2" id="gongyiyuan2" readOnly="true" value="<%=currentUser.getFullName() %>" /></td>
	</tr>
	<tr>
		<td><h4><%=ProcessConstants.JSP_SEARCH_TASKTYPE%></h4></td>
		<td>
			<select name="tasktype" id="tasktype" >
				<option value="" selected></options>
				<option value="<%=ProcessConstants.TASK_TYPE_LINSHIGONGYI %>"><%=ProcessConstants.TASK_TYPE_LINSHIGONGYI %></option>
				<option value="<%=ProcessConstants.TASK_TYPE_GONGYISHEJI %>"><%=ProcessConstants.TASK_TYPE_GONGYISHEJI %></option>
				<option value="<%=ProcessConstants.TASK_TYPE_GONGYIGENGGAI %>"><%=ProcessConstants.TASK_TYPE_GONGYIGENGGAI %></option>
				<option value="<%=SopConstants.SOP_TASK_TASKTYPE %>"><%=SopConstants.SOP_TASK_TASKTYPE %></option>
				<option value="<%=SopConstants.SOP_TASK_TASKTYPE_CHANGE %>"><%=SopConstants.SOP_TASK_TASKTYPE_CHANGE %></option>
			</select>
      </tr>
      <tr>
		<td><h4><%=ProcessConstants.JSP_SEARCH_TASKSTATE%></h4></td>
		<td>
			<select name="taskstate" id="taskstate" >
				<option value="" selected></option>
				<option value="<%=ProcessConstants.TASK_STATE_YIWANGONG %>"><%=ProcessConstants.TASK_STATE_YIWANGONG %></option>
				<option value="<%=ProcessConstants.TASK_STATE_JINGXINZHONG %>"><%=ProcessConstants.TASK_STATE_JINGXINZHONG %></option>
				<option value="<%=ProcessConstants.TASK_STATE_YIZUOFEI %>"><%=ProcessConstants.TASK_STATE_YIZUOFEI %></option>
			</select>       
      </tr>
      <tr>
      	<td><h4><%=ProcessConstants.JSP_SEARCH_ENDDATE%></h4></td>
      	<td>
			<input id="startDate" type="text" name="startDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
			<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('startDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
			<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>
			&nbsp&nbsp&nbsp&nbsp&nbsp<td align=center><h4><%=ProcessConstants.JSP_SEARCH_TO%></h4></td>
		<td>&nbsp&nbsp&nbsp&nbsp&nbsp
			<input id="endDate" type="text" name="endDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
			<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('endDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
			<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>
		</td>
		<td>&nbsp;&nbsp;</td>
		<td>&nbsp;&nbsp;</td>	
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

<jsp:include page="${mvc:getComponentURL('SearchProcessTask_table_id_gongyiyuan2')}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>