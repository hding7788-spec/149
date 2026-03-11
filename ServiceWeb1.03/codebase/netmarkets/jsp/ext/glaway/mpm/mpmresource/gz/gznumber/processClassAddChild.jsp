
<%@page import="com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassificationInfoContained"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassificationHelper"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassification"%>
<%
	String classPath = request.getParameter("classPath");
	String classParentPath = request.getParameter("classParentPath");
	String className = request.getParameter("className");
	String classValue = request.getParameter("classValue");
	classPath = classPath == null? "" : classPath.trim();
	classParentPath = classParentPath == null? "" : classParentPath.trim();
	className = className == null? "" : className.trim();
	classValue = classValue == null? "" : classValue.trim();
	
	GZNumberClassificationInfoContained classInfo = new GZNumberClassification();
	classInfo.setObjectclasspath(classPath);
	classInfo.setObjectparentclasspath(classParentPath);
	classInfo.setObjectname(className);
	classInfo.setObjectclassvalue(classValue);
	classInfo.setObjectclassdesc("");
	
	GZNumberClassificationHelper.regClassification(classInfo);
%>	

	<script>
		window.opener.location.reload();
		window.open('','_self');  
		window.opener=null;
		window.close();
	</script>