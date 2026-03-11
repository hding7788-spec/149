<%@ page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />	
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<%@ page import="ext.casc.ui.actionsRB,java.util.List"%>
<%@ page import="java.util.ArrayList" %>
<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<script language="JavaScript" src='netmarkets/javascript/util/revisionLabelPicker.js'></script>
<%
	String number = (String) session.getAttribute("number");
%>
<table>
	<tr>
		<td><h1>&nbsp;&nbsp;PBOM±àºÅ&nbsp;:</h1></td>
		<td><h1>&nbsp;<%=number%></h1></td>
	</tr>
	<tr>
		<td><h1>&nbsp;&nbsp;Åú&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;´Î&nbsp;:&nbsp;</h1></td>
		<td><h1 id="h1"></h1></td>
	</tr>
</table>
<div>
	<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.RefreshBatchesBuilder')}" flush="true" />
</div>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>