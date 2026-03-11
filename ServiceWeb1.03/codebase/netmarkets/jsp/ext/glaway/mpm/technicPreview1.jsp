<%@page import="com.glaway.mpm.util.Util"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="com.glaway.mpm.util.TechnicPreview"%>
<%@page import="com.glaway.mpm.util.PropertiesUtil"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@ page import="wt.util.*,
                 wt.httpgw.LanguagePreference,
                 java.util.*,
                 com.ptc.netmarkets.util.beans.NmCommandBean,
                 wt.inf.container.WTContainer,
                 wt.org.WTUser,
                 wt.session.SessionHelper,
                 wt.type.TypedUtilityServiceHelper,
                 wt.identity.IdentityFactory,
                 wt.preference.PreferenceHelper,
                 wt.fc.*" contentType="text/html; charset=UTF-8"
%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.glaway.mpm.util.WTDocumentUtil"%>
<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request"/>
<%
NmCommandBean cb2 = new NmCommandBean();
cb2.setCompContext(nmcontext.getContext().toString());
cb2.setRequest(request);

  String oid = request.getParameter("oid");
	//System.out.println(">>>>>>>>>>>>>oid:"+oid);
  String processPlanNumber = request.getParameter("processPlanNumber");
  System.out.println("##MES_processPlanNumber="+processPlanNumber);
  WTDocument document =null;
  if(processPlanNumber!=null&&!"".equals(processPlanNumber)){
	  document =  WTDocumentUtil.getDocumentByNumber(processPlanNumber);

  }else{
		 Persistable obj = (Persistable)cb2.getPageOid().getRefObject();
		 if(obj instanceof WTDocument){
			  oid = String.valueOf(PersistenceHelper.getObjectIdentifier(obj).getId());
		 }
		//System.out.println(">>>>>>>>>>>>>oid2222:"+oid);
		Object tempObj = cb2.getPrimaryOid().getRefObject();;
		if(tempObj instanceof WTDocument){
			oid = String.valueOf(PersistenceHelper.getObjectIdentifier((Persistable)tempObj).getId());
		}
//System.out.println("1>>>>>>>>>>>>>oid:"+oid);

		 document = (WTDocument) Util.getObjectByOid(WTDocument.class, oid);
  }
	if(document == null){
		document = com.glaway.mpm.processplan.helper.ProcessPlanHelper.getTechnicsDocByMPMPPlan(oid);
	}
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