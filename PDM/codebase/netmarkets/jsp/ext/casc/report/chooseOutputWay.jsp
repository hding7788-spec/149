<%@page import="com.glaway.mpm.intf.ProcessEditorToWCIntfRMI"%>
<%@page import="wt.inf.container.WTContainer"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="wt.part.WTPart"%>
<%

	String oid = request.getParameter("oid");
	Object object = new ReferenceFactory().getReference(oid).getObject();
	WTPart pbomPart = (WTPart) object;
	WTContainer container = pbomPart.getContainer();
	Vector<String> batchs = ProcessEditorToWCIntfRMI.getBatchsByProductName(container.getName());

%>
<table>
	<tr height="20"></tr>

	<tr>
		<td><h4>批次号</h4></td>
		<td>
			<select name="batch" id="batch">
			<%for(int i = 0; i < batchs.size(); i++){ %>
			<option value="<%=batchs.get(i) %>"><%=batchs.get(i) %></option>
			<%} %>
			</select>
		</td>
		<!-- <td>&nbsp;&nbsp;&nbsp;</td>
		<td align=center><h4><input type="button" value="   查询   "/></h4></td> -->
      </tr>
</table>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>