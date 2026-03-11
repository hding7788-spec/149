<%@page pageEncoding="GBK" contentType="text/html; charset=UTF-8" %>
<%@page import="com.glaway.mpm.util.PropertiesUtil" %>
<%@ page import="wt.util.WTException" %>
<%@ page import="java.io.*" %>

<%
    response.reset();
    String type = request.getParameter("type");
    String filePath = PropertiesUtil.getLocalCodeBase() + File.separator + "ext" + File.separator + "casc" + File.separator + "report" +
            File.separator + "template" + File.separator + type + ".xlsx";
    File file = new File(filePath);
    if(file.exists()){
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[8192];
        int length = 0;
        OutputStream os = null;
        os = response.getOutputStream();
        String fileName=file.getName();
        fileName=new String(fileName.getBytes("gbk"),"iso-8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentType("application/x-msdownload");
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