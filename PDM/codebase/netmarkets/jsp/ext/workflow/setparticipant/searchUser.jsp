<%@page import="ext.ases.workflow.setparticipant.PrincipalHelper"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList,java.util.Locale"%>
<%@ page import="wt.org.WTUser"%>
<%@ page import="wt.org.WTGroup"%>
<%@ page import="wt.project.Role"%>

<%
	response.setCharacterEncoding("UTF-8");
	String userName1 = request.getParameter("userName");
	String userName = java.net.URLDecoder.decode(userName1,"UTF-8");
	System.out.println("userName is "+userName);
	String locale = request.getParameter("locale");
	String oid = request.getParameter("oid");
	Locale loc = new Locale(locale);
	List userList = PrincipalHelper.service.getUser(userName,loc);
	String userNameList = "";
	for(int i = 0 ; i < userList.size();i++){
		WTUser user = (WTUser)userList.get(i);
	  userNameList = userNameList + user.getFullName()+"("+user.getName()+")`";
	}
	out.println("`"+userNameList);
%>