<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="com.glaway.mpm.processplan.checkouttable.CheckOutUtil"%>
<%@ page import="java.util.List" %>

<%
    String technicsNumber = request.getParameter("technicsNumber");
    String technicsVersion = request.getParameter("technicsVersion");
    if(technicsNumber.contains(" ")){
        technicsNumber = technicsNumber.substring(0,technicsNumber.indexOf(" "));
    }
    String pdfName = CheckOutUtil.getTechincsXMLPathByNumberAndVersion(technicsNumber,technicsVersion);
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    String pdfUrl = basePath + "temp" + "/" + "checkouttable" + "/" + technicsNumber + "/PDFPreview.pdf";
%>
<script type="text/javascript">
    function openTab(evt, tabName) {
        var i, tabcontent, tablinks;
        tabcontent = getElementsByClassName(document,"tab-content");
        for (i = 0; i < tabcontent.length; i++) {
            tabcontent[i].style.display = "none";
        }
        tablinks = getElementsByClassName(document,"tab");
        for (i = 0; i < tablinks.length; i++) {
            tablinks[i].className = tablinks[i].className.replace(" active", "");
        }
        document.getElementById(tabName).style.display = "block";
        evt.srcElement.className += " active";
    }

    function getElementsByClassName(node,classname){
        //先判断浏览器是否支持，如果支持则直接使用
        if(node.getElementsByClassName){
            return node.getElementsByClassName(classname);
        }else {
            //如果不支持
            var results = new Array();
            var elems = node.getElementsByTagName("*");
            for(var i=0;i<elems.length;i++){
                if (elems[i].className.indexOf(classname) != -1){
                    results[results.length] = elems[i];
                }
            }
            return results;

        }
    }
</script>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>查看汇总表</title>
    <style>
        .tabs {
            overflow: hidden;
        }

        .tab {
            width: 300px;
            text-align: center;
            float: left;
            cursor: pointer;
            padding: 8px 16px;
            border: 1px solid #ccc;
            border-bottom: none;
        }

        .tab.active {
            background-color: #ccc;
        }

        .tab-content {
            display: none;
            padding: 16px;
            /*border: 1px solid #ccc;*/
        }

        .tab-content.active {
            display: block;
        }
    </style>
</head>

<body>
<div class="tabs">
    <div class="tab active" onclick="openTab(event, 'tab1')">检验记录汇总表</div>
    <div class="tab" onclick="openTab(event, 'tab2')">照片样张汇总表</div>
    <div class="tab" onclick="openTab(event, 'tab3')">白羽表单汇总表</div>
</div>
<div class="pdf">
    <a href="<%=pdfUrl%>" target="_BLANK"><%=pdfName%></a>
</div>
<div id="tab1" class="tab-content active">
    <iframe src="showCheckTable.jsp?technicsNumber=<%=technicsNumber%>" width="100%" height="800px"></iframe>
</div>
<div id="tab2" class="tab-content">
    <iframe src="showPhotoTable.jsp?technicsNumber=<%=technicsNumber%>" width="100%" height="800px"></iframe>
</div>
<div id="tab3" class="tab-content">
    <iframe src="showBaiyuTable.jsp?technicsNumber=<%=technicsNumber%>" width="100%" height="800px"></iframe>
</div>
</body>




