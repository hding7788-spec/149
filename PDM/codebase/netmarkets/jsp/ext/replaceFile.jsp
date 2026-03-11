<%@page import="wt.pom.Transaction"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="wt.content.ContentServerHelper"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="wt.content.ContentHolder"%>
<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
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
<%@ page contentType="text/html;charset=utf-8"%>
<%@page pageEncoding="UTF-8" %>
<%
String oid = request.getParameter("oid");
ContentHolder hoder2 = (ContentHolder)ReferenceFactory.getObjectbyOid(oid);
Transaction trans = new Transaction();

trans.start();
ContentHolder holder = ContentHelper.service.getContents(hoder2);
Vector apps = ContentHelper.getApplicationData(holder);

for (Enumeration e = apps.elements(); e.hasMoreElements();) {
    ApplicationData contentItem = (ApplicationData) e.nextElement();
    String applicationdataRole = contentItem.getRole().toString();
    if (!"SECONDARY".equalsIgnoreCase(contentItem.getRole().toString()))
        continue;// 不是附件

    if (contentItem.getFileName().startsWith("SIGNED_")) {
    	contentItem = ContentServerHelper.service.updateContent((ContentHolder) hoder2, contentItem, new FileInputStream("")); // 更新内容
		break;
    }
}
trans.commit();
trans = null;
%>