<%@page import="ext.casc.process.mvc.builder.PartRenameProcessor"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.part.WTPart"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@page import="wt.util.WTProperties"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />
<%-- <%@ include file="/netmarkets/jsp/util/begin.jspf"%> --%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<%@ page import="java.util.Map,java.util.Locale"%>
<%@ page import="wt.org.WTUser, wt.session.SessionHelper"%>
<%@ page import="java.util.Calendar,java.text.SimpleDateFormat,java.util.GregorianCalendar"%>
<%@ page import="java.net.*"%>
<%@ page import="ext.casc.process.ProcessConstants" %>
<%@page import="ext.casc.process.util.ProcessUtil"%>
<%
NmCommandBean cb = new NmCommandBean();
/* cb.setCompContext(NmContext.getContext().toString()); */
cb.setRequest(request);
NmOid nmOid = cb.getPrimaryOid();
Object object = nmOid.getRefObject();
	String name="";
if(object instanceof WTPart){
    name=PartRenameProcessor.getPartNameByoid(nmOid);
}
%>
<table style="positin:absolute;left:50%">
<br>
<br>
<br>
<br>
<br>
	<tr >
		<td><h4><%=ProcessConstants.JSP_SEARCH_NAME%></h4></td>
		<td><input style="width:200px" type="text"  name="partName" id="partName" value="<%=name%>"></td>
	</tr>
	</table>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>