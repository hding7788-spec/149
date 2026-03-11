<%@page pageEncoding="GBK" contentType="text/html; charset=UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.io.File"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.OutputStreamWriter"%>
<%@page import="java.io.OutputStream"%>
<%@page import="ext.casc.report.technics.DownloadTechnicsReportHelper"%>

<%
	response.reset();
	String oid = request.getParameter("oid");
	File xls = null;
	try {
		xls = DownloadTechnicsReportHelper.service.export(oid, "peitaomingxibiao","");
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
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
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