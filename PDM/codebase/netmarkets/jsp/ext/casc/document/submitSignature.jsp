<%@page import="ext.casc.fileprint.FilePrintUtil"%>
<%@page import="java.util.*"%>
<%@page import="ext.casc.doc.SetSignatureProcessor"%>
<%@page import="ext.casc.workflow.util.PrintDistributionHelper,ext.casc.constants.Constants" %>
<%@ page language="java" pageEncoding="utf-8"%>
<%
	String oid = request.getParameter("oid");
	Map<String, String> map = new HashMap<String, String>();
	List<String> list = SetSignatureProcessor.addList();
	for(String role : list){
		String value = request.getParameter(role);
		if(SetSignatureProcessor.isInteger(role)){
			String time = request.getParameter(role + Constants.SHIJIAN);
			String dept = request.getParameter(role + Constants.BUMENG);
			map.put(role + Constants.SHIJIAN, time);
			map.put(role + Constants.BUMENG, dept);
		}else{
			String time = request.getParameter(role + Constants.SHIJIAN);
			map.put(role + Constants.SHIJIAN, time);
		}
		map.put(role, value);
	}
	map.put("ecnBiaoJi", request.getParameter("ecnBiaoJi")==null? "":request.getParameter("ecnBiaoJi"));
	map.put("ecnNumber", request.getParameter("ecnNumber")==null? "":request.getParameter("ecnNumber"));
	map.put("GENGGAI", request.getParameter("GENGGAI")==null? "":request.getParameter("GENGGAI"));
	map.put("GENGGAISHIJIAN", request.getParameter("GENGGAISHIJIAN")==null? "":request.getParameter("GENGGAISHIJIAN"));
	FilePrintUtil.writeProcessTempSign(oid, map);
%>
<script>
	window.onload = function () {
		window.close();
	}
</script>