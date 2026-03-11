<%@page import="com.glaway.mpm.pbom.PbomHelper" %>
<%
	String oid = request.getParameter("oid");

	String type = request.getParameter("type");
	String poids = request.getParameter("poids");

	if("part_type".equals(type)){
		String wltypes = request.getParameter("wltypes");
		PbomHelper.savePbomWlType(poids, wltypes);
	}else if("line".equals(type)){
		String zzcjs = request.getParameter("zzcjs");
		String ffcjs = request.getParameter("fzcjs");
		PbomHelper.savePbomLine(poids, zzcjs,ffcjs);
	}


%>
<script>
	window.close();
</script>