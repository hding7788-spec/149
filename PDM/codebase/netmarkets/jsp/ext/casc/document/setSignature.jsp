<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@page import="org.json.JSONObject"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="ext.casc.doc.SetSignatureProcessor"%>
<%@page import="ext.casc.workflow.util.PrintDistributionHelper,ext.casc.constants.Constants" %>


<%
	String oid = request.getParameter("oid");
	List<String> list = SetSignatureProcessor.addList();
	String path = request.getContextPath();

	JSONObject signInfos = SetSignatureProcessor.getSignInfo(oid);
%>
<script>
function check(){
// 	var bianzhizhe = document.getElementById("编制者").value;
// 	var bianzhishijian = document.getElementById("编制者时间").value;
// 	var jiaoduizhe = document.getElementById("校对者").value;
// 	var jiaoduizheshijian = document.getElementById("校对者时间").value;
// 	var shenhezhe = document.getElementById("审核者").value;
// 	var shenhezheshijian = document.getElementById("审核者时间").value;
// 	var biaoshenzhe = document.getElementById("标审者").value;
// 	var biaoshenzheshijian = document.getElementById("标审者时间").value;
// 	var pizhunzhe = document.getElementById("批准者").value;
// 	var pizhunzheshijian = document.getElementById("批准者时间").value;
// 	if(bianzhizhe == "" || bianzhishijian == "" || jiaoduizhe == "" || jiaoduizheshijian == ""
// 			|| shenhezhe == "" || shenhezheshijian == "" || biaoshenzhe == "" || biaoshenzheshijian== "" || pizhunzhe == "" || pizhunzheshijian == ""){
// 		alert("所填内容不能为空");
// 	}else{
		document.getElementById("submitinfo").submit();
// 	}
}
function getSignInfo(){
	//alert("暂不支持！");
	//return;
	new Ajax.Request(
			"http://pdm.149.sast.casc/Windchill/netmarkets/jsp/ext/casc/document/getSignInfo.jsp",
			{
				method: "post",
				postBody: "oid=" + encodeURIComponent($('oid').value),
				onComplete: setSignInfo
			}
		);

}

function setSignInfo(transport, result) {

}
</script>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<script language="javascript" src="<%=path%>/netmarkets/javascript/149/WdatePicker.js" type="text/javascript"></script>
	<link href="<%=path%>/netmarkets/javascript/149/skin/WdatePicker.css" rel="stylesheet" type="text/css">
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>自定义签名</title>

    </head>
<body>
<form id="submitinfo" method="post" action="<%=path%>/netmarkets/jsp/ext/casc/document/submitSignature.jsp">
<div width= "100%">
<table  width= "60%" id="tableSelect" border="1" style="border-collapse:collapse; margin:0 auto;white-space:nowrap;word-break:keep-all;" nowrap="nowrap">
<%for(String role : list){ %>
    <tr>
    	<%if(SetSignatureProcessor.isInteger(role)){ %>
       		 <td width=30% height=50px>
       		 <span><%= Constants.HUIQIAN %><%= role %>&nbsp;&nbsp; : &nbsp;&nbsp;</span>
        			<input id = "<%=role%>" name="<%=role%>" type="text" value="<%=signInfos.optString(role) %>"> &nbsp;&nbsp;&nbsp;&nbsp;
        			</td>
       		 <td width=30% height=50px>
       		 <span><%= Constants.HUIQIAN %><%= Constants.SHIJIAN %> &nbsp;&nbsp;: </span>&nbsp;&nbsp;
       		 <input id= "<%=role%><%= Constants.SHIJIAN %>" name = "<%=role%><%= Constants.SHIJIAN %>"value="<%=signInfos.optString(role+Constants.SHIJIAN) %>" class="Wdate" onclick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd'})" type="text">
        &nbsp;&nbsp;&nbsp;&nbsp;<span><%= Constants.HUIQIAN %><%= Constants.BUMENG %>&nbsp;&nbsp;:</span>&nbsp;&nbsp;
        <input id = "<%=role%><%= Constants.BUMENG %>" name="<%=role%><%= Constants.BUMENG %>" type="text" value="<%=signInfos.optString(role+Constants.BUMENG) %>">
        </td>
        <%}else{ %>
        	<td width=30% height=50px>
        	<span><%= role %> &nbsp;&nbsp; : &nbsp;&nbsp;</span>
        			<input id = "<%=role%>" name="<%=role%>" type="text" value="<%=signInfos.optString(role) %>"> &nbsp;&nbsp;&nbsp;&nbsp;
        			</td>
        <td width=30% height=50px>
        <span><%= role.replace(Constants.ZHE, "") %><%= Constants.SHIJIAN %> &nbsp;&nbsp; : &nbsp;&nbsp;</span>
        <input id= "<%=role%><%= Constants.SHIJIAN %>" name = "<%=role%><%= Constants.SHIJIAN %>" class="Wdate" value="<%=signInfos.optString(role+Constants.SHIJIAN) %>" onclick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd'})" type="text">
        <%} %>
    </tr>
<%} %>
<tr>
  <td width=30% height=50px>
  <span>更改标记 &nbsp;&nbsp; : &nbsp;&nbsp;</span>
     <input id = "ecnBiaoJi" name="ecnBiaoJi" type="text" value="<%=signInfos.optString("ecnBiaoJi") %>"> &nbsp;&nbsp;&nbsp;&nbsp;
  </td>
	<td width=30% height=50px>
		<span>更改单号 &nbsp;&nbsp; : &nbsp;&nbsp;</span>
		<input id = "ecnNumber" name="ecnNumber" type="text" value="<%=signInfos.optString("ecnNumber") %>"> &nbsp;&nbsp;&nbsp;&nbsp;
	</td>

</tr>
<tr>
<td width=30% height=50px>
		<span>更改签名 &nbsp;&nbsp; : &nbsp;&nbsp;</span>
		<input id = "GENGGAI" name="GENGGAI" type="text" value="<%=signInfos.optString("GENGGAI") %>"> &nbsp;&nbsp;&nbsp;&nbsp;
	</td>
	<td width=30% height=50px>
		<span>更改日期 &nbsp;&nbsp; : &nbsp;&nbsp;</span>
		<input id = "GENGGAISHIJIAN" name="GENGGAISHIJIAN"  class="Wdate" value="<%=signInfos.optString("GENGGAISHIJIAN") %>" onclick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd'})" type="text"> &nbsp;&nbsp;&nbsp;&nbsp;
	</td>
</tr>
	<tr><td style="border: none"></td><td style="border: none;text-align:right;"><input type="button" value="提交" onclick="check()"></td>
	<!-- <td style="border: none;text-align:right;"><input type="button" value="获取签名信息" onclick="getSignInfo()"></td> -->
	</tr>
</table>
<input id="oid" type="hidden" name="oid" value="<%=oid%>" >
</div>
</form>
</body>
</html>
