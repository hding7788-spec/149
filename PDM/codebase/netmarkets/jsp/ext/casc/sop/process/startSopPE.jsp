<%@page import="wt.org.WTUser"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="com.glaway.mpm.model.ProcessEditorBean"%>
<%@page import="com.glaway.mpm.util.LoadProcessEditorProperties"%>
<%@page import="java.io.File"%>
<%@page import="com.glaway.mpm.util.PropertiesUtil"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.httpgw.LanguagePreference"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="wt.util.EncodingConverter"%>
<%@page import="java.util.Locale"%>
<%@ page import="wt.part.WTPart" %>
<%@ page import="wt.vc.VersionControlHelper" %>
<%@ page import="wt.fc.PersistenceHelper" %>
<%@ page import="ext.casc.util.WCUtil" %>
<%@ page import="wt.fc.ReferenceFactory" %>
<%@ page import="ext.casc.sop.util.SopUtil" %>
<%@ page import="wt.util.WTException" %>
<%@ page import="wt.fc.Persistable" %>
<%@ page import="wt.workflow.work.WorkItem" %>
<%@ page import="wt.workflow.engine.WfActivity" %>
<%@ page import="wt.change2.ChangeOrder2" %>
<%@ page import="com.ptc.windchill.mpml.processplan.MPMProcessPlan" %>
<%@ page import="com.glaway.mpm.util.MPMProcessPlanUtil" %>

<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request" />

<input type="hidden" name="taskOid" id="taskOid" value=""/>


<%
    NmCommandBean nmCommandBean = new NmCommandBean();
    nmCommandBean.setCompContext(nmcontext.getContext().toString());
    nmCommandBean.setRequest(request);

    WTPrincipal admin = wt.session.SessionHelper.manager.getAdministrator();
    WTPrincipal current = wt.session.SessionContext.setEffectivePrincipal(admin);
    wt.session.SessionContext.setEffectivePrincipal(current);
   // String jwsRuntimeParameters = "-Xmx512m";
   WTUser user = (WTUser) SessionHelper.getPrincipal();


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



    Locale aLocale = LanguagePreference.getLocale(request.getHeader("Accept-Language"));

    StringBuffer contextObjParams = new StringBuffer();
    Object obj = nmCommandBean.getActionOid().getRefObject();

    /**获取参数 start **/
    long startTime = System.currentTimeMillis();
    String startType = "SOP";
    String taskOid =null;
    String workItemOid = null;
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
    String partOid = null;
    String docOid = null;
    WTPart part = null;
    String pbomXml = "";
    try {
        Object tempObj = nmCommandBean.getPrimaryOid().getRefObject();
        if(tempObj instanceof WTPart) {
            part = (WTPart) tempObj;
            //获取最新小版本
            part = (WTPart)VersionControlHelper.getLatestIteration(part,true);
            partOid = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
            pbomXml = SopUtil.createPbomXml(part);
        }else if (tempObj instanceof WTDocument) {
            workItemOid = "";
            WTDocument doc = (WTDocument) tempObj;
            part = WCUtil.getRelatedWTPartByDoc(doc);
            ReferenceFactory refefence = new ReferenceFactory();
            docOid = refefence.getReferenceString(doc);
            partOid = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
            pbomXml = SopUtil.createPbomXml(part);
        }else if(tempObj instanceof MPMProcessPlan){
            MPMProcessPlan processPlan = (MPMProcessPlan) tempObj;
            WTDocument wtDocument = MPMProcessPlanUtil.getWTDocumentByProcessPlan(processPlan);
            part = WCUtil.getRelatedWTPartByDoc(wtDocument);
            ReferenceFactory refefence = new ReferenceFactory();
            docOid = refefence.getReferenceString(wtDocument);
            partOid = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
            pbomXml = SopUtil.createPbomXml(part);
        }
        pbomXml = pbomXml.replace("\n","");
    } catch (WTException e) {
        e.printStackTrace();
    }
    String changeOrderOid = "";
        String context = request.getParameter("context");
        String changeWorkItemOid = context.split("\\$")[2];
        if(!"".equals(changeWorkItemOid)){
            ReferenceFactory rf = new ReferenceFactory();
            Persistable object = rf.getReference(changeWorkItemOid).getObject();
            if(object instanceof WorkItem){
                WorkItem wi = (WorkItem)object;
                WfActivity wfAct = (WfActivity) wi.getSource().getObject();
                Object pbo = wfAct.getContext().getValue("primaryBusinessObject");
                if(pbo instanceof ChangeOrder2){
                    changeOrderOid = String.valueOf(PersistenceHelper.getObjectIdentifier((ChangeOrder2)pbo).getId());;
                }
            }
        }
    long endTime = System.currentTimeMillis();
    System.out.println("获取参数耗时：" + (endTime - startTime));
    /**获取参数  end**/
    contextObjParams = contextObjParams.append(",param1=" + partOid);//父节点oid
    contextObjParams = contextObjParams.append(",param2=" + partOid);//部件oid
    contextObjParams = contextObjParams.append(",param4=" + workItemOid);//工艺活动oid
    contextObjParams = contextObjParams.append(",param6=" + docOid);
    contextObjParams = contextObjParams.append(",param7=" + startType);
    contextObjParams = contextObjParams.append(",param8=" + changeOrderOid);
    contextObjParams = contextObjParams.append(",param10=" + pbomXml);
    System.out.println("---contextObjParams---"+contextObjParams.toString());
%>

