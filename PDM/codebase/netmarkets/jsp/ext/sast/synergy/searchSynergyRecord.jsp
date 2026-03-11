<%@page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags" %>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<%@ page import="ext.sast.synergy.constants.SynergyConstants" %>
<%@ page import="ext.sast.synergy.util.SynergyUtil" %>
<%@ page import="java.util.*"%>

<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>

<script language="javascript">

	document.getElementById("CJSJ").value = "";
	document.getElementById("JSSJ").value = "";

	function preSubmit(){
		setworktype("search");
		PTC.jca.table.Utils.reload('SearchSynergyRecordBuilder_ID', {}, true);
	}

	function setworktype(worktype){
		var ele=document.getElementById("work");
		ele.value=worktype;
	}
	function clearPickerData(){
		document.getElementById("DJLX").value="";
		document.getElementById("DJZT").value="";
		document.getElementById("FQDW").value="";
		document.getElementById("JSDW").value="";
		document.getElementById("XXZT").value="";
		document.getElementById("CPMC").value="";
		document.getElementById("DJBH").value="";
		document.getElementById("DJMC").value="";
		// document.getElementById("DXBH").value="";
		// document.getElementById("DXMC").value="";
		document.getElementById("LCMC").value="";
		document.getElementById("CJSJ").value="";
		document.getElementById("JSSJ").value="";
		document.getElementById("FQR").value="";
	}

</script>

<input type="hidden" id="work" name="work" value=""/>
<%
	List<String> productNameList = SynergyUtil.getAllContainer();
%>
<table>

	<tr>
		<td><h4><%=SynergyConstants.DJLX%></h4></td>
		<td>
			<select name="DJLX" id="DJLX" style="width:100px;">
				<option value="" selected></options>
				<option value="<%=SynergyConstants.DJLX_SSD_VALUE%>"><%=SynergyConstants.DJLX_SSD_VALUE%></option>
				<option value="<%=SynergyConstants.DJLX_FFD_VALUE%>"><%=SynergyConstants.DJLX_FFD_VALUE%></option>
			</select>
		</td>

		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

		<td><h4><%=SynergyConstants.DJZT%></h4></td>
		<td>
			<select name="DJZT" id="DJZT" style="width:100px;">
				<option value="" selected></options>
				<option value="<%=SynergyConstants.DJZT_CG_VALUE%>"><%=SynergyConstants.DJZT_CG_VALUE%></option>
				<option value="<%=SynergyConstants.DJZT_SB_VALUE%>"><%=SynergyConstants.DJZT_SB_VALUE%></option>
			</select>
		</td>

		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

		<td><h4><%=SynergyConstants.FQDW%></h4></td>
		<td>
			<select name="FQDW" id="FQDW" style="width:100px;">
				<option value="" selected></options>
				<option value="<%=SynergyConstants.DW_805_VALUE%>"><%=SynergyConstants.DW_805_VALUE%></option>
				<option value="<%=SynergyConstants.DW_BB_VALUE%>"><%=SynergyConstants.DW_BB_VALUE%></option>
				<option value="<%=SynergyConstants.DW_509_VALUE%>"><%=SynergyConstants.DW_509_VALUE%></option>
				<option value="<%=SynergyConstants.DW_149_VALUE%>"><%=SynergyConstants.DW_149_VALUE%></option>
				<option value="<%=SynergyConstants.DW_800_VALUE%>"><%=SynergyConstants.DW_800_VALUE%></option>
				<option value="<%=SynergyConstants.DW_812_VALUE%>"><%=SynergyConstants.DW_812_VALUE%></option>
				<option value="<%=SynergyConstants.DW_802_VALUE%>"><%=SynergyConstants.DW_802_VALUE%></option>
				<option value="<%=SynergyConstants.DW_803_VALUE%>"><%=SynergyConstants.DW_803_VALUE%></option>
				<option value="<%=SynergyConstants.DW_804_VALUE%>"><%=SynergyConstants.DW_804_VALUE%></option>
				<option value="<%=SynergyConstants.DW_806_VALUE%>"><%=SynergyConstants.DW_806_VALUE%></option>
				<option value="<%=SynergyConstants.DW_811_VALUE%>"><%=SynergyConstants.DW_811_VALUE%></option>
			</select>
		</td>

		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

		<td><h4><%=SynergyConstants.JSDW%></h4></td>
		<td>
			<select name="JSDW" id="JSDW" style="width:100px;">
				<option value="" selected></options>
				<option value="<%=SynergyConstants.DW_805_VALUE%>"><%=SynergyConstants.DW_805_VALUE%></option>
				<option value="<%=SynergyConstants.DW_BB_VALUE%>"><%=SynergyConstants.DW_BB_VALUE%></option>
				<option value="<%=SynergyConstants.DW_509_VALUE%>"><%=SynergyConstants.DW_509_VALUE%></option>
				<option value="<%=SynergyConstants.DW_149_VALUE%>"><%=SynergyConstants.DW_149_VALUE%></option>
				<option value="<%=SynergyConstants.DW_800_VALUE%>"><%=SynergyConstants.DW_800_VALUE%></option>
				<option value="<%=SynergyConstants.DW_812_VALUE%>"><%=SynergyConstants.DW_812_VALUE%></option>
				<option value="<%=SynergyConstants.DW_802_VALUE%>"><%=SynergyConstants.DW_802_VALUE%></option>
				<option value="<%=SynergyConstants.DW_803_VALUE%>"><%=SynergyConstants.DW_803_VALUE%></option>
				<option value="<%=SynergyConstants.DW_804_VALUE%>"><%=SynergyConstants.DW_804_VALUE%></option>
				<option value="<%=SynergyConstants.DW_806_VALUE%>"><%=SynergyConstants.DW_806_VALUE%></option>
				<option value="<%=SynergyConstants.DW_811_VALUE%>"><%=SynergyConstants.DW_811_VALUE%></option>
			</select>
		</td>

		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

		<td><h4><%=SynergyConstants.XXZT%></h4></td>
		<td>
			<select name="XXZT" id="XXZT" style="width:100px;">
				<option value="" selected></options>
				<option value="新建">新建</option>
				<option value="处理中">处理中</option>
				<option value="已完成">已完成</option>
				<option value="有异常">有异常</option>
			</select>
		</td>

		<td><h4><%=SynergyConstants.CPMC%></h4></td>
		<td>
			<select name="CPMC" id="CPMC" style="width:100px;">
				<option value="" selected></options>
						<%for (String productName : productNameList) { %>
				<option value="<%=productName %>"><%=productName %></option>
				<%} %>
			</select>
		</td>
