<%@page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@page import="wt.util.WTProperties"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>


<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>

<jca:wizard title="${wizardTitle}" buttonList="DefaultWizardButtons">
	<jca:wizardStep action="otherChildDesingTaskManageStep"
		type="customProcessPlan" />
</jca:wizard>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>