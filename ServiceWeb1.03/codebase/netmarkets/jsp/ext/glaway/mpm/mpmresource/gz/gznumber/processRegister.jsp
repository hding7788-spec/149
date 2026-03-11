<%@page language="java" session="true" pageEncoding="GBK"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page
	import="com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassificationInfoContained"%>
<%@page
	import="com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassification"%>
<%@page
	import="com.glaway.mpm.mpmresource.gznumber.number.FormatedNumber"%>
<%@page
	import="com.glaway.mpm.mpmresource.gznumber.number.GZNumberRegister"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.bean.PropertiesBean"%>
<%@page import="com.glaway.mpm.util.GLLogger"%>

<%
	String type = request.getParameter("type");
	String parentClassPath = request.getParameter("parentPath");
	String fullClassPath = request.getParameter("fullPath");
	String gzNumber = request.getParameter("gzNumber");
	String objectName = request.getParameter("objectname");
	String requestdesc = request.getParameter("requestdesc");
	int requestvolumn = Integer.parseInt(request.getParameter("requestvolumn"));
	String requestname = request.getParameter("requestname");

	GLLogger.debug("~parentClassPath:" + type + parentClassPath);
	GLLogger.debug("~fullClassPath:" + fullClassPath);
	GLLogger.debug("~generatedNumber:" + gzNumber);
	GLLogger.debug("~requestdesc:" + requestdesc);
	GLLogger.debug("~requestvolumn:" + requestvolumn);
	GLLogger.debug("~requestname:" + requestname);

	String errorMsg = "";
	try {
		WTPrincipal usr = SessionHelper.manager.getPrincipal();
		//String grade = gzNumber.substring(0, 1);

		//	if("1".equals(grade)){			
		//		boolean flag = CommonUtil.isStandardMember(usr);
		//		if(!flag){
		//		errorMsg = "仅标准化人员可申请一级号编号!";
		//	}
		//}
		if ("".equals(errorMsg)) {
			GZNumberClassificationInfoContained classification = new GZNumberClassification();
			classification.setObjectname(objectName);
			classification.setObjectclasspath(fullClassPath);
			//classification.setObjectclasspath(parentClassPath);
			classification.setObjectclassvalue("");
			classification.setObjectclassdesc("");

			FormatedNumber fnumber = new FormatedNumber();
			fnumber.setClassification(classification);
			fnumber.setValue(type + gzNumber);
			//			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd" + " "	+ "hh:mm:ss");
			//			String datetime = tempDate.format(new java.util.Date());
			//			fnumber.setDateTime(datetime);
			fnumber.setRequestDesc(requestdesc);
			fnumber.setRequestor(SessionHelper.manager.getPrincipal().getName());
			fnumber.setRequestor(SessionHelper.manager.getPrincipal().getName());

			PropertiesBean pb = new PropertiesBean();
			GZNumberRegister register = new GZNumberRegister(pb, fnumber);
			register.registerNumbers(requestvolumn,requestname);
		}
	} catch (Exception e) {
		errorMsg = e.getMessage();
		Pattern p = Pattern.compile("\t|\r|\n");
		Matcher m = p.matcher(errorMsg);
		errorMsg = m.replaceAll("");
	}
	PrintWriter printwriter = response.getWriter();
	printwriter.println(errorMsg);
	printwriter.flush();
%>

