<%@page import="ext.casc.doc.QuickApprovedProcessor"%>
<%@page import="com.glaway.mpm.util.MPMProcessPlanUtil"%>
<%@page import="com.ptc.windchill.mpml.processplan.MPMProcessPlan"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.type.ClientTypedUtility"%>
<%@page import="wt.type.TypeDefinitionReference"%>
<%@ page import="java.util.*"%>
<%
String type = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.SOPDoc";
TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(type);
long typeId = 0;
if (tdr != null) {
	typeId = tdr.getKey().getBranchId();
}
QuerySpec qs = new QuerySpec(WTDocument.class);

qs.appendWhere(new SearchCondition(WTDocument.class,
"typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId),
 new int[]{0});
qs.appendAnd();
qs.appendWhere(new SearchCondition(WTDocument.class,WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { 0 });
// qs = new LatestConfigSpec().appendSearchCriteria(qs);
qs.appendAnd();
qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.EQUAL, "APPROVED", true));
QueryResult qr = PersistenceHelper.manager.find(qs);
out.println(qr.size());
while(qr.hasMoreElements()){
	WTDocument document = (WTDocument)qr.nextElement();

	 List<MPMProcessPlan> pplans = MPMProcessPlanUtil.getAllProcessPlanByWTDocument(document);
		if(pplans.isEmpty()){
			out.println(document.getNumber());
			new QuickApprovedProcessor().quickApproved(document);
		}
}
%>