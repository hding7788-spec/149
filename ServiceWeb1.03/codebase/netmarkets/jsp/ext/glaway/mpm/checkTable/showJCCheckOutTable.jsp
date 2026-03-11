<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.workflow.work.WorkItem"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="wt.org.WTPrincipalReference"%>
<%@page import="wt.preference.PreferenceHelper"%>
<%@page import="wt.httpgw.LanguagePreference"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="wt.inf.container.WTContainer"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.util.*"%>
<%@page import="java.util.*"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.glaway.mpm.print.util.PrintUtil"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="com.glaway.mpm.util.SWXMLUtil"%>
<%@page import="org.dom4j.Element"%>
<%@page import="org.dom4j.Document"%>
<%@page import="org.dom4j.Node"%>
<%@page import="org.dom4j.io.SAXReader"%>
<%@page import="com.glaway.mpm.processplan.checkouttable.CheckOutUtil"%>

<%
	String technicsNumber = request.getParameter("technicsNumber");
	if(technicsNumber.contains(" ")){
		technicsNumber = technicsNumber.substring(0,technicsNumber.indexOf(" "));
	}
// 	CheckOutUtil.getTechincsXMLPath(technicsNumber);

    String xmlPath = CheckOutUtil.getXmlPath(technicsNumber);
    System.out.println("------------------------------------------xmlPath---------------------------------"+xmlPath);
    List<String> tableNames = CheckOutUtil.getXMLCheckOutTbaleNameList(xmlPath, "检测类");
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
	    	<h2>检测类表单</h2>
	    	<hr/>
	    	<h3>套表名列表：</h3>
		    <div>
		    	<ol>
					<%
						for(int i=0;i<tableNames.size();i++){
					%>
					<li><a href="showJCCheckOutTableValue.jsp?technicsNumber=<%=technicsNumber%>&tableName=<%=tableNames.get(i)%>" target="JCL"><%=tableNames.get(i) %></a></li>
					<br/>
					<%} %>
				</ol>
		    </div>
    	</div>
    </body>
</html>




