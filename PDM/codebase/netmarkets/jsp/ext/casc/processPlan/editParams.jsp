<%@page import="ext.casc.mpm.process.GLProcessParamDefinition"%>
<%@page import="ext.casc.mpm.GyCsServerHelper"%>
<%@ page language="java" session="true" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>

<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>
<%@ page import="ext.casc.mpm.process.ProcessUtil" %>
<%@ page import="ext.casc.mpm.process.GLProcessParamValues" %>
<%@ page import="ext.casc.mpm.process.GLProcessParams" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="wt.httpgw.URLFactory" %>
<LINK REL=stylesheet HREF="netmarkets/css/windchill-base.css" TYPE="text/css">
<LINK REL=stylesheet id="theme0" HREF="netmarkets/themes/windchill/xtheme-windchill.css" TYPE="text/css">
<%
    URLFactory urlFactory = new URLFactory();
	String templateId = request.getParameter("templateId");
	String gyParamNumber = request.getParameter("gyParamNumber");
	String paramValue = request.getParameter("paramValue");
	String partNumber = request.getParameter("partNumber");
	templateId = java.net.URLDecoder.decode(templateId,"UTF-8");
	gyParamNumber = java.net.URLDecoder.decode(gyParamNumber,"UTF-8");
	paramValue = java.net.URLDecoder.decode(paramValue,"UTF-8");
	GLProcessParams df = GyCsServerHelper.getProcessParamDefinition(gyParamNumber);
	String gyParamName = gyParamNumber;
	if(df!=null){
		gyParamName=df.getGyName();
	}
	String pv = "";
	String gongChengZhi = "";
	String shangPianCha = "";
	String xiaPianCha = "";
	String fuHao = "";
	String jiZhun1 = "";
	String jiZhun2 = "";
	String jiZhun3 = "";
	if(paramValue!=null&&paramValue.contains(";")){
		String[] ss = paramValue.split(";");
		if(ss.length>0){
			if(!ss[0].contains(":")){
				pv = ss[0];
			}
		}
		for(String s:ss){
			if(s.contains(":")){
				String[] vv = s.split(":");
				if(vv.length>=2){
					if("公称值".equals(vv[0])){
						gongChengZhi = vv[1];
					}else if("上偏差".equals(vv[0])){
						shangPianCha = vv[1];
					}else if("下偏差".equals(vv[0])){
						xiaPianCha = vv[1];
					}else if("符号".equals(vv[0])){
						fuHao = vv[1];
					}else if("基准1".equals(vv[0])){
						jiZhun1 = vv[1];
					}else if("基准2".equals(vv[0])){
						jiZhun2 = vv[1];
					}else if("基准3".equals(vv[0])){
						jiZhun3 = vv[1];
					}
				}
			}
		}
	}else{
		pv = paramValue;
	}


%>

<script type="text/javascript">
	function saveParmas(){
		debugger;
		var paramValue = document.getElementById("paramValue").value;
		var totalValue = paramValue;
		var gongChengZhi = document.getElementById("gongChengZhi").value;
		var shangPianCha = document.getElementById("shangPianCha").value;
		var xiaPianCha = document.getElementById("xiaPianCha").value;
		var fuHao = document.getElementById("fuHao").value;
		var jiZhun1 = document.getElementById("jiZhun1").value;
		var jiZhun2 = document.getElementById("jiZhun2").value;
		var jiZhun3 = document.getElementById("jiZhun3").value;
		var parentDoc = window.opener.document;
		var paramId = "PRE_<%=templateId%>_<%=gyParamNumber%>_<%=partNumber%>";
		if(gongChengZhi){
			if(gongChengZhi.trim()==""){
			}else{
				totalValue = totalValue+";"+"公称值:"+gongChengZhi;
			}
		}
		if(shangPianCha){
			if(shangPianCha.trim()==""){
			}else{
				totalValue = totalValue+";"+"上偏差:"+shangPianCha;
			}
		}
		if(xiaPianCha){
			if(xiaPianCha.trim()==""){
			}else{
				totalValue = totalValue+";"+"下偏差:"+xiaPianCha;
			}
		}

		if(fuHao){
			if(fuHao.trim()==""){
			}else{
				totalValue = totalValue+";"+"符号:"+fuHao;
			}
		}
		if(jiZhun1){
			if(jiZhun1.trim()==""){
			}else{
				totalValue = totalValue+";"+"基准1:"+jiZhun1;
			}
		}
		if(jiZhun2){
			if(jiZhun2.trim()==""){
			}else{
				totalValue = totalValue+";"+"基准2:"+jiZhun2;
			}
		}
		if(jiZhun3){
			if(jiZhun3.trim()==""){
			}else{
				totalValue = totalValue+";"+"基准3:"+jiZhun3;
			}
		}

		parentDoc.getElementById(paramId).value = totalValue;
		window.close();

	}

