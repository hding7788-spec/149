<%@page pageEncoding="GBK" contentType="text/html; charset=GBK"%>
<%@page import="com.glaway.mpm.util.PropertiesUtil"%>
<%@page import="java.io.*"%>

<%
	response.reset();
	String filePath = PropertiesUtil.getLocalCodeBase() + File.separator + "PDM_Guide.doc";
	String filePath2 = PropertiesUtil.getLocalCodeBase() + File.separator + "PDM_Guide.docx";
	File file = new File(filePath);
	if(!file.exists()){
	    file = new File(filePath2);
	}
	if(file.exists()){
		InputStream is = new BufferedInputStream(new FileInputStream(file));
		byte[] buffer = new byte[8192];
		int length = 0;
		OutputStream os = null;
		os = response.getOutputStream();
		String fileName=file.getName();
		fileName=new String(fileName.getBytes("gbk"),"iso-8859-1");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		response.setContentType("application/msword");
		response.setContentLength((int) file.length());
		while ((length = is.read(buffer)) >= 0) {
			os.write(buffer, 0, length);
		}
		is.close();
		os.flush();
		os.close();
		os = null;
		out.clear();
		out = pageContext.pushBody();
	} else {
	    out.print("file not exist!");
	}

%>