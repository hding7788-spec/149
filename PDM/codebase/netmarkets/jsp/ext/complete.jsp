<%@page import="java.sql.Timestamp"%>
<%@page import="wt.util.WTException"%>
<%@page import="wt.query.QueryException"%>
<%@page import="wt.workflow.engine.WfEventHelper"%>
<%@page import="wt.workflow.work.WorkflowHelper"%>
<%@page import="wt.workflow.engine.WfActivity"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.PersistenceServerHelper"%>
<%@page import="wt.util.WTStandardDateFormat"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="ext.casc.util.CSCPrincipal"%>
<%@page import="wt.org.WTUser"%>
<%@ page import="java.util.*"%>
<%@ page import="ext.ases.envelope.ProcessEnvelope" %>
<%@ page import="wt.fc.PersistentReference" %>
<%@ page contentType="text/html;charset=utf-8"%>
<%@page pageEncoding="UTF-8" %>
<%
try {
	String userName = request.getParameter("userName");
	String id = request.getParameter("id");

	WTUser user = CSCPrincipal.getUserByName(userName);
	System.out.println(user);
	if(user!=null){
		QuerySpec queryspec = new QuerySpec(WorkItem.class);
		queryspec.setAdvancedQueryEnabled(true);
        queryspec.appendWhere(new SearchCondition(WorkItem.class, "status",
                "=", "POTENTIAL"), 0);
        queryspec.appendAnd();
        queryspec.appendWhere(new SearchCondition(WorkItem.class, "ownership.owner.key.id",
                "=",user.getPersistInfo().getObjectIdentifier().getId()), 0);

        Date dateFrom1 = WTStandardDateFormat.parse("2023/10/10", "yyyy/M/d");
        SearchCondition sc11 = new SearchCondition(WorkItem.class, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL, new Timestamp(dateFrom1.getTime()));
        queryspec.appendAnd();
        queryspec.appendSearchCondition(sc11);
        QueryResult queryresult = PersistenceServerHelper.manager.query(queryspec);
        while (queryresult.hasMoreElements()) {
			queryresult.size();
        	WorkItem  workitem = (WorkItem) queryresult.nextElement();
			if("ZHURENGONGYISHI".equals(workitem.getRole().toString())){
				System.out.println(workitem.getRole().toString());
			}

			WfActivity wfactivity = (WfActivity) workitem.getSource().getObject();
			PersistentReference pboR = workitem.getPrimaryBusinessObject();
			if(pboR !=null &&pboR.getObject()instanceof ProcessEnvelope) {
				if ("通过监听".equals(wfactivity.getName())) {
					System.out.println(wfactivity.getName());
					Vector vector = new Vector();
					vector.addElement("通过");
					WorkflowHelper.service.workComplete(workitem, workitem
							.getOwnership().getOwner(), vector);
					WfEventHelper.createVotingEvent(null, wfactivity, workitem,
							workitem.getOwnership().getOwner(), "程序完成", vector,
							false, workitem.isRequired());
				}
			}
        }
	}

} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
%>