
<%@page import="ext.casc.workflow.signtrue.zp.ExtJsonDataGenerator"%>

<%
String workOid = request.getParameter("workOid");
System.out.println("------------workOid-----"+workOid);
String type = request.getParameter("type");
String result="";
if("1".equals(type)){
	result = ExtJsonDataGenerator.genCheJianAndGYZZJsonData(workOid);
}else if("2".equals(type)){
	result = ExtJsonDataGenerator.genGyyJsonDataByCurrentUser(workOid);
}
else if("3".equals(type)){
	result = ExtJsonDataGenerator.genGyyJsonDataByCurrentUser(workOid);
}
out.println(result);
%>