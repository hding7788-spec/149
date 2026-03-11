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
<%
NmCommandBean cb = new NmCommandBean();
/* cb.setCompContext(NmContext.getContext().toString()); */
cb.setRequest(request);
NmOid nmOid = cb.getPrimaryOid();

%>
<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>
<script type="text/javascript">
	PTC.navigation.loadScript("netmarkets/javascript/casc/jquery16.js");
	var $j = jQuery.noConflict();
</script>
<script language="javascript">

function preSubmit(){
	debugger;
	var hj = $j("#lchj").val();
	var fzr = $j("#lchjfzr").val();
	if(((hj != null && hj != '') && (fzr == null || fzr == '')) || ((fzr != null && fzr != '') && (hj == null || hj == ''))){
		alert("流程环节和负责人必须同时填写或都为空!");
		return;
	}

	setworktype("search");
	PTC.jca.table.Utils.reload('SearchWorkItem_table_id_lcqsz', {}, true);
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

function changeLchjfzr(){
	var e = document.getElementById("lchjfzr$label$");
	var obj = document.getElementById("lchjfzr");
	if(obj!=null){
		obj.value="";
		e.value="";
	}
}

function changeLcmc() {
	$j.ajax({
		type: "POST",
		url: "netmarkets/jsp/ext/casc/report/getLchd.jsp",
		data: "lcmc=" + $j("#lcmc").val(),
		success: function (msg) {
			msg = msg.trim();
			/**清空下拉框值*/
			deleteTypeOptions('lchj');
			/**添加新的值*/
			addTypeOptions(msg);
		}
	});
}

function deleteTypeOptions(typeString) {
	var obj = document.getElementById(typeString);
	obj.options.length = 0;
}


function addTypeOptions(typesString) {
	var obj = document.getElementById('lchj');
	var typeArray = typesString.substring(1, typesString.length - 1).split(",");
	obj.options.add(new Option("", ""));
	for (var i = 0; i < typeArray.length; i++) {
		var str = typeArray[i].split("=");
		var keyStr = str[0].trim();
		var valueStr = str[1].trim();
		obj.options.add(new Option(keyStr, valueStr)); //这个兼容IE与firefox
	}
}

</script>
<input type="hidden" id="work" name="work" value=""/>
<table>
<tr>
<wctags:contextPicker id="containerTypeList" pickerTitle="" label="上下文:" customAccessController="com.ptc.windchill.enterprise.preference.PreferenceContextAccessController" multiSelect="false" pickerTextBoxLength="15" pickerType="search" readOnlyPickerTextBox="true"/>
<td>
<input type="button" value="清除"  onclick="changeContainer()">
</td>
</tr>
<tr>
<wctags:userPicker id="applicant" label="启动者" />
<td>
<input type="button" value="清除"  onclick="changAapplicant()">
</td>
</tr>
<tr>
<td><%=ProcessConstants.QIDONGCONG %></td>
<%-- <td><w:dateInputComponent name="startdate" required="true" dateValueType="DATE_ONLY"/>
</td> --%>

<td>
<input id="startDate" type="text" name="startDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('startDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>
</td></tr>
<tr>
<td><%=ProcessConstants.QIDONGZHI %></td>
<%-- <td>
<w:dateInputComponent name="enddate" required="true" dateValueType="DATE_ONLY"/>
</td> --%>
<td>
<input id="endDate" type="text" name="endDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('endDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>
</td>
</tr>
<tr>
<td><%=ProcessConstants.SHUJULEIXING %></td>
<td>
		<select name="sjlx" id="sjlx" >
		<option style="width:200px"> </option>
         <option style="width:200px"> <%=ProcessConstants.PART %></option>
         <option style="width:200px"> <%=ProcessConstants.DOCUMENT %></option>
         <option style="width:200px"> <%=ProcessConstants.ProcessEnvelope %></option>

         </select>
         </td>
</tr>
<tr>
<td><%=ProcessConstants.LIUCHENGZHUANGTAI %></td>
<td>
		<select name="lczt" id="lczt" >
		<option style="width:200px"> </option>
         <option style="width:200px"> <%=ProcessConstants.PROCESS_STATE_ZHENGZAIYUNXING %></option>
         <option style="width:200px"> <%=ProcessConstants.PROCESS_STATE_YIZHIXING %></option>
         <option style="width:200px"> <%=ProcessConstants.PROCESS_STATE_ZHONGZHI %></option>

         </select>
         </td>
</tr>
<tr>
	<td><%=ProcessConstants.LIUCHENGNAME %></td>
	<td>
		<select name="lcmc" id="lcmc" size="1" onchange="changeLcmc()" >
			<option style="width:800px"> </option>
			<option style="width:800px"> <%=ProcessConstants.LC_1 %></option>
			<option style="width:800px"> <%=ProcessConstants.LC_2 %></option>
			<option style="width:800px"> <%=ProcessConstants.LC_4 %></option>
			<option style="width:800px"> <%=ProcessConstants.LC_6 %></option>
			<option style="width:800px"> <%=ProcessConstants.LC_7 %></option>
			<option style="width:800px"> <%=ProcessConstants.LC_8 %></option>
			<option style="width:800px"> <%=ProcessConstants.LC_9 %></option>
			<option style="width:800px"> <%=ProcessConstants.LC_10 %></option>
		</select>
	</td>
</tr>
<tr>
	<td><%=ProcessConstants.LIUCHENGHUANJIE %></td>
	<td>
		<select name="lchj" id="lchj" >
		</select>
	</td>
</tr>
<tr>
	<wctags:userPicker id="lchjfzr" label="流程环节负责人" />
	<td>
		<input type="button" value="清除"  onclick="changeLchjfzr()">
	</td>
</tr>
<%-- <tr>
<td><%=ProcessConstants.WENJIANMINGCHENG %></td>
 <td><input type="text" name="name" value=""></td>
</tr>
<tr>
<td><%=ProcessConstants.WENJIANBIANHAO %></td>
<td><input type="text" name="number" value=""></td>
</tr> --%>
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
<jsp:include page="${mvc:getComponentURL('SearchWorkItem_table_id_lcqsz')}" />
<%@ include file="/netmarkets/jsp/util/end.jspf"%>