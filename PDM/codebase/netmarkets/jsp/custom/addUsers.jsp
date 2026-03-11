<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c_rt" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="wc"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@ page import="com.ptc.windchill.enterprise.picker.principal.PrincipalBean"%>
<%@ page import="java.util.LinkedHashMap"%>
<%@ page import="wt.inf.container.WTContainer"%>

<%! private static String RESOURCE = "com.ptc.windchill.principal.user.userResource"; %>


<%
   ResourceBundle addRb = ResourceBundle.getBundle(RESOURCE, localeBean.getLocale());
   NmCommandBean cb = new NmCommandBean();
   cb.setCompContext(nmcontext.getContext().toString());
   cb.setRequest(request);
   String contextStr = addRb.getString("PARTICIPANT_PICKER_CONTEXT_LABEL");
%>
    <c_rt:set var="principalType" value="<%= PrincipalBean.USER %>"/>

    <jca:participantPicker
	    actionClass="ext.casc.product.ProductTeamAddRoleUsersCommands"
	    actionMethod="addUsers"
	    participantType="${principalType}"
	    defaultContext="Container"
	    emailAllowed="false"
    >
    </jca:participantPicker>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
