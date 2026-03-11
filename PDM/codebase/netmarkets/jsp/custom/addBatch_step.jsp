<%@ page import="ext.casc.constants.Constants"%>
<%@ page import="com.ptc.windchill.principal.org.OrganizationCommands"%>
<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request"/>
<jsp:useBean id="localeBean" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>
<jsp:useBean id="sessionBean" class="com.ptc.netmarkets.util.beans.NmSessionBean" scope="session"/>
<jsp:useBean id="linkBean" class="com.ptc.netmarkets.util.beans.NmLinkBean" scope="request"/>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>


<table>
  <tr>
    <td align="right"><%=Constants.PRODUCT_MSG_PLEASEINPUTBATCH %> </td>
    <td align="left"><input type="text" name="batchName" id="batchName"/> </td>
  </tr>
</table>
