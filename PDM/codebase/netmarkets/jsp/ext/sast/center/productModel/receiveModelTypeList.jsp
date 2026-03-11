<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<jca:wizard title="接收类型范围列表" buttonList="NoStepsWizardButtons">
	<jca:wizardStep action="addReceivedModelType_step" type="syncProductModel" label="接收类型范围列表"/>
</jca:wizard>
<%@include file="/netmarkets/jsp/util/end.jspf"%>