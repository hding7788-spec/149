<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="ext.cirpoint.securitymgr.ca.CARandom" %>
<%@ page import="wt.httpgw.URLFactory" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
    URLFactory urlFactory = new URLFactory();
    String baseUrl = urlFactory.getBaseHREF();
    String strRadom = CARandom.genRandom();
    session.setAttribute("strRadom", strRadom);
    String oid = request.getParameter("oid");
%>
<html>
<head>
    <title>PDM登录</title>

    <script type="text/javascript" src="jquery-1.8.3.js"></script>
    <script type="text/javascript" src="json2.js"></script>
    <script type="text/javascript" src="UKeyAPI.js"></script>

    <script type="text/javascript">
        // 登录页面初始化。
        function initPage() {
            // 调用枚举接口，判断接口是否可用，枚举已插入的UKey
            var dev = enumDev();
            if (1 == dev) {
                alert("UKeyControl未安装或未启动运行，请安装UKeyControl");
                return;
            } else if (2 == dev || 3 == dev) {
                alert("未检测到key！");
                return;
            } else {
                login();
            }
        }

        // KEY检测
        function enumDev() {
            var ret = EnumDev();
            if (-1 != ret.search("NetworkError")) {
                // 调用接口获得网络错误，一般是因为没有安装UKeyControl服务
                return 1;
            }
            if (ret == "" || -1 != ret.search("#")) {
                return 2;
            }
            return 0;
        }

        //从UKey中读取证书数据，使用签名接口对随机数签名得到签名值，服务端验签并登录
        function login() {
            // CheckDev接口。新老key检测
			var port = "1";//选择使用的key，默认第一个
            var ret = CheckDev(port);
            if (-1 != ret.search("#")) {
                error("检测失败，错误值为：" + ret);
                return;
            }

			//老设备
            if ("1" == ret) {
                window.location.href="../ca_login.jsp?oid=<%=oid%>";
				return;
            }

            //新设备       
            //读取UKey中证书数据
			//读取的是哪个证书（3：用户签名证书；4：用户加密证书）
            var which_cert = "3";
            var cert_data = readCert(which_cert, port);
            if (-1 != cert_data.search("#")) {
                error(cert_data);
                return;
            }

            //使用concat对随机数做base64编码
            var random_base64 = concat("<%=strRadom%>", "", "");
            //对随机数进行签名
			//若random_base64已做hash,则输入"1"；若random_base64未做hash,则输入"0"
            var signed_data = dataSignCertEx(random_base64, port, "0");
            if (-1 != signed_data.search("#")) {	//返回结果包含"#"，为错误信息
                error(signed_data);
                return;
            }

			//服务端
			$.ajax({
            url: "<%=baseUrl%>ca/new/loginVerify.jsp",
            type: "POST",
            data:{
                cert: cert_data,
                signedData: signed_data,
                random: "<%=strRadom%>"
            },
            success:function (data){
                if(-1 != data.search("#")) {
                    error(data);
                    return;
                }
               window.location.href = "../loginPDM.jsp?username=" + data + "&oid=<%=oid%>";
            },
            error:function (data){
                error(data);
            }
        })
        }

		function error(error){
			error = error.replaceAll("#","");
			window.location.href = "../ca_login_err.jsp?errors=" + error;
		}

    </script>

</head>

<body onload="initPage()">
<div class="tpt-login">
    <h2></h2>
    <input id="signed_data" name="signed_data" type="hidden" value="" />
    <input id="cert" name="cert" type="hidden" value="" />
</div>
</body>

</html>