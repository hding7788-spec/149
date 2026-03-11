<%@taglib uri="http://java.sun.com/jsp/jstl/core" 				 prefix="c" 
%><%@taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" 
%><%@taglib uri="http://www.ptc.com/windchill/taglib/core" 		 prefix="wc" 
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


<fmt:setBundle basename="com.ptc.windchill.enterprise.change2.change2ClientResource" />
<fmt:message var="TableTitle"          key="AFFECTED_ITEMS_TABLE"/>
<fmt:message var="AnnotationSet"       key="ANNOTATION_SET_LABEL"/>
<fmt:setBundle basename="ext.ases.technotice.resource.technoticeRB" />
<fmt:message var="TableTitle"         key="technotice.BEFORE_DATA_TABLE"/>

<c:set var="changeTableId" value="changeTask_affectedItems_table" scope="request"/>

<script>
var mode = "${changeWizardBean.changeMode}";
if(mode == "EDIT" ||  mode == "CREATE") {
  ChangeUtils.registerTablePageFooter("${changeTableId}");
  //setChangePasteType("${changeTableId}", "changeables", mode);
  
  
  var launchWindowForMultiSelectAction = function( newURL, winName, props ) {
       var aSource = 'ActionSource';
       if ($(aSource) == null) {
         appendFormHiddenInput($("mainform"), aSource, 'PJLseedObjects');
       }
  
       var revFunction = 'ReviseCallBackFunction';
       if($(revFunction) == null) {
         appendFormHiddenInput($("mainform"), revFunction, 'ReviseCallBackFunction');
       }
       
       var jcaSelect = 'ignore_selected_oids';
       appendFormHiddenInput($("mainform"), jcaSelect, 'true'); 
       
       if(isSomethingChecked()) {
           submitFormToNewWindow( newURL, winName, props, "mainform" );
       } else {
           JCAAlert("com.ptc.netmarkets.util.utilResource.NO_OBJECT_SELECTED");
       }
  }
  
  var ReviseCallBackFunction = function(revisedObjects){
  	 var list = new Array();
  	 for(var i=0; i< revisedObjects.length;i++){
  	 	obid = revisedObjects[i]; 	
  	 	list[i] = obid;	 	
  	 }
       	var tableId =  "changeTask_resultingItems_table";
     	addRows( list, tableId, true )
  }
  
  change_goToWizardStep("affectedAndResultingItems", "changeTask_affectedItems_table");
  window.ReviseCallBackFunction=ReviseCallBackFunction;
  window.launchWindowForMultiSelectAction=launchWindowForMultiSelectAction;
}
</script>

<c:set var="CommentsColumnId" value="aadDescription" scope="request" />

<c:choose>
	<c:when test="${changeWizardBean.changeMode=='CREATE' || changeWizardBean.changeMode=='EDIT' }">
		<c:set var="affectedItems_table_actionModel" value="technotice.affectedItems.table.create_edit" scope="request" />
		<c:set var="affectedItems_row_actionModel"   value="technotice.affectedItems.row.actions" scope="request" />
		<c:set var="affectedItems_helpLink"          value="change_affectedItems_edit" scope="request" />
	</c:when>
	<c:otherwise>
		<c:set var="affectedItems_table_actionModel" value="technotice.affectedItems.table.view" scope="request" />
		<c:set var="affectedItems_row_actionModel"   value="technotice.affectedItems.row.actions.view" scope="request" />
		<c:set var="affectedItems_helpLink"          value="change_affectedItems" scope="request" />
	</c:otherwise>
</c:choose>

<jca:describeTable var="tdesc" id="${changeTableId}" configurable="true" type="wt.change2.Changeable2" label="${TableTitle}" >
	<jca:describeColumn id="statusFamily_General" sortable="true" /> 
	<jca:describeColumn id="statusFamily_Share" sortable="true" />
	<jca:describeColumn id="statusFamily_Change" sortable="true" />
	<%@ include file="/netmarkets/jsp/object/itemIdVersion.jspf" %> 
   	<jca:describeColumn id="change_tableData" label="&nbsp;" sortable="false">
   		<jca:setComponentProperty key="handleAdditionalProperties" value="inventoryDisposition,${CommentsColumnId}"/>
   		<jca:setComponentProperty key="supportAnnotations" value="true"/>
   	</jca:describeColumn>
   	<jca:describeColumn id="infoPageAction" />
	<jca:describeColumn id="nmActions" mode="${changeWizardBean.changeMode}">
		<jca:setComponentProperty key="actionModel" value="${requestScope.affectedItems_row_actionModel}"/>
	</jca:describeColumn>
	<jca:describeColumn id="name" sortable="true" />
	<jca:describeColumn id="lifeCycleState" sortable="true" />
	<jca:describeColumn id="thePersistInfo.modifyStamp" sortable="true" />
	<jca:describeColumn id="modifier.name" sortable="true" />
	<%--<jca:describeColumn id="inventoryDisposition" sortable="true" mode="${changeWizardBean.changeMode}"/>--%>
	<jca:describeColumn id="${CommentsColumnId}" sortable="false" mode="${changeWizardBean.changeMode}" />
	<%--<jca:describeColumn id="associatedAnnotations" label="${AnnotationSet}" />--%>
</jca:describeTable>

<c:set target="${tdesc.properties}" property="selectable" value="true"/>
<c:set target="${tdesc.properties}" property="actionModel" value="${requestScope.affectedItems_table_actionModel}" />

<c:set var="serviceName" value="ext.ases.technotice.TechNoticeBeforeQueryCommands" scope="request"/>
<c:set var="methodName" value="getAffectedData" scope="request"/>
                  
<jca:getModel var="tableModel" descriptor="${tdesc}"
               serviceName="${serviceName}"
               methodName="${methodName}"
               pageLimit="${pageLimit}"
               initialRows="true">
   <jca:addServiceArgument value="${commandBean}" type="com.ptc.netmarkets.util.beans.NmCommandBean" />
</jca:getModel>

<jca:renderTable model="${tableModel}" helpContext="${affectedItems_helpLink}" />


<%@ include file="/netmarkets/jsp/util/end.jspf"%>

