<%@page language="java" session="true" pageEncoding="GBK"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/components"
	prefix="jca"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<%@page import="wt.util.WTMessage"%>
<%@page import="wt.util.WTContext"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Enumeration"%>
<%@page import="com.ptc.netmarkets.util.table.NmDefaultHTMLTable"%>
<%@page import="com.glaway.mpm.constants.Constants"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.number.GZNumberManager"%>
<%@include file="/netmarkets/jsp/util/begin.jspf"%>

<link href="netmarkets/jsp/ext/numbergen/css/nmstyles.css"
	rel="stylesheet" type="text/css">
<script type="text/javascript">
	PTC.navigation.loadScript("netmarkets/javascript/nriet/ext-lang-zh_CN.js");
</script>


<% 
	//if(!AdministrationHelper.getUser().equals(AdministrationHelper.ADMINISTRATOR)){
%>
<script>//alert("对不起，您没有权限执行此操作！");</script>
<script>//history.go(-1);</script>
<%
	//}

	String strCodeBase = WTProperties.getLocalProperties().getProperty("wt.server.codebase", null);
    String classificationURL = strCodeBase + "/netmarkets/jsp/glaway/mpm/mpmresource/gz/gznumber/gzNumberSearchClassSelect.jsp?useJSCA=false";
 
	%>

<script language="javascript">
	this.document.mainform[0].classSubmit.disabled = true;
	this.document.mainform[0].gzNumber.value = "";

	Ext.onReady(function() {
		var startsdatePicker = new Ext.form.DateField({
			fieldLabel: 'Starts',
			name: 'beginTime',
			id: 'beginTime',
			width: 90,
			allowBlank: true,
			format: 'Y/m/d',
			showWeekNumber: true,
			editable: false
		});
		startsdatePicker.render('begin_date');
		var endsdatePicker = new Ext.form.DateField({
			fieldLabel: 'Ends',
			name: 'endTime',
			id: 'endTime',
			width: 90,
			allowBlank: true,
			format: 'Y/m/d',
			showWeekNumber: true,
			editable: false
		});
		endsdatePicker.render('end_date');
	});
	
	function mySubmit(){
		PTC.jca.table.Utils.reload("gnNumber_searchResultTable", {}, true);
	}

	function changeClass(){
		//disable or enable numberClass button
		var varsel = this.document.mainform[0].numberClass.value;
		
		if(varsel == "ALL"){
			this.document.mainform[0].classSubmit.disabled = true;
			this.document.mainform[0].fullPath.value = "";
		}else{
			this.document.mainform[0].classSubmit.disabled = false;
			
			var classURL = "<%=classificationURL%>";
			if(varsel == "<%=Constants.alGZ %>"){
				 classURL += "&parentPath=A&fullPath=RootA";
				 this.document.mainform[0].fullPath.value = "RootA";
			}else if(varsel == "<%=Constants.kGZ %>"){
				classURL += "&parentPath=B&fullPath=RootB";
				this.document.mainform[0].fullPath.value = "RootB";
			}else if(varsel== "<%=Constants.tGZ %>"){
				classURL += "&parentPath=C&fullPath=RootC";
				this.document.mainform[0].fullPath.value = "RootC";
			}	
			 this.document.mainform[0].classSubmit.onclick = function(){
				 window.open(classURL,'','toolbar=no,location=no,directories=no,menubar=no,scrollbars=yes,resizable=no,status=no');
			 };
		}

		
	}

	function cancelNumber(number,url){
		if(confirm('确认设置此编号为已作废状态？'))
			Ext.Ajax.request({
				url : url , 
				params : {  cnumber : number},
				method: 'POST',
				success: function ( result, request) { 
					var msg = trim(result.responseText);
					if(msg.length>0){
						alert(msg);
					}else{
						PTC.jca.table.Utils.reload("gnNumber_searchResultTable", {}, true);
					}
				},
				failure: function ( result, request) { 
					Ext.MessageBox.alert('Failed', 'Failed posted form: '+result.date); 
				} 
			  });
	}

	function reviseNumber(number,sGnClass,requestdesc,strSeq,url){
		if(confirm('确认修订此编号?'))
			Ext.Ajax.request({
				url : url , 
				params : {  cnumber : number,
							sGnClass : sGnClass,
							requestdesc : requestdesc,
							strSeq : strSeq},
				method: 'POST',
				success: function ( result, request) { 
					var msg = trim(result.responseText);
					if(msg.length>0){
						alert(msg);
					}else{
						PTC.jca.table.Utils.reload("gnNumber_searchResultTable", {}, true);
					}
				},
				failure: function ( result, request) { 
					Ext.MessageBox.alert('Failed', 'Failed posted form: '+result.date); 
				} 
			  });
	}
	
	function requestNumber(number,url){
		if(confirm('确认设置此编号为已申请状态？'))
			Ext.Ajax.request({
				url : url , 
				params : {  cnumber : number},
				method: 'POST',
				success: function ( result, request) { 
					var msg = trim(result.responseText);
					if(msg.length>0){
						alert(msg);
					}else{
						PTC.jca.table.Utils.reload("gnNumber_searchResultTable", {}, true);
					}
				},
				failure: function ( result, request) { 
					Ext.MessageBox.alert('Failed', 'Failed posted form: '+result.date); 
				} 
			  });
	}
	
	
	function changeUser(number, url) {
		if (confirm('确认修改编号所有者?')) {
			var left=parseInt((screen.availWidth   -   300)/2); 
			var top=parseInt((screen.availHeight   -   200)/2);		
			window.open(url + "?number=" + number, "设置所有者", "width=400,height=250,toolbar=no,menubar=no,top="+top+",left="+left);
		}
		
	}
	
	function reloadTable() {
		PTC.jca.table.Utils.reload("gnNumber_searchResultTable", {}, true);
	}

	function dateClear() {
		this.document.mainform[0].endTime.value = "";
		this.document.mainform[0].beginTime.value = "";
	}
