<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@taglib prefix="mvc" 	uri="http://www.ptc.com/windchill/taglib/mvc"%>
<%@taglib prefix="fmt" 	uri="http://www.ptc.com/windchill/taglib/fmt"%>
<%@taglib prefix="c" 	uri="http://java.sun.com/jsp/jstl/core"%>
<fmt:setBundle basename="ext.casc.sop.ui.SopActionsRB" />
<fmt:message var="filePath" key="sopCustom.importSOPPart_step.description" />
<table>
 	<tr>
		<td><B>${filePath}</B></td>
		<td>
			<input type="file" size="40" class="required" value="" name="xlsFile" id="xlsFile" />
		</td>
	</tr>
</table>
<%@include file="/netmarkets/jsp/util/end.jspf" %>