</script>

<title>编辑参数</title>
 
<div class="wizardPanel-tbar wizardPanel-tbar-noheader" id="ext-gen13"><div id="titleBar" class="x-toolbar x-small-editor x-panel-header wizard-title-text x-toolbar-layout-ct"><table cellspacing="0" class="x-toolbar-ct"><tbody><tr><td class="x-toolbar-left" align="left"><table cellspacing="0"><tbody><tr class="x-toolbar-left-row"><td class="x-toolbar-cell" id="ext-gen42"><div class="xtb-text" id="ext-comp-1002">设置<%=gyParamName%></div></td></tr></tbody></table></td><td class="x-toolbar-right" align="right"><table cellspacing="0" class="x-toolbar-right-ct"><tbody><tr><td><table cellspacing="0"><tbody><tr class="x-toolbar-right-row"></tr></tbody></table></td><td><table cellspacing="0"><tbody><tr class="x-toolbar-extras-row"></tr></tbody></table></td></tr></tbody></table></td></tr></tbody></table></div></div>
<h2><font color="#FF0000">编辑【<%=gyParamName%>】参数</font> </h2>
<h2><font color="#70DB93">旧参数值：【<%=paramValue%>】</font> </h2>

<br>
<br>

<table cellSpacing="2">
    <tr>
        <td><w:label value="参数值："/></td>
        <td><input type="text" style="width: 200px; height: 20px;"  name="paramValue" id="paramValue" value="<%=pv %>"></td>
    </tr>

    <tr>
        <td><w:label value="公称值："/></td>
        <td><input type="text" style="width: 200px; height: 20px;"  name="gongChengZhi" id="gongChengZhi" value="<%=gongChengZhi %>"></td>
    </tr>

    <tr>
        <td><w:label value="上偏差："/></td>
        <td><input type="text" style="width: 200px; height: 20px;"  name="shangPianCha" id="shangPianCha" value="<%=shangPianCha %>"></td>
    </tr>

    <tr>
        <td><w:label value="下偏差："/></td>
        <td><input type="text" style="width: 200px; height: 20px;"  name="xiaPianCha" id="xiaPianCha" value="<%=xiaPianCha %>"></td>
    </tr>

    <tr>
        <td><w:label value="符号："/></td>
        <td><input type="text" style="width: 200px; height: 30px;"  name="fuHao" id="fuHao" value="<%=fuHao %>"></td>
    </tr>

    <tr>
        <td><w:label value="基准1："/></td>
        <td><input type="text" style="width: 200px; height: 30px;"  name="jiZhun1" id="jiZhun1" value="<%=jiZhun1 %>"></td>
    </tr>

    <tr>
        <td><w:label value="基准2："/></td>
        <td><input type="text" style="width: 200px; height: 30px;"  name="jiZhun2" id="jiZhun2" value="<%=jiZhun2 %>"></td>
    </tr>

    <tr>
        <td><w:label value="基准3"/></td>
        <td><input type="text"  style="width: 200px; height: 30px;" name="jiZhun3" id="jiZhun3" value="<%=jiZhun3 %>"></td>
    </tr>


	<tr>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align=center><h4><input type="button" value="保存参数" onclick="saveParmas();"/></h4></td>
	</tr>
</table>


<%@ include file="/netmarkets/jsp/util/end.jspf" %>