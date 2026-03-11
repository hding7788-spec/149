<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.io.File"%>
<%@include file="/netmarkets/jsp/util/begin.jspf"%>

<%
	WTProperties wtp = WTProperties.getLocalProperties();
	String WT_HOME = wtp.getProperty("wt.home");
	String PATH = WT_HOME + File.separator + "temp";
	
	PATH = PATH.replace('\\', '/');
	String itemoid = request.getParameter("oid");
	System.out.println("taskOid===aaa>" + itemoid);
%>

<script>
	function fileupload(){
		var file = document.getElementById("file");
		var form = this.document.mainform[0];
		var filePath = file.value;
		if(file.value == ""){
			alert("请选择关联的文件!");
			return false;
		}
		
		var fileNames = filePath.split('\\');
		var fileName = fileNames[fileNames.length - 1];
		
		form.action="netmarkets/jsp/glaway/mpm/task/deliverable/skim2.jsp?itemoid=" + '<%=itemoid%>';
		form.encoding="multipart/form-data";
		form.target="_blank";
		form.method="post";
		form.submit();
		
		window.open('', '_self');
		window.opener = null;
		window.close();
		
	}
	
</script>

<table>
	<tr>
		<td>选择交付物文件:</td>
		<td><input name="file" type="file" id="file" size="40"/></td>
		<td><input name="Submit" type="button" onclick="fileupload()" value="关联交付物"/></td>
	</tr>
</table>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>