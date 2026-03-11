<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.io.*"%>
<%@page pageEncoding="GBK" contentType="text/html; charset=UTF-8"%>
<%@page import="ext.casc.zipfile.ZipFileHelper" %>
<%
	String oid = request.getParameter("oid");
	String zipFilepath = ZipFileHelper.service.zipPackets(oid);
	File zipFile = new File(zipFilepath);
	if(zipFile != null) {
		InputStream is = new BufferedInputStream(new FileInputStream(zipFile));
		byte[] buffer = new byte[8192];
		int length = 0;
		OutputStream os = null;
		os = response.getOutputStream();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		String fileName=zipFile.getName();
		fileName=new String(fileName.getBytes("gbk"),"iso-8859-1");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		response.setContentType("application/zip");
		response.setContentLength((int) zipFile.length());
		while ((length = is.read(buffer)) >= 0) {
			os.write(buffer, 0, length);
		}
		is.close();
		os.flush();
		os.close();
		os = null;
		out.clear();
		out = pageContext.pushBody();
		zipFile.deleteOnExit();
	}
%>
