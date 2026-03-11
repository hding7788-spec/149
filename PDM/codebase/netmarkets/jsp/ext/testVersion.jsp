<%@page import="wt.part.WTPart"%>
<%@page import="wt.part.WTPartMaster"%>
<%@page import="ext.casc.integrate.util.BomUtil"%>
<%@page import="ext.casc.workflow.CmWorkflowHelper"%>
<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@page import="wt.inf.container.WTContainerReferenceSearch"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="ext.ases.envelope.ProcessEnvelopeUtil"%>
<%@page import="ext.ases.envelope.ProcessEnvelope"%>
<%@page import="wt.workflow.definer.WfDefinerHelper"%>
<%@page import="wt.workflow.engine.ProcessData"%>
<%@page import="wt.workflow.engine.WfEngineHelper"%>
<%@page import="wt.workflow.engine.WfProcess"%>
<%@page import="wt.workflow.definer.WfProcessDefinition"%>
<%@page import="java.util.Vector"%>
<%@page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%
String oid = request.getParameter("oid");
Object o = ReferenceFactory.getObjectbyOid(oid);
WTPart part = BomUtil.getLatestPartByBatchView((WTPartMaster)o, "", "Manufacturing");
out.print(part);
%>