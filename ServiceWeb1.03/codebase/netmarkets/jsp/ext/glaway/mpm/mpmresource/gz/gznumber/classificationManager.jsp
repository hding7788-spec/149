
<%@page import="com.glaway.mpm.mpmresource.gznumber.number.AdministrationHelper"%><%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@page language="java" session="true" pageEncoding="GBK"%>

<jca:tabToHighlight actionName="classificationManager" objectType="numbermanagement" />
<%@page import="com.ptc.netmarkets.util.table.NmDefaultHTMLTable"%>
<%@page import="com.glaway.mpm.mvc.builders.mpmresource.gz.gznumber.GZNumberTablesBuilder"%>
<%@include file="/netmarkets/jsp/util/begin.jspf" %>
<link href="netmarkets/jsp/ext/numbergen/css/nmstyles.css" rel="stylesheet" type="text/css">
<style type="text/css">
.STYLE2 {font-size: small} 
.STYLE3 {font-size: 14px}
</style>

<div class="fieldsetbody">
  <table width="100%" border="0" cellpadding="2" cellspacing="10" class="layoutTable100">
  	<tr>
		<td height="70" valign="middle" class="footer">
			<div align="left" class="STYLE2">&nbsp;&nbsp;&nbsp;&nbsp;<strong>分类管理</strong></div>
		</td>
	</tr>
  </table>
</div>
<br>
<fieldset><legend>&nbsp;&nbsp;&nbsp;&nbsp;操作说明</legend></fieldset>
<div class="fieldsetbody">
  <table width="100%" class="layoutTable">
    <tr>
      <td width="25">&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td width="25"><span class="STYLE3"></span></td>
      <td width="24" height="30"><span class="STYLE3">1.</span></td>
      <td width="1466"><span class="STYLE3">选择需要修改的类别</span></td>
    </tr>
    <tr>
      <td><span class="STYLE3"></span></td>
      <td height="30"><span class="STYLE3">2.</span></td>
      <td><span class="STYLE3">点击“增加子类别”进行类别添加操作</span></td>
    </tr>
    <tr>
      <td><span class="STYLE3"></span></td>
      <td height="30"><span class="STYLE3">3.</span></td>
      <td><span class="STYLE3">点击“修改类别”进行类别名及类别值修改</span></td>
    </tr>
    <tr>
      <td><span class="STYLE3"></span></td>
      <td height="30"><span class="STYLE3">4.</span></td>
      <td><span class="STYLE3">点击“删除类别”按钮进行类别及其子类别的删除</span></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
  </table>
</div>

<fieldset><legend>&nbsp;&nbsp;&nbsp;&nbsp;类别选择器</legend></fieldset>
<div class="fieldsetbody">
  <table width="100%" border="0" class="layoutTable100">
    <tr>
      <td>&nbsp;</td>
      	<td>
			<%
				if(!AdministrationHelper.getUser().equals(AdministrationHelper.ADMINISTRATOR)){
			%>
					<script>alert("对不起，您没有权限执行此操作！");</script>
					<script>history.go(-1);</script>
			<%
				}
				
					GZNumberTablesBuilder gzNumberTable = new GZNumberTablesBuilder();
				
					request.setAttribute("useJSCA","false");
				
					String uri = request.getRequestURI().replaceAll("Windchill/","");
					String currentPageURL = uri;
					
					String parentClassPath = request.getParameter("parentPath");
					if ((parentClassPath == null) || (parentClassPath.trim().equals(""))) parentClassPath = "Root";
					
					String gzNumber = request.getParameter("gzNumber");
					if (gzNumber == null) gzNumber = "";
				
					NmDefaultHTMLTable table = gzNumberTable.getClassificationManagementTable(parentClassPath, currentPageURL, gzNumber);
					
					if (table != null) {
						   modelBean.setModel(table);
						   modelBean.setTableID(parentClassPath);
						   NmTableRenderer.draw( modelBean, objectBean, sessionBean, localeBean, urlFactoryBean, 
								 actionBean, stringBean, linkBean, nmcontext, checkBoxBean, textBoxBean,  
								 radioButtonBean, textAreaBean, comboBoxBean, dateBean, out, request, response);
						   modelBean.setModel(null);
					}else{
			%>
					<script>history.go(-1);</script>
<%
				}
			%>	
		</td>
    </tr>
  </table>
</div>
<br>




<%@include file="/netmarkets/jsp/util/end.jspf"%>