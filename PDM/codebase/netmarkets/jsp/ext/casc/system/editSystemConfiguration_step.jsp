<%@ page import="ext.casc.system.SystemConfigurationBean" %>
<%@ page import="ext.casc.system.SystemConfigurationUtil" %>
<%@page language="java" pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request"/>

<%
  String soid = request.getParameter("soid");
  String[] ss = soid.split("\\$");
  String tempKey =  ss[ss.length-1];
  String key = tempKey.substring(0, tempKey.length()-2);
  SystemConfigurationBean bean = SystemConfigurationUtil.getSystemConfigurationBean(key);
  String value = "";
  String remark = "";
  if(bean != null) {
    value = bean.getValue();
    remark = bean.getRemark();
  }
%>

<html>
<head>
  <style>
    body, td, input, select, textarea {
      font-size: 12px;  /* 设置字体大小 */
    }

    input[type="text"], select, textarea {
      padding: 4px 6px; /* 内边距增大 */
      font-size: 12px;
    }

    input[type="button"] {
      padding: 4px 8px;
      font-size: 12px;
      cursor: pointer;
    }

    .dataTable {
      border-spacing: 6px; /* 控制表格行间距 */
    }

    td {
      vertical-align: middle;
    }
  </style>

</head>
<body>
<table class="dataTable">
  <tr>
    <td colspan="1" style="text-align: left;"><w:label value="配置名称:"/></td>
    <td colspan="1" style="text-align: left;"><w:textBox name="key" id="key" size="30" maxlength="100" value="<%=key%>" readonly="true" required="true" /></td>
  </tr>
  <tr>
    <td colspan="1" style="text-align: left;"><w:label value="配置值:"/></td>
    <td colspan="1" style="text-align: left;"><w:textBox name="value" id="value" size="30" maxlength="100" value="<%=value%>" required="true" /></td>
  </tr>
  <tr>
    <td colspan="1" style="text-align: left;"><w:label value="备注:"/></td>
    <td colspan="1" style="text-align: left;"><w:textArea id="remark" name="remark" value="<%=remark%>" cols="40" rows="4" maxLength = "2000"/></td>
  </tr>
</table>

</body>

</html>