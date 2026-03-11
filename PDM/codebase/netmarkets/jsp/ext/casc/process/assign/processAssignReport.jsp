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
<%
	String strCodeBase = WTProperties.getLocalProperties().getProperty("wt.server.codebase", null);
	String productcode = (String)request.getParameter("productcode");
	if(productcode==null) productcode=""; 
	String botype = (String)request.getParameter("botype");
	if(botype==null) botype="";
	
	String startDate=request.getParameter("startDate");
	String endDate=request.getParameter("endDate");
	
	if(startDate==null) startDate="";
		if(startDate==null) endDate="";		

	// ArrayList ppcodes=BorrowUtil.BOUServer.findProductCodes("");
	String work="";
	request.setAttribute("productcode", productcode);
	request.setAttribute("botype", botype);
	request.setAttribute("startDate",startDate);
	request.setAttribute("endDate",endDate);
%>

<script language="javascript">
	
	function preSubmit(){
		setworktype("search");
		PTC.jca.table.Utils.reload('ext.casc.process.mvc.builder.ProcessAssignReportBuilder', {}, true);					
    }
	
	function setworktype(worktype){
    	var ele=document.getElementById("work");     
          	ele.value=worktype;  
    }
	
	function SearchProductCode(){
    	var bopanel = Ext.getBody();
    	bopanel.mask('<img src=\'netmarkets/javascript/ext/resources/images/default/shared/blue-loading.gif\'/><span>Loading...</span>');
    	Ext.Ajax.request({     
    	       url : 'ptc1/custom',  
    	       params : {
    	    	method : 'prodcodeshtml',
    	        pattern : Ext.get('productenditemcode').getValue(false)
    	       }, 
    	       success: function(resp, opts) {   
	    	     var vali = document.getElementById("pcodelist");
	    	     vali.innerHTML = resp.responseText;
	    	     bopanel.unmask();
               },   
    	       failure: function(resp,opts) {   
    	          Ext.Msg.alert('AJAX ERROR', 'Ajax request failed.');
 	              bopanel.unmask();
               }
        });
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

<input type="hidden" id="productcode" name="productcode" value="<%=productcode%>"/>
<input type="hidden" id="botype" name="botype" value="<%=botype%>"/>
<input type="hidden" id="work" name="work" value=""/>

<table>
	<tr>
		<td><%=Constants.PROCESS_ASSIGN_TASKTYPE%></td>
		<td>
			<select name="seltasktype" id="seltasktype" onChange="changetype()">
				<option value="" selected></options>
				<option value="工艺设计"><%=Constants.PROCESS_ASSIGN_TASKTYPE_GONGYISHEJI%></options>
				<option value="工艺更改"><%=Constants.PROCESS_ASSIGN_TASKTYPE_GONGYIGENGGAI%></options>
				<option value="零部件工艺"><%=Constants.PROCESS_ASSIGN_TASKTYPE_LINGBUJIANGONGYI%></options>
				<option value="临时工艺"><%=Constants.PROCESS_ASSIGN_TASKTYPE_LINSHIGONGYI%></options>
			</select>
		</td>
	</tr>
	<tr>
		<td><%=Constants.PROCESS_ASSIGN_TASKSTATE%></td>
		<td>
			<select name="seltasktype" id="seltasktype" onChange="changetype()">
				<option value="" selected></options>
				<option value="完工"><%=Constants.PROCESS_ASSIGN_TASKSTATE_WANGONG%></options>
				<option value="进行中"><%=Constants.PROCESS_ASSIGN_TASKSTATE_JINXINGZHONG%></options>
			</select>
		</td>
	</tr>
	<tr>
		<td><%=Constants.PROCESS_ASSIGN_SHIFOUZHUZHICHEJIAN%></td>
		<td>
			<select name="seltasktype" id="seltasktype" onChange="changetype()">
				<option value="" selected></options>
				<option value="主制车间"><%=Constants.PROCESS_ASSIGN_ZHUZHICHEJIAN%></options>
				<option value="辅制车间"><%=Constants.PROCESS_ASSIGN_FUZHICHEJIAN%></options>
			</select>
		</td>
	</tr>
	<tr>
		<td><%=Constants.PROCESS_ASSIGN_GONGYIYUAN%></td>
		<td><input type="text" name="gongyiyuan" id="gongyiyuan" readonly/></td>
	</tr>
      <tr>
      	<td><%=Constants.PROCESS_ASSIGN_DATE%></td>
      		<td>
<input id="startDate" type="text" name="startDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('startDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>
&nbsp&nbsp&nbsp&nbsp&nbsp<td align=center><%=Constants.PROCESS_ASSIGN_DATETO%></td>
<td>&nbsp&nbsp&nbsp&nbsp&nbsp
<input id="endDate" type="text" name="endDate" onBlur="validateDate1(this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" onkeypress="validateDate1ForEnterKey(event , this, 'com.ptc.core.ui.componentRB.DATE_ERROR', 3, 2, 1, 'yyyy/MM/dd', true)" value=""  size=10 maxlength=10/>
<A HREF="javascript:void(0)"  onClick="initCal('\u65e5\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/nmstyles.css TYPE=text/css>', '/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); setDateField(document.getElementsByName('endDate')[0], 'yyyy/MM/dd', '\u4e00\u6708#\u4e8c\u6708#\u4e09\u6708#\u56db\u6708#\u4e94\u6708#\u516d\u6708#\u4e03\u6708#\u516b\u6708#\u4e5d\u6708#\u5341\u6708#\u5341\u4e00\u6708#\u5341\u4e8c\u6708#', '#', '\u661f\u671f\u65e5#\u661f\u671f\u4e00#\u661f\u671f\u4e8c#\u661f\u671f\u4e09#\u661f\u671f\u56db#\u661f\u671f\u4e94#\u661f\u671f\u516d#', '0'); newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')">
<IMG name="calImg" SRC="/Windchill/netmarkets/images/calendar.gif" WIDTH=18 HEIGTH=16 BORDER=0></A><font class=hlpTxt>yyyy/mm/dd</font>
</td>
<td>&nbsp;&nbsp;</td>
<td>&nbsp;&nbsp;</td>	
	</tr>
	<tr>
	</tr>
	<td></td>
	<tr>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td align=center><input type="button" value="<%=Constants.BUTTON_JSP_SEARCH%>" onclick="preSubmit();"/></td>
	</tr>
</table>

<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.ProcessAssignReportBuilder')}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>