<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentServerHelper"%>
<%@page import="wt.workflow.engine.WfVotingEventAudit"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="wt.workflow.definer.UserEventVector"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.glaway.mpm.util.IBAHelper"%>
<%@page import="com.glaway.mpm.util.WTDocumentUtil"%>
<%@page import="com.glaway.mpm.util.PropertiesUtil"%>
<%@page import="java.io.File"%>
<%@page import="java.io.InputStream"%>
<%@page import="com.glaway.mpm.util.FileUtil"%>
<%@page import="com.glaway.mpm.mpmresource.gzcard.GZCardHelper"%>
<%@page import="java.util.Map"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="wt.part.WTPart"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%@page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%
	NmOid actionOid = commandBean.getActionOid();
	Object object =  actionOid.getRef();
	WTDocument document =null;
	if(object instanceof WTDocument){
		document = (WTDocument) actionOid.getRef();
	}else if(object instanceof WTPart){
		document = WTDocumentUtil.getDocumentByNumber(((WTPart)object).getNumber());
	}
	String toolingRequirements = IBAHelper.getAnyIBAValueOfObject(document,"toolingRequirements");
	request.setAttribute("toolingRequirements", toolingRequirements);
	QueryResult result = WTDocumentUtil.getSecondaryByDocument(document);
	String localPath = PropertiesUtil.getLocalCodeBase()+ File.separator + "temp";
    List<ApplicationData> imageList=new ArrayList<ApplicationData>();
    List<ApplicationData> secondaryList=new ArrayList<ApplicationData>();
    Map<String,String > pathMap=new  HashMap<String,String >();
	while(result.hasMoreElements()){
		ApplicationData data=(ApplicationData)result.nextElement();
		String fileName = data.getFileName();
		InputStream inputStream = ContentServerHelper.service
				.findContentStream(data);
		FileUtil.writeInputStream(localPath, fileName, inputStream);
        if(fileName.contains("secondaryfile")){
        	secondaryList.add(data);
        }else{
        	imageList.add(data);
        }
        pathMap.put(data.toString(),"temp"+ File.separator +fileName );
	}
	List<String> auditList= GZCardHelper.getAuditGZCardInfo(document);
	System.out.println("auditList:"+auditList);
%>
<script>
//显示指定的图片，没有图片就不显示
function showImagePreview(showdoc) {
	var imageUrl =  showdoc.name;
	var imgObjPreview = document.getElementById("preview");
	var localImagId = document.getElementById("localImage");
	
	if(""==imageUrl.trim()){
		localImagId.style.display = 'none';
		imgObjPreview.style.display = 'none';
	}
		imgObjPreview.style.width = "300px";
		imgObjPreview.style.height = "280px";
		imgObjPreview.src = imageUrl;
		imgObjPreview.style.display = 'block';
}
//显示添加的第一个图片
function showTheFirstFile(){
	var showfile = document.getElementById("showfile");
	var uploadFileArray=showfile.children;
	if(uploadFileArray.length==0){
		showImagePreview(null);
	}else{
		showImagePreview(uploadFileArray[0]);
	}
}
</script>
<div>
	<jsp:include
		page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.mpmresource.gz.gzcard.ShowGZCardBuilder')}"
		flush="true" />
	<fieldset>
		<legend>
			&nbsp;&nbsp;&nbsp;&nbsp;工装设计要求：
		</legend>

	<table width="600" height="350" border="0" id="newGZCardTable">
		<tr align="left" height="20">
			<td width="30"></td>
			<td width="60">
				<b>要求说明:</b>
			</td>
			<td width="160"></td>
			<td >
				<b>图片:</b>
			</td>
		</tr>
		<tr align="left" height="20">
			<td></td>
			<td rowspan="2" colspan="2">
				<w:textArea id="toolingRequirements" name="toolingRequirements" value="${toolingRequirements}" cols="33" rows="20" required="false" maxLength="500" readonly="true"/>
			</td>
			<td >
				<div id="showfile" >
				<%
				for(ApplicationData data :imageList){
				%>
					<a name="temp<%=File.separator+data.getFileName()%>" id=<%=data.toString()%> onclick="showImagePreview(this);" >&nbsp;&nbsp;<%=data.getFileName()%></a>
				<%
				}
				%>
				</div>
			</td>
		</tr>
		<tr align="left" height="290">
			<td></td>
			<td  align="center">
				<div id="localImage" style="width: 300px; height: 280px" >
					<img id="preview" width=-1 height=-1 style="display: none"/>
				</div>
			</td>
		</tr>
		<tr align="left" height="20">
			<td></td>
			<td >
				<b>附件:</b>
			</td>
			
			<td colspan="2">
				<div id="showsecondaryfile">
				<%
				for(ApplicationData data :secondaryList){
				%>
					<a name="temp<%=File.separator+data.getFileName()%>" id="secondary<%=data.toString()%>"  >&nbsp;&nbsp;<%=data.getFileName().replace("secondaryfile-","")%></a>
				<%
				}
				%>
				</div>
			</td>
		</tr>
	</table>
</fieldset>
	<fieldset>
		<legend>
			&nbsp;&nbsp;&nbsp;&nbsp;签审信息：
		</legend>
		<table width="550" border="0" >
			<tr height="10">
				<td colspan="9">
					&nbsp;
				</td>
			</tr>
			<tr align="center">
				<td width="30">
				</td>
				<td width="30">
					<b>拟制:</b>
				</td>
				<td width="100">
					<%=auditList.get(0) %>
				</td>
				<td width="30">
					<b>审核:</b>
				</td>
				<td width="100">
					<%=auditList.get(1) %>
				</td>
				<td width="30">
					<b>会签:</b>
				</td>
				<td width="100">
					<%=auditList.get(2) %>
				</td>
				<td width="30">
					<b>批准:</b>
				</td>
				<td width="100">
					<%=auditList.get(3) %>
				</td>
			</tr>
		</table>
	</fieldset>
</div>
<script>
	showTheFirstFile();
</script>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
