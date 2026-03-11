<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<fmt:setBundle basename="ext.casc.ui.actionsRB"/>
<%@tag import="wt.workflow.engine.WfProcess"%>
<%@tag import="com.ptc.windchill.enterprise.dsvcore.server.utils.PersistableHelper"%>
<%@tag import="wt.doc.WTDocument"%>
<%@tag import="wt.fc.Persistable"%>
<%@tag import="wt.workflow.engine.ProcessData"%>
<%@tag import="wt.workflow.engine.WfActivity"%>
<%@tag import="wt.workflow.work.WorkItem"%>
<%@tag import="wt.fc.ReferenceFactory"%>
<fmt:message var="outSign" key="custom.outSign.description"/>
<fmt:message var="setOutSignInfo" key="custom.setOutSignInfo.description"/>
<%
	String oid = (String)request.getParameter("oid");
	ReferenceFactory rf = new ReferenceFactory();
	WorkItem workItem = (WorkItem)rf.getReference(oid).getObject();
	WfActivity activity = (WfActivity)workItem.getSource().getObject();
	WfProcess wfProcess = activity.getParentProcess();
	String processOid = PersistableHelper.getOid(wfProcess);
%>
<tr>
 	<td align="right" valign="top" nowrap width="11%">
 		${outSign}:
 	</td>
 	<td align="left" valign="top" nowrap>
 		<jca:action actionName="setOutSignInfo" actionType="custom" button="false" />
 	</td>
</tr>