<%@page language="java" session="true" pageEncoding="GBK"%>
<%@page import="com.ptc.netmarkets.util.table.NmDefaultHTMLTable"%>
<%@page import="com.glaway.mpm.mvc.builders.mpmresource.gz.gznumber.GZNumberTablesBuilder"%>
<%@include file="/netmarkets/jsp/util/beginPopup.jspf" %>

<link href="css/nmstyles.css" rel="stylesheet" type="text/css">
<style type="text/css">
<!--
body {
	background-color: #D6D7C6;
}
table.gray_border{
	border:1px solid #A5AA9C;
}
.STYLE1 {color: #FFFFFF}
-->
</style>
<%
	String uri = request.getRequestURI().replaceAll("Windchill/","");
String currentPageURL = uri;

String parentClassPath = request.getParameter("parentPath");
if ((parentClassPath == null) || (parentClassPath.trim().equals(""))) parentClassPath = "Root";

String fullClassPath = request.getParameter("fullPath");
if ((fullClassPath == null) || (fullClassPath.trim().equals(""))) fullClassPath = "Root";

String gzNumber = request.getParameter("gzNumber");
if (gzNumber == null) gzNumber = "";
%>
<script language="javascript">
	function mySubmit(){
		this.document.mainform.action="netmarkets/jsp/glaway/mpm/mpmresource/gz/gznumber/processSearchClassSelect.jsp?gzNumber="+"<%=gzNumber%>";
		this.document.mainform.submit();
	}
</script>

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="wiz">
  <tr>
    <td width="1" colspan="2" class="wizCnr"></td>
    <td nowrap="nowrap" class="wizBdr"><img src="netmarkets/images/sp.gif" /></td>
    <td colspan="2" nowrap="nowrap" class="wizBdr"></td>
    <td width="1" nowrap="nowrap" class="wizBdr"></td>
    <td width="9" nowrap="nowrap" class="wizBdr"></td>
    <td width="1" colspan="2" class="wizCnr"></td>
  </tr>
  <tr>
    <td class="wizCnr"><img src="netmarkets/images/sp.gif" /></td>
    <td class="wizBdr"></td>
    <td class="wizCnr" nowrap="nowrap"></td>
    <td colspan="2" nowrap="nowrap" class="wizCnr"></td>
    <td nowrap="nowrap" class="wizCnr"></td>
    <td nowrap="nowrap" class="wizCnr"></td>
    <td class="wizBdr"></td>
    <td class="wizCnr"></td>
  </tr>
  <tr>
    <td class="wizBdr"><img src="netmarkets/images/sp.gif" height="1" /></td>
    <td height="38" colspan="7" class="pageHeader"><img src="netmarkets/images/sp.gif" height="1" /><font class="wizardtitlefont">&nbsp;选择类别项 &nbsp;</font></td>
    <td height="38" class="wizCnr"></td>
  </tr>
  <tr>
    <td class="wizBdr" rowspan="4"><img src="netmarkets/images/sp.gif" /></td>
    <td class="wizBdr" colspan="3"></td>
    <td class="wizBdr" ><img src="netmarkets/images/sp.gif" width="100" height="1" /></td>
    <td class="wizBdr" colspan="3"></td>
    <td class="wizBdr" rowspan="4"></td>
  </tr>
  <tr class="wizTblBdy">
    <td rowspan="3"><img src="netmarkets/images/sp.gif" /></td>
    <td rowspan="2" valign="top">     </td>
    <td height="18" colspan="3" valign="top">&nbsp;</td>
    <td valign="top">&nbsp;</td>
    <td></td>
  </tr>
  <tr class="wizTblBdy">
    <td colspan="3" valign="top">
<%
	GZNumberTablesBuilder gzNumberTable  = new GZNumberTablesBuilder();
	
	NmDefaultHTMLTable table = gzNumberTable.getClassificationTable2(parentClassPath, fullClassPath, currentPageURL, gzNumber);
	
	if (table != null) {
		modelBean.setModel(table);
	   	modelBean.setTableID(parentClassPath);
		NmTableRenderer.draw( modelBean, objectBean, sessionBean, localeBean, urlFactoryBean, 
	actionBean, stringBean, linkBean, nmcontext, checkBoxBean, textBoxBean,  
		    radioButtonBean, textAreaBean, comboBoxBean, dateBean, out, request, response);
		modelBean.setModel(null);
	}else{
%>
		<script language="javascript">history.go(-1)</script>
<%
	}
%>	</td>
    <td valign="top">&nbsp;</td>
    <td></td>
  </tr>
  <tr class="wizReqRow">
    <td height="72" colspan="4"><img src="netmarkets/images/sp.gif" /></td>
    <td>&nbsp;</td>
    <td></td>
  </tr>
  <tr>
    <td height="2"  class="wizCnr"><img src="netmarkets/images/sp.gif" /></td>
    <td class="wizInCnr"></td>
    <td colspan="5"></td>
    <td class="wizInCnr"></td>
    <td class="wizCnr"></td>
  </tr>
  <tr>
    <td class="wizCnr" colspan="2"><img src="netmarkets/images/sp.gif" /></td>
    <td class="wizBdr" colspan="5"></td>
    <td class="wizCnr" colspan="2"></td>
  </tr>
</table>
<table width="100%" border="0">
  <tr>
    <td>&nbsp;</td>
    <td><div align="right">
      <input name="Submit" type="button" class="wizBtn" value="确认" onclick="mySubmit();" />
    </div></td>
  </tr>
</table>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>