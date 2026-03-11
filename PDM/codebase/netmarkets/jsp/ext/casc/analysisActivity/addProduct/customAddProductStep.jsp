<%@page language="java" pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="ext.casc.analysisActivity.helper.AnalysisConstant" %>
<%@ page import="ext.casc.process.ProcessConstants" %>
<%@ page import="com.glaway.mpm.util.DateUtil" %>
<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request"/>

<%
    String contextPath = request.getContextPath();
    String oid = request.getParameter("oid");
    List<String> zhipin = new ArrayList<String>();
    zhipin.add(AnalysisConstant.ANALYSIS_ZHIPIN_FANXIU);
    zhipin.add(AnalysisConstant.ANALYSIS_ZHIPIN_BAOFEI);
    zhipin.add(AnalysisConstant.ANALYSIS_AFFECTED_NO);
    List<String> zhipinDisplay = new ArrayList<String>(zhipin);
    zhipinDisplay.set(0, AnalysisConstant.ANALYSIS_ZHIPIN_FANXIUFANGONG);
    request.setAttribute("zhipin", zhipin);
    request.setAttribute("zhipinDisplay", zhipinDisplay);
    String dateinfo = ProcessConstants.JSP_MSG_DATEINFO;
    String completeTime = DateUtil.afterNDay("yyyy/MM/dd", 5);
    String value = "<input type=\"text\" readonly id=\"completeTime\" name=\"completeTime\" onchange=\"verifyDate(this)\" " +
            "value=\"" + completeTime + "\"  size=\"10\" maxlength=\"10\"/>\n\t\t\t<A HREF=\"javascript:void(0)\"  " +
            "onmousedown=\"suppressCalendarBlur(event);\"" +
            "onClick=\"initCal('\\u65e5\\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/siteStyles.css TYPE=text/css>', " +
            "'/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); " +
            "setDateField(document.getElementsByName('completeTime')[0], 'yyyy/MM/dd', '\\u4e00\\u6708#\\u4e8c\\u6708#\\u4e09\\u6708#\\u56db\\u6708#\\u4e94\\u6708#\\u516d\\u6708#\\u4e03\\u6708#\\u516b\\u6708#\\u4e5d\\u6708#\\u5341\\u6708#\\u5341\\u4e00\\u6708#\\u5341\\u4e8c\\u6708#', '#', '\\u661f\\u671f\\u65e5#\\u661f\\u671f\\u4e00#\\u661f\\u671f\\u4e8c#\\u661f\\u671f\\u4e09#\\u661f\\u671f\\u56db#\\u661f\\u671f\\u4e94#\\u661f\\u671f\\u516d#', '0'); " +
            "newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')\">\n\t\t\t" +
            "<IMG name=\"calImg\" SRC=\"/Windchill/netmarkets/images/calendar.gif\" WIDTH=18 HEIGTH=16 BORDER=0></A>";
%>

<html>
<head>
    <style>
        body, td, input, select, textarea {
            font-size: 12px;  /* 设置字体大小 */
        }

        input[type="text"], select, textarea {
            padding: 4px 6px; /* 内边距增大 */
            font-size: 12px;
        }

        input[type="button"] {
            padding: 4px 8px;
            font-size: 12px;
            cursor: pointer;
        }

        .dataTable {
            border-spacing: 6px; /* 控制表格行间距 */
        }

        td {
            vertical-align: middle;
        }
    </style>

    <script type="text/javascript">
        function openSearchDialog() {
            window.open('<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/addProduct/searchPartNumber.jsp?oid=<%=oid%>', 'product', 'height=600, width=800, top=150, left=300');
        }

        function selectUser(oid, type, title) {
            window.open('<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/selectUser.jsp?oid=' + oid + '&type=' + type + '&title=' + title, title, 'height=500, width=500, top=150, left=300');
        }

        function verifyDate(object) {
            debugger;
            var today = getToDay();
            var selDate = object.value;
            if (Date.parse(today) > Date.parse(selDate)) {
                alert("<%=dateinfo%>");
                object.value = "<%=completeTime%>";
                return;
            }
            document.getElementById("completeTime_value").value = object.value;
        }

        function doHandleMonth(month) {
            if (month.toString().length == 1) {
                month = "0" + month;
            }
            return month;
        }

        function getToDay() {
            var now = new Date();
            var nowYear = now.getFullYear();
            var nowMonth = now.getMonth();
            var nowDate = now.getDate();
            newdate = new Date(nowYear, nowMonth, nowDate);
            nowMonth = doHandleMonth(nowMonth + 1);
            nowDate = doHandleMonth(nowDate);
            return nowYear + "/" + nowMonth + "/" + nowDate;
        }
    </script>
