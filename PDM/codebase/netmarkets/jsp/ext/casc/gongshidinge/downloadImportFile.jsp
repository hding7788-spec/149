<%@ page import="com.glaway.mpm.util.PropertiesUtil" %>
<%@ page import="java.io.*" %>
<%
    String fileName = request.getParameter("file");
    String tPath = PropertiesUtil.getWTHome() + File.separator + "gongshi" + File.separator + fileName;
    File file = new File(tPath);
    if (file != null) {
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[8192];
        int length = 0;
        OutputStream os = null;
        os = response.getOutputStream();
        fileName = new String(file.getName().getBytes("gbk"),"iso-8859-1");
        response.setHeader("Content-Disposition","attachment;filename=" + fileName);
        response.setContentType("application/zip");
        response.setContentLength((int) file.length());
        while ((length = is.read(buffer)) >= 0) {
            os.write(buffer, 0, length);
        }
        file.delete();
        is.close();
        out.clear();
        out = pageContext.pushBody();
        out.print("javascript:close()");
    } else {
        out.print("<script>window.close()</script>");
    }
%>
