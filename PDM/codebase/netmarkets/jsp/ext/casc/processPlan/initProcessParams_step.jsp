<%@ page import="ext.casc.processPlan.Constants" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ptc.netmarkets.model.NmOid" %>
<%@ page import="wt.part.WTPart" %>
<%@ page language="java" session="true" pageEncoding="GBK" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<jca:tabToHighlight actionName="propertyPanel" objectType="object"/>
<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%

%>
<script type="text/javascript">


    function changePplantype() {
        // 获取选择的工艺文件类别
        var selectedPplantype = document.getElementById("pplantype").value;

        // 获取临时工艺顺序号的输入框
        var linShiNumInput = document.getElementById("linShiNum");

        // 如果选择的工艺文件类别为"临时工艺文件"，则允许编辑临时工艺顺序号
        if (selectedPplantype === "临时工艺文件") {
            linShiNumInput.removeAttribute("readonly");
        } else {
            // 如果选择的不是"临时工艺文件"，则禁用临时工艺顺序号的编辑
            linShiNumInput.setAttribute("readonly", "readonly");
        }
    }


</script>
<%


    List<String> depts = Constants.DEPTS;
    String dept = "";

    List<String> pplantypes = Constants.PPLANTYPES;
    String pplantype = "";

    String linShiNum = "";

    List<String> zfFlags = Constants.ZFFLAGS;
    String zfFlag = "";

    List<String> technicsTypes_display = Constants.TECHNICSTYPES_DISPLAY;
    String technicsType = "";

    request.setAttribute("depts", depts);
    request.setAttribute("pplantypes", pplantypes);
    request.setAttribute("zfFlags", zfFlags);
    request.setAttribute("pplantypes", pplantypes);
    request.setAttribute("linShiNum", linShiNum);
    request.setAttribute("technicsTypes", technicsTypes_display);

    //值
    request.setAttribute("dept", dept);
    request.setAttribute("pplantype", pplantype);
    request.setAttribute("zfFlag", zfFlag);
    request.setAttribute("technicsType", technicsType);

%>

<table cellSpacing="4">
	<tr>
        <td><w:label value="工艺类别:"/></td>
        <td><select name="technicsType" id="technicsType">
            <c:forEach items="${technicsTypes}" var="item">
                <option value="${item}" <c:if test="${item eq technicsType}">selected</c:if>>${item}</option>
            </c:forEach>
        </select></td>
    </tr>
    <tr>
        <td colspan="1" style="text-align: left;">
            <w:label value="部门:"/>
        </td>
        <td>
            <select name="dept" id="dept">
                <c:forEach items="${depts}" var="item">
                    <option value="${item}" <c:if test="${item eq dept}">selected</c:if>>${item}</option>
                </c:forEach>
            </select>
        </td>
    </tr>

    <tr>
        <td colspan="1" style="text-align: left;">
            <w:label value="工艺文件类别:"/>
        </td>
        <td>
            <select name="pplantype" id="pplantype" onchange="changePplantype()">
                <c:forEach items="${pplantypes}" var="item">
                    <option value="${item}" <c:if test="${item eq pplantype}">selected</c:if>>${item}</option>
                </c:forEach>
            </select>
        </td>
    </tr>

    <tr>
        <td><w:label value="临时工艺顺序号:"/></td>
        <td><input type="text" name="linShiNum" id="linShiNum" readonly="readonly" value="${linShiNum}">
        </td>
    </tr>

    <tr>
        <td><w:label value="主辅制类别:"/></td>
        <td><select name="zfFlag" id="zfFlag">
            <c:forEach items="${zfFlags}" var="item">
                <option value="${item}" <c:if test="${item eq zfFlag}">selected</c:if>>${item}</option>
            </c:forEach>
        </select></td>
    </tr>



</table>


<%@ include file="/netmarkets/jsp/util/end.jspf" %>