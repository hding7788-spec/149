<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title>工时统计报表</title>
<link rel="stylesheet" type="text/css" href="template.css">
</head>
<body>
<div style="padding-left: 40%;">
		<div  style="float: left;">
			<img alt="" src="${path}">
		</div>
		<div style="float: left;padding-top: 15px;">
			<font size="6"><b>工时统计报表</b></font>
		</div>
	</div>
	<table name="part_info" id="tab_blank" width="100%"
		class="tb_part_info" cellpadding="0" cellspacing="0" border="0">
		<thead style="background-color: #87CEFA;" >
			<th height="35px">车间</th>
			<th>件号</th>
			<th>准备工时(h)</th>
			<th>单件工时(h)</th>
			<th>每组数量(个)</th>
		</thead>
		${tableshour}
	</table>
</body>
</html>