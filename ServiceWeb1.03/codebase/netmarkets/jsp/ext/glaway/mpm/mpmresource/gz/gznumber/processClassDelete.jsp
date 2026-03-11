
<%@page import="com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassificationHelper"%>
<%
	String classPath = request.getParameter("classPath");
	
	GZNumberClassificationHelper.deleteClassification(classPath);
%>	
	<script>
		window.opener.location.reload();
		window.open('','_self');  
		window.opener=null;
		window.close();
	</script>