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
<%@page import="com.glaway.mpm.util.CheckPacesUtil"%>

<%
	String technicsNumber = request.getParameter("technicsNumber");
	if(technicsNumber.contains(" ")){
		technicsNumber = technicsNumber.substring(0,technicsNumber.indexOf(" "));
	}
	String stepNumber = request.getParameter("gxStepNumber");
	String paceNumber = request.getParameter("gbStepNumber");
	System.out.println("------------------------------------------paceNumber---------------------------------"+paceNumber);
	 String temppath = request.getContextPath();
     String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+temppath+"/";
    String xmlPath = CheckPacesUtil.getXmlPath(technicsNumber);
    System.out.println("------------------------------------------xmlPath---------------------------------"+xmlPath);
    SAXReader sax = new SAXReader();
    Document document = sax.read(new File(xmlPath));
    Element root = document.getRootElement();
    Node pace = CheckPacesUtil.getCurrentPace(document, stepNumber, paceNumber);
   /*  System.out.println("------------------------------------------root---------------------------------"+root);
    System.out.println("------------------------------------------pace---------------------------------"+pace);
    String name = CheckPacesUtil.getCheckEquips(pace).valueOf("@name");
    String name1 = CheckPacesUtil.getCheckSdashboard(pace).valueOf("@name");

    System.out.println("------------------------------------------name---------------------------------"+name);
    System.out.println("------------------------------------------name1---------------------------------"+name1); */

