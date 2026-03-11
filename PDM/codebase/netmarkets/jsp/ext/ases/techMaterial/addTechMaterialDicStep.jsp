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
<font size=4>添加数据字典</font>
</head>
<body>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
 <div style="text-align:left">
    <table  cellpadding="2" cellspacing="0" style="width: 40%;margin:auto">
       <tr>
       <td> <label   for="PrintLabel"  style="color:red;font-size:20px">数据字典名称:</label></td> 
       <td><input  id="Print" name="Print" type="text"  ></input></input></td>
       </tr>
     </table>
</div>
	
</body>
