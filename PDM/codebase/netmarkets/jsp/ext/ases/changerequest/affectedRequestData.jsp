<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"
%><%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"
%>
<wctags:collectItems tableId="changeTask_affectedItems_table" collectorId="CollectItemsFromChangeItem_AffectedItems" returnObjectReferences="false" returnOrigCopy="true" />
<script src='netmarkets/javascript/hangingChanges/deferChange.js'></script>

<jsp:include page="/netmarkets/jsp/ext/ases/changerequest/affectedItemsTable.jsp" flush="true" />

<BR>

<jsp:include page="/netmarkets/jsp/ext/casc/changeRequest/relatedAnalysisActivity.jsp" flush="true" />