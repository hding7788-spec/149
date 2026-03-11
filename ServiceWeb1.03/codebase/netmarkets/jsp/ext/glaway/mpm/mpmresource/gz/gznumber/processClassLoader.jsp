<%@page import="ext.number.utilities.FileUploader"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.io.File"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.loader.ExcelFileDataSource"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.loader.GZNumberClassificationLoaderTranslator"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.loader.GZNumberClassificationLoader"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.loader.LoadReporter"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.loader.DataSource"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.loader.GZNumberClassificationTranslated"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.bean.PropertiesBean"%>

<%
	PropertiesBean pb = new PropertiesBean();
	//FileUploader uploader = new FileUploader(pageContext, pb.getValue("FileUploadingPath"));
	WTProperties wtp = WTProperties.getLocalProperties();
	String WT_HOME = wtp.getProperty("wt.home");
	String PATH = WT_HOME + File.separator + "temp";
	FileUploader uploader = new FileUploader(pageContext, PATH);
	
	String filepath = (String)((uploader.getFilesPath()).get(0));
	ExcelFileDataSource datasource = new ExcelFileDataSource(filepath);
	GZNumberClassificationLoaderTranslator translator = new GZNumberClassificationLoaderTranslator();
	
	GZNumberClassificationLoader loader = new GZNumberClassificationLoader((DataSource)datasource, (GZNumberClassificationTranslated)translator);
	loader.load();
	
	LoadReporter report = loader.getReporter();
	/*
	ArrayList logs = report.getLogs();
	String strLogs = "";
	String strErrors = "";
	for (int i = 0; i < logs.size(); i++) {
		strLogs = strLogs + (String)(logs.get(i)) + "@@";
	}
	
	ArrayList errorReport = report.getDisplayErrors();
	for (int i = 0; i < errorReport.size(); i++) {
		strErrors = strErrors + (String)(errorReport.get(i)) + "@@";
	}
	*/
	ArrayList backErrors = report.getBackErrors();
	String errorMsg = "";
	if(backErrors.size()>0)
		errorMsg = (String)backErrors.get(0);
%>

<script>
window.opener.document.getElementById('logBoard').value = "";
window.opener.document.getElementById('errorBoard').value = "";

var logs = "";//输出logs会影响性能。加上需放开上面代码，并改此行。
var logArr = logs.split("@@");
for(var i=0;i<logArr.length;i++){
	if(logArr[i]!=""){
		window.opener.document.getElementById('logBoard').value=window.opener.document.getElementById('logBoard').value + logArr[i] + "\n";
	}
}

var errorlogs = "";//输出logs会影响性能。加上需放开上面代码，并改此行。
var errLogArr = errorlogs.split("@@");
for(var i=0;i<errLogArr.length;i++){
	if(errLogArr[i]!=""){
		window.opener.document.getElementById('errorBoard').value=window.opener.document.getElementById('errorBoard').value + errLogArr[i] + "\n";
	}
}

var errorMsg = "<%=errorMsg%>";
if(errorMsg != ""){
	alert(errorMsg);
	window.opener.document.getElementById('errorBoard').value=window.opener.document.getElementById('errorBoard').value + errorMsg;
}else{
	window.opener.document.getElementById('logBoard').value = "Data imported successfully!"
}

	window.open('','_self');  
	window.opener=null;
	window.close();
</script>