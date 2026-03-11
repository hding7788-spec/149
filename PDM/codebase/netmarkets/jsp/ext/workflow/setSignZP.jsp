<%@page import="ext.casc.workflow.signtrue.zp.SignatureService"%>
<%@page import="java.util.Vector"%>
<%@page import="ext.casc.workflow.TaskConfigrationHelper"%>
<%
    String oid = request.getParameter("oid");
	String datas = request.getParameter("value");
	String value1 = java.net.URLDecoder.decode(datas,"UTF-8");
	//TaskConfigrationHelper.setProcessVariableByVarName(oid,"partContainer",value1);
	SignatureService.zpGYY(oid,value1);
%>
