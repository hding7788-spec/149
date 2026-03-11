<%@page pageEncoding="GBK" contentType="text/html; charset=UTF-8"%>
<%@page import="java.io.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ page import="ext.casc.analysisActivity.process.ExportAnalysisDataProcessor" %>

<%
	String type = request.getParameter("type");
	String oid = request.getParameter("oid");
	response.reset();
	File xls = null;
	try {
		xls = ExportAnalysisDataProcessor.export(type,oid);
	} catch (Exception e) {
		e.printStackTrace();
		return;
	}
	if(xls != null) {
		InputStream is = new BufferedInputStream(new FileInputStream(xls));
		byte[] buffer = new byte[8192];
		int length = 0;
		OutputStream os = null;
		os = response.getOutputStream();
		String fileName=xls.getName();
		fileName=new String(fileName.getBytes("gbk"),"iso-8859-1");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		response.setContentType("application/vnd.ms-excel");
		response.setContentLength((int) xls.length());
		while ((length = is.read(buffer)) >= 0) {
			os.write(buffer, 0, length);
		}
		is.close();
		os.flush();
		os.close();
		os = null;
		out.clear();
		out = pageContext.pushBody();
	    xls.deleteOnExit();
	} else {

	}

%>