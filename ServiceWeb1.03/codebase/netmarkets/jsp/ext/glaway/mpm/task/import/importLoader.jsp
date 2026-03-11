<%@ page language="java" pageEncoding="UTF-8" %>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.io.File"%>
<%@page import="ext.number.utilities.FileUploader"%>
<%@page import="com.glaway.mpm.task.util.ExcelItemTaskDataSource"%>
<%
	WTProperties wtp = WTProperties.getLocalProperties();
	String WT_HOME = wtp.getProperty("wt.home");
	String PATH = WT_HOME + File.separator + "temp";
	
	FileUploader uploader = new FileUploader(pageContext, PATH);
	
	String filepath = (String)((uploader.getFilesPath()).get(0));
	System.out.println("filePath===>" + filepath);
	
	ExcelItemTaskDataSource itemTaskData = new ExcelItemTaskDataSource(filepath);
	itemTaskData.buildItemTask(itemTaskData.getDataSource());
	String errMg = "导入完成!第三条数据出现错误!";
%>

<script language="javascript">
	
	var errorMg = "<%=errMg %>";
	
	window.opener.document.getElementById("logBoard").value = errorMg;
	
	window.open('', '_self');
	window.opener = null;
	window.close();
</script>