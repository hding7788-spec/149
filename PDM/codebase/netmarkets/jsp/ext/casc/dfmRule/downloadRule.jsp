<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@page import="wt.util.WTMessage"%>
<%@page import="java.util.Locale"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@taglib prefix="wctags" tagdir="/WEB-INF/tags"%>
<%@include file="/netmarkets/jsp/util/beginPopup.jspf"%>
<%
String contextPath = "";
%>>
<html>
<script type="text/javascript" language="javascript">
	var oid;
	function getRadValue(object) {
		oid = object.value;
	}

	function download() {
		if (oid == null || oid == "") {
			alert("请选择规则包");
		} else {
			window.opener = null;
			//window.open("<%=contextPath%>/Windchill/netmarkets/jsp/ext/casc/dfmRule/download.jsp?oid=" + oid);
			window.open("download.jsp?oid=" + oid);
			window.close();
		}
	}
</script>
<body>

	<jca:describeTableTree var="treeDescriptor" id="mytreetable" disableAction="true" nodeColumn="number" label="规则包列表">
		<jca:setComponentProperty key="selectable" value="false" />
		<jca:describeColumn id="glawayicon" label="类型" dataUtilityId="rulePackageDataUtility" sortable="false" />
		<jca:describeColumn id="version" sortable="false" />
		<jca:describeColumn id="state" sortable="false" />
		<jca:describeColumn id="operation" label="选择" sortable="false" dataUtilityId="rulePackageDataUtility" />
	</jca:describeTableTree>

	<jca:getModel var="treeModel" descriptor="${treeDescriptor}" treeHandler="rulePackageTreeHandler" />
	<jca:renderTableTree model="${treeModel}" showCount="true" showPagingLinks="true" pageLimit="200" showTreeLines="true" />

	<TABLE border=0 cellpadding=0 cellspacing=6 align=center>
		<TR>
			<TD>&nbsp;</TD>
		</TR>
		<TR>
			<TD><input type="button" name="sure" value="确定" class=wizOkBtn onClick="download();"></TD>
		</TR>
	</TABLE>
</body>
</html>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>