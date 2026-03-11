
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page language="java" pageEncoding="GBK" contentType="text/html; charset=GBK"%>

<%@ page import="com.glaway.mpm.mpmresource.ui.MPMResourceRB"%>
<fmt:setBundle basename="com.glaway.mpm.mpmresource.ui.MPMResourceRB"/>
<fmt:message var="wizardTitle" key="Add_Station" />


<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.processplan.SetupTeamTableBuilder')}" flush="true" />
