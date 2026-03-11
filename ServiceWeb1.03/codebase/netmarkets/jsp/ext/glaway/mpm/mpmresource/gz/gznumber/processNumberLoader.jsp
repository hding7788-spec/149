<%@page import="ext.number.utilities.FileUploader"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.io.File"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.loader.ExcelFileDataSource"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.loader.GZNumberLoaderTranslator"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.loader.GZNumberLoader"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.loader.DataSource"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.loader.GZNumberTranslated"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.loader.LoadReporter"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.bean.PropertiesBean"%>
<%
	PropertiesBean pb = new PropertiesBean();
	WTProperties wtp = WTProperties.getLocalProperties();
	String WT_HOME = wtp.getProperty("wt.home");
	String PATH = WT_HOME + File.separator + "temp";
	FileUploader uploader = new FileUploader(pageContext, PATH);
	
	Vector filePaths = uploader.getFilesPath();
	String filepath = (String)(filePaths.get(filePaths.size()-1));
	ExcelFileDataSource datasource = new ExcelFileDataSource(filepath);
	GZNumberLoaderTranslator translator = new GZNumberLoaderTranslator();
	
	GZNumberLoader loader = new GZNumberLoader((DataSource)datasource, (GZNumberTranslated)translator);
	loader.load();
%>	
	<script>
	window.opener.document.getElementById('logBoard').value = "";
	window.opener.document.getElementById('errorBoard').value = "";
	</script>
<%	
	LoadReporter report = loader.getReporter();
	
	ArrayList backErrors = report.getBackErrors();
	String errorMsg = "";
	if(backErrors.size()>0)
		errorMsg = (String)backErrors.get(0);
	
	ArrayList errorReport = report.getDisplayErrors();
	for (int i = 0; i < errorReport.size(); i++) {
%>
		<script>window.opener.document.getElementById('errorBoard').value=window.opener.document.getElementById('errorBoard').value + '<%=(String)(errorReport.get(i))%>' + '\n'</script>
<%
	}
%>
	<script>
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