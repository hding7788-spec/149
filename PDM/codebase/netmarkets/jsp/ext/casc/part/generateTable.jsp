<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="ext.casc.part.mvc.builder.GenerateMatchHistoryJson"%>
<%
request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");

String oid = request.getParameter("oid");
String type = request.getParameter("type");
String result = "";
if("1".equals(type)){
    result =  GenerateMatchHistoryJson.generateDocGrid(oid).toString();
}else if("2".equals(type)){
          result =  GenerateMatchHistoryJson.generateDocWZGrid(oid).toString();
 }else if("3".equals(type)){
    String matchWZNumber = request.getParameter("matchWZNumber");
    String gongYiWZNumber = request.getParameter("gongYiWZNumber");
    String orgWZNumber = request.getParameter("orgWZNumber");
    result =  GenerateMatchHistoryJson.generateWZHistoryGrid(matchWZNumber,gongYiWZNumber,orgWZNumber).toString();
}else if("4".equals(type)){
    result =  GenerateMatchHistoryJson.generateQueryWzGrid(request).toString();
}else if("5".equals(type)){
    result =  GenerateMatchHistoryJson.replaceGongYiWz(request);
    //result = "{success:true}";
}else if("6".equals(type)){
    result =  GenerateMatchHistoryJson.generatePeiTaoTableGrid(oid).toString();
    //result = "{success:true}";
}

System.out.println("#####"+result);
response.getWriter().print(result);
%>
