<%@page language="java" session="true" pageEncoding="GBK"%>
<%@page import="com.glaway.mpm.constants.Constants"%>
<%
	//Get selected classification path and number
	String classPath = request.getParameter("classpath");
	String[] aClassPath = classPath.split("_NumberGen_");

	out.print(classPath + " -- " + aClassPath.length);
	
	String className = "";
	String path = "";
	String gzNumber = "";
	
	className = aClassPath[0];
	path = aClassPath[1];
	gzNumber = aClassPath[2];
	//Get classification object by path
	if(path.startsWith("RootA")){
		gzNumber = Constants.alGZ  + gzNumber;
	}else if(path.startsWith("RootB")){
		gzNumber = Constants.kGZ + gzNumber;
	}else if(path.startsWith("RootC")){
		gzNumber = Constants.tGZ + gzNumber;
	}

%>
	<script>window.opener.document.getElementById('gzNumber').value='<%=gzNumber%>'</script>
 	<script>window.opener.document.getElementById('fullPath').value='<%=path%>'</script>   
	<script>
		window.open('','_self');  
		window.opener=null;
		window.close();
	</script>
