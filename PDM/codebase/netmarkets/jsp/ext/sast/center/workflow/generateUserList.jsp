<%@ page import="ext.sast.center.processor.InvokeRestServiceProcessor" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page contentType="text/html;charset=utf-8" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    String type = request.getParameter("type");
    String unitiid = request.getParameter("unitiid");
    String unitInner = request.getParameter("unitInner");
    String processName = request.getParameter("processName");
    if(processName != null){
        processName = URLDecoder.decode(processName,"UTF-8");
    }
    String searchValue = request.getParameter("searchValue");
    System.out.println(processName+ "------" + searchValue);
    String json="";
    if("siteInfo".equals(type)){
        json = InvokeRestServiceProcessor.getAllSiteInfo().toString();
    }else if("userInfo".equals(type) && searchValue != null && !searchValue.isEmpty()){
        json = InvokeRestServiceProcessor.getUserAndUnitInfo(unitiid,unitInner,processName,searchValue).toString();
    }
    response.getWriter().print(json);
%>