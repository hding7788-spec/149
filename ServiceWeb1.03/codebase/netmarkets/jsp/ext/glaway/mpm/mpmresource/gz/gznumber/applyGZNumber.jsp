<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="com.glaway.mpm.constants.Constants"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"
	prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.util.WTMessage"%>
<%@page import="java.util.Locale"%>
<%@page import="wt.util.WTContext"%>
<%@page import="com.ptc.netmarkets.util.table.NmDefaultHTMLTable"%>
<%@ page import="wt.util.WTProperties"%>
<%@include file="/netmarkets/jsp/util/begin.jspf"%>


<link href="netmarkets/jsp/ext/numbergen/css/nmstyles.css"
	rel="stylesheet" type="text/css">

<%

	String type = request.getParameter("type");
	String searchPath = "";
    String strCodeBase = WTProperties.getLocalProperties().getProperty("wt.server.codebase", null);
    String classificationURL = strCodeBase + "/netmarkets/jsp/glaway/mpm/mpmresource/gz/gznumber/classificationselect.jsp?useJSCA=false&gzNumber=";
    
    //分al，alj，alk三种编码申请
    if(type == null){
    	type = Constants.alGZ;
    }
    if(type.equalsIgnoreCase(Constants.alGZ)){
    	classificationURL += "&parentPath=A&fullPath=RootA";
    	searchPath = "RootA";
    }else if(type.equalsIgnoreCase(Constants.kGZ)){
    	classificationURL += "&parentPath=B&fullPath=RootB";
    	searchPath = "RootB";
    }else if(type.equalsIgnoreCase(Constants.tGZ)){
    	classificationURL += "&parentPath=C&fullPath=RootC";
    	searchPath = "RootC";
    }

%>

<script language="javascript">

	function mySubmit(){
		if(validInput()){
		
		
		var varGZNumber = document.getElementById("gzNumber");
		var varRequestVolum = document.getElementById("requestvolumn");
		var varRequestName = document.getElementById("requestname");
		var varRequestdesc = document.getElementById("requestdesc");
		var varParentPath = document.getElementById("parentPath");
		var varFullPath = document.getElementById("fullPath");
		var varObjectName = document.getElementById("objectname");
		
		Ext.Ajax.request({
					url : 'netmarkets/jsp/glaway/mpm/mpmresource/gz/gznumber/processRegister.jsp' , 
					params : { type : "<%=type%>",
					parentPath : varParentPath.value,
					fullPath : varFullPath.value,
					objectname : varObjectName.value,
					gzNumber : varGZNumber.value,
					requestdesc : varRequestdesc.value,
					requestvolumn : varRequestVolum.value,
					requestname : varRequestName.value},
					method: 'POST',
					success: function ( result, request) { 
						var msg = trim(result.responseText);
						if(msg.length>0){
							alert(msg);
						}else{
							PTC.jca.table.Utils.reload("gzNumber_searchResultTable", {}, true);
						}
					},
					failure: function ( result, request) { 
						Ext.MessageBox.alert('Failed', 'Successfully posted form: '+result.date); 
					} 
				  });
			
		}
	}

	function validInput(){
		var varGZNumber = document.getElementById("gzNumber");
		var varRequestVolum = document.getElementById("requestvolumn");
		var varRequestName = document.getElementById("requestname");
		var varRequestdesc = document.getElementById("requestdesc");
		
		var varFullPath = document.getElementById("fullPath");
		var varRequestdesc = document.getElementById("requestdesc");
		
		if(varGZNumber.value == ""){
			alert("请填写分类号！");
			return false;
		}
		if(varRequestVolum.value == ""){
			alert("请填写申请数量！");
			return false;
		}
		if(parseInt(varRequestVolum.value) > 100){
			alert("请填写小于100的申请数量！");
			return false;
		}
		if(varRequestName.value==""){
			alert("请填写名称！");
			return false;
		}else{
			var names=varRequestName.value.split("|");
			if(names.size()!=varRequestVolum.value){
				alert("名称个数必须和申请的编号个数相等！");
				return false;
			}
			for(var i=0;i<names.size();i++){
				if(names[i].trim()==""){
					alert("名称不能为空！");
					return false;
				}
			}
		}
		
//	    if(varRequestdesc.value == ""){
//			alert("请填写申请说明！");
//			return false;
//		}
		
		
		return true;
	}
	
	function isNum(numComponent){
		var r1= /^[0-9]*[1-9][0-9]*$/
			
		if(numComponent.value!="" &&!r1.test(numComponent.value)) { 
			alert("请输入数字！");			
		//	numComponent.value = numComponent.value.substring(0,numComponent.value.length-1);
		numComponent.value ="";
			return false;
		} 
	}

	function trim(str){
		return str.replace(/(^\s*)|(\s*$)/g,"");      //使用正则    \s匹配空格、换行符、换页符等。
	}

	function cancelNumber(number,url){
		if(confirm('确认作废此编号?'))
			Ext.Ajax.request({
				url : url , 
				params : {  cnumber : number},
				method: 'POST',
				success: function ( result, request) { 
					var msg = trim(result.responseText);
					if(msg.length>0){
						alert(msg);
					}else{
						PTC.jca.table.Utils.reload("gzNumber_searchResultTable", {}, true);
					}
				},
				failure: function ( result, request) { 
					Ext.MessageBox.alert('Failed', 'Failed posted form: '+result.date); 
				} 
			  });
	}

