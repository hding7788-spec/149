<%@page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>


<script language="javascript">

    function preSubmit() {
        setworktype("search");
        document.getElementById("sendStart").value=document.getElementById("startDate").value;
		document.getElementById("sendEnd").value=document.getElementById("endDate").value;
		document.getElementById("msgType").value=document.getElementById("msgT").value;
        PTC.jca.table.Utils.reload('ext.sast.center.mvc.builders.CenterWorkflowInfoNewTableBuilder', {}, true);
    }

    function setworktype(worktype) {
        var ele = document.getElementById("work");
        ele.value = worktype;
    }

</script>

<input type="hidden" id="work" name="work" value=""/>
<input type="hidden"  name="null___sendStart___textbox" id ="sendStart" value="" >
 <input type="hidden"  name="null___sendEnd___textbox" id ="sendEnd" value="" >
 <input type="hidden"  name="null___msgType___textbox" id ="msgType" value="" >

<table>
    <tr>
        <td><label for="startDate"><font style="font-weight: bold;">开始时间: </font></label>
<input id="startDate" type="text" name="startDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('startDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>
&nbsp&nbsp&nbsp&nbsp&nbsp<label for="endDate"><font style="font-weight: bold;"> 结束时间: </font></label>
<input id="endDate" type="text" name="endDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('endDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</font>
</td>

<tr>
	<td><font style="font-weight: bold;"> 单据类型: </font><select name="msgType" id="msgT">
		<option > </option>
		<option style="width:100px" value="送审单" >送审单</option>
		<option style="width:100px" value="发放单" >发放单</option>
		<option style="width:100px" value="预审单" >预审单</option>
		<option style="width:100px" value="变更单" >变更单</option>
	</select>&nbsp&nbsp&nbsp<input type="button" value="查 询" onclick="preSubmit();"/>&nbsp;&nbsp;&nbsp;</td>
    </tr>

</table>

<jsp:include page="${mvc:getComponentURL('ext.sast.center.mvc.builders.CenterWorkflowInfoNewTableBuilder')}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>