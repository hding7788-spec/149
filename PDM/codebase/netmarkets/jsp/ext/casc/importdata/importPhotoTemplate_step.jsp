<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@taglib prefix="mvc" 	uri="http://www.ptc.com/windchill/taglib/mvc"%>
<%@taglib prefix="fmt" 	uri="http://www.ptc.com/windchill/taglib/fmt"%>
<%@taglib prefix="c" 	uri="http://java.sun.com/jsp/jstl/core"%>
<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="filePath1" key="custom.importPhotoTemplate_step.description" />
<fmt:message var="filePath2" key="custom.importPhotoTemplate_step2.description" />
<table>
	<tr>
		<td><B>${filePath1}</B></td>
		<td>
			<input type="file" size="40" class="required" value="" name="xlsFile" id="xlsFile" />
		</td>
	</tr>
</table>
<table>
	<tr>
		<td><B>${filePath2}</B></td>
		<td>
			<input type="file" size="40" class="required" value="" name="photoFile" id="photoFile" />
		</td>
	</tr>
</table>
<%@include file="/netmarkets/jsp/util/end.jspf" %>