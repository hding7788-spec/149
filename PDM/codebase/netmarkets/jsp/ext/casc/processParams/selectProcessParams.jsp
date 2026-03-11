<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@page import="wt.util.WTProperties"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="util" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>

<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>
<%
	String parentId = request.getParameter("parentId");
%>


<script language="javascript">

	function submitQuery(){

		PTC.jca.table.Utils.reload('ext.casc.common.mvc.builder.SelectProcessParamsBuilder', {}, true);
	}


</script>

<fieldset >
	<legend>
		<font size="3px" style="font-weight: bold;"> 选择工艺参数</font>
	</legend>
</fieldset>

<table border="0" >


	<tr id="package_search">
		<td>
			<label for="gyName"><font style="font-weight: bold;"> 工艺参数名称： </font></label>
			&nbsp;&nbsp;
			<input type="text" value="${number_default}" name="null___gyName___textbox" id="gyName" maxlength="100" required="">

			&nbsp;&nbsp;&nbsp;&nbsp;
			<label for="parameterCategory"><font style="font-weight: bold;">工艺参数类型: </font></label>
			&nbsp;&nbsp;
			<select  name="null___parameterCategory___textbox" id="parameterCategory" maxlength="100" required="">
				<option value="基础参数">基础参数</option>
				<option value="枚举参数">枚举参数</option>
			</select>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<label for="processCategory"><font style="font-weight: bold;">专业: </font></label>
			&nbsp;&nbsp;
			<select  name="null___processCategory___textbox" id="processCategory" maxlength="100" required="">
				<option value=""></option>
				<option value="导管工艺">导管工艺</option>
				<option value="钣金工艺">钣金工艺</option>
			</select>

		</td>

	</tr>


	<tr/>
	<tr>
		<td>&nbsp;&nbsp;&nbsp;<input type="button" value="查询" onclick="submitQuery();"/></td>
	</tr>
</table>
<br/>
<fieldset >
	<legend>
		<font style="font-weight: bold;"> 查询结果</font>
	</legend>
</fieldset>
<table>
	<tr>
		<td>
			<input type="button" name="add" value="添加参数" class="x-btn-text" onclick="doAddParams();"/>&nbsp;&nbsp;
		</td>

	</tr>
	<tr id="trid_yunSuanExp"  style="display: none;">
		<td>
			<label for="yunSuanExp"><font style="font-weight: bold;">运算符号: </font></label>
			<select  name="yunSuanExp" id="yunSuanExp" maxlength="50" required="">
				<option value="||">或</option>
				<option value="&">与</option>
			</select>
		</td>
	</tr>
</table>
<jsp:include page="${mvc:getComponentURL('ext.casc.common.mvc.builder.SelectProcessParamsBuilder')}" />


<script type="text/javascript">

	window.onload = function(){
		var parameterCategory = window.opener.document.getElementById("parameterCategory").value;
		if("知识参数"==parameterCategory){
			document.getElementById("trid_yunSuanExp").style.display="none";
		}else{
			document.getElementById("trid_yunSuanExp").style.display = "";
		}

	}
	function doAddParams() {
		var table = PTC.jca.table.Utils.getTable('ext.casc.common.mvc.builder.SelectProcessParamsBuilder');
		var allSelection = table.getSelectionModel().getSelections();
		if (allSelection.length == 0) {
			alert("请选择参数");
			return;
		}
		var oldValue = window.opener.document.getElementById("<%=parentId%>").value;
		if (allSelection.length > 0) {
			var allData = "";

			var parameterCategory = window.opener.document.getElementById("parameterCategory").value;
			var expValue ;
			if("知识参数"==parameterCategory){
				expValue = "|";
			}else{
				expValue = document.getElementById("yunSuanExp").value;
			}
			for(var i=0;i<allSelection.length;i++){
				var gyName = allSelection[i].data.gyName;
				if(allData==""){
					allData = gyName;
				}else{
					allData = allData+expValue+gyName;
				}
			}
			if(oldValue!=""){
				allData = oldValue+expValue+allData;
			}

			window.opener.document.getElementById("<%=parentId%>").value = allData;

		}else{

			if(oldValue!=""){
				window.opener.document.getElementById("<%=parentId%>").value = oldValue+"|"+allSelection[0].data.gyName;
			}else{
				window.opener.document.getElementById("<%=parentId%>").value = allSelection[0].data.gyName;
			}
		}

		//window.close();
	}


</script>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>