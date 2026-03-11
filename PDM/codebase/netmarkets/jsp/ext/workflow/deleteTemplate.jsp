<%@page import="ext.casc.workflow.CwbmDBController"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.xyz.platform.drivers.xpdfj.PDFNormalizer.outputppXMLCell"%>
<%
Map<String,String> map=new HashMap<String,String>();
String nameValue= request.getParameter("nameValue");

CwbmDBController.deleteTemplateById(nameValue);
%>
<head>
<script type="text/javascript">
window.onload=function(){
     window.opener=null;
	 window.open('','_self');
	window.close();
};
</script>
</head>