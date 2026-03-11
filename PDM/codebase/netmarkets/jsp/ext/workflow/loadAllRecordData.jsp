
<%@page import="ext.casc.workflow.signtrue.zp.ExtJsonDataGenerator"%>

<%
String workItemOid = request.getParameter("workItemOid");
String objOid = request.getParameter("objOid");
String result="";
result = ExtJsonDataGenerator.genAllReviewRecordJsonData(workItemOid,objOid);

out.println(result);
%>