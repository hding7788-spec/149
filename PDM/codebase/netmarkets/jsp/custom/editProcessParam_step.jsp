<%@page import="ext.casc.mpm.process.GLProcessParams"%>
<%@page import="ext.casc.mpm.GyCsServerHelper"%>
<%@page import="java.util.ArrayList"%>
<%@ page import="ext.casc.constants.Constants"%>
<%@ page import="com.ptc.windchill.principal.org.OrganizationCommands"%>
<%@ page import="com.ptc.netmarkets.model.NmOid" %>
<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean" %>
<%@ page import="ext.casc.util.Tools" %>
<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request"/>
<jsp:useBean id="localeBean" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>
<jsp:useBean id="sessionBean" class="com.ptc.netmarkets.util.beans.NmSessionBean" scope="session"/>
<jsp:useBean id="linkBean" class="com.ptc.netmarkets.util.beans.NmLinkBean" scope="request"/>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@page language="java" pageEncoding="UTF-8"
        contentType="text/html; charset=UTF-8"%>
<%

  String soid = request.getParameter("soid");//processParams$processParamsManage$OR:wt.inf.library.WTLibrary:198039$JCCS-000023!*
  String[] ss = soid.split("\\$");
  String tempParamNumber =  ss[ss.length-1];
  String paramNumber = tempParamNumber.substring(0, tempParamNumber.length()-2);//processParams$processParamsManage$OR:wt.inf.library.WTLibrary:198039$JCCS-000023>
  GLProcessParams params =  GyCsServerHelper.getProcessParamDefinition(paramNumber,false);
  String contextPath = request.getContextPath();

