<%@ page language="java" pageEncoding="UTF-8" %>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.io.File"%>
<%@page import="ext.number.utilities.FileUploader"%>
<%@page import="com.glaway.mpm.task.util.ExcelItemTaskDataSource"%>
<%@page import="com.glaway.mpm.task.model.GMItemTask"%>
<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@page import="com.glaway.mpm.task.util.ItemTaskUtil"%>

<%
	WTProperties wtp = WTProperties.getLocalProperties();
	String WT_HOME = wtp.getProperty("wt.home");
	String PATH = WT_HOME + File.separator + "temp";
	System.out.println("PATH===>" + PATH);
	
	FileUploader uploader = new FileUploader(pageContext, PATH);
	
	String filepath = (String)((uploader.getFilesPath()).get(0));
	System.out.println("filePath===>" + filepath);
	String oid = request.getParameter("itemoid");
	System.out.println("oid====>" + oid);
	
	GMItemTask itemTask = (GMItemTask)ReferenceFactory.getObjectbyOid(oid);
%>

<script language="javascript">
	
	window.open('', '_self');
	window.opener = null;
	window.close();
</script>

