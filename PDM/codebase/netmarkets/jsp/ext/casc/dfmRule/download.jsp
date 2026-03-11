<%@page import="java.io.File"%>
<%@page import="ext.casc.dfmRule.util.TeamUtil"%>
<%@page import="wt.fc.WTObject"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="wt.folder.SubFolder"%>
<%@page import="wt.inf.container.WTContainer"%>
<%@page import="wt.org.WTUser"%>
<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="wt.util.WTException"%>
<%@page import="wt.httpgw.URLFactory"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.fc.WTReference"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%-- <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%> --%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@include file="/netmarkets/jsp/util/beginPopup.jspf"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
	String urlStr = WTProperties.getServerCodebase().toExternalForm();//域名http://pdm.cacgg.com/Windchill

	//WTObject object = (WTObject) commandBean.getActionOid().getRefObject();

	String oid = request.getParameter("oid");
	ReferenceFactory rFactory = new ReferenceFactory();
	WTReference reference = rFactory.getReference(oid);
	Object object = reference.getObject();
	WTDocument document = null;
	QueryResult primaryResult = null;
	QueryResult attachResult = null;
	String folderPath = null;
	if (object instanceof WTDocument) {
		document = (WTDocument) object;
		folderPath = document.getFolderPath();
		primaryResult = ContentHelper.service.getContentsByRole(document, ContentRoleType.PRIMARY);//文档主内容
		while(primaryResult.hasMoreElements()) {
			ApplicationData data = (ApplicationData)primaryResult.nextElement();
			folderPath = folderPath + "/" + data.getFileName();
			System.out.println("folderPath=====" + folderPath);
		}
		attachResult = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);//文档附件
	}

	WTContainer container = document.getContainer();
	boolean isManager = TeamUtil.isManager(container);//判断当前用户是否存储库管理员

%>

