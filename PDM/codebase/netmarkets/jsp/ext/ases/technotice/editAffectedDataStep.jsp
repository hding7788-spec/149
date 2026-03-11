<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"
%><%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"
%>
<wctags:collectItems tableId="changeTask_affectedItems_table" collectorId="CollectItemsFromChangeItem_AffectedItems" returnObjectReferences="false" returnOrigCopyVroid="true"/>
<wctags:collectItems tableId="changeTask_resultingItems_table" collectorId="CollectItemsFromChange_ResultingItems" returnObjectReferences="false" returnOrigCopyVroid="true"/>
<script src='netmarkets/javascript/hangingChanges/deferChange.js'></script>
<jsp:include page="/netmarkets/jsp/ext/ases/technotice/editAffectedItemsTable.jsp" flush="true" />
<BR>
<jsp:include page="/netmarkets/jsp/ext/ases/technotice/editResultingItemsTable.jsp" flush="true" />