<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<link href="netmarkets/jsp/ext/numbergen/css/nmstyles.css" rel="stylesheet" type="text/css">

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

	<div>
		<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.task.MonitorDetailTaskBuilder')}" flush="true" ></jsp:include>
	</div>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
