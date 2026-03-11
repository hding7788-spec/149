<%@page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@page import="wt.util.WTProperties"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<%@ page import="java.util.Map,java.util.Locale"%>
<%@ page import="wt.org.WTUser, wt.session.SessionHelper"%>
<%@ page import="java.util.Calendar,java.text.SimpleDateFormat,java.util.GregorianCalendar"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="ext.casc.constants.Constants" %>

<script language="javascript">

function checkFile() {
	var file = document.getElementById("file").value;
	if (file == null || file == "") {
		alert("file is null,please input file.");
	}
	var pos = file.lastIndexOf(".");
	var lastname = file.substring(pos, file.length);
	//if (lastname != ".doc") {
	//	alert("Please input excel file.");
	//	document.getElementById("span2").innerHTML="<input id=\"file\" name=\"file\" size=\"60\" type=\"file\" onchange=\"checkFile();\" />";
	//}
}

</script>

<table>
	<tr>
		<td align="right"><FONT CLASS="wizardlabel"><%=Constants.SELECT_UPLOAD_FILE%></Font></td>
		<td><span id="span2"><input id="file" name="file" size="60" type="file" onchange="checkFile();" /></span></td>
	</tr>
</table>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>