%>
<script>
  function parameterCategoryChange(obj){
      if("知识参数"==obj.value){
        document.getElementById("trid_knowledgeInferencePara").style.display = "";
        document.getElementById("trid_knowledgeOutputPara").style.display = "";
        document.getElementById("trid_enumValues").style.display = "none";
        document.getElementById("enumValues").value = "";

      }else if("基础参数"==obj.value){
        document.getElementById("trid_knowledgeInferencePara").style.display = "none";
        document.getElementById("trid_knowledgeOutputPara").style.display = "none";
        document.getElementById("trid_enumValues").style.display = "none";
        document.getElementById("knowledgeInferencePara").value = "";
        document.getElementById("knowledgeOutputPara").value = "";
        document.getElementById("enumValues").value = "";
        document.getElementById("knowledgeType").value = "";



      }else if("枚举参数"==obj.value){
        document.getElementById("trid_knowledgeInferencePara").style.display = "";
        document.getElementById("trid_knowledgeOutputPara").style.display = "";
        document.getElementById("trid_enumValues").style.display = "";

        //document.getElementById("knowledgeInferencePara").value = "";
        //document.getElementById("knowledgeOutputPara").value = "";
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
    <td align="right">工艺参数编号：</td>
    <td align="left"><input type="text" name="gyNumber" id="number" value="<%=paramNumber%>" readonly/> </td>
  </tr>
  <tr>
    <td align="right">工艺参数名称：</td>
    <td align="left"><input type="text" name="gyName" id="gyName" value="<%=params.getGyName()%>" /> </td>
  </tr>
  <tr>
    <td align="right">工艺参数类别：</td>
    <td align="left"><input type="text" name="parameterCategory" id="parameterCategory" value="<%=params.getParameterCategory()%>" readonly/> </td>

  </tr>
  <tr>
    <td align="right">参数来源：</td>
    <td align="left">
      <select name="source" id="source">
        <%if("设计".equals(params.getSource())||Tools.isNull(params.getSource())){%>
        <option value="设计" selected>设计</option>
        <option value="工艺">工艺</option>
        <%
        }else{%>
        <option value="设计">设计</option>
        <option value="工艺" selected>工艺</option>
        <%}
        %>

      </select>
    </td>

  </tr>
  <%if("知识参数".equals(params.getParameterCategory())) {%>
  <tr  id="trid_knowledgeType" >
    <td align="right">知识参数类别：</td>
    <td align="left">
      <select name="knowledgeType" id="knowledgeType"  onchange="knowledgeTypeChange(this);">
        <%if("普通知识".equals(params.getKnowledgeType())|| Tools.isNull(params.getKnowledgeType())){
          %>
        <option value="普通知识" selected>普通知识</option>
        <%
        }else{
          %>
          <option value="普通知识">普通知识</option>
        <%
        }
        %>
        <%if("配套表".equals(params.getKnowledgeType())){
        %>
        <option value="配套表" selected>配套表</option>
        <%
        }else{
        %>
        <option value="配套表">配套表</option>
        <%
          }
        %>
        <%if("工艺模板映射".equals(params.getKnowledgeType())){
        %>
        <option value="工艺模板映射" selected>工艺模板映射</option>
        <%
        }else{
        %>
        <option value="工艺模板映射">工艺模板映射</option>
        <%
          }
        %>
        <%if("工序模板映射".equals(params.getKnowledgeType())){
        %>
        <option value="工序模板映射" selected>工序模板映射</option>
        <%
        }else{
        %>
        <option value="工序模板映射">工序模板映射</option>
        <%
          }
        %>
        <%if("工装".equals(params.getKnowledgeType())){
        %>
        <option value="工装" selected>工装</option>
        <%
        }else{
        %>
        <option value="工装">工装</option>
        <%
          }
        %>

      </select>
    </td>
  </tr>
  <%} %>
  <%if("配套表".equals(params.getKnowledgeType())){%>
  <tr  id="trid_isCanZhuang" >
    <td align="right">是否参装：</td>
    <td align="left">
      <select name="isCanZhuang" id="isCanZhuang">
        <%if("否".equals(params.getIsCanZhuang())){%>
          <option value="否" selected>否</option>
          <option value="是">是</option>
          <option value=""></option>
        <%
        }else{%>
          <option value="是">是</option>
          <option value="否">否</option>
          <option value=""></option>
        <%}
        %>

      </select>
    </td>
  </tr>
  <%} %>
  <tr>
    <td align="right">默认单位：</td>
    <td align="left"><input type="text" name="unit" id="unit" value="<%=params.getUnit()%>"/> </td>
  </tr>
  <tr>
    <td align="right">专业：</td>
    <td align="left">

      <select name="processCategory" id="processCategory">
        <%for(String typeDisplay :ext.casc.processPlan.Constants.TECHNICSTYPES_DISPLAY){ %>
            <%if(typeDisplay.equals(params.getProcessCategory())){%>
                 <option value="<%=typeDisplay%>"  selected><%=typeDisplay%></option>
            <%
              }else{%>
                 <option value="<%=typeDisplay%>"><%=typeDisplay%></option>
              <%}
              %>
        <%}%>
      </select>
    </td>
<%if("枚举参数".equals(params.getParameterCategory())) {%>
  <tr   id="trid_enumValues"  style="">
    <td align="right">合法值列表：</td>
    <td align="left"><input type="text" name="enumValues" id="enumValues" value="<%=params.getEnumValues()%>"/> </td>
  </tr>
  <%} %>
  <%if("知识参数".equals(params.getParameterCategory())||"枚举参数".equals(params.getParameterCategory())) {%>
  <tr id="trid_knowledgeInferencePara" >
    <td align="right">知识推理参数：</td>
    <td align="left"><textarea rows="3" cols="50"  name="knowledgeInferencePara" id="knowledgeInferencePara" readonly><%=params.getKnowledgeInferencePara()==null? "":params.getKnowledgeInferencePara()%></textarea> </td>
    <td><a href='javascript:void(0)' onclick='window.open( "<%=contextPath%>/netmarkets/jsp/ext/casc/processParams/selectProcessParams.jsp?parentId=knowledgeInferencePara","_blank","location=no,resizable=yes,top=0,toolbar=no,height=800")'>
      <img title="搜索" alt="搜索" src="<%=contextPath%>/netmarkets/images/search.gif" hspace="0" vspace="0" align="top" border="0"></a>
    </td>
    <td><input type="button" name="clearParams1" value="清空" class="x-btn-text" onclick="document.getElementById('knowledgeInferencePara').value='';"/>&nbsp;&nbsp;

  </tr>
  <tr  id="trid_knowledgeOutputPara">
    <td align="right">知识输出参数：</td>
    <td align="left"><textarea rows="3" cols="50" name="knowledgeOutputPara" id="knowledgeOutputPara" readonly><%=Tools.isTrimNull(params.getKnowledgeOutputPara())? "":params.getKnowledgeOutputPara()%></textarea> </td>
    <td><a href='javascript:void(0)' onclick='window.open("<%=contextPath%>/netmarkets/jsp/ext/casc/processParams/selectProcessParams.jsp?parentId=knowledgeOutputPara","_blank","location=no,resizable=yes,top=0,toolbar=no,height=800")'>
      <img title="搜索" alt="搜索" src="<%=contextPath%>/netmarkets/images/search.gif" hspace="0" vspace="0" align="top" border="0"></a>
    </td>
    <td><input type="button" name="clearParams2" value="清空" class="x-btn-text" onclick="document.getElementById('knowledgeOutputPara').value='';"/>&nbsp;&nbsp;

  </tr>
  <tr>
    <td align="right">输出规则：</td>
    <td align="left"><textarea rows="4" cols="50"  name="outputRules" id="outputRules"><%=params.getOutputRules()==null? "":params.getOutputRules()%></textarea> </td>
  </tr>
<%} %>

  <tr  id="trid_isOnlyValue" >
    <td align="right">只输出参数值：</td>
    <td align="left">
      <select name="isOnlyValue" id="isOnlyValue">
        <%if("是".equals(params.getIsOnlyValue())){%>
          <option value="是" selected>是</option>
          <option value="否">否</option>
          <option value=""></option>
        <%
        }else{%>
          <option value=""></option>
          <option value="是">是</option>
          <option value="否" selected>否</option>
        <%}
        %>

      </select>
    </td>
  </tr>
</table>
