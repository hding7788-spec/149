
<%@page import="com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassificationHelper"%>
<%
	String classPath = request.getParameter("classPath");
	String className = request.getParameter("className");
	String classValue = request.getParameter("classValue");
	
	classPath = classPath == null? "" : classPath.trim();
	className = className == null? "" : className.trim();
	classValue = classValue == null? "" : classValue.trim();
	
	GZNumberClassificationHelper.updateClassification(classPath,className,classValue);
%>	
	<script>
		window.opener.location.reload();
		window.open('','_self');  
		window.opener=null;
		window.close();
	</script>