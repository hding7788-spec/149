<%@page language="java" contentType="text/html; charset=GBK"%>
<%@page import="javax.servlet.RequestDispatcher" %>
<%@page import="Cert.CertOper"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
String Cer = request.getParameter("Cer");
String UserSign = request.getParameter("UserSign");
String oid = request.getParameter("oid");
String CAServerIP = "10.125.192.27";

String strRadom = (String)session.getAttribute("strRadom");



byte[] byteDominUser = CertOper.VerifyCert(2,Cer.getBytes());
String DominUser = new String(byteDominUser);



if(DominUser!=null&&DominUser.startsWith("#")){
	request.getRequestDispatcher("ca_login_err.jsp?errors=11").forward(request,response);
	return;
}

String tempUser = DominUser;
tempUser = tempUser.substring(tempUser.lastIndexOf("\\")+1);
DominUser.indexOf("\\");
session.setAttribute("GDPUsername", tempUser);

/*

String strValid = CertOper.CertValid(2,Cer.getBytes(),CAServerIP);
if(strValid!=null&&strValid.startsWith("#")){
	//out.println("验证证书合法性出错！");
	request.getRequestDispatcher("ca_login_err.jsp?errors=12").forward(request,response);
	return;
}

String Key = CertOper.ReadCertSNPUBK(Cer.getBytes()).toString();
//System.out.println("???????????????????????"+Key);

if(Key!=null&&Key.startsWith("#")){
	//out.println("取证书公钥出错！");
	request.getRequestDispatcher("ca_login_err.jsp?errors=13").forward(request,response);
	return;
}

byte[] vf = CertOper.VERIFYSIG(UserSign.getBytes(),(DominUser+strRadom).getBytes(),CAServerIP.getBytes(),1);
String Verifysign = new String(vf);
System.out.println("Verifysign>>>>>>"+Verifysign);
//Verifysign = Cert.CertOper.VERIFYSIGJMJ(UserSign,strRadom & DominUser,CAServerIP,1)
if(Verifysign!=null&&Verifysign.startsWith("#")){
	//out.println("验证签名出错！");
	request.getRequestDispatcher("ca_login_err.jsp?errors=14").forward(request,response);
	return;
}

if(!DominUser.toLowerCase().equals(Verifysign.toLowerCase())){
	//out.println("签名与证书用户不符！");
	request.getRequestDispatcher("ca_login_err.jsp?errors=16").forward(request,response);
	return;
}

*/







//String tempUser="xxxxx";
%>

<body>
	<script type="text/javascript">
		window.location.href="loginPDM.jsp?username=<%=tempUser%>&oid=<%=oid%>";
	</script>
</body>


</html>