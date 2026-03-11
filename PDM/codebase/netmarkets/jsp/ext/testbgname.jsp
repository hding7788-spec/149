<%@page import="ext.BgNameTest"%>
<%@page import="wt.method.RemoteMethodServer"%>
<%@page import="wt.method.MethodContext"%>
<%@page import="wt.util.WTContext"%>
<%@page import="wt.pdmlink.PDMLinkProduct"%>
<%@page import="com.ptc.windchill.uwgm.soap.uwgmdb.PDMPartType"%>
<%@page import="ext.casc.util.WCUtil"%>
<%@page import="wt.workflow.engine.ProcessData"%>
<%@page import="wt.workflow.engine.WfEngineHelper"%>
<%@page import="wt.workflow.engine.WfProcess"%>
<%@page import="wt.workflow.definer.WfProcessDefinition"%>
<%@page import="wt.workflow.definer.WfDefinerHelper"%>
<%@page import="java.util.Vector"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>

<%
MethodContext mc = MethodContext.getContext(Thread.currentThread());
System.out.println(WTContext.getContext());
System.out.println(RemoteMethodServer.getDefault().getInfo());
BgNameTest.test();

%>