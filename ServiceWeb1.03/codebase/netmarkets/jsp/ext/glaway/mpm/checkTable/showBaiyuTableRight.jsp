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
    String type = request.getParameter("type");
    String xmlPath = CheckOutUtil.getXmlPath(technicsNumber);
    List<SummaryRecordBean> summaryRecordBeanList = CheckOutUtil.getBaiyuRecords(xmlPath,stepName,type);
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>查看白羽检验记录汇总表</title>
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
    <h2 style="color: #ff0000;">白羽表单详细信息</h2>
    <hr/>
    <div>
        <table id="table1_content" border="1" width="1200px" margin-top: 20px>
            <tr class="bttr">
                <th width="5%">工序</th>
                <th width="5%">工步</th>
                <th width="10%">编号</th>
                <th width="10%">名称</th>
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
                    <%= summaryRecordBean.getNumber()%>
                </td>
                <td align="center">
                    <%= summaryRecordBean.getName()%>
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




