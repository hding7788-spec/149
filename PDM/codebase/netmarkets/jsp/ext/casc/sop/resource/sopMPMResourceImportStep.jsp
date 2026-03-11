<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ page import="ext.casc.sop.constants.SopConstants" %>

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
		debugger;
		var file = document.getElementById("file2").value;
		if (file == null || file == "") {
			alert("file is null,please input file.");
		}
		var pos = file.lastIndexOf(".");
		var lastname = file.substring(pos, file.length);
		if (lastname != ".xls" && lastname != ".xlsx") {
			alert("Please input excel file.");
			document.getElementById("span2").innerHTML="<input id=\"file2\" name=\"file2\" size=\"60\" type=\"file\" onchange=\"checkEXCELFile();\" />";
		}
	}

</script>

<table>
	<tr>
		<td align="left" colspan="2"><h3><%=SopConstants.SOP_MSG_SOPRESOURCEIMPORT_1%></h3></td>
	</tr>
	</br>
	<tr>
		<td align="right"><FONT CLASS="wizardlabel"><%=SopConstants.SOP_MSG_SOPRESOURCEIMPORT_2%></Font></td>
		</br>
		<td><span id="span2"><input id="file2" name="file2" size="60" type="file" onchange="checkEXCELFile();" /></span></td>
	</tr>
</table>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>