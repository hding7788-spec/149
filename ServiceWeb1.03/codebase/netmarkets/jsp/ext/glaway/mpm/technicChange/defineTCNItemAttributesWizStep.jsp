
<%@page import="java.util.ArrayList"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.inf.container.WTContainer"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<head>
	<style>
 		.input_textarea{
  		align:left;
 		}
 	</style>
</head>


<jsp:useBean id="commandbean" scope="request" class="com.ptc.netmarkets.util.beans.NmCommandBean" ></jsp:useBean>
<%
	commandbean.setRequest(request);
	
	WTContainer container = commandbean.getContainer();
	String containerName = container.getName();
	
	ArrayList<String> listTypeKey = new ArrayList<String>();
	ArrayList<String> listTypeValue = new ArrayList<String>();
	listTypeKey.add("wt.change2.WTChangeIssue|com.nriet.TechnicChangeNotice");
	listTypeValue.add("工艺变更通知 ");
	request.setAttribute("listTypeKey", listTypeKey);
	request.setAttribute("listTypeValue", listTypeValue);
	
	HashMap hmap = commandbean.getParameterMap();
	System.out.println("map:" + commandbean.getParameterMap());
	
 %>
<table class="pp">
	<tr>
		<td class="ppdata" >
			<table class="pp">
				<tr>
					<td class="ppdata" colspan="1"><w:label name="containerName1" value="产品:" style="input_textarea"/></td>
					<td class="ppdata" colspan="1"><w:label name="containerName" value="<%=containerName%>" styleClass="input_textarea"/></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td class="ppdata" colspan="1">
			<jca:renderPropertyPanel>
				<w:comboBox propertyLabel="类型:" id="type___createType" name="type___createType" required="true" internalValues="${listTypeKey}" displayValues="${listTypeValue}"></w:comboBox>
			</jca:renderPropertyPanel>
		</td>
	</tr>
	
	<tbody id="driverAttributes"></tbody>
</table>

<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.change.TechnicChangeNoticeInfoBuilder')}" flush="true" />

<%@include file="/netmarkets/jsp/util/end.jspf"%>