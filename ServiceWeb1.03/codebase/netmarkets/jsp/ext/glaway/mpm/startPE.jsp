<%@page import="com.glaway.mpm.model.ProcessEditorBean"%>

<%@page import="com.glaway.mpm.processplan.helper.ProcessPlanHelper"%>
<%@page import="com.glaway.mpm.util.LoadProcessEditorProperties"%>
<%@page import="com.glaway.mpm.util.PropertiesUtil"%>
<%@page import="com.glaway.mpm.util.WTPartUtil"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="com.ptc.windchill.mpml.processplan.MPMProcessPlan"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="ext.casc.process.ProcessConstants"%>
<%@page import="ext.casc.util.WCUtil"%>
<%@page import="wt.change2.ChangeOrder2"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.httpgw.LanguagePreference"%>
<%@page import="wt.inf.container.WTContainer"%>
<%@page import="wt.org.WTPrincipalReference"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.preference.PreferenceHelper"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.util.EncodingConverter"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.workflow.engine.WfActivity"%>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="java.io.File"%>
<%@page import="java.util.Locale"%>
<%@ page import="java.util.Map" %>

<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request" />

<input type="hidden" name="taskOid" id="taskOid" value=""/>

<SCRIPT language="JAVASCRIPT">
	var taskOid = window.opener.document.getElementById("taskOid").value;
	document.getElementById("taskOid").value=taskOid;
	var taskOid2 = document.getElementById("taskOid").value;
</SCRIPT>

