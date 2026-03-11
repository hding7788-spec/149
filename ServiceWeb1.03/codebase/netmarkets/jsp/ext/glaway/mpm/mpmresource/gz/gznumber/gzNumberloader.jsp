
<%@page import="com.glaway.mpm.mpmresource.gznumber.number.AdministrationHelper"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<jca:tabToHighlight actionName="numberGenloader" objectType="numbermanagement" />
<%@page language="java" session="true" pageEncoding="GBK"%>

<%@ page import="wt.util.WTProperties"%>
<%@include file="/netmarkets/jsp/util/begin.jspf"%>

<link href="netmarkets/jsp/ext/numbergen/css/nmstyles.css" rel="stylesheet" type="text/css">
<style type="text/css">
.STYLE2 {font-size: small} 
.STYLE3 {font-size: 14px}
</style>

<script language="javascript">
	function myNumberSubmit(){	
		var varNumFile = document.getElementById("numberFile");
		var form = this.document.mainform[0];

		if(varNumFile.value == ""){
			alert("请选择导入文件！");
			return false;
		}
		
		if(form.logBoard){
			form.logBoard.value="";
		}
		if(form.errorBoard){
			form.errorBoard.value="";
		}		

		form.action="netmarkets/jsp/glaway/mpm/mpmresource/gz/gznumber/processNumberLoader.jsp";
		form.encoding="multipart/form-data";
		form.target="_blank"
		form.method="post";
		form.submit();
	}
	
	function myClassSubmit(){
		var varClassFile = document.getElementById("classFile");
		var form = this.document.mainform[0];

		if(varClassFile.value == ""){
			alert("请选择导入文件！");
			return false;
		}

		form.action="netmarkets/jsp/glaway/mpm/mpmresource/gz/gznumber/processClassLoader.jsp";
		form.encoding="multipart/form-data";
		form.target="_blank"
		form.method="post";
		form.submit();
	}
	
	function showEnctype(){
		var x=document.getElementById("mainform");
		alert(x.enctype);
	}
</script>

<% 
	if(!AdministrationHelper.getUser().equals(AdministrationHelper.ADMINISTRATOR)){
%>
		<script>alert("对不起，您没有权限执行此操作！");</script>
		<script>history.go(-1);</script>
<%
	}
%>
<div class="fieldsetbody">
   <table width="100%" border="0" cellpadding="2" cellspacing="10" class="layoutTable100">
  	<tr>
		<td height="70" valign="middle" class="footer">
			<div align="left" class="STYLE2">&nbsp;&nbsp;&nbsp;&nbsp;<strong>导入工具</strong></div>
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
      <td width="25">&nbsp;</td>
      <td width="24" height="30"><span class="STYLE8">1.</span></td>
      <td class="title STYLE8">选择所要导入文件类型</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td height="30"><span class="STYLE8">2.</span></td>
      <td><span class="STYLE8">下载导入模板文件</span></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td height="30"><span class="STYLE8">3.</span></td>
      <td><span class="STYLE8">按照模板文件填写待导入数据</span></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td height="30"><span class="STYLE8">4.</span></td>
      <td><span class="STYLE8">点击“选择导入文件”按钮选择文件</span></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td height="30"><span class="STYLE8">5.</span></td>
      <td><span class="STYLE8">点击“导入”按钮开始导入工作</span></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td height="30"><span class="STYLE8">6.</span></td>
      <td><span class="STYLE8">察看导入日志，确定导入状态</span></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
  </table>
</div>

<fieldset><legend>&nbsp;&nbsp;&nbsp;&nbsp;工装类别定义导入模块</legend></fieldset>
<div class="fieldsetbody">
  <table width="100%" class="layoutTable100">
      <tr>
        <td width="24">&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td colspan="2">&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td height="30" class="STYLE8"><span class="STYLE9">文件类型</span></td>
        <td width="10%" class="STYLE8"><a href="" class="STYLE9">
          <select name="select">
            <option>Excel文件</option>
          </select>
        </a></td>
        <td class="STYLE8"><a href="netmarkets\jsp\glaway\mpm\mpmresource\gz\gznumber\loadTemplate\ClassLoadTemplate.xls" target="_blank" class="STYLE9">工装类别定义导入模板文件下载</a></td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td height="30" class="STYLE8"><span class="STYLE9">导入文件</span></td>
        <td colspan="2" class="STYLE8"><input name="classFile" type="file" class="tablehighlightrowbg " id="classFile" size="100" /></td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td height="30" class="STYLE8"><span class="STYLE9">操作</span></td>
        <td colspan="2" class="STYLE8"><input name="Submit2" type="button" onclick="myClassSubmit()" value="导入类别定义文件" /></td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td colspan="2">&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
    </table>
</div>


<fieldset><legend>&nbsp;&nbsp;&nbsp;&nbsp;工装编号导入模块</legend></fieldset>
<div class="fieldsetbody">
  <table width="100%" class="layoutTable100">
      <tr>
        <td width="24">&nbsp;</td>
        <td width="10%">&nbsp;</td>
        <td colspan="2">&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td class="STYLE8"><span class="STYLE9"></span></td>
        <td height="30" class="STYLE8"><span class="STYLE9">文件类型</span></td>
        <td width="10%" class="STYLE8"><a href="" class="STYLE9">
          <select name="select2">
            <option>Excel文件</option>
          </select>
        </a></td>
        <td class="STYLE8"><a href="netmarkets\jsp\glaway\mpm\mpmresource\gz\gznumber\loadTemplate\NumberLoadTemplate.xls" target="_blank" class="STYLE9">工装编号导入模板文件下载</a></td>
        <td class="STYLE8"><span class="STYLE9"></span></td>
      </tr>
      <tr>
        <td class="STYLE8"><span class="STYLE9"></span></td>
        <td height="30" class="STYLE8"><span class="STYLE9">导入文件</span></td>
        <td colspan="2" class="STYLE8"><input name="numberFile" type="file" class="tablehighlightrowbg " id="numberFile" size="100" /></td>
        <td class="STYLE8"><span class="STYLE9"></span></td>
      </tr>
      <tr>
        <td class="STYLE8"><span class="STYLE9"></span></td>
        <td height="30" class="STYLE8"><span class="STYLE9">操作</span></td>
        <td colspan="2" class="STYLE8"><input name="Submit2" type="button" onclick="myNumberSubmit();" value="导入编码文件" /></td>
        <td class="STYLE8"><span class="STYLE9"></span></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td colspan="2">&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
    </table>
</div>

<fieldset><legend>&nbsp;&nbsp;&nbsp;&nbsp;日志报告</legend></fieldset>
<div class="fieldsetbody">
  <table width="100%" border="0" class="layoutTable100">
    <tr>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td width="25" class="STYLE8"><span class="STYLE9"></span></td>
      	<td width="50%" class="STYLE8"><span class="STYLE9">导入日志</span></td>
        <td class="STYLE8"><span class="STYLE9">错误报告</span></td>
    </tr>
    <tr>
      <td class="STYLE8"><span class="STYLE9"></span></td>
      <td class="STYLE8"><span style="font-size: 14">
        <textarea name="logBoard" id="logBoard" cols="50" rows="10" style="width:90%" readonly="readonly"></textarea>
      </span></td>
      <td class="STYLE8"><span style="font-size: 14">
        <textarea name="errorBoard" id="errorBoard" cols="50" rows="10" style="width:90%" readonly="readonly"></textarea>
      </span></td>
    </tr>
  </table>
</div>

<br>
<%@include file="/netmarkets/jsp/util/end.jspf"%>