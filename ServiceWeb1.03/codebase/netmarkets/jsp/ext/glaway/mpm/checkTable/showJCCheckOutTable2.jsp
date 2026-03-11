<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="com.glaway.mpm.processplan.checkouttable.CheckOutUtil"%>
<%@page import="java.util.List"%>

<%
	String technicsNumber = request.getParameter("technicsNumber");
// 	CheckOutUtil.getTechincsXMLPath(technicsNumber);
	if(technicsNumber.contains(" ")){
		technicsNumber = technicsNumber.substring(0,technicsNumber.indexOf(" "));
	}
    String xmlPath = CheckOutUtil.getXmlPath(technicsNumber);
    System.out.println("------------------------------------------xmlPath---------------------------------"+xmlPath);
    List<String> allStepName = CheckOutUtil.getAllStepName(xmlPath);
%>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>查看检验汇总表</title>
		<style type="text/css">
			body{
				background-color: #cFcFcF;
			}
		</style>
    </head>
    <body>
	    <div id = "checkResource" style="margin-top:10px;overflow:hidden">
	    	<h2>查看检验汇总表</h2>
	    	<hr/>
	    	<h3>工序号：</h3>
		    <div>
		    	<ul>
					<%
						for(int i=0;i<allStepName.size();i++){
					%>
					<li>
						<a href="showJCCheckOutTableValue2.jsp?technicsNumber=<%=technicsNumber%>&tableName=<%=allStepName.get(i)%>" target="JCL"><%=allStepName.get(i) %></a>
					</li>
					<br/>
					<%} %>
				</ul>
		    </div>
    	</div>
    </body>

</html>