<jsp:useBean id="url_factory" class="wt.httpgw.URLFactory" scope="request">
    <%
        url_factory.setRequestURL(request.getScheme(), request.getHeader("HOST"), request.getRequestURI());
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
<TABLE width="95%" border="0" cellpadding="6" cellspacing="0"
       class="wizBorder">
    <%
        String url_jsp = url_factory.getHREF("Windchill/servlet/GWJNLPGeneratorServlet/processEditor.jnlp");
        String sid = session.getId();

        //String jars = "ptcCore.jar,wtApplet.jar,wncWeb.jar,dom4j-1.6.1.jar,jaxen-1.1.1.jar,newCapp.jar,cappPart.jar,j3dcore.jar,log4j.jar,assembler.jar,ant-launcher.jar,ant.jar,jgoodies-common-1.4.0.jar,jgoodies-looks-2.5.2.jar,freemarker.jar,swt32.jar,jdic.jar,DJNativeSwing-SWT.jar,DJNativeSwing.jar,poi.jar,ie3rdpartylibs.jar,jacob.jar,pdm_specialword.jar,itextpdf-5.4.3.jar,itext-asian-5.4.3.jar,xmlworker-5.4.3.jar,jnlp.jar,html2image-0.9.jar";
        //String jars = "newCapp.jar,log4j.jar,jgoodies-common-1.4.0.jar,jgoodies-looks-2.5.2.jar,DJNativeSwing.jar,DJNativeSwing-SWT.jar,swt32.jar,dom4j.jar,ant.jar,pdm_specialword.jar,html2image-0.9.jar";
		String jars = null;
		if("32".equals(swt)){
			jars = "ptcCore.jar,wtApplet.jar,wncWeb.jar,dom4j-1.6.1.jar,jaxen-1.2.0.jar,newCapp.jar,cappPart.jar,j3dcore.jar,log4j.jar,assembler.jar,ant-launcher.jar,ant.jar,jgoodies-common-1.4.0.jar,jgoodies-looks-2.5.2.jar,freemarker.jar,swt32.jar,jdic.jar,DJNativeSwing-SWT.jar,DJNativeSwing.jar,poi.jar,ie3rdpartylibs.jar,jacob.jar,pdm_specialword.jar,itextpdf-5.4.3.jar,itext-asian-5.4.3.jar,xmlworker-5.4.3.jar,jnlp.jar,html2image-0.9.jar,jsoup-1.10.2.jar";
		}else if("64".equals(swt)){
			jars = "ptcCore.jar,wtApplet.jar,wncWeb.jar,dom4j-1.6.1.jar,jaxen-1.2.0.jar,newCapp.jar,cappPart.jar,j3dcore.jar,log4j.jar,assembler.jar,ant-launcher.jar,ant.jar,jgoodies-common-1.4.0.jar,jgoodies-looks-2.5.2.jar,freemarker.jar,swt64.jar,jdic.jar,DJNativeSwing-SWT.jar,DJNativeSwing.jar,poi.jar,ie3rdpartylibs.jar,jacob.jar,pdm_specialword.jar,itextpdf-5.4.3.jar,itext-asian-5.4.3.jar,xmlworker-5.4.3.jar,jnlp.jar,html2image-0.9.jar,jsoup-1.10.2.jar";
		}


        // create URL for the JNLP generator
        EncodingConverter ec = new EncodingConverter();
        StringBuffer url_b = new StringBuffer();
        url_b = url_b.append(url_jsp);
        url_b = url_b.append("?");
        url_b = url_b.append("title=RelateTypecialProcess Manager");

        if (jwsRuntimeParameters != null) {
            url_b = url_b.append("&vm_args=" + ec.encode(jwsRuntimeParameters, "UTF-8"));
        }
        url_b = url_b.append("&mainclass=com.ptc.jws.JNLPApplicationLauncher");
        url_b = url_b.append("&params=mainClass=com.glaway.mpm.util.MPMConnectFrame");
        url_b = url_b.append(",application=ProcessEditor");

        if (contextObjParams.length() != 0) {
            url_b = url_b.append(contextObjParams);
        }
        url_b = url_b.append(",hide=true");
        url_b = url_b.append(",singleton=false");
        url_b = url_b.append(",wt.context.locale=" + aLocale.toString());
        url_b = url_b.append(",authorization=" + request.getHeader("AUTHORIZATION"));
        url_b = url_b.append("&sid=" + sid);
        url_b = url_b.append("&jars=" + "lib/JWSUtil.jar,lib/WtHttpClientAddOns.jar,lib/pview.jar");
        url_b = url_b.append("&exts=" + jars);
        url_b = url_b.append("&exts_sec=" + "wt/security/security.jar,install/boot.jar");
        url_b = url_b.append("&allperm=1");
        url_b = url_b.append("&documentbase=" + "/");
        url_b = url_b.append("&width=1");
        url_b = url_b.append("&height=1");

        // In IE, a GET URL cannot contain more than 2048 characters. We will just issue a warning on the console for debugging purpose
        // eventually we could use a proper logger and verify the User-Agent for IE
        if (url_b.length() >= 2048) {
            System.out.println("WARNING: The url to launch PE via JWS is longer than 2048 characters. This might cause issues with IE");
        }
    %>
    <TR>
        <TD class="wizStepSel">It's safe to close this window!</TD>
    </TR>
</TABLE>
</BODY>

<SCRIPT language="JAVASCRIPT">
    var win = open('<%=url_b%> ', '_self', 'status=yes');
    window.setTimeout("SelfCloser()", 5000);
    function SelfCloser() {
        close();
    }
</SCRIPT>
</HTML>