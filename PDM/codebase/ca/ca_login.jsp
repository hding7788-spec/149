<%@page import="ext.cirpoint.securitymgr.ca.CARandom"%>
<%@ page language="java" contentType="text/html; charset=GBK"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String strRadom = CARandom.genRandom();
	session.setAttribute("strRadom",strRadom);
	String oid = request.getParameter("oid");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>CA</title>
<script language="Javascript">
var CAServerIP = "10.125.192.27";
var strRadom = "<%=strRadom %>";
var oid = "<%=oid %>";
function checkBrowser(){
	var userAgent=navigator.userAgent;
	if(userAgent.indexOf("Firefox")>-1){
		return "FF";
	}else if(userAgent.indexOf("Chrome")>-1||userAgent.indexOf("Safari")>-1){
		return "FF";
	}else{
		return "IE";
	}
	
}


function LoginOA()
{
	var browser=checkBrowser();
	if(browser=="FF"){
		alert("暂不支持谷歌和火狐浏览器登录，请使用IE8及以上版本登录！");
		window.location = "ca_error.jsp";
	}
	rtn = usb_ocx.VgetnameFirst("","1");
		if(rtn == "#2")
		{
			alert("暂未检测到智能卡,请刷新重试或者使用其它方式登录!");
			window.login.action = "../index.html";
			window.login.submit();
		}
		SubmitForm(rtn);
		return rtn;
	
}

function SubmitForm(strUserName)
{
	GetCer();
	GetSignUser(strUserName,strRadom);
	window.login.submit();
}

function GetCer()
{
	Rtn=usb_ocx.readCert("3","1");
	window.login.Cer.value = Rtn;
}
function GetSignUser(m_User,m_Radom)
{
	Rtn1=usb_ocx.concat(m_User,"","");
	Rtn2=usb_ocx.concat(m_Radom,Rtn1,"");//把随机串＋登陆名转换成base64串；
	Rtn3=usb_ocx.dataSignCertEx(Rtn2,"1","0");
	window.login.UserSign.value = Rtn3;
	window.login.oid.value = oid;
}
</script>
</head>

<body onload="Javascript:LoginOA()">
	<form action="ca_verify.jsp" method=post id=login name=login>
		<p><input id="Cer" name="Cer" type="hidden"></p>
		<p><input id="UserSign" name="UserSign" type="hidden"></p>
		<p><input id="oid" name="oid" type="hidden"></p>
		<p><input id="password" name="password" value ="" type="hidden"></p>
		
	</form>

<object classid="clsid:9703D810-ACC0-4C22-83C7-3FD9ED198B6E" id="usb_ocx" name="usb_ocx" style="VISIBILITY:hidden"
     codebase="IB_USBKEY.ocx" width="100" height="50">

</body>
</html>