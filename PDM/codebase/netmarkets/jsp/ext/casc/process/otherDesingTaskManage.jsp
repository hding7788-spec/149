<%@page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@page import="wt.util.WTProperties"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<%@ page import="java.util.Map,java.util.Locale"%>
<%@ page import="wt.org.WTUser, wt.session.SessionHelper"%>
<%@ page import="java.util.Calendar,java.text.SimpleDateFormat,java.util.GregorianCalendar"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="ext.casc.process.ProcessConstants" %>
<%@page import="ext.casc.process.util.ProcessUtil"%>

<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>

<%
WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
Map<String, List<WTUser>> map = ProcessUtil.getGroupAndUsersInOrgContainer();
Set<String> set = new TreeSet<String>();
set.addAll(map.keySet());
String msg = ProcessConstants.JSP_MSG_VALIDTE;
%>


<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.OtherDesingTaskManageBuilder')}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>