<%
	NmCommandBean nmCommandBean = new NmCommandBean();
	nmCommandBean.setCompContext(nmcontext.getContext().toString());
	nmCommandBean.setRequest(request);

	WTContainer container = nmCommandBean.getViewingContainer();
	WTUser user = (WTUser) SessionHelper.getPrincipal();
	WTPrincipalReference currentPrin = SessionHelper.manager.getPrincipalReference();
	Boolean useJWS = (Boolean) PreferenceHelper.service.getValue("com/ptc/windchill/explorer/useJWS", "WINDCHILL",container, user);
	//String jwsRuntimeParameters = (String)PreferenceHelper.service.getValue("com/ptc/windchill/explorer/javaRuntimeParametersForJWS" ,  "WINDCHILL" , container , user);
	Locale aLocale = LanguagePreference.getLocale(request.getHeader("Accept-Language"));
	StringBuffer contextObjParams = new StringBuffer();
	// 启动工艺编辑器
	// 第一个参数
	// ---------1.正常做工艺
	// --------------------第二个参数： PBOM最上皆整件oid
	// ---------2.合编工艺
	// --------------------第二个参数： PBOM最上皆整件oid
	// --------------------第三个参数： 需要合编的工艺对应的零件的oid
	// ---------3.变更工艺
	// --------------------第二个参数： PBOM最上皆整件oid
	// --------------------第三个参数： 需要变更的工艺对应的零件的oid
	// --------------------第四个参数： 需要变更的工艺对应的压缩包文件oid
	// --------------------第五个参数： 该流程活动activity的oid
	// ---------4.返工工艺
	// --------------------第二个参数： PBOM最上皆整件oid
	// --------------------第三个参数： 返工工艺任务的oid
	// ---------5.临时工艺
	// --------------------第二个参数： PBOM最上皆整件oid
	// --------------------第三个参数： 临时工艺任务的oid

	//获取变量
	String taskType = request.getParameter("ty");
	String startType =request.getParameter("startType");
	System.out.println(">>>>>>>>>>>startType=" + startType);
	String parentPartOid = request.getParameter("O1");
	String partOid = request.getParameter("O2");
	String documentOid = request.getParameter("O3");
	String workItemOid = request.getParameter("O4");
	String workItemStatus = request.getParameter("status");
    String docOid=null;
    String changeOrderOid = null;

	//根据任务类型判断属于什么情况启动工艺编辑器
	Object obj=null;
	if ("1".equals(taskType)) {
		NmOid nmoid = nmCommandBean.getPageOid();
		obj = nmoid.getRefObject();
		System.out.println(">>>>>>>>>>>obj=" + obj);
	}

	String taskState =null;
	workItemStatus = taskState;
	//OR:ext.casc.process.ProcessTaskItem:283946
	String taskOid =null;

	if(taskOid == null||"null".equals(taskOid)) {
		String context = request.getParameter("context");
		System.out.println(">>>>>>>>>>>context=" + context);
		String tempTaskOid = context.split("\\$")[2];
		if(tempTaskOid!=null&&tempTaskOid.contains("ProcessTaskItem")){
	taskOid = context.split("\\$")[2];
		}
	}

	if(taskOid != null && !"".equals(taskOid)) {
		taskOid = taskOid.substring(taskOid.lastIndexOf(":")+1, taskOid.length());
		workItemOid = taskOid;
	}
	System.out.println(">>>>>>>>>>>taskOid=" + taskOid);

	String tType = String.valueOf(session.getAttribute("taskType"));
	System.out.println(">>>>>>>>>>>taskType=" + tType);

	taskType = "1";
	Object tempObj = nmCommandBean.getPrimaryOid().getRefObject();
	System.out.println(">>>>>>>>>>>tempObj=" + tempObj);

	if(changeOrderOid == null) {
	   String context = request.getParameter("context");
	   System.out.println(">>>>>>>>>>>context=" + context);
	   String changeWorkItemOid = context.split("\\$")[2];
	   if(!"".equals(changeWorkItemOid)){
		   ReferenceFactory rf = new ReferenceFactory();
	       Persistable object = rf.getReference(changeWorkItemOid).getObject();
	       if(object instanceof WorkItem){
	       //WorkItem wi= (WorkItem) rf.getReference(changeWorkItemOid).getObject();
	       WorkItem wi = (WorkItem)object;
	       WfActivity wfAct = (WfActivity) wi.getSource().getObject();
	       Object pbo = wfAct.getContext().getValue("primaryBusinessObject");
	       if(pbo instanceof ChangeOrder2){
	    	   changeOrderOid = String.valueOf(PersistenceHelper.getObjectIdentifier((ChangeOrder2)pbo).getId());;
	       }
	     }
	   }
	}

	String swt ="32";
	String jwsRuntimeParameters = "-Xmx1024m";
	String filePath = PropertiesUtil.getLocalCodeBase() + File.separator + "processEditor.properties";
    File file = new File(filePath);
    if (file.exists()) {
    	ProcessEditorBean processEditorBean = LoadProcessEditorProperties.getInstance().checkUser(user.getName());
    	if(processEditorBean != null) {
	swt = processEditorBean.getSwt();
	jwsRuntimeParameters = "-Xmx" + processEditorBean.getJwsRuntimeParameters() + "m";
		}
    }
   	WTPart part = null;
	if (tempObj instanceof WTPart) {
		part = (WTPart) tempObj;
		//获取最新小版本
		part = (WTPart) VersionControlHelper.getLatestIteration(part, true);
		partOid = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
		if ("1".equals(startType)) {
			if (parentPartOid == null || "".equals(parentPartOid) || "null".equals(parentPartOid)) {
				if (WTPartUtil.isHasPbomXml(part)) {
					parentPartOid = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
				} else {
					parentPartOid = WTPartUtil.getHasPbomXmlParentPartIda2a2(part, parentPartOid, false);
				}
			}
			if (parentPartOid == null || "".equals(parentPartOid) || "null".equals(parentPartOid)) {
				parentPartOid = WTPartUtil.getHasPbomXmlParentPartIda2a2(part, parentPartOid, false);
			}
		} else {
			parentPartOid = WTPartUtil.getHasPbomXmlLastestParentPartIda2a2(part, parentPartOid);
			if (parentPartOid == null || "".equals(parentPartOid) || "null".equals(parentPartOid)) {
				if (WTPartUtil.isHasPbomXml(part)) {
					parentPartOid = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
				} else {
					parentPartOid = WTPartUtil.getHasPbomXmlParentPartIda2a2(part, parentPartOid, false);
				}
			}
		}
	} else if (tempObj instanceof MPMProcessPlan) {
		MPMProcessPlan pplan = (MPMProcessPlan) tempObj;
		Map<String, String> map = ProcessPlanHelper.getPartIda2a2ByWorkItem(pplan);
		parentPartOid = map.get("ida2a2");
		partOid = map.get("partOid");
	} else if (tempObj instanceof WorkItem) {
		WorkItem workItem = (WorkItem) tempObj;
		parentPartOid = ProcessPlanHelper.getPartIda2a2ByWorkItem(workItem);
	} else if (tempObj instanceof WTDocument) {
		workItemOid = "";
		WTDocument doc = (WTDocument) tempObj;
		part = WCUtil.getRelatedWTPartByDoc(doc);
		ReferenceFactory refefence = new ReferenceFactory();
		docOid = refefence.getReferenceString(doc);
		partOid = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
		if (WTPartUtil.isHasPbomXml(part)) {
			parentPartOid = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
		} else if (ProcessConstants.TASK_TYPE_GONGYISHEJI.equals(tType)) {
			parentPartOid = String.valueOf(session.getAttribute("topOid"));
		} else {
			parentPartOid = WTPartUtil.getHasPbomXmlParentPartIda2a2(part, parentPartOid, false);
		}

		if (parentPartOid == null || "".equals(parentPartOid) || "null".equals(parentPartOid)) {
			parentPartOid = WTPartUtil.getHasPbomXmlParentPartIda2a2(part, parentPartOid, false);
		}
	}

	String isTemplateCapp = "false";

	if (part != null && (part.getNumber().endsWith("PROCESS_PLAN") || part.getNumber().endsWith("PROCESSPLAN"))) {
		isTemplateCapp = "true";
	}

	contextObjParams = contextObjParams.append(",taskType=" + taskType);
	contextObjParams = contextObjParams.append(",param1=" + parentPartOid);
	contextObjParams = contextObjParams.append(",param2=" + partOid);
	contextObjParams = contextObjParams.append(",param3=" + documentOid);
	contextObjParams = contextObjParams.append(",param4=" + workItemOid);
	contextObjParams = contextObjParams.append(",param5=" + workItemStatus);
	contextObjParams = contextObjParams.append(",param6=" + docOid);
	contextObjParams = contextObjParams.append(",param7=" + startType);
	contextObjParams = contextObjParams.append(",param8=" + changeOrderOid);
	contextObjParams = contextObjParams.append(",param9=" + isTemplateCapp);
	System.out.println(">>>>>>>>>>taskType=" + taskType);
	System.out.println(">>>>>>>>>>parentPartOid=" + parentPartOid);
	System.out.println(">>>>>>>>>>partOid=" + partOid);
	System.out.println(">>>>>>>>>>documentOid=" + documentOid);
	System.out.println(">>>>>>>>>>workItemOid=" + workItemOid);
	System.out.println(">>>>>>>>>>workItemStatus=" + workItemStatus);
	System.out.println(">>>>>>>>>>docOid=" + docOid);
	System.out.println(">>>>>>>>>>startType=" + startType);
	System.out.println(">>>>>>>>>>changeOrderOid=" + changeOrderOid);
	System.out.println(">>>>>>>>>>isTemplateCapp=" + isTemplateCapp);
