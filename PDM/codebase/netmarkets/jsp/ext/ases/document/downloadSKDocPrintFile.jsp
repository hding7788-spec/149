<%@page import="ext.casc.search.SearchUtil"%><%@page import="ext.casc.util.Tools"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.OutputStream"%><%@page import="ext.casc.zipfile.ZipFileHelper, com.jspsmart.upload.SmartUpload, java.io.File"%>
<%String fileNameTemp = request.getParameter("fileNameTemp");String tempPath = WTProperties.getLocalProperties().getProperty("wt.temp");	String filePath = tempPath + File.separator +fileNameTemp+".zip";		if (filePath != null && !"".equals(filePath))		{			if (filePath != null)			{				File f = new File(filePath);				InputStream is = new BufferedInputStream(new FileInputStream(f));				byte[] buffer = new byte[102400];				int length = 0;				OutputStream os = null;				os = response.getOutputStream();				fileNameTemp = fileNameTemp.replaceAll(" ", "");				String fileName = new String(fileNameTemp.getBytes("gbk"),"iso-8859-1");				response.setHeader("Content-Disposition","attachment;filename=" + fileName);				response.setContentType("application/zip");				response.setContentLength((int) f.length());				while ((length = is.read(buffer)) >= 0)				{					os.write(buffer, 0, length);				}				f.delete();				is.close();				out.clear();				out = pageContext.pushBody();				out.print("javascript:close()");			}		}		else		{			out.print("<script>window.close()</script>");		}
%>
