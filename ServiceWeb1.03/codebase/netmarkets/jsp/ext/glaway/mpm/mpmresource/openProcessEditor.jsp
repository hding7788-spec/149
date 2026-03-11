<%@ page
	import="wt.util.*,
                 wt.httpgw.LanguagePreference,
                 java.util.*,
                 com.ptc.netmarkets.util.beans.NmCommandBean,
                 wt.inf.container.WTContainer,
                 wt.org.WTUser,
                 wt.session.SessionHelper,
                 wt.preference.PreferenceHelper,
                 wt.fc.*"
	contentType="text/html; charset=UTF-8"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.ptc.windchill.mpml.processplan.MPMProcessPlan"%>
<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@page import="wt.part.WTPart"%>
<%@page import="com.glaway.mpm.util.MPMProcessPlanUtil"%>
<%@page import="com.glaway.mpm.util.ChangeUtil"%>
<jsp:useBean id="nmcontext"
	class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request" />
<% 
//response.setContentType("text/html; charset=UTF-8");
NmCommandBean  nmCommandBean= new NmCommandBean();
nmCommandBean.setCompContext(nmcontext.getContext().toString());
nmCommandBean.setRequest(request);

NmOid nmoid = nmCommandBean.getActionOid();
String mpmOid = nmoid.getOidObject().getStringValue();
System.out.println("nmoid===>" + mpmOid);
MPMProcessPlan mpmProcess = (MPMProcessPlan)ReferenceFactory.getObjectbyOid(mpmOid);
WTPart part = MPMProcessPlanUtil.getMPMProcessplanRelatedPart(mpmProcess);
HashMap<String, String> map = ChangeUtil.getTechnicInfo(part.getPersistInfo().getObjectIdentifier().getStringValue());
String oid = map.get("topPartOid");
System.out.println("oid=====>" + oid);

long pid = part.getPersistInfo().getObjectIdentifier().getId();
String poid = String.valueOf(pid);
System.out.println("poid=====>" + poid);

//String oid = request.getParameter("oid"); //$NON-NLS-1$
//String poid = request.getParameter("poid"); //$NON-NLS-1$
String charset="UTF-8";

WTContainer container = nmCommandBean.getViewingContainer();
WTUser user = (WTUser)SessionHelper.getPrincipal();  
Boolean useJWS = (Boolean)PreferenceHelper.service.getValue("com/ptc/windchill/explorer/useJWS" ,  "WINDCHILL" , container , user);
//String jwsRuntimeParameters = (String)PreferenceHelper.service.getValue("com/ptc/windchill/explorer/javaRuntimeParametersForJWS" ,  "WINDCHILL" , container , user);
String jwsRuntimeParameters="-Xmx512m";
Locale aLocale = LanguagePreference.getLocale( request.getHeader("Accept-Language") ); //$NON-NLS-1$
String locale = aLocale.toString();
StringBuffer contextObjParams = null;
if (oid != null) {
		contextObjParams = new StringBuffer();
		contextObjParams = contextObjParams.append(",oid=" + oid);	
		if(poid!=null)
		contextObjParams = contextObjParams.append(",poid=" + poid);	
}
%>
<jsp:useBean id="url_factory" class="wt.httpgw.URLFactory"
	scope="request">
	<% url_factory.setRequestURL(request.getScheme(), request.getHeader("HOST"), request.getRequestURI()); //$NON-NLS-1$ %>
</jsp:useBean>
<HTML>
	<HEAD>
		<LINK rel="stylesheet" type="text/css"
			href="<%=url_factory.getHREF("com/ptc/core/ui/solutions.css") %>">
		<STYLE>
.wizTitle {
	color: #FDF8CE;
	font-weight: bold;
	font-size: 1.1em;
	background-color: #0D4794;
}
</STYLE>
	</HEAD>

	<BODY class="wizBorder" leftmargin="0" topmargin="0" marginwidth="0"
		marginHeight="0" onload="loaded()" <%if(true) {%>
		onBeforeUnload="return confirmExit()" <%}%>>
		<TABLE width="95%" border="0" cellpadding="6" cellspacing="0"
			class="wizBorder">
			<%
		String url_jsp = url_factory.getHREF("Windchill/servlet/JNLPGeneratorServlet/processEditor.jnlp"); 		
		String sid = session.getId();

		String jars="ptcCore.jar,wtApplet.jar,wncWeb.jar,newCapp.jar,dom4j.jar,framework.jar,jaxen-1.1.1.jar,base.jar,QMInterface.jar,j3dcore.jar,log4j.jar,assembler.jar,ant-launcher.jar,ant.jar,jgoodies-common-1.4.0.jar,jgoodies-looks-2.5.2.jar,freemarker.jar,swt-debug.jar,jdic.jar";
		
		// create URL for the JNLP generator
		EncodingConverter ec = new EncodingConverter();
		StringBuffer url_b = new StringBuffer();
		url_b = url_b.append(url_jsp);
		url_b = url_b.append("?");
		url_b = url_b.append("title=Process Editor");

		if(jwsRuntimeParameters!=null) url_b = url_b.append("&vm_args=" + ec.encode(jwsRuntimeParameters,charset)); 
		url_b = url_b.append("&mainclass=com.ptc.jws.JNLPApplicationLauncher");
		url_b = url_b.append("&params=mainClass=com.glaway.mpm.util.MPMConnectFrame");
		url_b = url_b.append(",application=ProcessEditor");
		
		if(contextObjParams!=null)url_b = url_b.append(contextObjParams);
		url_b = url_b.append(",hide=true");
		url_b = url_b.append(",singleton=false");
		url_b = url_b.append(",wt.context.locale=" + locale);
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
		if(url_b.length() >= 2048) System.out.println("WARNING: The url to launch PSE via JWS is longer than 2048 characters. This might cause issues with IE");
		%>
			<TR>
				<TD class="wizStepSel">
					It's safe to close this window!
				</TD>
			</TR>
		</TABLE>
	</BODY>
	<SCRIPT language="JAVASCRIPT">                      
       var win = open('<%=url_b%>','_self','status=yes');
       window.setTimeout("SelfCloser()", 5000);
       function SelfCloser(){
       	//alert("schliess mich...win ref:"+win.status);       	
       }
   </SCRIPT>
</HTML>
