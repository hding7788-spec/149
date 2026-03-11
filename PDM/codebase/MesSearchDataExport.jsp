<%@page import="com.glaway.mpm.mesDataSearch.MesDataExport" %>
<%@page import="com.glaway.mpm.mesParameter.MesParameterProcessor" %>
<%@page import="com.glaway.mpm.model.TempObject" %>
<%@page import="com.glaway.mpm.util.PropertiesUtil" %>
<%@page import="ext.casc.cadsign.wcserver.ZipFileUtil" %>
<%@page import="wt.httpgw.LanguagePreference" %>
<%@page import="java.io.*" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.util.Date" %>
<%@page import="java.util.List" %>
<%@page import="java.util.Locale" %>
<%@page pageEncoding="GBK" contentType="text/html; charset=UTF-8" %>

<%
    String tempPath = PropertiesUtil.getTempPath() + File.separator + "MesData";
    File path = new File(tempPath);
    if (!path.exists()) {
        path.mkdirs();
    }
    wt.session.SessionHelper.manager.setAdministrator();
    //WTPrincipal current = wt.session.SessionContext.setEffectivePrincipal(admin);
    //wt.session.SessionContext.setEffectivePrincipal(admin);
    String jwsRuntimeParameters = "-Xmx512m";
    Locale aLocale = LanguagePreference.getLocale(request.getHeader("Accept-Language"));

    String resultStr = request.getParameter("params");
    //String resultStr = "RzDf$RzDf2";
    //HashMap map=new HashMap();
    //String[] input = {"RzDf","RzDf2"};
    //map.put("processNumber", input);
    //String resultStr=Deserialize.serializeMap(map);
    //HashMap resultMap=(HashMap)Deserialize.deserializeMap(resultStr);
    //String[] processNumbers = (String[]) resultMap.get("processNumber");
    String[] result = resultStr.split("[$]");
    for (int i = 0; i < result.length; i++) {
        String[] str = result[i].split("@");
        String processNumber = "";
        String productNumber = "";
        if (str.length >= 2) {
            processNumber = str[0];
            productNumber = str[1];
        }
        if (result[i].endsWith("@")) {
            processNumber = str[0];
            productNumber = "";
        }
        processNumber = processNumber.replace("^", "%");
        System.out.println("processNumber============>>>>>>>>>>>" + processNumber);
        System.out.println("productNumber============>>>>>>>>>>>" + productNumber);
        String technicsNumber = MesParameterProcessor.getTechnicsNumberByProductNumber(processNumber);
        System.out.println("technicsNumber=====>>>>>" + technicsNumber);
//        if (technicsNumber != null && !"".equals(technicsNumber)) {
//            String processNum = processNumber.replace("/", "_");
//            String filePath = tempPath + File.separator + technicsNumber + "_" + processNum + ".xls";
//            System.out.println("filePath====" + filePath);
//            File file = MesDataSearchProcesser.exportSearchData(technicsNumber, processNumber, productNumber, filePath);
//        } else {
            List<TempObject> technicsNumberList = MesDataExport.getTechnicsNumberList("", processNumber, "", "");
            if (technicsNumberList != null && technicsNumberList.size() > 0) {
                for (TempObject tempObj : technicsNumberList) {
                    String number = tempObj.getDocNumber();
                    String zfFlag = tempObj.getName();
                    String version = tempObj.getOccId();
                    if ("Z".equals(zfFlag)) {
                        technicsNumber = number + "_ZF";
                        String processNum = processNumber.replace("/", "_");
                        String filePath = tempPath + File.separator + number + "_" + processNum + "_" + productNumber + ".xls";
                        System.out.println("filePath====" + filePath);
                        File file = MesDataExport.exportSearchData(technicsNumber,version, processNumber, productNumber, filePath);
                    }
                }
            }

//        }
    }
    ZipFileUtil.zipFile2(tempPath, tempPath + ".zip");
    File zipFile = new File(tempPath + ".zip");
    if (zipFile != null) {
        InputStream is = new BufferedInputStream(new FileInputStream(zipFile));
        byte[] buffer = new byte[8192];
        int length = 0;
        OutputStream os = null;
        os = response.getOutputStream();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String fileName = zipFile.getName();
        fileName = new String(fileName.getBytes("gbk"), "iso-8859-1");
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