<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<%@ page import="java.util.ResourceBundle"%>
<%@ page import="ext.casc.importdata.*"%>
<jsp:useBean id="localeBean2"
	class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request" />

<%!private static final String PART_RESOURCE = "ext.casc.importdata.productRB";%>
<%
    ResourceBundle resourceRB = ResourceBundle.getBundle(PART_RESOURCE,
					localeBean2.getLocale());
%>

<script language="javascript">


	function checkZIPFile() {
		var file = document.getElementById("file").value;
		if (file == null || file == "") {
			alert("file is null,please input file.");
		}
		var pos = file.lastIndexOf(".");
		var lastname = file.substring(pos, file.length);
		if (lastname != ".zip") {
			alert("Please input zip file.");
			document.getElementById("span1").innerHTML="<input id=\"file\" name=\"file\" size=\"60\" type=\"file\" onchange=\"checkZIPFile();\" />";
		}
	}

	function checkEXCELFile() {
		var file = document.getElementById("file2").value;
		if (file == null || file == "") {
			alert("file is null,please input file.");
		}
		var pos = file.lastIndexOf(".");
		var lastname = file.substring(pos, file.length);
		if (lastname != ".xls") {
			alert("Please input excel file.");
			document.getElementById("span2").innerHTML="<input id=\"file2\" name=\"file2\" size=\"60\" type=\"file\" onchange=\"checkEXCELFile();\" />";
		}
	}
	
</script>

<table>
	<tr>
		<td align="left" colspan="2"><h3><%=resourceRB.getString(productRB.IMPORTCAD_NOTICE)%></h3></td>
	</tr>
	<tr>
		<td align="right"><FONT CLASS="wizardlabel"><%=resourceRB.getString(productRB.IMPORTDATA_FILES_TITLE)%></Font></td>
		<td><span id="span1"><input id="file" name="file" size="60" type="file" onchange="checkZIPFile();" /></span></td>
	</tr>
	<tr>
		<td align="right"><FONT CLASS="wizardlabel"><%=resourceRB.getString(productRB.IMPORTDATA_EXCEL_TITLE)%></Font></td>
		<td><span id="span2"><input id="file2" name="file2" size="60" type="file" onchange="checkEXCELFile();" /></span></td>
	</tr>
</table>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>