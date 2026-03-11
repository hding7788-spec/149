<%@ page import="ext.casc.change.ChangeHelper" %>
<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");

	String oid = request.getParameter("oid");
	String data = request.getParameter("data");;
	ChangeHelper.saveCollectDatas(oid, data);

%>
