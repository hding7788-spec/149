<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="ext.casc.workflow.signtrue.zp.SignatureService"%>
<%@page import="java.util.Vector"%>
<%@page import="ext.casc.workflow.TaskConfigrationHelper"%>
<%@page import="ext.casc.part.SignatureHelper"%>
<%
	String oid = request.getParameter("oid");
	String value = request.getParameter("value");
	String proxy = request.getParameter("proxy");
	//System.out.println(">>>>>>>proxy:"+proxy);
	String value1 = java.net.URLDecoder.decode(value,"UTF-8");
	String zpGYZZ = request.getParameter("zpGYZZ");

	if(proxy!=null&&!"no".equals(proxy)){//外部会签，需要特殊处理
	    SignatureHelper.setSignatureImplementAdvise(oid,value1,proxy);
	}else {
	    SignatureHelper.setSignature(oid,value1);
	}
	if(zpGYZZ!=null&&"1".equals(zpGYZZ))//
		SignatureService.zpGYZZ(oid,value1);


%>
<script>
	window.close();
</script>