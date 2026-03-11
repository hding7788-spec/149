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
<%@ page import="ext.casc.constants.Constants"%>
<%@ page import="java.net.*"%>
<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>

<fmt:setBundle basename="ext.ases.dataSearch.resource.viewHelperResource" />
<fmt:message var="dataTransferTitle" key="DATATRANSFERTITLE" />
<fmt:message var="number" key="NUMBER" />
<fmt:message var="name" key="NAME" />
<fmt:message var="package_num" key="PACKAGE_NUM" />
<fmt:message var="send_date" key="SEND_DATE" />
<fmt:message var="from" key="FROM" />
<fmt:message var="to" key="TO" />
<fmt:message var="product_code" key="PRODUCT_CODE" />
<fmt:message var="send_type" key="SEND_TYPE" />
<fmt:message var="carft_sign" key="CRAFTSIGN" />
<fmt:message var="sendindueform" key="SENDINDUEFORM" />
<fmt:message var="search" key="SEARCH" />
<fmt:message var="search_result" key="SEARCH_RESULT" />
<fmt:message var="obj_version" key="OBJ_VERSION" />

<script language="javascript">
	
	function submitQuery(){
		document.getElementById("searchFlag").value="search";
		document.getElementById("sendStart").value=document.getElementById("startDate").value;
		document.getElementById("sendEnd").value=document.getElementById("endDate").value;
		PTC.jca.table.Utils.reload('ext.ases.dataSearch.mvc.builder.DataSendRecordBuilder', {}, true);					
    }
	
	function changetype(){
		//alert("wag");
		var varsel = document.getElementById("botypeselector").value;
		//alert(varsel);
		
		if(varsel=="YF"){
			//alert("this is SJ");
			document.getElementById("seltasktype").value = "YF";
		}else{
			document.getElementById("seltasktype").value= "SJ";
		}
	}
		
</script>

<fieldset >
	<legend>
	    <font size="3px" style="font-weight: bold;"> ${dataTransferTitle}</font>
	</legend>
</fieldset>

<table border="0" >

 <input type="hidden"  name="null___searchFlag___textbox" id ="searchFlag" value="${searchFlag_default}" >
 <input type="hidden"  name="null___sendStart___textbox" id ="sendStart" value="" >
 <input type="hidden"  name="null___sendEnd___textbox" id ="sendEnd" value="" >

	<tr id="package_search">
	 <td>
	   <label for="number"><font style="font-weight: bold;"> ${number}: </font></label>
       &nbsp;&nbsp;
	  <input type="text" value="${number_default}" name="null___number___textbox" id="number" maxlength="100" required="">
	  
	   &nbsp;&nbsp;&nbsp;&nbsp;
	   
	   <label for="number"><font style="font-weight: bold;"> ${name}: </font></label>
	   &nbsp;&nbsp;
	  <input type="text" value="${name_default}" name="null___name___textbox" id="name" maxlength="100" required="">
	 </td>
	  
	</tr>
	
	<tr id="package_search">
	 <td>
	   <label for="package_num"><font style="font-weight: bold;"> ${package_num}: </font></label>
       &nbsp;&nbsp;
	   <input type="text" value="${packNum_default}" name="null___packNum___textbox" id="packNum" maxlength="100" required="">
	 </td>

	</tr>
      <tr>
      	<td><label for="startDate"><font style="font-weight: bold;"> ${send_date}: </font></label>
<input id="startDate" type="text" name="startDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('startDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>
&nbsp&nbsp&nbsp&nbsp&nbsp<label for="endDate"><font style="font-weight: bold;"> ${to}: </font></label>
&nbsp&nbsp&nbsp&nbsp&nbsp
<input id="endDate" type="text" name="endDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('endDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>
</td>
	</tr><tr>
	 <td>
	   <label for="send_type"><font style="font-weight: bold;"> ${send_type}: </font></label>
       &nbsp;&nbsp;
	  <select  name="null___sendtype___textbox" id="send_type" maxlength="100" required="">
	  
	    <option value=""></option>
	    <option value="${carft_sign }">${carft_sign }</option>
	    <option value="${sendindueform }">${sendindueform }</option>
	  </select>
	  
	   &nbsp;&nbsp;&nbsp;&nbsp;
	  <span id="pc">
	   <label for="product_code"><font style="font-weight: bold;"> ${product_code}: </font></label>
	   &nbsp;&nbsp;
	  <input type="text" value="${product_code_default}" name="null___pc___textbox" id="product_code" maxlength="100" required="">
	  </span> 
	 </td>	  
	</tr>
	<tr/>
	<tr>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" value="${search}" onclick="submitQuery();"/></td>
	</tr>
</table>
<br/>
<fieldset >
	<legend>
	    <font style="font-weight: bold;"> ${search_result}</font>
	</legend>
</fieldset>

<jsp:include page="${mvc:getComponentURL('ext.ases.dataSearch.mvc.builder.DataSendRecordBuilder')}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>