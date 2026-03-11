<%@taglib uri="http://java.sun.com/jsp/jstl/core" 				 prefix="c" 
%><%@taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" 
%><%@taglib uri="http://www.ptc.com/windchill/taglib/fmt"        prefix="fmt" 
%><%@ taglib uri="http://www.ptc.com/windchill/taglib/changeWizards" prefix="cwiz"
%><%@ include file="/netmarkets/jsp/util/begin.jspf"
%>

<jsp:useBean id="changeWizardBean" class="com.ptc.windchill.enterprise.change2.beans.ChangeWizardBean" scope="request">
	<jsp:setProperty name="changeWizardBean" property="request" value="${pageContext.request}"/>
</jsp:useBean>

<%--
<cwiz:initializeChangeTable pageLimit="pageLimit"/>
--%>

<c:set var="changeTableId" value="changeTask_resultingItems_table" scope="request"/>

<script>
var mode = "${changeWizardBean.changeMode}";
if(mode == "EDIT" ||  mode == "CREATE") {
  ChangeUtils.registerTablePageFooter("${changeTableId}");
  //setChangePasteType("${changeTableId}", "changeables", mode);
  
  change_goToWizardStep("affectedAndResultingItems", "${changeTableId}");
}

var hangingChangeCallback = function(key, value) {
   var keyvalue = $(key);
   if (keyvalue != null) {
      keyvalue.value=value;    		
   } else{
      insertNEWDeferHiddenField(value, "", key);
   }
}

window.hangingChangeCallback=hangingChangeCallback;
</script>


<fmt:setBundle basename="ext.ases.changepackaged.changepackagedResource" />
<fmt:message var="TableTitle"          key="changepackaged.RESULT_DATA_TABLE"/>

<c:choose>
	<c:when test="${changeWizardBean.changeMode=='CREATE' || changeWizardBean.changeMode=='EDIT' }">
		<c:set var="resultingItems_table_actionModel" value="technotice.resultingItems.table.create_edit" scope="request" />
		<c:set var="resultingItems_row_actionModel"   value="technotice.resultingItems.row.actions" scope="request" />
		<c:set var="resultingItems_helpLink"          value="change_resultingItems_edit" scope="request" />
	</c:when>
	<c:otherwise>
		<c:set var="resultingItems_table_actionModel" value="technotice.resultingItems.table.view" scope="request" />
		<c:set var="resultingItems_row_actionModel"   value="technotice.resultingItems.row.actions.view" scope="request" />
		<c:set var="resultingItems_helpLink"          value="change_resultingItems" scope="request" />
	</c:otherwise>
</c:choose>

<c:set var="CommentsColumnId" value="crDescription" scope="request" />

<jca:describeTable var="tdesc" id="${changeTableId}" configurable="true" type="wt.change2.Changeable2" label="${TableTitle}">
    <jca:describeColumn id="statusFamily_General" sortable="true" />
	<jca:describeColumn id="statusFamily_Share" sortable="true"  />
    <jca:describeColumn id="statusFamily_Change" sortable="true"  />
    <%@ include file="/netmarkets/jsp/object/itemIdVersion.jspf" %> 
    <jca:describeColumn id="launchIncorpHangingChange"/>
    <c:choose>
       	<c:when test="${changeWizardBean.changeMode=='VIEW' }">
       		<jca:describeColumn id="compare" mode="VIEW"/>	
       	</c:when>
    </c:choose>
  	<jca:describeColumn id="change_tableData" label="&nbsp;" >
  		<jca:setComponentProperty key="handleAdditionalProperties" value="${CommentsColumnId}"/>
  		<jca:setComponentProperty key="supportHangingChanges" value="true"/>
  	</jca:describeColumn>
    <jca:describeColumn id="infoPageAction" />	
    <jca:describeColumn id="name" sortable="true" />
    <jca:describeColumn id="lifeCycleState" sortable="true"/>
	<jca:describeColumn id="thePersistInfo.modifyStamp" sortable="true" />
	<jca:describeColumn id="modifier.name" sortable="true" />
	<jca:describeColumn id="${CommentsColumnId}" sortable="false" mode="${changeWizardBean.changeMode}" />
</jca:describeTable>

<c:set target="${tdesc.properties}" property="actionModel" value="${requestScope.resultingItems_table_actionModel}" />
<c:set target="${tdesc.properties}" property="selectable" value="true"/>

<c:set var="serviceName" value="ext.ases.changepackaged.ChangePackagedResultItemQueryCommands" scope="request"/>
<c:set var="methodName" value="getResultData" scope="request"/>

<jca:getModel var="tableModel" descriptor="${tdesc}"
               serviceName="${serviceName}"
               methodName="${methodName}"
               pageLimit="${pageLimit}"
               initialRows="true">
   <jca:addServiceArgument value="${commandBean}" type="com.ptc.netmarkets.util.beans.NmCommandBean" />
</jca:getModel>

<jca:renderTable model="${tableModel}" helpContext="${resultingItems_helpLink}" />


<%@ include file="/netmarkets/jsp/util/end.jspf"%>



