<%@page import="ext.casc.constants.Constants"%>
<%@page import="wt.util.WTProperties"%><%@ taglib uri="http://www.ptc.com/windchill/taglib/components"  prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt"         prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc"         prefix="mvc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"               prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers"    prefix="w"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ page import="ext.casc.process.ProcessConstants" %>
<%@ page import="java.util.Date" %>
<%@page language="java" pageEncoding="GBK" contentType="text/html; charset=GBK"%>
<%
String strCodeBase = WTProperties.getLocalProperties().getProperty("wt.server.codebase", null);
String url=request.getRequestURL().toString();
String mpmResourceType=request.getParameter("mpmResourceType");
Date date = new Date();
String number = String.valueOf(date.getTime());
 %>

<table>
	<%-- <tr>
		<td>
			<b><%=ProcessConstants.JIHUABIANHAO %></b>
		</td>
		<td>
			<input type="text" name="number" id="number" value="<%=number %>" readOnly="true" />
		</td>
	</tr> --%>
	<tr>
		<td>
			<b><%=ProcessConstants.JIHUAMINGCHENG %></b>
		</td>
		<td>
			<input type="text" name="name" id="name"/>
		</td>
	</tr>
	<tr>
		<td>
			<b><%=ProcessConstants.JIHUAZHUTI %></b>
		</td>

		<td>
		<select name="zhuti" id="zhuti" style='width:150px'>
				<option value="" selected></options>
			    <%for(String title:ProcessConstants.titles){%>
			    	<option value=<%=title%>><%=title%></options>
			    <%}%>
         </select>
		</td>
	</tr>
	<tr>
		<td>
			<b><%=ProcessConstants.XIANGMUBU %></b>
		</td>
		<!-- <td>
			<input type="text" name="xiangmubu" id="xiangmubu"/>
		</td> -->
		<td>
		<select name="xiangmubu" id="xiangmubu" style='width:150px'>
				<option value="" selected></options>
			    <%for(String xiangmubu:Constants.allPartMents){%>
			    	<option value=<%=xiangmubu%>><%=xiangmubu%></options>
			    <%}%>
         </select>
         </td>
	</tr>
	<tr>
			<wctags:contextPicker id="containerTypeList" pickerTitle="" label="<%=ProcessConstants.XINGHAO2 %>" customAccessController="com.ptc.windchill.enterprise.preference.PreferenceContextAccessController" multiSelect="false" pickerTextBoxLength="15" pickerType="search" readOnlyPickerTextBox="true"/>

	</tr>
</table>
