<%@page contentType="text/html; charset=UTF-8"%>
<%@ page import="wt.org.WTPrincipal,
					wt.session.SessionHelper,
					wt.util.WTException,
					java.io.FileInputStream,
					java.io.BufferedInputStream,
					java.io.InputStream,
					java.io.OutputStream,
					java.io.*"%>
<%@include file="/netmarkets/jsp/util/context.jsp"%>
<%
	java.util.Properties wtprop = wt.util.WTProperties.getLocalProperties();
	String WT_TEMP = wtprop.getProperty("wt.temp");
	try {
		String fileName = (String) request.getParameter("fileName");
		fileName = WT_TEMP + fileName;
		//System.out.println("!!!!!!!!fileName" + fileName);
		File file = new java.io.File(fileName);
		if (file == null){
			throw new Exception("文件读取异常");
		}
		String filename = file.getAbsolutePath();
		filename = filename.replace('\\', '/');
		filename = filename.substring(filename.lastIndexOf('/') + 1);
    	out.clear();
    	out=pageContext.pushBody();
    
		response.addHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
		response.setContentLength((int) file.length());
		response.setContentType("application/octet-stream");
		OutputStream os = response.getOutputStream();
		InputStream is = new BufferedInputStream(new FileInputStream(file));

		byte[] buffer = new byte[8192];
		int length = 0;
		while ((length = is.read(buffer)) >= 0) {
			os.write(buffer, 0, length);
		}
		is.close();
		os.flush();

		try {
			if (file.exists()){
				file.delete();
			}
		} catch (Throwable tt) {
			tt.printStackTrace();
		}

	} catch (WTException wtexception) {
		out.println(wtexception.getMessage());
	}
%>

