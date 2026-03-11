<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.vc.VersionReference"%>
<%@page import="com.ptc.windchill.mpml.processplan.MPMProcessPlan"%>
<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@page import="wt.part.WTPart"%>
<%@page import="com.glaway.mpm.util.MPMProcessPlanUtil"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.ptc.windchill.mpml.processplan.MPMProcessPlanHelper"%>
<%@page import="com.glaway.mpm.util.TechnicPreview"%>
<%@page import="com.glaway.mpm.util.PropertiesUtil"%>
<%@page import="java.net.URLEncoder"%>

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%
	NmCommandBean cb = commandBean;
	NmOid nmoid = cb.getActionOid();
	String vrStr = nmoid.getOidObject().getStringValue();
	MPMProcessPlan mpmPlan = (MPMProcessPlan)ReferenceFactory.getObjectbyOid(vrStr);
	WTDocument document=MPMProcessPlanUtil.getWTDocumentByProcessPlan(mpmPlan);
	String oid=document.toString();
	
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

<%@ include file="/netmarkets/jsp/util/end.jspf"%>