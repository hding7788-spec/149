<%@ page import="wt.util.*,wt.httpgw.LanguagePreference,java.util.*,com.ptc.netmarkets.util.beans.NmCommandBean,wt.inf.container.WTContainer,wt.org.WTUser,wt.session.SessionHelper,wt.preference.PreferenceHelper,wt.fc.*" contentType="text/html; charset=UTF-8"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.glaway.mpm.task.model.GMChiefTask"%>
<%@page import="com.glaway.mpm.task.model.GMChangeProcessTask"%>
<%@page import="com.glaway.mpm.task.util.TaskUtil"%>
<%@page import="com.glaway.mpm.task.model.GMToolTechDsnTask"%>
<%@page import="com.glaway.mpm.task.util.ToolUtil"%>
<%@page import="wt.part.WTPart"%>
<%@page import="com.glaway.mpm.util.Util"%>
<%@page import="com.glaway.mpm.task.model.GMReworkProcessTask"%>
<%@page import="com.glaway.mpm.util.WTPartUtil"%>
<%@page import="com.glaway.mpm.task.util.TaskActivityUtil"%>
<%@page import="com.glaway.mpm.task.model.GMChangeProChiefTask"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="wt.org.WTPrincipalReference"%>
<%@page import="com.glaway.mpm.task.util.ChangeProcessUtil"%>
<%@page import="com.glaway.mpm.constants.Constants"%>
<%@page import="com.glaway.mpm.task.model.GMTempTask"%>
<%@page import="com.ptc.windchill.mpml.processplan.MPMProcessPlan"%>
<%@page import="com.glaway.mpm.util.ReferenceFactory"%>
<%@page import="com.glaway.mpm.util.MPMProcessPlanUtil"%>
<%@page import="com.glaway.mpm.task.util.TechnicTaskUtil"%>
<%@page import="com.glaway.mpm.task.model.GMTechnicTask"%>
<%@page import="com.glaway.mpm.processplan.helper.ProcessPlanHelper"%>
<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request" />
<%
	NmCommandBean nmCommandBean = new NmCommandBean();
	nmCommandBean.setCompContext(nmcontext.getContext().toString());
	nmCommandBean.setRequest(request);
	WTContainer container = nmCommandBean.getViewingContainer();
	WTUser user = (WTUser) SessionHelper.getPrincipal();
	WTPrincipalReference currentPrin = SessionHelper.manager.getPrincipalReference();
	Boolean useJWS = (Boolean) PreferenceHelper.service.getValue("com/ptc/windchill/explorer/useJWS", "WINDCHILL",container, user);
	//String jwsRuntimeParameters = (String)PreferenceHelper.service.getValue("com/ptc/windchill/explorer/javaRuntimeParametersForJWS" ,  "WINDCHILL" , container , user);
	String jwsRuntimeParameters = "-Xmx512m";
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
	String parentPartOid = request.getParameter("O1");
	String partOid = request.getParameter("O2");
	String documentOid = request.getParameter("O3");
	String workItemOid = request.getParameter("O4");
	String workItemStatus = request.getParameter("status");

	//根据任务类型判断属于什么情况启动工艺编辑器
	Object obj=null;
	if ("1".equals(taskType)) {
		List selectList = nmCommandBean.getSelectedOidForPopup();
		if(selectList.size() > 1){
%>
			<jsp:forward page="/netmarkets/jsp/glaway/mpm/task/activity/error.jsp?param=1"></jsp:forward>
		<%
			}
				NmOid nmoid = (NmOid)selectList.get(0);
				obj = nmoid.getRefObject();
				if(obj instanceof GMReworkProcessTask){
			 taskType="4";
				}else if(obj instanceof GMChangeProChiefTask){
			 taskType="3";
				}else if(obj instanceof GMTempTask){
			 taskType = "5";
				}
			}

			if ("1".equals(taskType)) {
				if(!(obj instanceof GMChiefTask) && !(obj instanceof GMToolTechDsnTask)){
		%>
			<jsp:forward page="/netmarkets/jsp/glaway/mpm/task/activity/error.jsp?param=2"></jsp:forward>
		<%
			}
				if(TaskActivityUtil.isTaskUndispatch((GMTechnicTask)obj)){
		%>
			<jsp:forward page="/netmarkets/jsp/glaway/mpm/task/activity/error.jsp?param=3"></jsp:forward>
		<%
			}
				if(obj instanceof GMChiefTask){
			GMChiefTask chiefTask = (GMChiefTask)obj;
			HashMap<String, String> map = TaskUtil.getStartEBOMParamsByTechTask(chiefTask);
			parentPartOid = map.get("oid");
				}else{
			GMToolTechDsnTask toolDsn = (GMToolTechDsnTask)obj;
			WTPart part = TaskUtil.getPartByIntegerOrPartTech(toolDsn);
			parentPartOid = Util.getStringOid(part);
				}
			}else if("2".equals(taskType)){

			}else if("3".equals(taskType)){
				if(obj instanceof GMChangeProChiefTask){
			GMChangeProChiefTask changeChiefTask = (GMChangeProChiefTask)obj;
			if(TaskActivityUtil.isTaskUndispatch((GMTechnicTask)obj)){
		%>
				<jsp:forward page="/netmarkets/jsp/glaway/mpm/task/activity/error.jsp?param=3"></jsp:forward>
			<%
				}
				ArrayList<GMChangeProcessTask> changeList = ChangeProcessUtil.getChangeProcessOfCurrentUser(changeChiefTask, currentPrin);
				for(int k = 0; k < changeList.size(); k++){
					GMChangeProcessTask changeTask = changeList.get(k);
					WTPart temp = TaskUtil.getPartByIntegerOrPartTech(changeTask);
					String tempoid = Util.getStringOid(temp);
					partOid += tempoid + ",";
				}
				if(!"".equals(partOid)){
					partOid = partOid.substring(0, partOid.length() - 1);
				}
				WTPart parentPart = WTPartUtil.getLatestPartByNumberAndView(changeChiefTask.getWholePartNumber(), Constants.planning);
				parentPartOid = Util.getStringOid(parentPart);
					}
				}else if("4".equals(taskType)){
					GMReworkProcessTask gm=(GMReworkProcessTask)obj;
					if(TaskActivityUtil.isTaskUndispatch((GMTechnicTask)obj)){
			%>
			<jsp:forward page="/netmarkets/jsp/glaway/mpm/task/activity/error.jsp?param=3"></jsp:forward>
		<%
		}
		WTPart part = (WTPart)ReferenceFactory.getObjectbyOid(gm.getTemp01());
		parentPartOid = TechnicTaskUtil.getParentOidByPart(part);
		partOid = gm.toString();
	}else if("5".equals(taskType)){
		if(obj instanceof GMTempTask){
			GMTempTask tempTask = (GMTempTask)obj;
			if(TaskActivityUtil.isTaskUndispatch((GMTechnicTask)obj)){
			%>
				<jsp:forward page="/netmarkets/jsp/glaway/mpm/task/activity/error.jsp?param=3"></jsp:forward>
			<%
			}
			MPMProcessPlan plan = (MPMProcessPlan)ReferenceFactory.getObjectbyOid(tempTask.getMpmoid());
			WTPart part = MPMProcessPlanUtil.getMPMProcessplanRelatedPart(plan);
			parentPartOid = TechnicTaskUtil.getParentOidByPart(part);
			partOid=tempTask.toString();
		}
	}
	contextObjParams = contextObjParams.append(",taskType=" + taskType);
	contextObjParams = contextObjParams.append(",param1=" + parentPartOid);
	contextObjParams = contextObjParams.append(",param2=" + partOid);
	contextObjParams = contextObjParams.append(",param3=" + documentOid);
	contextObjParams = contextObjParams.append(",param4=" + workItemOid);
	contextObjParams = contextObjParams.append(",param5=" + workItemStatus);
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
				String url_jsp = url_factory.getHREF("Windchill/servlet/JNLPGeneratorServlet/processEditor.jnlp");
				String sid = session.getId();

				String jars = "ptcCore.jar,wtApplet.jar,wncWeb.jar,dom4j.jar,jaxen-1.2.0.jar,newCapp.jar,cappPart.jar,j3dcore.jar,log4j.jar,assembler.jar,ant-launcher.jar,ant.jar,jgoodies-common-1.4.0.jar,jgoodies-looks-2.5.2.jar,freemarker.jar,swt3.jar,jdic.jar,DJNativeSwing-SWT.jar,DJNativeSwing.jar,poi.jar";

				// create URL for the JNLP generator
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
				url_b = url_b.append("&sid=" + sid);
				url_b = url_b.append("&jars="+ "lib/JWSUtil.jar,lib/WtHttpClientAddOns.jar,lib/pview.jar");
				url_b = url_b.append("&exts=" + jars);
				url_b = url_b.append("&exts_sec="+ "wt/security/security.jar,install/boot.jar");
				url_b = url_b.append("&allperm=1");
				url_b = url_b.append("&documentbase=" + "/");
				url_b = url_b.append("&width=1");
				url_b = url_b.append("&height=1");

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
</HTML>
