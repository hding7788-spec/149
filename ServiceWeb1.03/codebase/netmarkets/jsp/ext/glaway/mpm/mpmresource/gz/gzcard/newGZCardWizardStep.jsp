<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.io.File"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"
	prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

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
		var hiddenDoc = document.getElementById("hidden" + docObj.name);
		addShowUploadFile(docObj,docObj.value);
		addChooseUploadFile(docObj);
		hiddenDoc.value = docObj.value;
	}

	//添加显示图片的链接
    function addShowUploadFile(docObj,imgSrc){
    	var showUploadFile = "";
	    var fileName="&nbsp;&nbsp;"+imgSrc.split("\\")[imgSrc.split("\\").length-1];
	    var newA ="";
        if(docObj.name.indexOf("secondary")==-1){
    	   	showUploadFile = document.getElementById("showfile");
	        newA = document.createElement("a");
	    	newA.name="show" + docObj.name;
	    	newA.id= "show" + docObj.name;
	    	var newA_onclick = document.createAttribute("onclick");
	    		newA_onclick.nodeValue = "showImagePreview(this);";
    		newA.setAttributeNode(newA_onclick);
	    	newA.innerHTML = fileName;
        }else{
        	showUploadFile = document.getElementById("showsecondaryfile");
	        newA = document.createElement("a");
	    	newA.name="show" + docObj.name;
	    	newA.id= "show" + docObj.name;
	    	newA.innerHTML = fileName;
        }
	   	var deleteFile = document.createElement("img");
	   		deleteFile.name="deleteshow" + docObj.name;
	   		deleteFile.id= "deleteshow" + docObj.name;
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
		var name=parseInt(docObj.name.substring(docObj.name.length-1, docObj.name.length))+1;
		var docName=docObj.name.substring(0,docObj.name.length-1)+name;
		var hiddenName="hidden"+docObj.name.substring(0,docObj.name.length-1)+name;
		var newDoc =""
		if(docObj.name.indexOf("secondary")==-1){
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
		}else if(showdoc.name.indexOf("show")!=-1){
    		imageUrl = document.getElementById(showdoc.name.replace("show","hidden")).value;
		}else{
			imageUrl = document.getElementById("hidden"+showdoc.name).value;
		}
		var imgObjPreview = document.getElementById("preview");
		var localImagId = document.getElementById("localImage");
		
		if(""==imageUrl.trim()){
			localImagId.style.display = 'none';
			imgObjPreview.style.display = 'none';
		}
			localImagId.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
			localImagId.filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = imageUrl;
			localImagId.style.display = 'block';
			imgObjPreview.style.display = 'none';
	}
	//删除以选择的图片
	function deleteUploadFile(deleteshowdoc){
		var doc = document.getElementById(deleteshowdoc.name.replace("deleteshow",""));
		var hiddenDoc = document.getElementById(deleteshowdoc.name.replace("deleteshow","hidden"));
		var showDoc = document.getElementById(deleteshowdoc.name.replace("deleteshow","show"));
			doc.parentNode.removeChild(doc); 
			hiddenDoc.parentNode.removeChild(hiddenDoc); 
			showDoc.parentNode.removeChild(showDoc); 
		deleteshowdoc.parentNode.removeChild(deleteshowdoc);
		if(deleteshowdoc.name.indexOf("secondary")==-1){
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

	function setName(nameComponent) {
		var name = nameComponent.value.split("|")[1];
		document.getElementById("name").value = name;
	}
</script>


<jsp:include
	page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.mpmresource.gz.gzcard.NewGZCardBuilder')}"
	flush="true" />
<fieldset>
	<legend>
		&nbsp;&nbsp;&nbsp;&nbsp;工装设计要求：
	</legend>
	<table width="600" height="340" border="0" id="newGZCardTable">
		<tr align="left" height="20">
			<td width="30"></td>
			<td width="220">
				<b>选择图片:</b>
			</td>
			<td >
				<div id="showfile">
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
				<w:textArea id="toolingRequirements" name="toolingRequirements" value="" cols="33" rows="16" required="false" maxLength="500" />
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
				</div>
			</td>
		</tr>
	</table>
</fieldset>
<%@include file="/netmarkets/jsp/util/end.jspf"%>