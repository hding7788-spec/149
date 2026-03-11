<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<%@page language="java" pageEncoding="utf-8"
	contentType="text/html; charset=utf-8"%>
<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.processplan.WorkTimeReportBuilder')}" flush="true" />
