<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.glaway.mpm.task.util.TaskUtil"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="p" uri="http://www.ptc.com/windchill/taglib/picker"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.glaway.mpm.pbom.ui.PBOMResource" %>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.glaway.mpm.pbom.ui.PBOMResource"/>
<fmt:message var="mpmProductNumber" key="<%=PBOMResource.PRODUCT %>"/>
<fmt:message var="mpmParentNumber" key="<%=PBOMResource.PARENTNUMBER %>"/>
<fmt:message var="mpmPartNumber" key="<%=PBOMResource.PARTNUMBER %>" />
<fmt:message var="mpmAmount" key="<%=PBOMResource.AMOUNT %>"/>
<fmt:message var="mpmTechType" key="<%=PBOMResource.TECHTYPE %>"/>
<fmt:message var="mpmProfessionalGroup" key="<%=PBOMResource.PROFESSIONAL_GROUP %>"/>
<fmt:message var="mpmTechType" key="<%=PBOMResource.TECHTYPE %>"/>
<fmt:message var="mpmPlanner" key="<%=PBOMResource.PLANNER %>"/>
<fmt:message var="mpmAllocateTime" key="<%=PBOMResource.ALLOCATETIME%>"/>
<fmt:message var="mpmProfessionalGroupSetTime" key="<%=PBOMResource.PROFESSIONALGROUPSETTIME %>"/>
<fmt:message var="mpmComments" key="<%=PBOMResource.COMMENTS %>"/>
<fmt:message var="mpmTaskType" key="<%=PBOMResource.TASKTYPE %>"/>
<fmt:message var="mpmEstimateReview" key="<%=PBOMResource.ESTIMATEREVIEWTIME %>"/>
<fmt:message var="mpmEstimateApprove" key="<%=PBOMResource.ESTIMATEAPPROVETIME %>"/>

	<%
		ArrayList<String> productListIn = TaskUtil.getAllContainer().get(0);
		ArrayList<String> productListOut = TaskUtil.getAllContainer().get(1);
		request.setAttribute("productListIn",productListIn);
		request.setAttribute("productListOut",productListOut);
		
		ArrayList<String> technicTaskIn = TaskUtil.getTechnicType().get(0);
		ArrayList<String> technicTaskOut = TaskUtil.getTechnicType().get(1);
		request.setAttribute("technicTaskIn", technicTaskIn);
		request.setAttribute("technicTaskOut", technicTaskOut);
		
		ArrayList<String> groupIn = TaskUtil.getTechnicGroups().get(0);
		ArrayList<String> groupOut = TaskUtil.getTechnicGroups().get(1);
		request.setAttribute("groupIn", groupIn);
		request.setAttribute("groupOut", groupOut);
		
		WTPrincipal prin = SessionHelper.manager.getPrincipal();
		String currentUser = ((WTUser)prin).getFullName();
		String user = ((WTUser)prin).getName().trim();
		request.setAttribute("currentUser", currentUser);
	 %>

	<body bgcolor="#E6E6FA">
		<p align="center" style="font-size:30px; font-weight:bold">&nbsp;新建工艺任务</p>
		
		<jca:renderPropertyPanel>
			<w:comboBox propertyLabel="${mpmTaskType}" id="taskType" name="taskType" required="true" internalValues="${technicTaskIn}" displayValues="${technicTaskOut}"/>
		</jca:renderPropertyPanel>
		<wctags:itemPicker id="cscPickerForPart" label="${mpmPartNumber}" pickerTitle="零件搜索" pickedAttributes="number" displayAttribute="number">
		</wctags:itemPicker>	
		<jca:renderPropertyPanel>
						
			<w:comboBox propertyLabel="${mpmProfessionalGroup}" id="group" name="group" required="true" internalValues="${groupIn}" displayValues="${groupOut}" />

						
			<w:label propertyLabel="${mpmPlanner}" value="${currentUser}"/>
			<w:dateInputComponent propertyLabel="${mpmAllocateTime}" id="allocateTime" name="allocateTime" dateValueType="DATE_ONLY"></w:dateInputComponent>
			<w:dateInputComponent propertyLabel="${mpmEstimateReview}" id="reviewTime" name="reviewTime" dateValueType="DATE_ONLY"></w:dateInputComponent>
			<w:dateInputComponent propertyLabel="${mpmEstimateApprove}" id="approveTime" name="approveTime" dateValueType="DATE_ONLY"></w:dateInputComponent>
			<w:textArea propertyLabel="${mpmComments}" id="comments" name="comments" value="" cols="39" rows="5" required="false" maxLength="100"/>
		</jca:renderPropertyPanel>
	</body>
