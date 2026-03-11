<%@page import="java.util.Vector"%>
<%@page import="ext.casc.workflow.TaskConfigrationHelper"%>
<%@page import="ext.casc.part.SignatureHelper"%>
<%@page import="ext.casc.workflow.signtrue.zp.SignatureService"%>
<%
    String oid = request.getParameter("oid");
	String value = request.getParameter("value");
	String zpGYZZ = request.getParameter("zpGYZZ");
	String value1 = java.net.URLDecoder.decode(value,"UTF-8");
	SignatureHelper.setSignatureImplementAdvise(oid,value1);
	if(zpGYZZ!=null&&"1".equals(zpGYZZ))
		SignatureService.zpGYZZ(oid,value1);
%>
<script>
	window.close();
</script>
