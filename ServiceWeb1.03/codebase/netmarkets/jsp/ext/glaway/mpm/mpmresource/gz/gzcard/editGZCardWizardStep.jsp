<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.io.File"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.glaway.mpm.util.IBAHelper"%>
<%@page import="com.glaway.mpm.util.WTDocumentUtil"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="com.glaway.mpm.util.FileUtil"%>
<%@page import="java.io.InputStream"%>
<%@page import="wt.content.ContentServerHelper"%>
<%@page import="com.glaway.mpm.util.PropertiesUtil"%>
<%@page import="java.util.Date"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"
	prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%
	NmOid actionOid = commandBean.getActionOid();
	WTDocument document = (WTDocument) actionOid.getRef();
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
%>
<script>
function setImagePreview(docObj) {
	var hiddenDoc = document.getElementById("hidden" + docObj.name);
	var localImagId = document.getElementById("localImage");
	var imgObjPreview = document.getElementById("preview");

	//IE下，使用滤镜 
	docObj.select();
	docObj.blur();
	var imgSrc = document.selection.createRange().text;
	//必须设置初始大小 
	localImagId.style.width = "300px";
	localImagId.style.height = "280px";

	try {
		if(imgSrc!=""){
			localImagId.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
			localImagId.filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = imgSrc;
			localImagId.style.display = 'block';
			imgObjPreview.style.display = 'none';
			hiddenDoc.value = imgSrc;
			try {
				addShowUploadFile(docObj,imgSrc);
				addChooseUploadFile(docObj);
			} catch (e) {
			}
		}
	} catch (e) {
		hiddenDoc.value = "";
		alert("您上传的图片格式不正确，请重新选择!");
		document.selection.clear();
	}
	document.selection.empty();
}


function uploadSecondaryFile(docObj) {
	var hiddenDoc = document.getElementById("hidden" + docObj.id);
	addShowUploadFile(docObj,docObj.value);
	addChooseUploadFile(docObj);
	hiddenDoc.value = docObj.value;
}

//添加显示图片的链接
function addShowUploadFile(docObj,imgSrc){
	var showUploadFile = "";
    var fileName="&nbsp;&nbsp;"+imgSrc.split("\\")[imgSrc.split("\\").length-1];
    var newA ="";
    if(docObj.id.indexOf("secondary")==-1){
	   	showUploadFile = document.getElementById("showfile");
        newA = document.createElement("a");
    	newA.name="show" + docObj.id;
    	newA.id= "show" + docObj.id;
    	var newA_onclick = document.createAttribute("onclick");
    		newA_onclick.nodeValue = "showImagePreview(this);";
		newA.setAttributeNode(newA_onclick);
    	newA.innerHTML = fileName;
    }else{
    	showUploadFile = document.getElementById("showsecondaryfile");
        newA = document.createElement("a");
    	newA.name="show" + docObj.id;
    	newA.id= "show" + docObj.id;
    	newA.innerHTML = fileName;
    }
   	var deleteFile = document.createElement("img");
   		deleteFile.name="deleteshow" + docObj.id;
   		deleteFile.id= "deleteshow" + docObj.id;
   		deleteFile.src= "netmarkets/images/cancel9x9.gif";
	    var deleteFile_onclick = document.createAttribute("onclick");
	    	deleteFile_onclick.nodeValue = "deleteUploadFile(this);";
	    deleteFile.setAttributeNode(deleteFile_onclick);
    showUploadFile.appendChild(newA);
    showUploadFile.appendChild(deleteFile);
	//showUploadFile.innerHTML="<a name=\""+"show" + docObj.name+"\" id=\""+"show" + docObj.name+"\" onclick=\"showImagePreview(this);\">"+fileName+"</a>";
}
	//添加上传图片的按钮
