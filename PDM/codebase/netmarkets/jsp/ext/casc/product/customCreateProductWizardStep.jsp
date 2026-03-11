<%@page import="ext.casc.product.util.CustomCreateProductUtil"%>
<%@ taglib prefix="jca"
	uri="http://www.ptc.com/windchill/taglib/components"%>
<%@page language="java" pageEncoding="GBK"
	contentType="text/html; charset=GBK"%>
<%@page import="java.util.Map"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<jsp:useBean id="nmcontext"
	class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request" />
<%
	Map<String, String> map = CustomCreateProductUtil.getContainerTemplateList();
%>
<html>
<head>
<font size=3>新建产品</font>
</head>
<body>
	<table style="text-align: left">
		<tr>
			<td><label for="productNameLabel" style="font-size: 15px">*名称:</label></td>
			<td><input id="productName" name="productName" type="text"
				style="width: 240px"></input></td>
		</tr>
		<br />
		<tr>
			<td><label for="productTemplateLabel" style="font-size: 15px">*产品模板:</label></td>
			<td><select id="productTemplate" name="productTemplate"
				style="width: 250px;">
					<%
						for (String s : map.keySet()) {
					%>
					<option><%=s%></option>
					<%
						}
					%>
			</select></td>
		</tr>
		<tr>
			<td><label for="descriptionLabel" style="font-size: 15px">说明:</label></td>
			<!--  <td><input   id="description" name="" type="text" value="" ></input></td> -->
			<td><textarea id="description" name="description" rows="3"
					cols="80"></textarea></td>
		</tr>
		<tr>
			<td><label for="xhjhLabel" style="font-size: 15px">型号简号:</label></td>
			<td><input id="xhjh" name="xhjh" type="text"
				style="width: 240px"></input></td>
		</tr>
		<tr>
			<td><label for="xhlxLabel" style="font-size: 15px">*型号类型:</label></td>
			<td><select id="xhlx" name="xhlx" style="width: 250px;">
					<option>运载型号</option>
					<option>飞船型号</option>
					<option>战术型号</option>
					<option>技术中心</option>
					<option>其他</option>
			</select></td>
		</tr>
		<tr>
			<td><label for="cpdhLabel" style="font-size: 15px">产品代号:</label></td>
			<td><input id="cpdh" name="cpdh" type="text"
				style="width: 240px"></input></td>
		</tr>
		<tr>
			<td><label for="fmxhdhLabel" style="font-size: 15px">非密型号代号:</label></td>
			<td><input id="fmxhdh" name="fmxhdh" type="text"
				style="width: 240px"></input></td>
		</tr>
	</table>

</body>

</html>
