<%@page import="wt.util.WTProperties"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="ext.casc.util.Tools"%>
<%@page import="ext.casc.zipfile.ZipFileHelper,
	com.jspsmart.upload.SmartUpload,
	java.io.File"%>
<%
	String fileNameTemp =  ZipFileHelper.service.userGuidesZipFile("userGuides");
	System.out.println("-------fileNameTemp:"+fileNameTemp);
	if (fileNameTemp != null){
		/*SmartUpload su = new SmartUpload();
		su.initialize(pageContext);
		su.setContentDisposition(null);
		su.downloadFile(fileNameTemp,"application/zip");
		File f = new File(fileNameTemp);
		f.delete();*/
		File f = new File(fileNameTemp);
		InputStream is = new BufferedInputStream(new FileInputStream(f));
		byte[] buffer = new byte[8192];
		int length = 0;
		OutputStream os = null;
		os = response.getOutputStream();
		fileNameTemp =fileNameTemp.replaceAll(" ","");
		String fileName = new String(Tools.getFileName(fileNameTemp).getBytes("gbk"),"iso-8859-1");
		response.setHeader("Content-Disposition", "attachment;filename="+fileName);
		response.setContentType("application/zip");
		response.setContentLength((int) f.length());
		while ((length = is.read(buffer)) >= 0) {
			os.write(buffer, 0, length);
		}
		f.delete();
		is.close();
		out.clear();
		out=pageContext.pushBody();
		out.print("javascript:close()");
	}
		
%>