</script>

<!-- 
<div class="fieldsetbody">
  <table width="100%" border="0" cellpadding="2" cellspacing="10" class="layoutTable100">
  	<tr>
		<td height="54" valign="middle" class="footer">
			<div align="left" class="STYLE2">&nbsp;&nbsp;&nbsp;&nbsp;<strong>
			取号管理系统 - 编号管理</strong></div>
		</td>
	</tr>
  </table>
</div>
<br>
 -->
<fieldset>
	<legend>
		&nbsp;&nbsp;查询条件
	</legend>
</fieldset>
<div class="fieldsetbody">
	<table width="100%" class="layoutTable100">
		<tr>
			<td width="30">
				&nbsp;
			</td>
			<td width="100">
				&nbsp;
			</td>
			<td>
				&nbsp;
			</td>
		</tr>
		<tr height="30">
			<td>
				&nbsp;
			</td>
			<td class="STYLE3">
				<b>图号类别</b>
			</td>
			<td class="STYLE3">
				<select name="numberClass" id="numberClass"
					onchange="changeClass();">
					<option value="ALL" selected>
						所有工装号类别
					</option>
					<option value="<%=Constants.alGZ %>">
						自制工装号(<%=Constants.alGZ %>)
					</option>
					<option value="<%=Constants.kGZ %>">
						科研工装号(<%=Constants.kGZ %>)
					</option>
					<option value="<%=Constants.tGZ %>">
						通用工装号(<%=Constants.tGZ %>)
					</option>
				</select>
				<input name="gzNumber" type="text" align="left"
					class="tablehighlightrowbg" id="gzNumber" size="30" />
				<input name="classSubmit" id="classSubmit" type="button"
					align="left"
					onclick="window.open('<%=classificationURL%>','','toolbar=no,location=no,directories=no,menubar=no,scrollbars=yes,resizable=no,status=no');"
					value="选择分类号" />
				<input type="hidden" id="fullPath" name="fullPath" />
			</td>
		</tr>
		<% 
	// if(AdministrationHelper.getUser().equals(AdministrationHelper.ADMINISTRATOR)){
%>

		<tr height="30">
			<td>
				&nbsp;
			</td>
			<wctags:userPicker id="user" label="申请人" />
		</tr>
		<%
	// }
%>
		<tr height="30">
			<td>
				&nbsp;
			</td>
			<td>
				<b>编号状态</b>
			</td>
			<td class="STYLE3">
				<select name="flag" id="flag">
					<option value="<%=GZNumberManager.GENERATED_FLAG%>">
						已申请
					</option>
					<option value="<%=GZNumberManager.USED_FLAG%>">
						已使用
					</option>
					<option value="<%=GZNumberManager.CANCELLED_FLAG%>">
						已作废
					</option>
					<option value="all" selected>
						所有状态
					</option>
				</select>
			</td>
		</tr>
		<tr height="30">
			<td>
				&nbsp;
			</td>
			<td class="STYLE3">
				<b>起始时间</b>
			</td>
			<td class="STYLE3">
				<div id='begin_date' style='width: 100px; float: left; clear: both'></div>
			</td>
		</tr>
		<tr height="30">
			<td>
				&nbsp;
			</td>
			<td class="STYLE3">
				<b>截止时间</b>
			</td>
			<td class="STYLE3">
				<div id='end_date' style='width: 100px; float: left; clear: both'></div>
			</td>
		</tr>
		<tr height="30">
			<td>
				&nbsp;
			</td>
			<td class="STYLE3"></td>
			<td class="STYLE3">
				<input name="Submit" type="button" onclick="mySubmit();"
					value="查询编号">
				<input name="clearDate" type="button" onclick="dateClear()"
					value="清空时间">
			</td>
		</tr>
	</table>
</div>
<br>

<fieldset>
	<legend>
		&nbsp;&nbsp;编号查询结果
	</legend>
</fieldset>

<jsp:include
	page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.mpmresource.gz.gznumber.GZNumberSearchTableBuilder2')}"
	flush="true" />

<%@include file="/netmarkets/jsp/util/end.jspf"%>