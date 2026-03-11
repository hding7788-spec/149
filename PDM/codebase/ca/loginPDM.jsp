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
			session.setAttribute("isChecked", "0");

			wt.httpgw.URLFactory urlFactory=new wt.httpgw.URLFactory();
			String overviewURI=urlFactory.getBaseHREF()+"app/";
			String userName = request.getParameter("username");
			String oid = request.getParameter("oid");
			String password = null;
			List<User> userList=AccessUtil.getUserAccess(userName);
			if(userList.size()>0){
				User user=(User)userList.get(0);
				password=user.getPassword();
			}
%>

<script>
var xhr;
var i=1;
var infoOid = '<%=oid%>';
function login(){
	var username='<%=userName%>';
	var password='<%=password%>';
	//password=Base64.decode(password);
	if(window.ActiveXObject){
		xhr = new ActiveXObject("Microsoft.XMLHTTP");
	}else {  
		xhr = new XMLHttpRequest();
	}
	if(password=='null'){
		window.location="/Windchill/ptc1/ext/cirpoint/key/loginError4?";
	}
	xhr.open("POST", '<%=overviewURI%>', false, username, password);	
	xhr.onreadystatechange=handleStateChange;	
	xhr.send(null);
	
}

function handleStateChange(){	
	i++;
	if(i==5 && xhr.status==401){
		alert("KEY用户密码与PDM系统密码不匹配，请点击下方 \"确定\" 按钮！");
		//window.location.replace("/Windchill");
		window.location.replace("/Windchill/ca/updateUserPassWord.jsp?userName="+'<%=userName%>');
	}
	if(i==5 && (xhr.status==200 || xhr.status==0)){
		if(infoOid == null || infoOid == "" || infoOid == "null"){
			window.location.replace("/Windchill?isFromCa=0");
		} else{
			window.location.replace("/Windchill/app/#ptc1/tcomp/infoPage?oid="+infoOid);
		}
	}
}
</script>
</head>

<body bgColor=#ffffff>
<script>login();</script>
</body>
</html>
