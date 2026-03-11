<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"
	prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<div>
	<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.task.TechEquTaskTableBuilder')}" flush="true" ></jsp:include>
</div>
		
