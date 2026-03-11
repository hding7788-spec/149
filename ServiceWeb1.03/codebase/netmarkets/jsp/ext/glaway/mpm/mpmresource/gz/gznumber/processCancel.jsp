<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="java.io.PrintWriter" %>
<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.number.GZNumberRegister"%>

<%
	String errorMsg = "";
	try{
		String cnumber = request.getParameter("cnumber");
		GZNumberRegister.manager.cancelOneNumber(cnumber,"");
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

	