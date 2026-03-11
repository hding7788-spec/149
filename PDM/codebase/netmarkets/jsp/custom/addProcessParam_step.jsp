<%@ page import="ext.casc.constants.Constants"%>
<%@ page import="com.ptc.windchill.principal.org.OrganizationCommands"%>
<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request"/>
<jsp:useBean id="localeBean" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>
<jsp:useBean id="sessionBean" class="com.ptc.netmarkets.util.beans.NmSessionBean" scope="session"/>
<jsp:useBean id="linkBean" class="com.ptc.netmarkets.util.beans.NmLinkBean" scope="request"/>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@page language="java" pageEncoding="UTF-8"
        contentType="text/html; charset=UTF-8"%>
<%
  String contextPath = request.getContextPath();

%>
<script>
  function parameterCategoryChange(obj){
      if("知识参数"==obj.value){
        document.getElementById("trid_knowledgeInferencePara").style.display = "";
        document.getElementById("trid_knowledgeOutputPara").style.display = "";
        document.getElementById("trid_enumValues").style.display = "none";
        document.getElementById("enumValues").value = "";
        document.getElementById("trid_knowledgeType").style.display = "";



      }else if("基础参数"==obj.value){
        document.getElementById("trid_knowledgeInferencePara").style.display = "none";
        document.getElementById("trid_knowledgeOutputPara").style.display = "none";
        document.getElementById("trid_enumValues").style.display = "none";
        document.getElementById("trid_knowledgeType").style.display = "none";

        document.getElementById("knowledgeInferencePara").value = "";
        document.getElementById("knowledgeOutputPara").value = "";
        document.getElementById("enumValues").value = "";
        document.getElementById("knowledgeType").value = "";



      }else if("枚举参数"==obj.value){
        document.getElementById("trid_knowledgeInferencePara").style.display = "";
        document.getElementById("trid_knowledgeOutputPara").style.display = "";
        document.getElementById("trid_enumValues").style.display = "";
        document.getElementById("trid_knowledgeType").style.display = "none";

        document.getElementById("knowledgeInferencePara").value = "";
        document.getElementById("knowledgeOutputPara").value = "";
        document.getElementById("knowledgeType").value = "";

      }
  }

  function knowledgeTypeChange(obj){
    if("配套表"==obj.value){
      document.getElementById("trid_isCanZhuang").style.display = "";
    }else{
      document.getElementById("trid_isCanZhuang").style.display = "none";
      document.getElementById("isCanZhuang").value = "";
    }
  }
</script>
<table>
  <tr>
    <td align="right">工艺参数名称：</td>
    <td align="left"><input type="text" name="gyName" id="gyName"/> </td>
  </tr>
  <tr>
    <td align="right">工艺参数类别：</td>
    <td align="left">
      <select name="parameterCategory" id="parameterCategory" onchange="parameterCategoryChange(this);">
        <option value="基础参数">基础参数</option>
        <option value="枚举参数">枚举参数</option>
        <option value="知识参数">知识参数</option>
      </select>
    </td>
  </tr>
  <tr>
    <td align="right">参数来源：</td>
    <td align="left">
      <select name="source" id="source" >
        <option value="设计">设计</option>
        <option value="工艺">工艺</option>
      </select>
    </td>
  </tr>
  <tr  id="trid_knowledgeType"   style="display: none;">
    <td align="right">知识参数类别：</td>
    <td align="left">
      <select name="knowledgeType" id="knowledgeType" onchange="knowledgeTypeChange(this);">
        <option value="普通知识">普通知识</option>
        <option value="配套表">配套表</option>
        <option value="工艺模板映射">工艺模板映射</option>
        <option value="工序模板映射">工序模板映射</option>
        <option value="工装">工装</option>
      </select>
    </td>
  </tr>
  <tr  id="trid_isCanZhuang"  style="display: none;">
    <td align="right">是否参装：</td>
    <td align="left">
      <select name="isCanZhuang" id="isCanZhuang">
        <option value="是">是</option>
        <option value="否">否</option>
        <option value=""></option>
      </select>
    </td>
  </tr>
  <tr>
    <td align="right">默认单位：</td>
    <td align="left"><input type="text" name="unit" id="unit"/> </td>
  </tr>
  <tr>
    <td align="right">专业：</td>
    <td align="left">
      <select name="processCategory" id="processCategory">
        <% for(String typeDisplay :ext.casc.processPlan.Constants.TECHNICSTYPES_DISPLAY){ %>
            <option value="<%=typeDisplay%>"><%=typeDisplay%></option>
        <% }%>
      </select>
    </td>

  <tr   id="trid_enumValues"  style="display: none;">
    <td align="right">合法值列表：</td>
    <td align="left"><input type="text" name="enumValues" id="enumValues"/> </td>
  </tr>
  <tr id="trid_knowledgeInferencePara" style="display: none;">
    <td align="right">知识推理参数：</td>
    <td align="left"><textarea rows="3" cols="50"  name="knowledgeInferencePara" id="knowledgeInferencePara" readonly></textarea> </td>
    <td><a href='javascript:void(0)' onclick='window.open( "<%=contextPath%>/netmarkets/jsp/ext/casc/processParams/selectProcessParams.jsp?parentId=knowledgeInferencePara","_blank","location=no,resizable=yes,top=0,toolbar=no,height=800")'>
      <img title="搜索" alt="搜索" src="<%=contextPath%>/netmarkets/images/search.gif" hspace="0" vspace="0" align="top" border="0"></a>
    </td>
    </td>
    <td><input type="button" name="clearParams1" value="清空" class="x-btn-text" onclick="document.getElementById('knowledgeInferencePara').value='';"/>&nbsp;&nbsp;
    </td>
  </tr>
  <tr  id="trid_knowledgeOutputPara" style="display: none;">
    <td align="right">知识输出参数：</td>
    <td align="left"><textarea rows="3" cols="50" name="knowledgeOutputPara" id="knowledgeOutputPara" readonly></textarea> </td>
    <td><a href='javascript:void(0)' onclick='window.open( "<%=contextPath%>/netmarkets/jsp/ext/casc/processParams/selectProcessParams.jsp?parentId=knowledgeOutputPara","_blank","location=no,resizable=yes,top=0,toolbar=no,height=800")'>
      <img title="搜索" alt="搜索" src="<%=contextPath%>/netmarkets/images/search.gif" hspace="0" vspace="0" align="top" border="0"></a>
    </td>
    <td><input type="button" name="clearParams2" value="清空" class="x-btn-text" onclick="document.getElementById('knowledgeOutputPara').value='';"/>&nbsp;&nbsp;
    </td>
  </tr>
  <tr>
    <td align="right">输出规则：</td>
    <td align="left"><textarea rows="4" cols="50"  name="outputRules" id="outputRules"></textarea> </td>
  </tr>
  <tr  id="trid_isOnlyValue"  >
    <td align="right">只输出参数值：</td>
    <td align="left">
      <select name="isOnlyValue" id="isOnlyValue">
        <option value=""></option>
        <option value="是">是</option>
        <option value="否">否</option>
      </select>
    </td>
  </tr>
</table>
