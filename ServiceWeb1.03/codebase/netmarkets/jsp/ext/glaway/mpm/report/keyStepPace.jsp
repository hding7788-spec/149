<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="com.glaway.mpm.report.KeyTechnicStep"%>
<%@page import="com.glaway.mpm.util.PropertiesUtil"%>

<%
	String oid = request.getParameter("oid");
	System.out.println("oid==>" + oid);
	String url = KeyTechnicStep.getKeyTechnicUrl(oid);
	System.out.println("url==>" + url);
	if("".equals(url)){
		url = "codebase/netmarkets/jsp/ext/glaway/mpm/report/noKeyStepPace.jsp";
	}
	PropertiesUtil propertiesUtil = new PropertiesUtil();
	String httpCodeBase = propertiesUtil.getHttpCodeBase();

	url = httpCodeBase + url.split("codebase")[1].replace("\\", "/");
	System.out.println("url==>" + url);
%>

<script	language="javascript">
	window.open(encodeURI("<%=url%>"), "_self");
</script>