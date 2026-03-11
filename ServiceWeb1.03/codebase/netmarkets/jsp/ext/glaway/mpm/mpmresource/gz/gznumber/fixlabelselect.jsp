<%@page language="java" session="true" pageEncoding="GBK"%>
<%@page import="com.ptc.netmarkets.util.table.NmDefaultHTMLTable"%>
<%@page import="com.glaway.mpm.mvc.builders.mpmresource.gz.gznumber.GZNumberTablesBuilder"%>

<%@page import="java.util.ArrayList"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.bean.PropertiesBean"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.rule.GenerationRuleHelper"%>
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

<script language="javascript">
	function mySubmit(){
		this.document.mainform.action="netmarkets/jsp/glaway/mpm/mpmresource/gz/gznumber/processRequestBean.jsp";
		this.document.mainform.submit();
	}
</script>

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="wiz">
  <tr>
    <td width="1" colspan="2" class="wizCnr"></td>
    <td  nowrap="nowrap" class="wizBdr"><img src="netmarkets/images/sp.gif" /></td>
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
    <td height="38" colspan="7" class="pageHeader"><img src="netmarkets/images/sp.gif" height="1" /><font class="wizardtitlefont">&nbsp;其它规则定义 &nbsp;</font></td>
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
	request.setAttribute("useJSCA","false");

	String parentClassPath = request.getParameter("parentPath");
	if ((parentClassPath == null) || (parentClassPath.trim().equals(""))) parentClassPath = "Root";
	
	String fullClassPath = request.getParameter("fullPath");
	if ((fullClassPath == null) || (fullClassPath.trim().equals(""))) fullClassPath = "Root";
	
	String gzNumber = request.getParameter("gzNumber");
	System.out.print("------------------------1----"+gzNumber);
	if (gzNumber == null) gzNumber = "";

	GZNumberTablesBuilder gzNumberTable = new GZNumberTablesBuilder();
	
	PropertiesBean pb = new PropertiesBean();
	ArrayList aLabelDef = GenerationRuleHelper.getFixLabelDefinitions(pb);
	
	for(int i=0; i<aLabelDef.size(); i++){
		String strLabelDef = (String)aLabelDef.get(i);
		NmDefaultHTMLTable table = gzNumberTable.getFixLabelDefinitionTable(pb,strLabelDef);
		
		if (table != null) {
		   	modelBean.setModel(table);
		   	modelBean.setTableID(strLabelDef);
		   	NmTableRenderer.draw( modelBean, objectBean, sessionBean, localeBean, urlFactoryBean, 
		         actionBean, stringBean, linkBean, nmcontext, checkBoxBean, textBoxBean,  
		         radioButtonBean, textAreaBean, comboBoxBean, dateBean, out, request, response);
		   	modelBean.setModel(null);
		}
	}
%>
<input type="hidden" name="parentPath" id="parentPath" value="<%=parentClassPath%>" />
<input type="hidden" name="fullPath" id="fullPath" value="<%=fullClassPath%>" />
<input type="hidden" name="gzNumber" id="gzNumber" value="<%=gzNumber%>" />

<% 
	if(aLabelDef.size() == 0){
%>
	<script language="javascript">mySubmit();</script>
<%
	}
%>

</td>
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
      <input name="Submit" type="button" class="wizBtn" value="提交" onclick="mySubmit();" />
    </div></td>
  </tr>
</table>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>

