<%@ taglib prefix="jca"
	uri="http://www.ptc.com/windchill/taglib/components"%>
<%@page language="java" pageEncoding="GBK"
	contentType="text/html; charset=GBK"%>
	<%@page import="java.util.Map"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request"/>
<html>
<head>
<font size=4>新建工艺物资名称</font>
</head>
<body>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
 <div style="text-align:center">
    <table  cellpadding="2" cellspacing="0" style="width: 40%;margin:auto">
       <tr>
       <td> <label   for="tmNumberLabel" style="font-size:20px">编号:</label></td> 
       <td><input   id="tmNumber" name="" type="text" value="自动生成" readonly="readonly" ></input></td>
       </tr>
       </br>
       <tr>
       <td> <label   for="tmNameLabel"  style="color:red;font-size:20px">名称:</label></td> 
       <td><input  id="tmame" name="tmame" type="text"  ></input></input></td>
       </tr>
     </table>
</div>
	
</body>

</html>