<html>
<head>
<title>工艺规则包文件下载</title>
<script type="text/javascript" language="javascript">
    var folderPath = '<%=folderPath%>';
    var path = "DFMPro_ProE_Installation";
    var ruleFilePath = "DFMPRO_RULEFILE";
    var dataBasePath = "DFM_PRO_DATABASE_DIR";
    //var path = "%TEST_PATH%";
    var isManager = '<%=isManager%>';
	function InitAjax() {
		var ajax;
		if (window.ActiveXObject) {//浏览器是否支持ActiveX控件
			var versions = [ 'Microsoft.XMLHTTP', 'MSXML.XMLHTTP',
					'Microsoft.XMLHTTP', 'Msxml2.XMLHTTP.7.0',
					'Msxml2.XMLHTTP.6.0', 'Msxml2.XMLHTTP.5.0',
					'Msxml2.XMLHTTP.4.0', 'MSXML2.XMLHTTP.3.0',
					'MSXML2.XMLHTTP' ];
			for ( var i = 0; i < versions.length; i++) {
				try {
					ajax = new ActiveXObject(versions[i]);
					if (ajax) {
						return ajax;
					}
				} catch (e) {
				}

			}
		} else if (window.XMLHttpRequest) {
			ajax = new XMLHttpRequest();
		}

		return ajax;
	}

	var fso = new ActiveXObject("Scripting.FileSystemObject");//创建文件系统对象
	function deleteFile(name) {
		//var fso = new ActiveXObject("Scripting.FileSystemObject");//创建文件系统对象
		if (fso.FileExists(name)) {
			fso.DeleteFile(name);
		} else {
			return false;
		}
	}

	function createFile(name) {
		//var fso = new ActiveXObject("Scripting.FileSystemObject");
		if (fso.FileExists(name)) {
			fso.DeleteFile(name);
		}
		var tf = fso.CreateTextFile(name, true);
		if (isManager == "true") {
			tf.WriteLine("IsRuleManager=true");
		} else {
			tf.WriteLine("IsRuleManager=false");
		}

		tf.writeBlankLines(0);
		tf.write(folderPath);
		tf.Close();
	}

	function getLocalPath(fileType) {
		var wshShell = new ActiveXObject("WScript.Shell");
		//var localPath = wshShell.ExpandEnvironmentStrings(path);
		var localPath = "";
		if (fileType == "ruleFile") {
			localpath = wshShell.Environment("System").item(ruleFilePath);//下载规则包路径
			if (localPath == "" || localPath == null) {
				//localPath = wshShell.Environment("System").item(path) + "\\" + "RuleFiles";
				localPath = wshShell.Environment("System").item(path) + "RuleFiles";
				if (!fso.FolderExists(localPath)) {
					fso.CreateFolder(localPath);
				}
			}
		} else if (fileType == "dataFile") {
			localPath = wshShell.Environment("System").item(dataBasePath);//下载规则数据库文件路径
		}

		return localPath;
	}

	/* function downloadFiles(urls){
		var ss = urls.split("|");
		for(var i = 0; i < ss.length; i++){
	        var url = ss[i];
	        getFile(url);
	    }
		createFile("d:\\dd.txt");
	} */

	//js自动下载文件到本地
	var xh;
	var fileName;
	var downloadPath;
	function getFile(url, localPath) {
		//deleteFile("d:\\dd.txt");
		//var localPath = getLocalPath();
		downloadPath = localPath;
		//alert('downloadPath=' + downloadPath);
		fileName = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
		//alert('fileName=' + fileName);
		xh = InitAjax();
		deleteFile(localPath + "\\" + fileName);
		xh.onreadystatechange = getReady;//发送请求响应事件
		//alert("url" + url);
		xh.open("GET", url, false);//true 异步请求 false 同步请求
		xh.send();
	}

	function getReady() {
		/* readyState参数值
		0：请求未初始化
		1：服务器连接建立
		2：请求已接收
		3：请求处理中
		4：请求已完成，且响应就绪 */
		/* status参数值
		200：“OK”
		404：未找到页面 */
		//var localPath = getLocalPath();
		if (xh.readyState == 4 && xh.status == 200) {
			saveFile(downloadPath + "\\" + fileName);
			return true;
		} else {
			return false;
		}
	}

	function saveFile(tofile) {
		var objStream;
		var file;
		file = xh.responseBody;//获取数据
		try{
		objStream = new ActiveXObject("ADODB.Stream");
		objStream.Type = 1;
		objStream.open();
		objStream.write(file);
		objStream.SaveToFile(tofile);
		objStream.close();
		}catch(err){
			alert(err);
		}
	}
	//js自动下载文件到本地结束
	/* 在“跨域访问数据资源”设置为启用 */

	function download() {
		var localPath = getLocalPath("ruleFile");
		createFile(localPath + "\\user-flag.txt");
		var ruleObj = document.getElementById("ruleFile");
		var dataObj = document.getElementById("dataFile");
		//alert('ruleObj=' + ruleObj);
		if(ruleObj != null){
			getFile(document.getElementById("ruleFile").value, localPath);//下载规则数据库文件
		}
		if(dataObj != null){
			getFile(document.getElementById("dataFile").value, getLocalPath("dataFile"));//下载规则数据库文件
		}
		window.opener = null;
        window.open('', '_self');
        window.close();
	}
	window.onload = function(){download();}
</script>

</head>
<body>
     <%
        ApplicationData data = null;
        primaryResult = ContentHelper.service.getContentsByRole(document, ContentRoleType.PRIMARY);//文档主内容
        if(primaryResult.hasMoreElements()){
            data = (ApplicationData) primaryResult.nextElement();
     %>
             <input type="hidden" id="ruleFile" value="<%=urlStr %>servlet/WindchillAuthGW/wt.content.ContentHttp/viewContent/<%=data.getFileName() %>?u8&HttpOperationItem=<%=data.toString() %>&ContentHolder=<%=document.toString() %>&originalFileName=<%=data.getFileName() %>&forceDownload=true">
     <%
        }
        while(attachResult.hasMoreElements()){
            data = (ApplicationData) attachResult.nextElement();
            if(data.getFileName().endsWith(".dat")){

     %>
             <input type="hidden" id="dataFile" value="<%=urlStr %>servlet/WindchillAuthGW/wt.content.ContentHttp/viewContent/<%=data.getFileName() %>?u8&HttpOperationItem=<%=data.toString() %>&ContentHolder=<%=document.toString() %>&originalFileName=<%=data.getFileName() %>&forceDownload=true">
     <%
            }

        }
     %>
</body>
</html>
<%@include file="/netmarkets/jsp/util/end.jspf"%>
