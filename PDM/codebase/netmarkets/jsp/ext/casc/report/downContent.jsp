<%@page pageEncoding="GBK" contentType="text/html; charset=UTF-8" %>
<%@page import="com.glaway.mpm.util.PropertiesUtil" %>
<%@page import="com.glaway.mpm.util.WTDocumentUtil" %>
<%@page import="wt.content.ApplicationData" %>
<%@page import="wt.content.ContentHelper" %>
<%@page import="wt.content.ContentRoleType" %>
<%@page import="wt.content.ContentServerHelper" %>
<%@ page import="wt.doc.WTDocument" %>
<%@ page import="wt.fc.QueryResult" %>
<%@ page import="wt.util.WTException" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileOutputStream" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.OutputStream" %>

<%
    System.out.println("==========>>>>>>>>>>>>>>>>");
    response.reset();
    String oid = request.getParameter("oid");
    String type = request.getParameter("type");
    String documentNumber = request.getParameter("documentNumber");
    try {
        WTDocument document = WTDocumentUtil.getDocumentByNumber(documentNumber);
        ApplicationData appData = null;
        QueryResult queryResult = ContentHelper.service.getContentsByRole(document, ContentRoleType.PRIMARY);
        if (queryResult.hasMoreElements()) {
            appData = (ApplicationData) queryResult.nextElement();
        }
        InputStream is = ContentServerHelper.service.findContentStream(appData);
        String fileName = appData.getFileName();
        fileName = new String(fileName.getBytes("gbk"), "iso-8859-1");
        if (appData != null) {
            byte[] buffer = new byte[8192];
            int length = 0;
            OutputStream os = null;
            os = response.getOutputStream();
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentType("application/zip");
            response.setContentLength((int)appData.getFileSize());
            while ((length = is.read(buffer)) >= 0) {
                os.write(buffer, 0, length);
            }
            is.close();
            os.flush();
            os.close();
            os = null;
            out.clear();
            out = pageContext.pushBody();
        }
    } catch (WTException e) {
        e.printStackTrace();
    }

%>