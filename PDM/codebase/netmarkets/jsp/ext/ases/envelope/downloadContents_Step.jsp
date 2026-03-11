<!-- file encoding: utf-8 -->
<%@page import="ext.ases.envelope.DownloadEnvelopeContent, ext.ases.envelope.envelopeResource"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<fmt:setBundle basename="ext.ases.envelope.envelopeResource"/>
<fmt:message var="select" key="ENVELOPE_SELECT" />
<fmt:message var="all" key="ENVELOPE_ALL" />
<fmt:message var="primary" key="ENVELOPE_PRIMARY" />
<fmt:message var="print" key="ENVELOPE_PRINT" />
<fmt:message var="wvs" key="ENVELOPE_WVS" />
<fmt:message var="attachment" key="ENVELOPE_ATTACHMENT" />
<fmt:message var="download" key="ENVELOPE_DOWNLOAD" />
<fmt:message var="nocontent" key="ENVELOPE_NOCONTENT" />
<%@ page pageEncoding="utf-8"%>
<%request.setCharacterEncoding("utf-8");%>
<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>
<%
		String type = (String)request.getParameter("type");
		System.out.println("type is:" + type);
		String oid = (String)request.getParameter("oid");
		String fileName = "";
		if(type!=null){
		    DownloadEnvelopeContent pr = new DownloadEnvelopeContent(pageContext);
		    fileName = pr.generateFile();
    	}
%>

<br>
<table width="100%">
	<tr>
		<td align="center">
			<font color="red">${nocontent}</font>
		</td>
	</tr>

</table>
	<br>
<table width="100%">
	<tr>
		<td align="center">
			<input name="oid" value="<%=oid%>" type="hidden">
			<input name="type" value="<%=type%>" type="hidden">
			<select onchange=JavaScript:ChangeOption(this.value,"<%=oid%>")>
				<option selected value="#">${select}</option>
				<option value="ALL">${all}</option>
				<option value="Primary">${primary}</option>
				<option value="Print">${print}</option>
				<option value="WVS">${wvs}</option>
				<option value="Attachment">${attachment}</option>
			</select>
		</td>

	</tr>
</table>
			<br>
<br>
<br>
<br>
<table width="100%">

<%if(type!=null){%>
	<tr>
		<td align="center">
			<font color="red"><a href="netmarkets/jsp/ext/ases/envelope/exportZip.jsp?fileName=<%=fileName%>">${download}</a></font>
		</td>
	</tr>
<%}else{%>

</table>
<%}%>
</table>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>

<script>

function ChangeOption(value,oid){
	if(navigator.userAgent.indexOf("MSIE")>0) {
		window.location="downloadContents" + "?oid="+oid + "&type=" + value;
	} 
	if(isFirefox=navigator.userAgent.indexOf("Firefox")>0){
		window.location="ptc1/ext/ases/envelope/downloadContents" + "?oid="+oid + "&type=" + value;
	}
}

</script>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>