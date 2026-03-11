<%@ page contentType="text/html; charset=gb2312"  %>
<%@page import="wt.util.WTProperties"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="com.ptc.windchill.mpml.processplan.MPMProcessPlan"%>
<%@page import="ext.ptc.ViewWIHelper"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="java.io.FileNotFoundException"%>
<%@page import="java.io.IOException"%>
<%@page import="java.io.InputStream,java.io.FileInputStream"%>
<%@page import="java.util.Properties"%>
<%
    String user = "";
	String password = "";
	try {
		WTProperties prop = WTProperties.getLocalProperties();
		StringBuffer br = new StringBuffer();
		String filePath = br.append(prop.getProperty("wt.codebase.location"))
				.append(prop.getProperty("dir.sep")).append("ext")
				.append(prop.getProperty("dir.sep")).append("ptc")
				.append(prop.getProperty("dir.sep")).toString();
		InputStream in = new BufferedInputStream(new FileInputStream(filePath+"user.properties"));
		Properties p = new Properties();
		p.load(in);
		user = p.getProperty("defaultUser");
		password = p.getProperty("defaultPassword");
		System.out.println("-----user:" + p.getProperty("defaultUser")
				+ "   password:" + p.getProperty("defaultPassword"));
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	WTProperties prop = WTProperties.getLocalProperties();
	String hName = prop.getProperty("wt.server.hostname");
%>

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<SCRIPT LANGUAGE="JavaScript">
			function processView() {
				var submitFlg = document.getElementById("submitFlg").value;
				var partNumber = document.getElementById("partNumber").value;
				//alert("-----partNumber:"+partNumber.length);
				if(partNumber.length==0){
					alert("number is null!");
					return;
				}
				var hostName = document.getElementById("hostName").value;
				var user = document.getElementById("user").value;
				var password = document.getElementById("password").value;
				var url = "http://" + user + ":" + password + "@" + hostName + "/Windchill/netmarkets/jsp/mpml/viewWorkInstruction.jsp?"
						+ "submitFlg=" + submitFlg + "&partNumber=" + partNumber;
				window.location.replace(url);
			}
		</Script>
	</head>
	<body>
		<input type="hidden" id="hostName" name="hostName" value="<%=hName%>">
		<input type="hidden" id="user" name="user" value="<%=user%>">
		<input type="hidden" id="password" name="password" value="<%=password%>">
		<form>
			«Î ‰»ÎÕº∫≈£∫
			<input type="text" name="partNumber" id="partNumber" /> 
			<input type="hidden" name="submitFlg" id="submitFlg" value="1" /> 
			<input type="button" name="View" value="View" onClick="processView();" />
		</form>
	</body>
</html>