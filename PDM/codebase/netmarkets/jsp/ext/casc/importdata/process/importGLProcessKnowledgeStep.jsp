<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="ext.casc.importdata.*" %>
<jsp:useBean id="localeBean2" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>

<%!private static final String PART_RESOURCE = "ext.casc.importdata.productRB";%>
<%
    ResourceBundle resourceRB = ResourceBundle.getBundle(PART_RESOURCE, localeBean2.getLocale());
%>

<script language="javascript">
</script>

<table>
    <tr>
        <td align="right"><FONT CLASS="wizardlabel"><%=resourceRB.getString(productRB.IMPORTDATA_EXCEL_TITLE)%>
        </Font></td>
        </br>
        <td><span id="span2"><input id="file2" name="file2" size="60" type="file" accept=".xlsx" /></span>
        </td>
    </tr>
</table>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>