function addChooseUploadFile(docObj){
	var chooseUploadFile = "";
	var name=parseInt(docObj.id.substring(docObj.id.length-1, docObj.id.length))+1;
	var docName=docObj.id.substring(0,docObj.id.length-1)+name;
	var hiddenName="hidden"+docObj.id.substring(0,docObj.id.length-1)+name;
	var newDoc =""
	if(docObj.id.indexOf("secondary")==-1){
		chooseUploadFile = document.getElementById("choosefile");
		newDoc = document.createElement("input");
		newDoc.name=docName;
		newDoc.id=docName;
		newDoc.type="file";
		var newDoc_onchange = document.createAttribute("onchange");
			newDoc_onchange.nodeValue = "setImagePreview(this);";
			newDoc.setAttributeNode(newDoc_onchange);
	}else{
		chooseUploadFile = document.getElementById("choosesecondaryfile");
		newDoc = document.createElement("input");
		newDoc.name=docName;
		newDoc.id=docName;
		newDoc.type="file";
		var newDoc_onchange = document.createAttribute("onchange");
			newDoc_onchange.nodeValue = "uploadSecondaryFile(this);";
			newDoc.setAttributeNode(newDoc_onchange);
	}
	var newHidden = document.createElement("input");
		newHidden.name=hiddenName;
		newHidden.id=hiddenName;
		newHidden.type="hidden";
	chooseUploadFile.appendChild(newDoc);
	chooseUploadFile.appendChild(newHidden);
	docObj.style.display = 'none';
   // chooseUploadFile.innerHTML="<input name=\""+docName+"\" type=\"file\" id=\""+docName+"\" onchange=\"setImagePreview(this);\" /><input name=\""+hiddenName+"\" type=\"hidden\" id=\""+hiddenName+"\" />";
    
}
//显示指定的图片，没有图片就不显示
function showImagePreview(showdoc) {
	var imageUrl = "";
	if(showdoc==null){
		imageUrl = "";
	}else if(showdoc.id.indexOf("wt.content.ApplicationData")!=-1){
		imageUrl = showdoc.name;
	}else if(showdoc.id.indexOf("show")!=-1){
		imageUrl = document.getElementById(showdoc.id.replace("show","hidden")).value;
	}else {
		imageUrl = document.getElementById("hidden"+showdoc.id).value;
	}
	var imgObjPreview = document.getElementById("preview");
	var localImagId = document.getElementById("localImage");
	
	if(""==imageUrl.trim()){
		localImagId.style.display = 'none';
		imgObjPreview.style.display = 'none';
	}
	if(showdoc.id.indexOf("wt.content.ApplicationData")!=-1){
		imgObjPreview.style.width = "300px";
		imgObjPreview.style.height = "280px";
		imgObjPreview.src = imageUrl;
		imgObjPreview.style.display = 'block';
	}else{
		localImagId.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
		localImagId.filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = imageUrl;
		localImagId.style.display = 'block';
		imgObjPreview.style.display = 'none';
	}
}
//删除以选择的图片
function deleteUploadFile(deleteshowdoc){
	if(deleteshowdoc.id.indexOf("wt.content.ApplicationData")==-1){
		var doc = document.getElementById(deleteshowdoc.id.replace("deleteshow",""));
		var hiddenDoc = document.getElementById(deleteshowdoc.id.replace("deleteshow","hidden"));
		var showDoc = document.getElementById(deleteshowdoc.id.replace("deleteshow","show"));
			doc.parentNode.removeChild(doc); 
			hiddenDoc.parentNode.removeChild(hiddenDoc); 
			showDoc.parentNode.removeChild(showDoc); 
			deleteshowdoc.parentNode.removeChild(deleteshowdoc);
	}else{
		var dataOid="";
		var showDoc ="";
		if(deleteshowdoc.id.indexOf("secondary")==-1){
			dataOid=deleteshowdoc.id.replace("deleteshow","");
			showDoc = document.getElementById(dataOid);
		}else{
			dataOid=deleteshowdoc.id.replace("deleteshow","").replace("secondary","");
		  	showDoc = document.getElementById("secondary"+dataOid);
		}
		var deleteUploadedFile = document.getElementById("deleteUploadedFile");
			showDoc.parentNode.removeChild(showDoc); 
			deleteshowdoc.parentNode.removeChild(deleteshowdoc);
		if(deleteUploadedFile.value==""){
			deleteUploadedFile.value=dataOid;
		}else{
			deleteUploadedFile.value=deleteUploadedFile.value+";"+dataOid;
		}
	}
	if(deleteshowdoc.id.indexOf("secondary")==-1){
		showTheFirstFile();
	}
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

function isNum(numComponent) {
	var r1 = /^[1-9][0-9]*$/;
	if (numComponent.value != "" && !r1.test(numComponent.value)) {
		alert("请输入正整数！");
		numComponent.value = "1";
		return false;
	}
}
</script>
<jsp:include
	page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.mpmresource.gz.gzcard.EditGZCardBuilder')}"
	flush="true" />
<fieldset>
	<legend>
		&nbsp;&nbsp;&nbsp;&nbsp;工装设计要求：
	</legend>
	<input type="hidden" name="deleteUploadedFile" id="deleteUploadedFile"/>
	<table width="600" height="340" border="0" id="newGZCardTable">
		<tr align="left" height="20">
			<td width="30"></td>
			<td width="220">
				<b>选择图片:</b>
			</td>
			<td >
				<div id="showfile">
				<%
				for(ApplicationData data :imageList){
				%>
					<a name="temp<%=File.separator+data.getFileName()%>" id=<%=data.toString()%> onclick="showImagePreview(this);" >&nbsp;&nbsp;<%=data.getFileName()%></a>
					<img name="deleteshow<%=data.toString() %>" id="deleteshow<%=data.toString() %>"src="netmarkets/images/cancel9x9.gif" onclick="deleteUploadFile(this);">
				<%
				}
				%>
				</div>
			</td>
		</tr>
		<tr align="left" height="20">
			<td ></td>
			<td >
				<div id="choosefile">
					<input name="file0" type="file" id="file0" onchange="setImagePreview(this);" />
					<input name="hiddenfile0" type="hidden" id="hiddenfile0" />
				</div>				
			</td>
			<td rowspan="3" align="center">
				<div id="localImage" style="width: 300px; height: 280px" >
					<img id="preview" width=-1 height=-1 style="display: none"/>
				</div>
			</td>
		</tr>
		<tr align="left" height="20">
			<td></td>
			<td >
				<b>要求说明:</b>
			</td>
		</tr>
		<tr align="left" height="240">
			<td></td>
			<td >
				<w:textArea id="toolingRequirements" name="toolingRequirements" value="${toolingRequirements}" cols="33" rows="16" required="false" maxLength="500" />
			</td>
		</tr>
		<tr align="left" height="20">
			<td></td>
			<td >
				<b>添加附件:</b>
			</td>
			<td></td>
		</tr>
		<tr align="left" height="20">
			<td></td>
			<td>
				<div id="choosesecondaryfile">
					<input name="secondaryfile0" type="file" id="secondaryfile0" onchange="uploadSecondaryFile(this);" />
					<input name="hiddensecondaryfile0" type="hidden" id="hiddensecondaryfile0" />
				</div>	
			</td>
			<td >
				<div id="showsecondaryfile">
				<%
				for(ApplicationData data :secondaryList){
				%>
					<a name="temp<%=File.separator+data.getFileName()%>" id="secondary<%=data.toString()%>"  >&nbsp;&nbsp;<%=data.getFileName().replace("secondaryfile-","")%></a>
					<img name="deleteshowsecondary<%=data.toString() %>" id="deleteshowsecondary<%=data.toString() %>"src="netmarkets/images/cancel9x9.gif" onclick="deleteUploadFile(this);">
				<%
				}
				%>
				</div>
			</td>
		</tr>
	</table>
</fieldset>
<script>
	showTheFirstFile();
</script>
<%@include file="/netmarkets/jsp/util/end.jspf"%>