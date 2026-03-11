<%@page pageEncoding="GBK" contentType="text/html; charset=UTF-8"%>
<%@page import="ext.casc.report.technics.DownloadTechnicsReportHelper"%>
<%@page import="java.io.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>

<%
	response.reset();
	String oid = request.getParameter("oid");
	File xls = null;
	try {
		xls = DownloadTechnicsReportHelper.service.export(oid, "gongxupeitao","");
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