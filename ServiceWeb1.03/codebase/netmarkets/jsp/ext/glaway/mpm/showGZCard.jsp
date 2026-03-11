<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt"        prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"              prefix="c"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@include file="/netmarkets/jsp/zchange/propagationConfiguration.jspf"%>
<%@page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@include file="/netmarkets/jsp/annotation/wizardConfig.jspf" %>

<jca:wizard title="工装申请卡"  buttonList="WizardButtonClose">
	<jca:wizardStep action="showGZCardWizardStep" type="MPMResource" />
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
