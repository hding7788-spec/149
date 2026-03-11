<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="ext.casc.importdata.*" %>
<jsp:useBean id="localeBean2" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>
<%@page language="java" pageEncoding="UTF-8"
        contentType="text/html; charset=UTF-8"%>
<%!private static final String PART_RESOURCE = "ext.casc.importdata.productRB";%>
<%
    ResourceBundle resourceRB = ResourceBundle.getBundle(PART_RESOURCE, localeBean2.getLocale());
    String paramNumber = request.getParameter("oid");

%>

<script language="javascript">
</script>

<table>
    <input type="hidden" name="paramNumber" id="paramNumber" value="<%=paramNumber%>"/>

    <tr>
        <td align="right"><FONT CLASS="wizardlabel"><%=resourceRB.getString(productRB.IMPORTDATA_EXCEL_TITLE)%>
        </Font></td>
        </br>
        <td><span id="span2"><input id="file2" name="file2" size="60" type="file" accept=".xls" /></span>
        </td>
    </tr>
</table>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>