<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<jca:wizard helpSelectorKey="task_reassign_help" buttonList="reassignTaskButtons">
   <jca:wizardStep action="reassign_step" type="work"/>
</jca:wizard>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>

