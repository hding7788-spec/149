<%@ taglib prefix="jca"
           uri="http://www.ptc.com/windchill/taglib/components" %>
<%@page language="java" pageEncoding="utf-8"
        contentType="text/html; charset=utf-8" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean" %>
<%@ page import="java.util.ArrayList" %>
<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request"/>

<%
    ArrayList<String> secretList = new ArrayList<String>();
    secretList.add("公开");
    secretList.add("内部");
    secretList.add("秘密★10年");
    secretList.add("机密★20年");
    request.setAttribute("secretList", secretList);
%>

<html>
<head>
    <font style="font-weight: bolder" size=3>新建更改影响分析</font>
</head>
<body>

<jca:renderPropertyPanel>
    <w:textBox propertyLabel="编号" id="number" name="number" value="已生成" size="30" readonly="true"/>
    <w:textBox propertyLabel="名称" id="name" name="name" required="true" size="30" maxlength="100" value="" />
    <w:comboBox propertyLabel="密级" id="secret" name="secret" required="true" internalValues="${secretList}"
                displayValues="${secretList}"/>
</jca:renderPropertyPanel>

</body>

</html>
