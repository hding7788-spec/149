<%@page import="com.glaway.mpm.util.Util"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="com.glaway.mpm.util.TechnicPreview"%>
<%@page import="com.glaway.mpm.util.PropertiesUtil"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@page import="wt.vc.VersionControlHelper"%>

<%
	String oid = request.getParameter("oid");
	System.out.println(">>>>>>>>>>>>>oid:"+oid);
	WTDocument document = (WTDocument) Util.getObjectByOid(WTDocument.class, oid);
	document = (WTDocument) VersionControlHelper.service.getLatestIteration(document, true);
	oid=document.toString();

	TechnicPreview technicPreview = new TechnicPreview();
	String url = technicPreview.preview(oid);
	PropertiesUtil propertiesUtil = new PropertiesUtil();
	String httpCodeBase = propertiesUtil.getHttpCodeBase();

	String code = "";
	boolean flag = false;
	if(System.getProperty("os.name").contains("Windows")){
		code = "UTF-8";
		flag = true;
	}else{
		code = "GBK";
	}

	System.out.println("code===>" + code);
	String afterUrl = url.split("codebase")[1];
	System.out.println("before afterUrl = :" + afterUrl);
	afterUrl = URLEncoder.encode(afterUrl, code);
	System.out.println("afterUrl===>" + afterUrl);
	String reglex = afterUrl.substring(0, afterUrl.indexOf("temp"));
	System.out.println("reglex====>" + reglex);
	afterUrl = afterUrl.replace(reglex, "/");
	if(flag){
		afterUrl = afterUrl.replace("+", "20%");
		afterUrl = afterUrl.replace("20%", "%20");//零件中的空格
	}
	url = httpCodeBase + afterUrl;
	System.out.println("url:" + url);
%>

<script	language="javascript">
	window.open("<%=url%>", "_self");
</script>