</head>
<body>
<table class="dataTable">
    <tr>
        <td colspan="1" style="text-align: left;"><w:label value="部件编号:"/></td>
        <td colspan="1" style="text-align: left;"><w:textBox name="number" id="number" size="30" maxlength="100" value="" readonly="true" required="true" />
            <input type="button" value="🔍" onclick="openSearchDialog();" title="搜索部件"/>
            <div style="display:none;">
                <w:textBox name="number_value" id="number_value" size="30" maxlength="100" value=""/>
            </div>
        </td>
    </tr>
    <tr>
        <td colspan="1" style="text-align: left;"><w:label value="在制品:"/></td>
        <td colspan="1" style="text-align: left;"><w:comboBox id="zaizhipin" name="zaizhipin"
            internalValues="${zhipin}" displayValues="${zhipinDisplay}" onchange=""/>
        </td>
    </tr>
    <tr>
        <td colspan="1" style="text-align: left;"><w:label value="工艺员:"/></td>
        <td colspan="1" style="text-align: left;"><w:textBox name="_responser_product" id="_responser_product" size="30" maxlength="100" value="" readonly="true" required="true" />
            <input type="button" value="🔍" onclick="selectUser('','<%=AnalysisConstant.PRODUCT%>','选择责任人');" title="选择责任人"/>
            <div style="display:none;">
                <w:textBox name="_responser_value_product" id="_responser_value_product" size="30" maxlength="100" value=""/>
            </div>
        </td>
    </tr>
    <tr>
        <td colspan="1" style="text-align: left;"><w:label value="已制品:"/></td>
        <td colspan="1" style="text-align: left;"><w:comboBox id="yizhipin" name="yizhipin"
            internalValues="${zhipin}" displayValues="${zhipinDisplay}" onchange=""/>
        </td>
    </tr>
    <tr>
        <td colspan="1" style="text-align: left;"><w:label value="计调员:"/></td>
        <td colspan="1" style="text-align: left;"><w:textBox name="_jidiaoyuan_product" id="_jidiaoyuan_product" size="30" maxlength="100" value="" readonly="true" required="true" />
            <input type="button" value="🔍" onclick="selectUser('','<%=AnalysisConstant.PRODUCT%>','选择计调员');" title="选择计调员"/>
            <div style="display:none;">
                <w:textBox name="_jidiaoyuan_value_product" id="_jidiaoyuan_value_product" size="30" maxlength="100" value=""/>
            </div>
        </td>
    </tr>
    <tr>
        <td colspan="1" style="text-align: left;"><w:label value="更改要求:"/></td>
        <td colspan="1" style="text-align: left;"><w:textArea id="requirement" name="requirement" value="" cols="40" rows="4" maxLength = "2000"/></td>
    </tr>
    <tr>
        <td colspan="1" style="text-align: left;"><w:label value="要求完成时间:"/></td>
        <td colspan="1" style="text-align: left;"><%=value%>
            <div style="display:none;">
                <w:textBox name="completeTime_value" id="completeTime_value" size="30" maxlength="100" value="<%=completeTime%>"/>
            </div>
        </td>
    </tr>
</table>

</body>

</html>
