<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.glaway.mpm.task.util.TaskUtil"%>

<%
	String group = request.getParameter("para");
	System.out.println("group===>" + group);
	String allMember = TaskUtil.getAllTechGroupUser(group);//获取组织下的所有成员
	System.out.println("allMember====>" + allMember);
	
	out.println(allMember);
%>
