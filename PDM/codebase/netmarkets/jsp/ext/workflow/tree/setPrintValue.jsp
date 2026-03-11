<%@page import="ext.casc.workflow.util.PrintDistributionHelper"%>
<%
    String oid = request.getParameter("oid");
	String dep = request.getParameter("dep");
	String value = java.net.URLDecoder.decode(dep,"UTF-8");
	PrintDistributionHelper.setSelDepartValue(oid,value);
%>