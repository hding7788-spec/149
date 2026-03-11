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
<%@page import="wt.fc.PersistenceHelper" %>
<%@page import="wt.doc.WTDocument" %>
<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request"/>
<%
//response.setContentType("text/html; charset=UTF-8");
NmCommandBean cb2 = new NmCommandBean();
cb2.setCompContext(nmcontext.getContext().toString());
cb2.setRequest(request);
String oid = null;
Persistable obj = (Persistable)cb2.getPageOid().getRefObject();
if(obj instanceof WTDocument){
	oid = String.valueOf(PersistenceHelper.getObjectIdentifier(obj).getId());
}
//String oid = request.getParameter("oid"); //$NON-NLS-1$
//if(oid.contains(":")){
	//oid=new NmOid(oid).getOid().getId()+"";
//}
String charset="UTF-8";
System.out.println(oid+"-----oid");

WTContainer container = cb2.getViewingContainer();
String cid =  String.valueOf(PersistenceHelper.getObjectIdentifier(container).getId()) ;

WTUser user = (WTUser)SessionHelper.getPrincipal();
Boolean useJWS = (Boolean)PreferenceHelper.service.getValue("com/ptc/windchill/explorer/useJWS" ,  "WINDCHILL" , container , user);
String jwsRuntimeParameters = (String)PreferenceHelper.service.getValue("com/ptc/windchill/explorer/javaRuntimeParametersForJWS" ,  "WINDCHILL" , container , user);
Locale aLocale = LanguagePreference.getLocale( request.getHeader("Accept-Language") ); //$NON-NLS-1$
String locale = aLocale.toString();
StringBuffer contextObjParams = new StringBuffer();
if (oid != null) {
		contextObjParams = contextObjParams.append(",param1=" + oid);
}
contextObjParams = contextObjParams.append(",param2=WC");
contextObjParams = contextObjParams.append(",param3=" + cid);
%>
<jsp:useBean id="url_factory" class="wt.httpgw.URLFactory" scope="request" >
    <% url_factory.setRequestURL(request.getScheme(), request.getHeader("HOST"), request.getRequestURI()); //$NON-NLS-1$ %>
</jsp:useBean>
<HTML>
<HEAD>
<LINK rel="stylesheet" type="text/css" href="<%=url_factory.getHREF("com/ptc/core/ui/solutions.css") %>">
<STYLE>
.wizTitle  {color: #FDF8CE; font-weight: bold; font-size: 1.1em; background-color: #0D4794;}
</STYLE>
</HEAD>

<BODY class="wizBorder" leftmargin="0" topmargin="0" marginwidth="0" marginHeight="0" onload="loaded()" <%if(true) {%>onBeforeUnload="return confirmExit()" <%}%>>
	<TABLE width="95%" border="0" cellpadding="6" cellspacing="0" class="wizBorder">
		<%
		String url_jsp = url_factory.getHREF("Windchill/servlet/JNLPGeneratorServlet/genPbom.jnlp");
		String sid = session.getId();

		String jars = "pbom.jar,ptcCore.jar,wtApplet.jar,wncWeb.jar,commons-collections.jar,dom4j-1.6.1.jar,jacob.jar,log4j.jar,looks-2_1_4.jar,wc3rdpartylibs.jar";


		// create URL for the JNLP generator
		EncodingConverter ec = new EncodingConverter();
		StringBuffer url_b = new StringBuffer();
		url_b = url_b.append(url_jsp);
		url_b = url_b.append("?");
		url_b = url_b.append("title=PBOM Editor");

		if(jwsRuntimeParameters!=null) url_b = url_b.append("&vm_args=" + ec.encode(jwsRuntimeParameters,charset));
		url_b = url_b.append("&mainclass=com.ptc.jws.JNLPApplicationLauncher");
		url_b = url_b.append("&params=mainClass=com.glaway.mpm.util.MPMConnectFrame");
		url_b = url_b.append(",application=GENPBOM");

		if(contextObjParams!=null)url_b = url_b.append(contextObjParams);
		url_b = url_b.append(",hide=true");
		url_b = url_b.append(",singleton=false");
		url_b = url_b.append(",wt.context.locale=" + locale);
		url_b = url_b.append(",authorization=" + request.getHeader("AUTHORIZATION"));
		url_b = url_b.append("&sid=" + sid);
		url_b = url_b.append("&jars=" + "lib/JWSUtil.jar,lib/WtHttpClientAddOns.jar,pview.jar");
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
		<TR><TD class="wizStepSel">It's safe to close this window!</TD></TR>
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
