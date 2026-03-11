
<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@ include file="/netmarkets/jsp/zchange/includeWizBean.jspf"
%><%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"
%><%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<c:set var="affectedDataWizardStep" value="affectedDataStepZ" scope="request"/>

<BR></BR>
<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.change.TechnicAffectedUsabilityTableBuilder')}" flush="true"></jsp:include>
<BR></BR>
<jsp:include page="${mvc:getComponentURL('zchange.affectedonbuildtablez')}" flush="true"></jsp:include>