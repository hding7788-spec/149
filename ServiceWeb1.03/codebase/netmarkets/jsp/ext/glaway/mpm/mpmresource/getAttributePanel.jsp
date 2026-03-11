<%@page language="java" pageEncoding="GBK" contentType="text/html; charset=GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"  prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc"         prefix="mvc"%>

<jca:initializeItem operation="CREATE" baseTypeName="com.ptc.windchill.mpml.resource.MPMResource" />
<% 
    String typeName=request.getParameter("typeName");
    if(!"".equals(typeName)){
%>

<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.mpmresource.CreateMPMResourceBuilder')}" flush="true" />

<%} %>



