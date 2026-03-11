<%@page import="ext.cirpoint.securitymgr.util.UserUpdatePassword"%>
<%@page import="ext.cirpoint.securitymgr.util.MD5Util"%>
<%@page import="ext.cirpoint.securitymgr.bean.User"%>
<%@page import="java.util.List"%>
<%@page import="ext.cirpoint.securitymgr.access.AccessUtil"%>
<%@ page import="java.sql.Connection,java.io.InputStream,org.apache.commons.io.IOUtils,
		java.sql.ResultSet, oracle.sql.BLOB,java.util.Arrays,
		java.sql.PreparedStatement,wt.pds.oracle81.OracleDataSource"
%>
<%@ include file="/../../../../../netmarkets/jsp/util/context.jsp" %>
<html>
<head>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=GBK2312" >

<title>Windchill</title>
<%
			try{
				String userName=request.getParameter("userName");
				UserUpdatePassword.updateUserPassword(userName);
			}catch(Exception ex){
				ex.printStackTrace();
			}
%>


<body >
已重置密码，请重新从门户登录!
</body>
</html>
