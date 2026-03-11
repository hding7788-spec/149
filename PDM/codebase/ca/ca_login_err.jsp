<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>Insert title here</title>
</head>
<body>
		<%
		String msg = null;
		String errors = request.getParameter("errors");
		System.out.println("errors>>"+errors);
		if("11".equals(errors)){
				msg = "验证证书出错！";
		}else if("12".equals(errors)){
			msg = "验证证书合法性出错！";
		}else if("13".equals(errors)){
			msg = "取证书公钥出错！";
		}else if("14".equals(errors)){
			msg = "验证签名出错！";
		}else if("15".equals(errors)){
			msg = "签名与证书用户不符！";
		}else if(errors != ""){
			msg = errors;
		}
		%>
<table width="100%">

	<tr align="center">
		<td >
			<font color="#FF0000" ><%=msg %></font>
		</td>
	</tr>
</table>


</body>
</html>