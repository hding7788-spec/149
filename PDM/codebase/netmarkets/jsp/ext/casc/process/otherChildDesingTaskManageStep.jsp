<%@page language="java" session="true" pageEncoding="GBK"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ page import="wt.util.WTProperties"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<%@ page import="ext.casc.process.ProcessPlan" %>
<%@ page import="wt.fc.ReferenceFactory,wt.fc.Persistable"%>
<%@ page import="ext.casc.process.ProcessConstants" %>
<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>
<%
String oid = request.getParameter("oid");
System.out.println("oid======="+oid);
	ProcessPlan processplan = null;
	String state = "";
	ReferenceFactory referencefactory = new ReferenceFactory();
	Persistable persistable = referencefactory.getReference(oid).getObject();
	if(persistable instanceof ProcessPlan){
		processplan = (ProcessPlan)persistable;
	}
%>
<div>
  <table>
  <tr></tr>
  <tr>
		<td>
			<b><%=ProcessConstants.JIHUABIANHAO %></b>
		</td>
		<td>
			<input type="text" name="number" id="number" value="<%=processplan.getNumber() %>" readOnly="true" />
		</td>
		<td>
			<b><%=ProcessConstants.JIHUAMINGCHENG %></b>
		</td>
		<td>
			<input type="text" name="name" id="name" value="<%=processplan.getName() %>" readOnly="true" />
		</td>


		<td>
			<b><%=ProcessConstants.JIHUAZHUTI %></b>
		</td>
		<td>
            <input type="text" name="zhuti" id="zhuti" value="<%=processplan.getZhuti() %>" readOnly="true" />
		</td>
		<td>
			<b><%=ProcessConstants.XIANGMUBU %></b>
		</td>
		<td>
            <input type="text" name="xiangmubu" id="xiangmubu" value="<%=processplan.getXiangmubu() %>" readOnly="true" />
         </td>
  </tr>
  <tr></tr>
  </table>

</div>

<jsp:include page="${ mvc:getComponentURL('ext.casc.process.mvc.builder.OtherChildDesingTaskManageBuilder')}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>