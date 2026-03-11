<%@ page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.number.AdministrationHelper"%>
<%@page import="com.glaway.mpm.constants.Constants"%>



<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<a href="netmarkets/jsp/glaway/mpm/mpmresource/gz/gznumber/applyGZNumber.jsp?type=<%=Constants.alGZ %>">申请自制工装号(<%=Constants.alGZ %>)</a><br/>
<a href="netmarkets/jsp/glaway/mpm/mpmresource/gz/gznumber/applyGZNumber.jsp?type=<%=Constants.kGZ %>">申请科研工装号(<%=Constants.kGZ %>)</a><br/>
<a href="netmarkets/jsp/glaway/mpm/mpmresource/gz/gznumber/applyGZNumber.jsp?type=<%=Constants.tGZ %>">申请通用工装号(<%=Constants.tGZ %>)</a><br/>
<a href="netmarkets/jsp/glaway/mpm/mpmresource/gz/gznumber/gzNumberSearch.jsp">工装编号查询</a><br/>
<% 
	if(AdministrationHelper.getUser().equals(AdministrationHelper.ADMINISTRATOR)){ 
%>
<a href="netmarkets/jsp/glaway/mpm/mpmresource/gz/gznumber/classificationManager.jsp">工装分类维护</a><br/>
<a href="netmarkets/jsp/glaway/mpm/mpmresource/gz/gznumber/gzNumberloader.jsp">数据导入</a><br/>
<% 
	}
%>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>  