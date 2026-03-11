
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="java.util.Enumeration"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%><%@ taglib
	uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page language="java" pageEncoding="UTF-8"%>

<jsp:include
	page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.processplan.PlanUniteTaskAllocateBuilder')}"
	flush="true" />
<input type="button" name="button1" value="完成派工"
	onclick="window.close();" />
<%@include file="/netmarkets/jsp/util/end.jspf"%>