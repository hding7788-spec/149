<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<%@ page import="ext.casc.analysisActivity.helper.AnalysisConstant" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="ext.casc.util.WTUserUtil" %>
<%@ page import="wt.session.SessionHelper" %>
<%@ page import="wt.org.WTUser" %>

<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>

<%
	List<String> depts = new ArrayList<String>();
	depts.add("");
	depts.add("1");
	depts.add("2");
	depts.add("3");
	depts.add("4");
	depts.add("5");
	depts.add("6");
	depts.add("7");
	depts.add("8");
	depts.add("9");
	depts.add("10");
	depts.add("事业四部");
	request.setAttribute("depts", depts);

	WTUser user = (WTUser) SessionHelper.getPrincipal();
	String dept = WTUserUtil.getDeptShortName(user);
	String contextPath = request.getContextPath();
%>

<script language="javascript">
	function preSubmit(){
		setworktype("search");
		PTC.jca.table.Utils.reload('ext.casc.common.mvc.builder.analysis.SearchDeptDesignAnalysisActivityBuilder', {}, true);
	}

	function setworktype(worktype){
    	var ele=document.getElementById("work");
		ele.value=worktype;
    }

	function changeContainer(){
		var e = document.getElementById("containerTypeList$label$");
		var obj = document.getElementById("containerTypeList");
		if(obj!=null){
			obj.value="";
			e.value="";
		}
	}

	function changeResponser(){
		var e = document.getElementById("responser$label$");
		var obj = document.getElementById("responser");
		if(obj!=null){
			obj.value="";
			e.value="";
		}
	}

	Ext.getDom('dept').value = '<%=dept%>';

	function viewAnalysis(analysisIda2a2) {
		var url =" <%=contextPath%>/app/#ptc1/tcomp/infoPage?oid="+analysisIda2a2;
		window.open(url, '_blank');
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
		<td><h4><%=AnalysisConstant.SEARCH_TYPE %></h4></td>
		<td>
			<select name="type" id="type" >
				<option style="width:200px" value="<%=AnalysisConstant.SEARCH_TYPE_ALL%>"> <%=AnalysisConstant.SEARCH_TYPE_ALL %></option>
				<option style="width:200px" value="<%=AnalysisConstant.SEARCH_TYPE_SHEJIGENGAI%>"> <%=AnalysisConstant.SEARCH_TYPE_SHEJIGENGAI %></option>
				<option style="width:200px" value="<%=AnalysisConstant.SEARCH_TYPE_SHEJIPIANLI %>"> <%=AnalysisConstant.SEARCH_TYPE_SHEJIPIANLI %></option>
			</select>
		</td>
	</tr>

	<tr>
		<td><h4><%=AnalysisConstant.SEARCH_ANANUMBER%></h4></td>
		<td><input type="text" name="ananumber" id="ananumber"/></td>
	</tr>

	<tr>
		<td><h4><%=AnalysisConstant.SEARCH_OBJNUMBER%></h4></td>
		<td><input type="text" name="objnumber" id="objnumber"/></td>
	</tr>

	<tr>
		<td><h4><%=AnalysisConstant.SEARCH_OBJNAME%></h4></td>
		<td><input type="text" name="objname" id="objname"/></td>
	</tr>

	<tr>
		<td><h4><%=AnalysisConstant.SEARCH_STATE %></h4></td>
		<td>
			<select name="state" id="state" >
				<option style="width:200px"> </option>
				<option style="width:200px" > <%=AnalysisConstant.SEARCH_STATE_INWORK %></option>
				<option style="width:200px" > <%=AnalysisConstant.SEARCH_STATE_APPROVED %></option>
			</select>
		</td>
	</tr>

	<tr>
		<td><h4><%=AnalysisConstant.SEARCH_TASKTYPE %></h4></td>
		<td>
			<select name="tasktype" id="tasktype" >
				<option style="width:200px"> </option>
				<option style="width:200px" value="<%=AnalysisConstant.TYPE_PBOM%>" > <%=AnalysisConstant.SEARCH_TASKTYPE_PBOM %></option>
				<option style="width:200px" value="<%=AnalysisConstant.TYPE_TECHNICS%>" > <%=AnalysisConstant.SEARCH_TASKTYPE_TEC %></option>
				<option style="width:200px" value="<%=AnalysisConstant.PRODUCT%>" > <%=AnalysisConstant.SEARCH_TASKTYPE_ZP %></option>
			</select>
		</td>
	</tr>

	<tr>
		<td><h4><%=AnalysisConstant.SEARCH_DEPT %></h4></td>
		<td>
			<select name="dept" id="dept">
				<c:forEach items="${depts}" var="item">
					<option value="${item}" <c:if test="${item eq dept}">selected</c:if>>${item}</option>
				</c:forEach>
			</select>
		</td>
	</tr>

	<tr>
		<wctags:userPicker id="responser" multiSelect="false" pickerTextBoxLength="15" label="责任人:" />
		<td>
			<input type="button" value="清除"  onclick="changeResponser()">
		</td>
	</tr>

	<tr>
		<td><h4><%=AnalysisConstant.SEARCH_TIME%></h4></td>
		<td>
			<input id="startDate" type="text" name="startDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
			<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('startDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
				<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>
			&nbsp&nbsp&nbsp&nbsp&nbsp<td align=center><h4><%=AnalysisConstant.SEARCH_TIME_TO%></h4></td>
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
		<td align=center><h4><input type="button" value="<%=AnalysisConstant.SEARCH_SEARCHBUTTON%>" onclick="preSubmit();"/></h4></td>
	</tr>
</table>

<jsp:include page="${mvc:getComponentURL('ext.casc.common.mvc.builder.analysis.SearchDeptDesignAnalysisActivityBuilder')}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>