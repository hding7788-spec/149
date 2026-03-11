<%@ page import="ext.casc.util.ProcessErrorFixTool" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.FileReader" %>
<%@ page import="java.io.IOException" %>

<%@page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%
//    txt格式如下：
    //9000001
//    9000001
//    9000001
//    9000001
//    90000022
//    9000003
//    9000001

    String filePath = "C:\\Users\\fny\\OneDrive\\桌面\\1.txt"; // 替换为实际文件路径

    BufferedReader br = null;
    try {
        br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null) {
            // 去除前后空格
            String trimmedLine = line.trim();
            if (!trimmedLine.isEmpty()) {
                System.out.println("处理后的行内容：" + trimmedLine);
                out.println(ProcessErrorFixTool.rePreStep("", trimmedLine));
            }
        }
    } catch (Exception e) {
        out.println("读取文件时出现错误: " + e.getMessage());
    } finally {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                out.println("关闭文件时出现错误: " + e.getMessage());
            }
        }
    }
%>