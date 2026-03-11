<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<title>关键工序工步统计报表</title>
<link rel="stylesheet" type="text/css" href="template.css">
</head>
<body>
<div>
		<div style=" float: left; width: 40%;">
			<img alt="" src="${path}">
		</div>
		<div style="float: left;padding-top: 15px;">
			<font size="6"><b>关键工序工步统计报表</b></font>
		</div>
	</div>
	<table name="part_info" id="tab_blank" width="100%"
		class="tb_part_info" cellpadding="0" cellspacing="0" border="0">
		<thead style="background-color: #87CEFA;" >
			<th height="35px">装配/零件加工</th>
			<th>工序/工步名称</th>
			<th>车间</th>
			<th>工种</th>
			<th>件号</th>
			<th>工步/工序内容</th>
			<th>关键工序/工步标识</th>
		</thead>
		${tableskey}
	</table>
</body>
</html>