<%--        <td><h4><%=SynergyConstants.DXBH%></h4></td>
        <td><input type="text" name="DXBH" id="DXBH" style="width:100px;"/></td>
        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

        <td><h4><%=SynergyConstants.DXMC%></h4></td>
        <td><input type="text" name="DXMC" id="DXMC" style="width:100px;"/></td>
        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>--%>
<%--		<wctags:userPicker id="FQR" label="<%=SynergyConstants.FQR%>" readOnlyPickerTextBox="false" editable="true" showSuggestion="true" suggestMinChars="1"/>--%>
		<td><h4><%=SynergyConstants.FQR%></h4></td>
		<td><input type="text" name="FQR" id="FQR" style="width:100px;"/></td>
	</tr>

	<tr>
		<td><h4><%=SynergyConstants.DJBH%></h4></td>
		<td><input type="text" name="DJBH" id="DJBH" style="width:100px;"/></td>

		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

		<td><h4><%=SynergyConstants.DJMC%></h4></td>
		<td><input type="text" name="DJMC" id="DJMC" style="width:100px;"/></td>

		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

		<td><h4><%=SynergyConstants.LCMC%></h4></td>
		<td><input type="text" name="LCMC" id="LCMC" style="width:100px;"/></td>

		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>


		<td><h4><%=SynergyConstants.CJSJ%></h4></td>
		<td>
			<w:dateInputComponent id="CJSJ" name="CJSJ" dateValueType="DATE_ONLY"/>
		</td>

		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

		<td>
			<h4><%=SynergyConstants.Z%></h4>
		</td>
		<td>
			<w:dateInputComponent id="JSSJ" name="JSSJ" dateValueType="DATE_ONLY"/>
		</td>

		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

		<td>
			<h4><input type="button" value="<%=SynergyConstants.CX%>" onclick="preSubmit();"/></h4>
		</td>

		<td>
			<h4><input type="button" value="<%=SynergyConstants.CANCEL%>" onclick="clearPickerData();"/></h4>
		</td>
	</tr>

</table>

<jsp:include page="${mvc:getComponentURL('SearchSynergyRecordBuilder_ID')}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>