%>

<jsp:useBean id="url_factory" class="wt.httpgw.URLFactory" scope="request">
	<%
		url_factory.setRequestURL(request.getScheme(), request.getHeader("HOST"), request.getRequestURI()); //$NON-NLS-1$
	%>
</jsp:useBean>
<HTML>
	<HEAD>
		<LINK rel="stylesheet" type="text/css" href="<%=url_factory.getHREF("com/ptc/core/ui/solutions.css")%>">
		<STYLE>
              .wizTitle {
	                  	color: #FDF8CE;
	                 	font-weight: bold;
						font-size: 1.1em;
						background-color: #0D4794;
			   }
		</STYLE>
	</HEAD>

	<BODY class="wizBorder" leftmargin="0" topmargin="0" marginwidth="0" marginHeight="0" onload="loaded()">
		<TABLE width="95%" border="0" cellpadding="6" cellspacing="0" class="wizBorder">
			<%
				String url_jsp = url_factory.getHREF("Windchill/servlet/GWJNLPGeneratorServlet/processEditor.jnlp");
				String jars = null;
				if("32".equals(swt)){
					jars = "ptcCore.jar,wtApplet.jar,wncWeb.jar,dom4j.jar,jaxen-1.2.0.jar,newCapp.jar,cappPart.jar,j3dcore.jar,log4j.jar,assembler.jar,ant-launcher.jar,ant.jar,jgoodies-common-1.4.0.jar,jgoodies-looks-2.5.2.jar,freemarker.jar,swt32.jar,jdic.jar,DJNativeSwing-SWT.jar,DJNativeSwing.jar,poi.jar,ie3rdpartylibs.jar,jacob.jar,pdm_specialword.jar,itextpdf-5.4.3.jar,itext-asian-5.4.3.jar,xmlworker-5.4.3.jar,jnlp.jar,html2image-0.9.jar,jsoup-1.10.2.jar,commons-collections4.jar";
				}else if("64".equals(swt)){
					jars = "ptcCore.jar,wtApplet.jar,wncWeb.jar,dom4j.jar,jaxen-1.2.0.jar,newCapp.jar,cappPart.jar,j3dcore.jar,log4j.jar,assembler.jar,ant-launcher.jar,ant.jar,jgoodies-common-1.4.0.jar,jgoodies-looks-2.5.2.jar,freemarker.jar,swt64.jar,jdic.jar,DJNativeSwing-SWT.jar,DJNativeSwing.jar,poi.jar,ie3rdpartylibs.jar,jacob.jar,pdm_specialword.jar,itextpdf-5.4.3.jar,itext-asian-5.4.3.jar,xmlworker-5.4.3.jar,jnlp.jar,html2image-0.9.jar,jsoup-1.10.2.jar,commons-collections4.jar";
				}
				EncodingConverter ec = new EncodingConverter();
				StringBuffer url_b = new StringBuffer();
				url_b = url_b.append(url_jsp);
				url_b = url_b.append("?");
				url_b = url_b.append("title=Process Editor");

				if (jwsRuntimeParameters != null)url_b = url_b.append("&vm_args="+ ec.encode(jwsRuntimeParameters, "UTF-8"));
				url_b = url_b.append("&mainclass=com.ptc.jws.JNLPApplicationLauncher");
				url_b = url_b.append("&params=mainClass=com.glaway.mpm.util.MPMConnectFrame");
				url_b = url_b.append(",application=ProcessEditor");


				if (contextObjParams.length()!=0)url_b = url_b.append(contextObjParams);
				url_b = url_b.append(",hide=true");
				url_b = url_b.append(",singleton=false");
				url_b = url_b.append(",wt.context.locale=" + aLocale.toString());
				url_b = url_b.append(",authorization="+ request.getHeader("AUTHORIZATION"));
				url_b = url_b.append("&jars="+ "lib/JWSUtil.jar,lib/WtHttpClientAddOns.jar,lib/pview.jar");
				url_b = url_b.append("&exts=" + jars);
				url_b = url_b.append("&exts_sec="+ "install/boot.jar");
				url_b = url_b.append("&allperm=1");
				url_b = url_b.append("&documentbase=" + "/");
				url_b = url_b.append("&width=1");
				url_b = url_b.append("&height=1");
				System.out.println("url_b:" + url_b);
				System.out.println("url_b.length()::" + url_b.length());
				// In IE, a GET URL cannot contain more than 2048 characters. We will just issue a warning on the console for debugging purpose
				// eventually we could use a proper logger and verify the User-Agent for IE
				if (url_b.length() >= 2048)System.out.println("WARNING: The url to launch PSE via JWS is longer than 2048 characters. This might cause issues with IE");
			%>
			<TR><TD class="wizStepSel">It's safe to close this window!</TD></TR>
		</TABLE>
	</BODY>
	<SCRIPT language="JAVASCRIPT">
    	var win = open('<%=url_b%>','_self','status=yes');
		window.setTimeout("SelfCloser()", 5000);
		function SelfCloser() {
			close();
		}
	</SCRIPT>
</HTML>-