%>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>检验记录表预览</title>
        <style type="text/css">
       		body{
       			width: 100%;
                height: 100%;
       		}
            #content {
                width: 1200px;
                height: 400px;
            }
            #content1 {
                width: 800px;
                height: 400px;
            }
            #content2 {
                width:400px;
                height: 200px;
            }
  			#content table tr td{
              	text-align: center;
              	font-size:12px;
              	width:135px;
   				height:40px;
              /* 	width:40px;
   				height:20px; */
            }
            #content table tr th{
             	text-align: center;
             	background:#dcdcdc;
              	font-size:12px;
              	width:135px;
   				height:40px;
            }
           	#content1 table tr td{
               	text-align: center;
              	font-size:12px;
          		width:160px;
   				height:40px;
            }
      /*       #content table{
            	width:100%;
            }
           	#content1 table{
           		width:100%;
           	} */
            #content1 table tr th{
              	text-align: center;
              	background:#dcdcdc;
              	font-size:12px;
              	width:160px;
   				height:40px;
            }
 		 	#content2 table tr td{
              	font-size:12px;
           		width:80px;
   				height:30px;
            }
            #content2 table tr th{
                background:#dcdcdc;
              	font-size:12px;
              	width:80px;
   				height:30px;
            }
            #checkResource {
            	height: 300px;
            }
            #tab_bar {
                width: 1200px;
                height: 100px;
                float: left;
            }
            #tab_bar1 {
                width: 800px;
                height: 50px;
                float: left;
            }
            #tab_bar ul {
                padding: 0px;
                margin: 0px;
                height: 100px;
                text-align: center;
            }
   			#tab_bar1 ul {
                padding: 0px;
                margin: 0px;
                height: 100px;
                text-align: center;
            }
            #tab_bar li {
                list-style-type: none;
                float: left;
                text-align: center;
                vertical-align:middle;
                background-color: none;
            }
    		#tab_bar1 li {
                list-style-type: none;
                float: left;
            }
            .tab_css {
                width: 800px;
                height: 300px;
                position:absolute;
                background-color: none;
                display: none;
                float: left;
            }
            .order-title {
            	background:#e4f1ff;
            	width: 100%;
            }
            .tabResource {
            	 background-color: #F8F8F8;
            	 width: 150px;
            	 height: 20px;
            	 border:1px solid black;
            	 font-size:12px;
            	 text-align: center;
            	 vertical-align:middle;
            	 line-height:20px;
            }

        </style>
        <script type="text/javascript">
            var myclick = function(v) {
                var llis = document.getElementsByTagName("li");
                for(var i = 0; i < llis.length; i++) {
                    var lli = llis[i];
                    if(lli == document.getElementById("tab" + v)) {
                        lli.style.backgroundColor = "#ADADAD";
                    } else {
                        lli.style.backgroundColor = "#F8F8F8";
                    }
                }

               	var tables = document.getElementById("checkResource").getElementsByTagName("table");
                for(var i = 0; i < tables.length; i++) {
                    var table = tables[i];
                    if(table == document.getElementById("table" + v + "_content")) {
                        table.style.display = "block";
                    } else {
                        table.style.display = "none";
                    }
                }

            }

            var myclick1 = function(v) {
                var llis = document.getElementsByTagName("li");
                for(var i = 0; i < llis.length; i++) {
                    var lli = llis[i];
                    if(lli == document.getElementById("tab1" + v)) {
                        lli.style.backgroundColor = "#ADADAD";
                    } else {
                        lli.style.backgroundColor = "#F8F8F8";
                    }
                }
               // class = "checkTable"
               // var tables = document.getElementsByName("checkTable")
             //  	var tables = document.getElementsByTagName("table");
               	var tables = document.getElementById("checkTable").getElementsByTagName("table");
                for(var i = 0; i < tables.length; i++) {
                    var table = tables[i];
                    if(table == document.getElementById("table" + v + "_content1") || table == document.getElementById("table" + v + "_content2")) {
                        table.style.display = "block";
                    } else {
                        table.style.display = "none";
                    }
                }

             /*   	var tables = document.getElementsByTagName("table");
                for(var i = 0; i < tables.length; i++) {
                    var table = tables[i];
                    if(table == document.getElementById("table" + v + "_content2")) {
                        table.style.display = "block";
                    } else {
                        table.style.display = "none";
                    }
                } */
            }
        </script>
    </head>
    <body>
    	<!-- <div class = "order-title" style="text-align:left;margin-top:0px;font-size:28px;">
    		检验记录表预览：
    	</div> -->
        <div id = "checkResource" style="margin-top:10px;overflow:hidden">
	        <div class = "order-title" style="text-align:left;font-size:24px;margin-top:0px;">
	    		检验资源
	    	</div>
            <div id="tab_bar" style="margin-top:0px;">
                <ul>
                    <li id="tab1" class = "tabResource" onclick="myclick(1)" style="background-color: #ADADAD" border="1" >
                     	 检验设备
                    </li>
                    <li id="tab2" class = "tabResource" onclick="myclick(2)" border="1px" >
                       	检验标准仪器仪表
                    </li>
                    <li id="tab3" class = "tabResource" onclick="myclick(3)" border="1px" >
                     	检验非标准仪器仪表
                    </li>
                    <li id="tab4" class = "tabResource" onclick="myclick(4)" border="1px" >
                     	检验量具
                    </li>
                    <li id="tab5" class = "tabResource" onclick="myclick(5)" border="1px" >
                     	检验工装
                    </li>
                    <li id="tab6" class = "tabResource" onclick="myclick(6)" border="1px" >
                     	检验工艺辅料
                    </li>
                </ul>
            </div>
            <div id = "content" style="margin-top:10px;">
     			<table id = "table1_content" border="1"  width="800px" cellpadding="0" cellspacing="0" style="table-layout:fixed;word-break:break-all;word-wrap:break-word;display: block; margin-top: 20px">
               		<tr>
               			<th>设备编号</th><th>设备名称</th><th>设备型号</th><th>类别</th><th>规格</th><th>使用数量</th><th>备注</th>
               		</tr>
               		<% if(pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkEquips/QMEquipmentInfo").size() > 0){
               				Iterator it = pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkEquips/QMEquipmentInfo").iterator();
               				while(it.hasNext()){
               					Node node = (Node)it.next();
               		%>
               		<tr>
               			<td><%=node.valueOf("@number") %></td><td><%=node.valueOf("@name") %></td><td><%=node.valueOf("@pindex") %></td><td><%=node.valueOf("@equipmentType") %></td><td><%=node.valueOf("@csize") %></td><td><%=node.valueOf("@useCount") %></td><td><%=node.valueOf("@bz") %></td>
               		</tr>
               		<%}} %>

               	</table>

                <table id = "table2_content" border="1" width="800px" cellpadding="0" cellspacing="0" style="table-layout:fixed;word-break:break-all;word-wrap:break-word;display:none; margin-top: 20px">
               		<tr>
               			<th>编号</th><th>名称</th><th>型号</th><th>类别</th><th>规格</th><th>使用数量</th><th>备注</th>
               		</tr>
               		<% if(pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkSdashboard/QMSDashboardInfo").size() > 0){
               				Iterator it = pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkSdashboard/QMSDashboardInfo").iterator();
               				while(it.hasNext()){
               					Node node = (Node)it.next();
               		%>
               		<tr>
               			<td><%=node.valueOf("@number") %></td><td><%=node.valueOf("@name") %></td><td><%=node.valueOf("@pindex") %></td><td><%=node.valueOf("@equipmentType") %></td><td><%=node.valueOf("@csize") %></td><td><%=node.valueOf("@useCount") %></td><td><%=node.valueOf("@bz") %></td>
               		</tr>
               		<%}} %>
               	</table>

                 <table id = "table3_content" border="1" width="800px"  cellpadding="0" cellspacing="0" style="table-layout:fixed;word-break:break-all;word-wrap:break-word;display:none; margin-top: 20px">
               		<tr>
               			<th>编号</th><th>名称</th><th>型号</th><th>类别</th><th>规格</th><th>使用数量</th><th>备注</th>
               		</tr>
               		<% if(pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkUnsdashboard/QMUnSDashboardInfo").size() > 0){
               				Iterator it = pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkUnsdashboard/QMUnSDashboardInfo").iterator();
               				while(it.hasNext()){
               					Node node = (Node)it.next();
               		%>
               		<tr>
               			<td><%=node.valueOf("@number") %></td><td><%=node.valueOf("@name") %></td><td><%=node.valueOf("@pindex") %></td><td><%=node.valueOf("@equipmentType") %></td><td><%=node.valueOf("@csize") %></td><td><%=node.valueOf("@useCount") %></td><td><%=node.valueOf("@bz") %></td>
               		</tr>
               		<%}} %>
               	</table>

                <table id = "table4_content" border="1" width="800px" cellpadding="0" cellspacing="0" style="table-layout:fixed;word-break:break-all;word-wrap:break-word;display:none; margin-top: 20px">
               		<tr>
               			<th>编号</th><th>名称</th><th>型号</th><th>规格</th><th>使用数量</th><th>备注</th>
               		</tr>
               		<% if(pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkMeasures/QMMeasureInfo").size() > 0){
               				Iterator it = pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkMeasures/QMMeasureInfo").iterator();
               				while(it.hasNext()){
               					Node node = (Node)it.next();
               		%>
               		<tr>
               			<td><%=node.valueOf("@number") %></td><td><%=node.valueOf("@name") %></td><td><%=node.valueOf("@equipmentType") %></td><td><%=node.valueOf("@csize") %></td><td><%=node.valueOf("@useCount") %></td><td><%=node.valueOf("@bz") %></td>
               		</tr>
               		<%}} %>
               	</table>

                <table id = "table5_content" border="1" width="800px" cellpadding="0" cellspacing="0" style="table-layout:fixed;word-break:break-all;word-wrap:break-word;display:none; margin-top: 20px">
               		<tr>
               			<th>编号</th><th>名称</th><th>工装类别</th><th>规格</th><th>型号</th><th>使用数量</th><th>备注</th>
               		</tr>
               		<% if(pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkTools/QMToolInfo").size() > 0){
               				Iterator it = pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkTools/QMToolInfo").iterator();
               				while(it.hasNext()){
               					Node node = (Node)it.next();
               		%>
               		<tr>
               			<td><%=node.valueOf("@toolNum") %></td><td><%=node.valueOf("@toolName") %></td><td><%=node.valueOf("@frockType") %></td><td><%=node.valueOf("@csize") %></td><td><%=node.valueOf("@toolType") %></td><td><%=node.valueOf("@useCount") %></td><td><%=node.valueOf("@bz") %></td>
               		</tr>
               		<%}} %>
               	</table>

                <table id = "table6_content" border="1" width="800px" cellpadding="0" cellspacing="0" style="table-layout:fixed;word-break:break-all;word-wrap:break-word;display:none; margin-top: 20px">
               		<tr>
               			<th>编号</th><th>名称</th><th>型号</th><th>规格</th><th>技术条件</th><th>计量单位</th><th>附加条件</th><th>使用数量</th><th>使用车间</th><th>备注</th>
               		</tr>
               		<% if(pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkMaterials/QMMaterialInfo").size() > 0){
               				Iterator it = pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkMaterials/QMMaterialInfo").iterator();
               				while(it.hasNext()){
               					Node node = (Node)it.next();
               		%>
               		<tr>
               			<td><%=node.valueOf("@number") %></td><td><%=node.valueOf("@materialName") %></td><td><%=node.valueOf("@mindex") %></td><td><%=node.valueOf("@csize") %></td><td><%=node.valueOf("@jstj") %></td><td><%=node.valueOf("@jldw") %></td><td><%=node.valueOf("@fjtj") %></td><td><%=node.valueOf("@sl") %></td><td><%=node.valueOf("@sycj") %></td><td><%=node.valueOf("@bz") %></td>
               		</tr>
               		<%}} %>
               	</table>
        </div>
        </div>

        <div id="checkTable" style = "margin-top:10px;overflow:hidden">
        <div class = "order-title" style="text-align:left;font-size:24px;margin-top:0px;">
	    		检验记录表
	    </div>

	    <div id="tab_bar1" style = "margin-top:0px;">
                <ul>
                	<%
                	int i = 1;
                	if(pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkRecordTables/parameterTable").size() > 0){
               				Iterator it = pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkRecordTables/parameterTable").iterator();
               				while(it.hasNext()){
               					Node node = (Node)it.next();
               			if(i == 1){
               		%>
               		<li id="tab1<%=i%>" name = "check" onclick="myclick1(<%=i%>)" class = "tabResource" style="background-color: #ADADAD" border="1" >
                     	<%=node.valueOf("@name")+ "(" + node.valueOf("@type") + ")"%>
                    </li>
                    <%}else{ %>
                    	<li id="tab1<%=i%>" name = "check" onclick="myclick1(<%=i%>)" class = "tabResource" style="background-color: #F8F8F8" border="1" >
                     	<%=node.valueOf("@name")+ "(" + node.valueOf("@type") + ")"%>
                    </li>
                    <%} %>
               		<%i = i + 1;} } %>
                </ul>
         </div>
		<div id="content2" style = "margin-top:0px;text-align:left;">
				<div style="text-align:left;font-size:18px;margin-top:1px;">
					检验表属性：
				</div>
				<div style = "margin-top:1px;">
			 	<%
	            	int s = 1;
	            	if(pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkRecordTables/parameterTable").size() > 0){
	            		Iterator it = pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkRecordTables/parameterTable").iterator();
	       				while(it.hasNext()){
	       					Node node = (Node)it.next();
	       			if(s == 1){%>

	           			<table id = "table<%=s%>_content2"  valign="left" cellpadding="0" cellspacing="0" style="table-layout:fixed;word-break:break-all;word-wrap:break-word;display: block;">
							<tr><td>单元表表名：</td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']").valueOf("@name")%></td><td>项目名：</td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']").valueOf("@projectName")%></td></tr>
							<tr><td>表主键类型：</td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']").valueOf("@type")%></td><td>套表明：</td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']").valueOf("@tableName")%></td></tr>
							<tr><td>每项/次数：</td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']").valueOf("@eachName")%></td></tr>
	           			</table>

					<%}else{ %>

						<table id = "table<%=s%>_content2"  valign="left" cellpadding="0" cellspacing="0" style="table-layout:fixed;word-break:break-all;word-wrap:break-word;display: none;">
							<tr><td>单元表表名：</td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']").valueOf("@name")%></td><td>项目名：</td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']").valueOf("@projectName")%></td></tr>
							<tr><td>表主键类型：</td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']").valueOf("@type")%></td><td>套表明：</td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']").valueOf("@tableName")%></td></tr>
							<tr><td>每项/次数：</td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']").valueOf("@eachName")%></td></tr>
	           			</table>

					<%} %>
	            <%s = s + 1;}} %>
	          </div>
	     </div>
    	<div id="content1" style = "z-index:2; position:absolute;margin-top:20px;">
			<div>
	            <%
	            	int j = 1;
	            	if(pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkRecordTables/parameterTable").size() > 0){
	            		Iterator it = pace.selectNodes("//QMProcedureInfo[@bsoID='"+pace.valueOf("@bsoID")+"']/checkRecordTables/parameterTable").iterator();
	       				while(it.hasNext()){
	       					Node node = (Node)it.next();
	            %>
	            <%if(j == 1){
	            	if("检测类".equals(node.valueOf("@type"))){
	            %>

	           	<table id = "table<%=j%>_content1" border="1" width="800px" cellpadding="0" cellspacing="0" style="table-layout:fixed;word-break:break-all;word-wrap:break-word;display: block;">
	           		<tr><th>序号</th><th>检测项</th><th>公称值</th><th>上偏差</th><th>下偏差</th><th>实测值</th><th>判定/结论</th></tr>
					<% if(node.selectNodes("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter").size() > 0){
						for(int z = 0; z < node.selectNodes("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter").size(); z++){
					%>
					<tr><td><%=String.valueOf(z + 1)%></td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='检测项']/attribute").getText()%></td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='公称值']/attribute").getText()%></td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='上偏差']/attribute").getText()%></td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='下偏差']/attribute").getText()%></td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='实测值']/attribute").getText()%></td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='判定/结论']/attribute").getText()%></td></tr>
					<%
					}}%>
	           	</table>

	           	<%}else if("记录类".equals(node.valueOf("@type"))){ %>

	           	<table id = "table<%=j%>_content1" border="1" width="800px" cellpadding="0" cellspacing="0" style="table-layout:fixed;word-break:break-all;word-wrap:break-word;display: block;">
	           		<tr><th>序号</th><th>记录项</th><th>要求</th><th>记录</th><th>判定/结论</th></tr>
	           		<% if(node.selectNodes("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter").size() > 0){
						for(int z = 0; z < node.selectNodes("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter").size(); z++){
					%>
					<tr>
					<td><%=String.valueOf(z + 1)%></td>
					<td>
					<%String jlx = CheckPacesUtil.Html2Text(CheckPacesUtil.changeHtml(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='记录项']/attribute").getText()));
					List<String> listJlx = CheckPacesUtil.getImgStr(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='记录项']/attribute").getText());
					int jlxVar = 0;
					char[]  jlxArr=jlx.toCharArray();
					for(int n=0;n<jlxArr.length;n++) {
						String value = "";
						if(jlxArr[n] == '♀') {
							String path = listJlx.get(jlxVar).toString().replaceAll("\\\\", "/").replace("@#$%^", basePath+"temp/publish/"+technicsNumber);
							jlxVar++;
							%>
							<img src="<%=path%>">
							<% }else{
								 value = value + jlxArr[n];
							%>
							<%=value%>
					<% }}%>
					</td>
					<td>
					<%String yq = CheckPacesUtil.Html2Text((node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='要求']/attribute").getText()));
					List<String> listYq = CheckPacesUtil.getImgStr(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='要求']/attribute").getText());
					int yqVar = 0;
					char[]  yqArr=yq.toCharArray();
					for(int n=0;n<yqArr.length;n++) {
						String value = "";
						if(yqArr[n] == '♀') {
							String path = listYq.get(yqVar).toString().replaceAll("\\\\", "/").replace("@#$%^",basePath+"temp/publish/"+technicsNumber);
							yqVar++;
							%>
							<img src="<%=path%>">
							<% }else{
								 value = value + yqArr[n];
							%>
							<%=value%>
					<% }}%>
					</td>
					<td>
					<%String jl = CheckPacesUtil.Html2Text(CheckPacesUtil.changeHtml(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='记录']/attribute").getText()));
					List<String> listJl = CheckPacesUtil.getImgStr(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='记录']/attribute").getText());
					int jlVar = 0;
					char[]  jlArr=jl.toCharArray();
					for(int n=0;n<jlArr.length;n++) {
						String value = "";
						if(jlArr[n] == '♀') {
							String path = listJl.get(jlVar).toString().replaceAll("\\\\", "/").replace("@#$%^",basePath+"temp/publish/"+technicsNumber);
							jlVar++;
							%>
							<img src="<%=path%>">
							<% }else{
								 value = value + jlArr[n];
							%>
							<%=value%>
					<% }}%>
					</td>
					<td>
					<%String pd = CheckPacesUtil.Html2Text(CheckPacesUtil.changeHtml(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='判定/结论']/attribute").getText()));
						List<String> listPd = CheckPacesUtil.getImgStr(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='判定/结论']/attribute").getText());
						int pdVar = 0;
						char[]  pdArr=pd.toCharArray();
						for(int n=0;n<pdArr.length;n++) {
						String value = "";
						if(pdArr[n] == '♀') {
							String path = listPd.get(pdVar).toString().replaceAll("\\\\", "/").replace("@#$%^", basePath+"temp/publish/"+technicsNumber);
							pdVar++;
							%>
							<img src="<%=path%>">
							<% }else{
								 value = value + pdArr[n];
							%>
							<%=value%>
						<% }}%>
					</td>
					</tr>
					<%
					}}%>
	           	</table>

	            	<% }%>
	            <%}else{
	            	if("检测类".equals(node.valueOf("@type"))){
	            	%>

	           	<table id = "table<%=j%>_content1"  border="1"  width="800px" cellpadding="0" cellspacing="0" style="table-layout:fixed;word-break:break-all;word-wrap:break-word;display:none;">
	           		<tr><th>序号</th><th>检测项</th><th>公称值</th><th>上偏差</th><th>下偏差</th><th>实测值</th><th>判定/结论</th></tr>
	           		<% if(node.selectNodes("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter").size() > 0){
						for(int z = 0; z < node.selectNodes("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter").size(); z++){
					%>
					<tr><td><%=String.valueOf(z + 1)%></td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='检测项']/attribute").getText()%></td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='公称值']/attribute").getText()%></td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='上偏差']/attribute").getText()%></td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='下偏差']/attribute").getText()%></td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='实测值']/attribute").getText()%></td><td><%=node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='判定/结论']/attribute").getText()%></td></tr>
					<% }}%>
	           	</table>

				<%}else if("记录类".equals(node.valueOf("@type"))){ %>

	           	<table id = "table<%=j%>_content1" border="1" width="800px"  cellpadding="0" cellspacing="0" style="table-layout:fixed;word-break:break-all;word-wrap:break-word;display:none;">
	           		<tr><th>序号</th><th>记录项</th><th>要求</th><th>记录</th><th>判定/结论</th></tr>
	           		<% if(node.selectNodes("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter").size() > 0){
						for(int z = 0; z < node.selectNodes("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter").size(); z++){
					%>
					<tr><td><%=String.valueOf(z + 1)%></td>
					<td>
					<%String jlx = CheckPacesUtil.Html2Text(CheckPacesUtil.changeHtml(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='记录项']/attribute").getText()));
					List<String> listJlx = CheckPacesUtil.getImgStr(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='记录项']/attribute").getText());
					int jlxVar = 0;
					char[]  jlxArr=jlx.toCharArray();
					for(int n=0;n<jlxArr.length;n++) {
						String value = "";
						if(jlxArr[n] == '♀') {
							String path = listJlx.get(jlxVar).toString().replaceAll("\\\\", "/").replace("@#$%^", basePath+"temp/publish/"+technicsNumber);
							jlxVar++;
							%>
							<img src="<%=path%>">
							<% }else{
								 value = value + jlxArr[n];
							%>
							<%=value%>
					<% }}%>
					</td>
					<td>
					<%String yq = CheckPacesUtil.Html2Text(CheckPacesUtil.changeHtml(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='要求']/attribute").getText()));
					List<String> listYq = CheckPacesUtil.getImgStr(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='要求']/attribute").getText());
					int yqVar = 0;
					char[]  yqArr=yq.toCharArray();
					for(int n=0;n<yqArr.length;n++) {
						String value = "";
						if(yqArr[n] == '♀') {
							String path = listYq.get(yqVar).toString().replaceAll("\\\\", "/").replace("@#$%^", basePath+"temp/publish/"+technicsNumber);
							yqVar++;
							%>
							<img src="<%=path%>">
							<% }else{
								 value = value + yqArr[n];
							%>
							<%=value%>
					<% }}%>
					</td>
					<td>
					<%String jl = CheckPacesUtil.Html2Text(CheckPacesUtil.changeHtml(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='记录']/attribute").getText()));
					List<String> listJl = CheckPacesUtil.getImgStr(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='记录']/attribute").getText());
					int jlVar = 0;
					char[]  jlArr=jl.toCharArray();
					for(int n=0;n<jlArr.length;n++) {
						String value = "";
						if(jlArr[n] == '♀') {
							String path = listJl.get(jlVar).toString().replaceAll("\\\\", "/").replace("@#$%^", basePath+"temp/publish/"+technicsNumber);
							jlVar++;
							%>
							<img src="<%=path%>">
							<% }else{
								 value = value + jlArr[n];
							%>
							<%=value%>
					<% }}%>
						</td>
						<td>
						<%String pd = CheckPacesUtil.Html2Text(CheckPacesUtil.changeHtml(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='判定/结论']/attribute").getText()));
						List<String> listPd = CheckPacesUtil.getImgStr(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='判定/结论']/attribute").getText());
						int pdVar = 0;
						char[]  pdArr=pd.toCharArray();
						for(int n=0;n<pdArr.length;n++) {
						String value = "";
						if(pdArr[n] == '♀') {
							String path = listPd.get(pdVar).toString().replaceAll("\\\\", "/").replace("@#$%^", basePath+"temp/publish/"+technicsNumber);
							pdVar++;
							%>
							<img src="<%=path%>">
							<% }else{
								 value = value + pdArr[n];
							%>
							<%=value%>
						<% }}%>
						</td>
						</tr>
					<%
					System.out.println(CheckPacesUtil.Html2Text(node.selectSingleNode("//parameterTable[@bsoID='"+node.valueOf("@bsoID")+"']/parameter/values[@number='"+ String.valueOf(z) +"']/value[@columnName='记录项']/attribute").getText()));
					}}%>
	           	</table>

	            	<%} %>
	            <%} %>
	           <%j = j + 1;}} %>
        </div>
        </div>
        </div>
    </body>
</html>




