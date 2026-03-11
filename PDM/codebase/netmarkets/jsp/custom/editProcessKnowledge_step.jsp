<%@ page import="ext.casc.constants.Constants"%>
<%@ page import="com.ptc.windchill.principal.org.OrganizationCommands"%>
<%@ page import="ext.casc.mpm.GyCsServerHelper" %>
<%@ page import="ext.casc.mpm.process.GLProcessParams" %>
<%@ page import="ext.casc.util.Tools" %>
<%@ page import="ext.casc.mpm.process.GLProcessKnowledge" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="ext.sast.common.fc.CmPersistable" %>
<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request"/>
<jsp:useBean id="localeBean" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>
<jsp:useBean id="sessionBean" class="com.ptc.netmarkets.util.beans.NmSessionBean" scope="session"/>
<jsp:useBean id="linkBean" class="com.ptc.netmarkets.util.beans.NmLinkBean" scope="request"/>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@page language="java" pageEncoding="UTF-8"
        contentType="text/html; charset=UTF-8"%>
<%
  String paramNumber = request.getParameter("oid");
  GLProcessParams processParams = GyCsServerHelper.getProcessParamDefinition(paramNumber,false);
  String output = processParams.getKnowledgeOutputPara();
  String input = processParams.getKnowledgeInferencePara();
  String[] outs  = output.split("\\|");
  String[] ins = input.split("\\|");

  String soid = request.getParameter("soid");//processParams$processKnowledgeManage$ZSCS-000001$cee28414-b7a8-4a9a-be85-e1acd638dbf9!*
  String[] ss = soid.split("\\$");
  String tempKnowledgeId =  ss[ss.length-1];
  String knowledgeId = tempKnowledgeId.substring(0, tempKnowledgeId.length()-2);
  Map<String,String> queryParams = new HashMap<String,String>();
  queryParams.put(CmPersistable.KEY_ID, knowledgeId);
  GLProcessKnowledge kl = (GLProcessKnowledge)GyCsServerHelper.queryGLObject(GLProcessKnowledge.class, queryParams);

%>
<script>

</script>
<table>
  <input type="hidden" name="paramNumber" id="paramNumber" value="<%=paramNumber%>"/>
  <input type="hidden" name="knowledgeId" id="knowledgeId" value="<%=knowledgeId%>"/>

  <%
    for(int i = 0;i<ins.length;i++){
      if(!Tools.isNull(ins[i])){
  %>
  <tr>
    <td align="right"><%=ins[i]%>：</td>
    <td align="left"><input type="text" name="column<%=(i+1)%>" id="column<%=(i+1)%>" value="<%=(String)Tools.getReflectValue(kl,"column"+(i+1))%>"/> </td>
  </tr>
  <%
      }
    }
  %>
  <%
    for(int i = 0;i<outs.length;i++){
      if(!Tools.isNull(outs[i])){
  %>
  <tr>
    <td align="right"><%=outs[i]%>：</td>
    <td align="left"><input type="text" name="column<%=(ins.length+i+1)%>" id="column<%=(ins.length+i+1)%>"  value="<%=(String)Tools.getReflectValue(kl,"column"+(ins.length+i+1))%>"/> </td>
  </tr>
  <%
      }
    }
  %>

</table>
