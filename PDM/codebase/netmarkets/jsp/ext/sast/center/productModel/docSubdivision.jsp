<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<jca:wizard title="文档细分" buttonList="NoStepsWizardButtons">
	<jca:wizardStep action="docSubdivision_step" type="syncProductModel" label="文档细分"/>
</jca:wizard>
<%@include file="/netmarkets/jsp/util/end.jspf"%>
<script type="text/javascript">
PTC.onReady(myfun);
</script>