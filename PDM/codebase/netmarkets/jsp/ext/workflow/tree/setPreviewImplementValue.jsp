<%@page import="ext.casc.part.SignatureHelper"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%
    String oid = request.getParameter("oid");
	String value = request.getParameter("value");
	String zpGYZZ = request.getParameter("zpGYZZ");
	String value1 = java.net.URLDecoder.decode(value,"UTF-8");
	SignatureHelper.setPreviewImplementAdvise(oid,value1);
	if(zpGYZZ!=null&&"1".equals(zpGYZZ))
		SignatureHelper.zpPreviewGYZZ(oid,value1);
%>
<script>
	window.close();
</script>
