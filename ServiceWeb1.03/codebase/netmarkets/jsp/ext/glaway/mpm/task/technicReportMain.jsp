<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.glaway.mpm.mpmresource.gznumber.number.AdministrationHelper"%>


<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<% 
	//if(AdministrationHelper.getUser().equals(AdministrationHelper.ADMINISTRATOR)){
%>
<a href="netmarkets/jsp/glaway/mpm/mpmresource/gz/gzcard/gzcard.jsp">工装申请卡</a><br/>
<% 
//	}
%>
<a href="netmarkets/jsp/glaway/mpm/task/report/technicCountReport.jsp">工艺统计报表</a><br/>
<a href="netmarkets/jsp/glaway/mpm/task/report/toolCountReport.jsp">工装统计报表</a><br/>
<a href="netmarkets/jsp/glaway/mpm/task/report/technicTaskCount.jsp">工艺任务统计</a><br/>
<a href="netmarkets/jsp/glaway/mpm/task/report/technicQuery.jsp">工艺查询</a>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>