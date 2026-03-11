<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="wt.org.WTPrincipalReference"%>
<%@page import="wt.preference.PreferenceHelper"%>
<%@page import="wt.httpgw.LanguagePreference"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="wt.inf.container.WTContainer"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.util.*"%>
<%@page import="java.util.*"%>

<jsp:useBean id="nmcontext"
	class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request" />

<%
	NmCommandBean nmCommandBean = new NmCommandBean();
	nmCommandBean.setCompContext(nmcontext.getContext().toString());
	nmCommandBean.setRequest(request);

	WTContainer container = nmCommandBean.getViewingContainer();
	WTUser user = (WTUser) SessionHelper.getPrincipal();
	WTPrincipalReference currentPrin = SessionHelper.manager.getPrincipalReference();
	Boolean useJWS = (Boolean) PreferenceHelper.service.getValue("com/ptc/windchill/explorer/useJWS", "WINDCHILL", container, user);
	//String jwsRuntimeParameters = (String)PreferenceHelper.service.getValue("com/ptc/windchill/explorer/javaRuntimeParametersForJWS" ,  "WINDCHILL" , container , user);
	String jwsRuntimeParameters = "-Xmx512m";
	Locale aLocale = LanguagePreference.getLocale(request.getHeader("Accept-Language"));

	StringBuffer contextObjParams = new StringBuffer();
	String startFrom = request.getParameter("startFrom");
	String type = request.getParameter("type");
	contextObjParams.append(",taskType="+type);
	System.out.println("---contextObjParams---"+contextObjParams);
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
			if("workitem".equals(startFrom)) {
				url_jsp = url_factory.getHREF("servlet/GWJNLPGeneratorServlet/processEditor.jnlp");
			}
			String sid = session.getId();

			String jars = "newCapp.jar,wtApplet.jar,log4j.jar,jgoodies-common-1.4.0.jar,jgoodies-looks-2.5.2.jar,swt32.jar,DJNativeSwing-SWT.jar,DJNativeSwing.jar";

			// create URL for the JNLP generator
			EncodingConverter ec = new EncodingConverter();
			StringBuffer url_b = new StringBuffer();
			url_b = url_b.append(url_jsp);
			url_b = url_b.append("?");
			url_b = url_b.append("title=Parameter Manager");

			if (jwsRuntimeParameters != null) {
				url_b = url_b.append("&vm_args=" + ec.encode(jwsRuntimeParameters, "UTF-8"));
			}
			url_b = url_b.append("&mainclass=com.ptc.jws.JNLPApplicationLauncher");
			url_b = url_b.append("&params=mainClass=com.glaway.mpm.util.MPMConnectFrame");
			url_b = url_b.append(",application=ConsCheck");

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