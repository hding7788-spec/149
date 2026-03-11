<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.glaway.mpm.task.util.TaskUtil"%>

<%
	String group = request.getParameter("para");
	System.out.println("group===>" + group);
	String allMember = TaskUtil.getAllTechGroupUser(group);
	System.out.println("allMember====>" + allMember);
	
	String allLeader = TaskUtil.getAllMemberOfGroup(group + "组长");
	System.out.println("allLeader====>" + allLeader);
	System.out.println("allUser==>" + allLeader + ";" + allMember);
	out.println(allLeader + ";" + allMember);
%>