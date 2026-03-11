<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="wt.org.WTPrincipalReference"%>
<%@page import="wt.preference.PreferenceHelper"%>
<%@page import="wt.httpgw.LanguagePreference"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="wt.inf.container.WTContainer"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.util.*"%>
<%@page import="java.util.*"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.glaway.mpm.print.util.PrintUtil"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.workflow.engine.WfActivity"%>
<%@page import="wt.workflow.engine.WfProcess"%>
<%@page import="com.glaway.mpm.intf.PrintToWCIntfRMI" %>>

<jsp:useBean id="nmcontext"
	class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request" />

<%
	NmCommandBean nmCommandBean = new NmCommandBean();
	nmCommandBean.setCompContext(nmcontext.getContext().toString());
	nmCommandBean.setRequest(request);

	WTContainer container = nmCommandBean.getViewingContainer();
	WTUser user = (WTUser) SessionHelper.getPrincipal();
	System.out.println(user.toString());
	String userName = user.getName();
	WTPrincipalReference currentPrin = SessionHelper.manager.getPrincipalReference();
	Boolean useJWS = (Boolean) PreferenceHelper.service.getValue("com/ptc/windchill/explorer/useJWS", "WINDCHILL", container, user);
	String jwsRuntimeParameters = "-Xmx512m";
	Locale aLocale = LanguagePreference.getLocale(request.getHeader("Accept-Language"));

	/**
	* 参数说：
	* type：  SQSPHZXDY 表示文件申请审批和执行打印界面；
			DYSQBH表示文件打印申请驳回界面；
			LQZZWJTZ表示领取纸质文件通知界面；
	* oid:  工艺文件OID
	  startFrom：       0表示从任务活动页面启动；1表示从action启动;
				        默认设置从action启动
	*/
	StringBuffer contextObjParams = new StringBuffer();

	String type = request.getParameter("type");
	String oid = request.getParameter("oid");
	String startFrom = request.getParameter("startFrom");

	System.out.println("type=" + type);
	System.out.println("oid=" + oid);
	System.out.println("startFrom=" + startFrom);

	if(startFrom != null){
		startFrom = "0";
	}else{
		startFrom = "1";
	}
	System.out.println("startFrom=" + startFrom);
	List list = nmCommandBean.getSelectedOidForPopup();
	if(list.size() != 0){
		System.out.println("基于多个文件启动");
		oid = PrintUtil.getMoreObjectOids(list);
	}

	if(oid != null && !"".equals(oid)) {
		contextObjParams.append(",type="+type).append(",oid="+oid);
	} else{
		contextObjParams.append(",type="+type);
	}

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
			if("0".equals(startFrom)) {
				url_jsp = url_factory.getHREF("servlet/GWJNLPGeneratorServlet/processEditor.jnlp");
			}

			System.out.println(url_jsp);

			String sid = session.getId();

			String jars = "newCapp.jar,wtApplet.jar,log4j.jar,jgoodies-common-1.4.0.jar,jgoodies-looks-2.5.2.jar,QRCode.jar" +
					",pdfbox-2.0.1.jar,itextpdf-5.4.3.jar,itext-asian-5.4.3.jar,commons-logging.jar";

			// create URL for the JNLP generator
			EncodingConverter ec = new EncodingConverter();
			StringBuffer url_b = new StringBuffer();
			url_b = url_b.append(url_jsp);
			url_b = url_b.append("?");
			url_b = url_b.append("title=File Print Request");

			if (jwsRuntimeParameters != null) {
				url_b = url_b.append("&vm_args=" + ec.encode(jwsRuntimeParameters, "UTF-8"));
			}
			url_b = url_b.append("&mainclass=com.ptc.jws.JNLPApplicationLauncher");
			url_b = url_b.append("&params=mainClass=com.glaway.mpm.print.util.StartMPMApplicationFrame");
			url_b = url_b.append(",application=MPMPrint");

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

			System.out.println(url_b);
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