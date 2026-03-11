<%@page import="com.glaway.mpm.util.MPMProcessPlanUtil"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.ptc.windchill.mpml.processplan.MPMProcessPlan"%>
<%@page import="ext.casc.util.WNCUtil"%>
<%@ page import="java.util.*"%>
<%@page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%


String oid = request.getParameter("oid");
wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
wt.fc.ObjectReference self = (wt.fc.ObjectReference)rf.getReference(oid);

MPMProcessPlan plan = (MPMProcessPlan)self.getObject();
WTDocument doc = MPMProcessPlanUtil.getWTDocumentByProcessPlan(plan);
System.out.println("版本："+doc.getVersionIdentifier().getValue()+"."+doc.getIterationIdentifier().getValue());

%>