<%@taglib uri="http://java.sun.com/jsp/jstl/core"                prefix="c"
%><%@taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"
%><%@taglib uri="http://www.ptc.com/windchill/taglib/core"       prefix="wc"
%><%@taglib uri="http://www.ptc.com/windchill/taglib/fmt"        prefix="fmt"
%><%@ taglib uri="http://www.ptc.com/windchill/taglib/changeWizards" prefix="cwiz"
%><%@include file="/netmarkets/jsp/util/begin.jspf"
%>

<jsp:useBean id="changeWizardBean" class="com.ptc.windchill.enterprise.change2.beans.ChangeWizardBean" scope="request">
	<jsp:setProperty name="changeWizardBean" property="request" value="${pageContext.request}"/>
</jsp:useBean>


<wc:isA var="isAVariance" scope="page" type="wt.change2.WTVariance" ref="${param.oid}"/>

<!-- Setup the localization string constants -->
<fmt:setLocale value="${localeBean.locale}"/>

<fmt:setBundle basename="ext.ases.envelope.envelopeResource" />
<fmt:message var="tableTitle"		   key="ENVELOPE_RELATED_DATA" />

<fmt:setBundle basename="com.ptc.windchill.enterprise.annotation.annotationClientResource" />
<fmt:message var="AnnotationsColumn"       key="ASSOCIATEDANNOTATIONS_COLUMN"/>

<c:set var="CommentsColumnId" value="aiDescription" scope="request" />
<c:set var="changeTableId" value="change_affectedData_table" scope="request"/>

<script>
var mode = "${changeWizardBean.changeMode}";
if(mode == "EDIT" ||  mode == "CREATE") {
  <%--ChangeUtils.registerTablePageFooter("${changeTableId}");--%>
 // setChangePasteType("change_affectedData_table", "changeables", mode);

  change_goToWizardStep("affectedDataStep", "change_affectedData_table");

}
</script>


<c:choose>
	<c:when test="${changeWizardBean.changeMode == 'EDIT' || changeWizardBean.changeMode == 'CREATE'}">
		<c:set var="affectedData_table_actionModel" value="variance.affectedData.table.create_edit" scope="request" />
		<c:set var="affectedData_row_actionModel"   value="variance.affectedData.row.actions" scope="request" />
		<c:set var="affectedData_helplink"          value="change_affectedData_edit" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="affectedData_table_actionModel" value="variance.affectedData.table.view" scope="request" />
		<c:set var="affectedData_row_actionModel"   value="variance.affectedData.row.actions.view" scope="request" />
		<c:set var="affectedData_helplink"          value="change_affectedData" scope="request"/>
	</c:otherwise>
</c:choose>

<jca:describeTable var="tdesc" id="${changeTableId}" configurable="true" type="wt.change2.Changeable2" label="${tableTitle}" >
	<jca:describeColumn id="statusFamily_General" sortable="true" />
	<jca:describeColumn id="statusFamily_Share" sortable="true"  />
	<jca:describeColumn id="statusFamily_Change" sortable="true" />
	<%@ include file="/netmarkets/jsp/object/itemIdVersion.jspf" %>
  	<jca:describeColumn id="change_tableData" label="&nbsp;" sortable="false" >
  		<jca:setComponentProperty key="handleAdditionalProperties" value="${CommentsColumnId}"/>
  	    <jca:setComponentProperty key="supportAnnotations" value="true"/>
  	</jca:describeColumn>
  	<jca:describeColumn id="infoPageAction" />
	<jca:describeColumn id="nmActions" mode="${changeWizardBean.changeMode}">
		<jca:setComponentProperty key="actionModel" value="${requestScope.affectedData_row_actionModel}"/>
	</jca:describeColumn>
	<jca:describeColumn id="name" sortable="true" />
	<jca:describeColumn id="lifeCycleState"  sortable="true" />
	<jca:describeColumn id="thePersistInfo.modifyStamp" sortable="true" />
	<jca:describeColumn id="modifier.name" sortable="true" />
	<jca:describeColumn id="associatedAnnotations" label="${AnnotationsColumn}" sortable="false" />
	<c:choose>
		<c:when test="${changeWizardBean.varianceEffectivity == 'true'}">
			<jca:describeColumn id="change_approvedQuantity" inputRequired="true" sortable="true" mode="${changeWizardBean.changeMode}" />
		</c:when>
		<c:when test="${isAVariance &&  (changeWizardBean.varianceEffectivity != 'false' )}">
			<jca:describeColumn id="change_approvedQuantity" sortable="true" mode="VIEW" />
		</c:when>
	</c:choose>
	<jca:describeColumn id="${CommentsColumnId}" sortable="true" mode="${changeWizardBean.changeMode}" />
</jca:describeTable>

<c:set target="${tdesc.properties}" property="selectable" value="true"/>
<c:set target="${tdesc.properties}" property="actionModel" value="${requestScope.affectedData_table_actionModel}" />
<%--
<c:set var="serviceName" value="com.ptc.windchill.enterprise.change2.commands.ChangeItemQueryCommands" scope="request"/>
<c:set var="methodName" value="getAffectedData" scope="request"/>

<jca:getModel var="tableModel" descriptor="${tdesc}"
               serviceName="${serviceName}"
               methodName="${methodName}"
               pageLimit="${pageLimit}"
               initialRows="true">
   <jca:addServiceArgument value="${commandBean}" type="com.ptc.netmarkets.util.beans.NmCommandBean" />
</jca:getModel>--%>
<c:set var="serviceName" value="ext.ases.envelope.SubEnReleaseQueryCommands2" scope="request"/>
<c:set var="methodName" value="getAffectedData" scope="request"/>

<jca:getModel var="tableModel" descriptor="${tdesc}"
               serviceName="${serviceName}"
               methodName="${methodName}"
               pageLimit="${pageLimit}"
               initialRows="true">
   <jca:addServiceArgument value="${commandBean}" type="com.ptc.netmarkets.util.beans.NmCommandBean" />
</jca:getModel>
<jca:renderTable model="${tableModel}" helpContext="${affectedData_helplink}" />
	

<%@ include file="/netmarkets/jsp/util/end.jspf"%>