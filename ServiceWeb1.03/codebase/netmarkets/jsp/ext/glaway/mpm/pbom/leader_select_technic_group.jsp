<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<jca:wizard buttonList="OkCancelWizardButtons">
  <jca:wizardStep action="leader_select" type="pbom"/>
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>