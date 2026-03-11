<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="com.glaway.mpm.processplan.checkouttable.CheckOutUtil"%>

<%
    String technicsNumber = request.getParameter("technicsNumber");
    if(technicsNumber.contains(" ")){
        technicsNumber = technicsNumber.substring(0,technicsNumber.indexOf(" "));
    }
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>照片样张汇总表</title>

</head>

<frameset cols="10%,*">
    <frameset rows="100%,0%">
        <frame src="showPhotoTableLeft.jsp?technicsNumber=<%=technicsNumber%>">
    </frameset>
    <frameset rows="100%,0%">
        <frame src="showPhotoTableRight.jsp?technicsNumber=<%=technicsNumber%>" name="PTR">
    </frameset>
</frameset>
</html>




