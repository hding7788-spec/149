<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="java.io.PrintWriter" %>
<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.number.GZNumberManager"%>

<%

	String cnumber = request.getParameter("cnumber");
	String sGnClass = request.getParameter("sGnClass");
	String requestdesc = request.getParameter("requestdesc");
	//byte B[]=requestdesc.getBytes("ISO8859_1"); 
	//requestdesc=new String(B,"GB2312"); 

	String strSeq = request.getParameter("strSeq");
	String errorMsg = "";
	
	//modify by LongXiuChuan 20120203
	String flag = "0";//修改前的值是1
	if(requestdesc == null){
		requestdesc = "";
	}
	//end
	
	try{	
		if(cnumber != null){
			String gnRequestor = SessionHelper.manager.getPrincipal().getName();
			GZNumberManager nm = new GZNumberManager();
			nm.createQYNumber(cnumber,sGnClass,gnRequestor,requestdesc,strSeq,flag);
		}
	}catch(Exception e){
		errorMsg = e.getMessage();
		//去除换行符，制表符
		Pattern p = Pattern.compile("\t|\r|\n");
		Matcher m = p.matcher(errorMsg);
		errorMsg = m.replaceAll("");
	}
	
	PrintWriter printwriter = response.getWriter();
	printwriter.println(errorMsg);
    printwriter.flush();

%>



	