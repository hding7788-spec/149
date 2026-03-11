<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="com.glaway.mpm.util.TechnicPreview"%>
<%@page import="com.glaway.mpm.util.PropertiesUtil"%>
<%@page import="com.glaway.mpm.task.model.GMFillTimeTask"%>
<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@page import="java.io.File"%>
<%@page import="wt.doc.WTDocument"%>
<%
	String oid = request.getParameter("oid");
	System.out.println("part oid:" + oid);
	session.setAttribute("partOid", oid);
	String technicOid = request.getParameter("technicOid");
	System.out.println("technicOid===>" + technicOid);
	GMFillTimeTask fillTask = (GMFillTimeTask)ReferenceFactory.getObjectbyOid(technicOid);
	System.out.println("technicName===>" + fillTask.getName());
	String type = request.getParameter("type");
	WTDocument doc = (WTDocument)ReferenceFactory.getObjectbyOid(fillTask.getPlanDocOid());
	TechnicPreview technicPreview = new TechnicPreview();
	String url = technicPreview.fillTime(doc);
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
	System.out.println("before afterUrl =:" + afterUrl);
	afterUrl = URLEncoder.encode(afterUrl, code);
	System.out.println("afterUrl===>" + afterUrl);
	String reglex = afterUrl.substring(0, afterUrl.indexOf("temp"));
	System.out.println("reglex===>" + reglex);
	afterUrl = afterUrl.replace(reglex, "/");
	if(flag){
		afterUrl = afterUrl.replace("+", "%20");
	}
	url = httpCodeBase + afterUrl;
	System.out.println("url:" + url);
%>

<script	language="javascript">
	window.open("<%=url%>", "_self");
</script>

