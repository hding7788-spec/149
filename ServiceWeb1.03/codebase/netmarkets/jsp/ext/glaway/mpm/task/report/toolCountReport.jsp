<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.io.File"%>
<%@include file="/netmarkets/jsp/util/begin.jspf"%>

<%
	WTProperties wtp = WTProperties.getLocalProperties();
	String WT_HOME = wtp.getProperty("wt.home");
	String PATH = WT_HOME + File.separator + "temp";
	
	PATH = PATH.replace('\\', '/');
%>

<script>
	function fileupload(){
		var file = document.getElementById("file");
		var form = this.document.mainform[0];
		var filePath = file.value;
		if(file.value == ""){
			alert("请选择导入的文件!");
			return false;
		}
		
		var fileNames = filePath.split('\\');
		var fileName = fileNames[fileNames.length - 1];
		
		form.action="netmarkets/jsp/glaway/mpm/task/report/importImage.jsp";
		form.encoding="multipart/form-data";
		form.target="_blank";
		form.method="post";
		form.submit();
		
		pause(500);
		
		var imgDis = document.getElementById("imgDisplay");
		imgDis.src = "<%=PATH%>" + "/" + fileName;
		
		window.open('', '_self');
		window.opener = null;
		window.close();
		
	}
	
	function pause(millisecondi)
		{
		    var now = new Date();
		    var exitTime = now.getTime() + millisecondi;
		
		    while(true)
		    {
		        now = new Date();
		        if(now.getTime() > exitTime) return;
		    }
		}

	
</script>

<table>
	<tr>
		<td>选择图片:</td>
		<td><input name="file" type="file" id="file" size="40"/></td>
		<td><input name="Submit" type="button" onclick="fileupload()" value="上传图片"/></td>
	</tr>
	<tr>
		<td>图片显示:</td>
		<td><div id="imgdiv"><img id="imgDisplay"/></div></td>
	</tr>
</table>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>