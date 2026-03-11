<%@ page import="ext.ExportTechQuota"%>
<%
	String oid = request.getParameter("oid");
	if("".equals(oid)||"null".equals(oid)||oid==null){
		ExportTechQuota.exportExcel();
	}else{
		ExportTechQuota.exportExcel(oid);
	}
%>