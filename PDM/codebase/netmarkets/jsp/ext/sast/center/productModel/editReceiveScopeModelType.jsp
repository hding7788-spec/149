<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<div style="width:100%;hieght:20px;background:#0080C0" align="center"><font size="5" style="color:#FFFFFF"><B>接收类型范围设置</B></font></div>
<jca:wizard title="接收类型范围设置" buttonList="NoStepsWizardButtons">
	<jca:wizardStep action="editReceiveScopeModelType_step" type="syncProductModel" label="接收类型范围设置"/>
</jca:wizard>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>