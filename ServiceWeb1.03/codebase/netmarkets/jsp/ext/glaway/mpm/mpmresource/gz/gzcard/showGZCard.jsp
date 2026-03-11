<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.glaway.mpm.util.IBAHelper"%>
<%@page import="com.glaway.mpm.util.WTDocumentUtil"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="com.glaway.mpm.util.PropertiesUtil"%>
<%@page import="java.io.File"%>
<%@page import="wt.content.ContentServerHelper"%>
<%@page import="java.io.InputStream"%>
<%@page import="com.glaway.mpm.util.FileUtil"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"
	prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>
<%@page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%
	NmOid actionOid = commandBean.getActionOid();
	WTDocument document = (WTDocument) actionOid.getRef();
	IBAHelper helper = new IBAHelper(document);
	String toolingRequirements = helper.getIBAValue("toolingRequirements");
	request.setAttribute("toolingRequirements", toolingRequirements);
	ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
	String localPath = PropertiesUtil.getLocalCodeBase() + File.separator + "temp";
	String fileName = data.getFileName();
	InputStream inputStream = ContentServerHelper.service.findContentStream(data);
	FileUtil.writeInputStream(localPath, fileName, inputStream);
	request.setAttribute("httpPath", "temp" + File.separator + fileName);
%>
<table>

	<tr>
		<td>
			<font size="5">工装申请卡</font>
		</td>
	</tr>

</table>

<div>
	<jsp:include
		page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.mpmresource.gz.gzcard.ShowGZCardBuilder')}"
		flush="true" />
	<fieldset>
		<legend>
			&nbsp;&nbsp;&nbsp;&nbsp;工装设计要求：
		</legend>

		<table width="525" border="0">
			<tr height="10">
				<td colspan="3">
					&nbsp;
				</td>
			</tr>
			<tr align="left" height="20">
				<td>
				</td>
				<td>
					<b>要求说明:</b>
				</td>
				<td>
				</td>
				<td>
					<b>图片:</b>
				</td>
			</tr>
			<tr align="left" height="100">
				<td width="30">
				</td>
				<td width="100">
					<w:textArea id="toolingRequirements" name="toolingRequirements"
						value="${toolingRequirements}" cols="30" rows="9" required="false"
						editable="false" maxLength="255" />
				</td>
				<td width="10">
				</td>
				<td>
					<div id="localImage">
						<img id="preview" width="200" height="150" src="${ httpPath}" />
					</div>
				</td>
			</tr>
		</table>
	</fieldset>
</div>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