</script>

<fieldset>
	<legend>
		&nbsp;&nbsp;申请工装编号 （<%=type %>）
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
			<td width="2%">
				&nbsp;
			</td>
		</tr>
		<tr height="30">
			<td>
				&nbsp;
			</td>
			<td class="STYLE3" align='left'>
				* 分类号：
			</td>
			<td align="right"><%=type %></td>
			<td class="STYLE3">
				<input name="gzNumber" type="text" class="tablehighlightrowbg"
					id="gzNumber" size="30" readonly>
				<input name="Submit" type="button"
					onclick="window.open('<%=classificationURL%>','','toolbar=no,location=no,directories=no,menubar=no,scrollbars=yes,resizable=no,status=no');"
					value="选择分类号">
				<input type="hidden" id="parentPath" name="parentPath" />
				<input type="hidden" id="fullPath" name="fullPath"
					value="<%=searchPath%>"></input>
				<input type="hidden" id="type" name="type" value="<%=type%>" />
				<input type="hidden" id="objectname" name="objectname"></input>
			</td>
		</tr>

		<tr height="30">
			<td>
				&nbsp;
			</td>
			<td class="STYLE3" align='left'>
				* 申请数量：
			</td>
			<td>
				&nbsp;
			</td>
			<td class="STYLE3">
				<input name="requestvolumn" id="requestvolumn" type="text" size="3"
					maxlength="3" onkeyup="isNum(this);">
				(请输入小于100的数字)
			</td>
		</tr>

		<tr height="30">
			<td>
				&nbsp;
			</td>
			<td class="STYLE3" align='left'>
				* 工装名称：
			</td>
			<td>
				&nbsp;
			</td>
			<td class="STYLE3">
				<input name="requestname" id="requestname" type="text" size="50">
				(名称个数必须和申请的编号个数相等，多个名称时以'|'分割)
			</td>
		</tr>

		<tr height="30">
			<td>
				&nbsp;
			</td>
			<td class="STYLE3" align='left'>
				&nbsp; 申请说明：
			</td>
			<td>
				&nbsp;
			</td>
			<td class="STYLE3">
				<textarea name="requestdesc" id="requestdesc" cols="40" rows="3"></textarea>
			</td>
		</tr>

		<tr height="30">
			<td>
				&nbsp;
			</td>
			<td>
				&nbsp;
			</td>
			<td>
				&nbsp;
			</td>
			<td>
				<input name="Submit2" type="button" value="申请编号"
					onclick="mySubmit();" />
			</td>
		</tr>
		<tr>
			<td>
				&nbsp;
			</td>
			<td>
				&nbsp;
			</td>
			<td>
				&nbsp;
			</td>
			<td>
				&nbsp;
			</td>
			<td>
				&nbsp;
			</td>
		</tr>
	</table>
</div>

<fieldset>
	<legend>
		&nbsp;&nbsp;编号管理
	</legend>
</fieldset>
<div class="fieldsetbody">
	<jsp:include
		page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.mpmresource.gz.gznumber.GZNumberSearchTableBuilder')}"
		flush="true" />
</div>
<br>
<%@include file="/netmarkets/jsp/util/end.jspf"%>