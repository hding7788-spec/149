<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="com.glaway.mpm.processplan.checkouttable.CheckOutUtil" %>
<%@page import="com.glaway.mpm.processplan.checkouttable.checkbean.*" %>
<%@page import="java.util.List" %>

<%
    String technicsNumber = request.getParameter("technicsNumber");
    if(technicsNumber.contains(" ")){
        technicsNumber = technicsNumber.substring(0,technicsNumber.indexOf(" "));
    }
    String stepName = request.getParameter("tableName");
    String xmlPath = CheckOutUtil.getXmlPath(technicsNumber);
    List<SummaryRecordBean> summaryRecordBeanList = CheckOutUtil.getPhotoRecords(xmlPath, stepName);
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    String photoUrl = basePath + "temp/checkouttable/" + technicsNumber + "/photoTemplate";
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>查看照片样张汇总表</title>
    <style type="text/css">
        body {
            margin: 0px;
            width: 2500px;
            min-width: 100px;
            max-width: 2500px;
            height: 100%;
        }

        .bttr {
            color: #2222fF;
            background-color: #cFcFcF;
        }

        .bttd {
            background-color: #ccdFf1;
        }
    </style>
</head>

<body>
<div id="checkResource" style="margin-top:10px;overflow:hidden">
    <h2 style="color: #ff0000;">照片样张详细信息</h2>
    <hr/>
    <div>
        <table id="table1_content" border="1" width="1200px" margin-top: 20px>
            <tr class="bttr">
                <th width="5%">工序</th>
                <th width="5%">工步</th>
                <th width="10%">名称</th>
                <th width="20%">拍摄要素</th>
                <th width="20%">判别准则</th>
                <th width="20%">照片样张压缩包</th>
            </tr>
            <%
            for(SummaryRecordBean summaryRecordBean : summaryRecordBeanList){
                %>
            <tr>
                <td align="center">
                    <%= summaryRecordBean.getStepNumber()%>
                </td>
                <td align="center">
                    <%= summaryRecordBean.getPaceNumber()%>
                </td>
                <td align="center">
                    <%= summaryRecordBean.getName()%>
                </td>
                <td align="center">
                    <%= summaryRecordBean.getPsyq()%>
                </td>
                <td align="center">
                    <%= summaryRecordBean.getPbzz()%>
                </td>
                <td align="center">
                    <a style="color:blue" target = "_blank" href="<%= photoUrl%><%= summaryRecordBean.getPhotoUrl()%>">照片样张查看</a>
                </td>
            </tr>
            <%
            }
            %>
        </table>
    </div>
</div>
</body>
</html>




