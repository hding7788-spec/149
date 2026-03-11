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
// 	CheckOutUtil.getTechincsXMLPath(technicsNumber);
    String stepName = request.getParameter("tableName");
    String xmlPath = CheckOutUtil.getXmlPath(technicsNumber);
    System.out.println("------------------------------------------xmlPath---------------------------------" + xmlPath);
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    String technicsUrl = basePath + "temp" + "/" + "checkouttable" + "/" + technicsNumber;
    System.out.println("------------------------------------------technicsUrl---------------------------------" + technicsUrl);
    List<CheckRecordBean> checkRecordBeanList = CheckOutUtil.getCheckRecords(xmlPath,basePath,stepName);
//    List<ProjectNameBean> projectNameBeanList = CheckOutUtil.getXMLCheckOutTbaleList(xmlPath, tableName, "检测类", technicsUrl);
//     CheckOutUtil.getXMLCheckOutTbaleList(xmlPath);
//     SAXReader sax = new SAXReader();
//     Document document = sax.read(new File(xmlPath));
//     Element root = document.getRootElement();
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>查看检验汇总表</title>
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
    <h2 style="color: #ff0000;">检验工步详细信息</h2>
    <hr/>
    <div>
        <%--<h3>套表名：<%=tableName %></h3>--%>
        <table id="table1_content" border="1" width="2500px" margin-top: 20px>
            <tr class="bttr">
                <th width="2%">工序</th>
                <th width="2%">工步</th>
                <th width="4%">表主件类型</th>
                <th width="4%">单元表表名</th>
                <th width="4%">项目名</th>
                <th width="4%">套表名</th>
                <th width="4%">记录项/检测项</th>
                <th width="4%">要求值/公称值</th>
                <th width="4%">记录</th>
                <th width="5%">上偏差</th>
                <th width="5%">下偏差</th>
                <th width="16%">实测值</th>
                <th width="8%">签署信息</th>
                <th width="16%">检验方法描述</th>
                <th width="16%">检验内容描述</th>
            </tr>
            <%
            for(CheckRecordBean checkRecordBean : checkRecordBeanList){
                %>
            <tr>
                <td align="center">
                    <%= checkRecordBean.getStepNumber()%>
                </td>
                <td align="center">
                    <%= checkRecordBean.getPaceNumber()%>
                </td>
                <td align="center">
                    <%= checkRecordBean.getBzjlx()%>
                </td>
                <td align="center">
                    <%= checkRecordBean.getDybbm()%>
                </td>
                <td align="center">
                    <%= checkRecordBean.getXmm()%>
                </td>
                <td align="center">
                    <%= checkRecordBean.getTbm()%>
                </td>
                <td align="center">
                    <%= checkRecordBean.getJlx_jcx()%>
                </td>
                <td align="center">
                    <%= checkRecordBean.getYqz_gcz()%>
                </td>
                <td align="center">
                    <%= checkRecordBean.getJl()%>
                </td>
                <td align="center">
                    <%= checkRecordBean.getSpc()%>
                </td>
                <td align="center">
                    <%= checkRecordBean.getXpc()%>
                </td>
                <td align="center">
                    <%= checkRecordBean.getScz()%>
                </td>
                <td align="center">
                    <%= checkRecordBean.getQsxx()%>
                </td>
                <td>
                    <%= checkRecordBean.getJcffms()%>
                </td>
                <td>
                    <%= checkRecordBean.getJcnrms()%>
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




