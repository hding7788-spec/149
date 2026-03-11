var myPvApi;
SetPvBaseUrl("./");
var propSceneName = "";
var isPageLoaded = false;
var isBlocked = true;
var tdvalue="";


var viewStates;//meixin添加
var xsl_version = 10;
var propBackground3DColor = "#8080C0";
var propTableColor = "#000000";
var propTableBackgroundColor = "#FFFFFF";
var propTableSelectedColor = "#000000";
var propTableSelectedBackgroundColor = "#FFFFA0";

var propMessageBodyColor = "#FFFFFF";
var propMessageBodyBackgroundColor = "#808080";
var propMessageTextAreaColor = "#000000";
var propMessageTextAreaBackgroundColor = "#FFFFFF";

var propHideActions = false;
var propShowSubsteps = true;
var propAutoNumbering = true;
var propHihlightParents = true;
var propHihlightNumbers = true;

var propMuteSound = false;
var propShowMuteSoundCheckBox = false;
var propIsActiveFocus = true;
var propIsMessageboxEnabled = false;
var prop3DFrameSize = 60;
var prop3DFramePosition = "Left";

var resFolder = "pv/";
var helpFile = "generic_msg.html";
var skinID = "{0650E9E3-1999-45C1-A24F-50BBE472326B}";
var smoothcontrol_flag = true;
var axis_flag = true;
var vcr_flag = false;
var zoom_flag = true;
var isShowNavigationBar = false;
var autostopbox_checked = false;
var warningbox_checked = true;
var autostopOnSubsteps = false;
var autorepeat = false;
var startAfterLoading = false;
var skin_navigation = {spin: true, pan: true, zoom: true, fit: false, setcenter: true, skipsensors: true};
var exporterVersion = "6.1.3.417 (64-bit)";
var iLoadingMessage = "Loading...";
var iWarningErrorOnVRMLLoading = "VRML Loading error.";
var iWarningCortonaNotFound = "Cortona3D Viewer cannot be loaded. Please check Microsoft Internet Explorer security options or install Cortona3D Viewer.";
var iWarningEmbedCortonaNotFound = "Cortona3D Viewer cannot be loaded. Please install Cortona3D Viewer.";
var iWarningUnsupportedFile = "Error: The specified file cannot be identified as a supported type.";
var iContextMenuCortonaProperties = "Cortona Properties";
var iContextMenuAbout = "About";
var iCaptionCortonaNotFound = "Cortona3D Viewer cannot be loaded.<br>Please check Microsoft Internet Explorer security options or install <a href=\"http://www.cortona3d.com/install/\">Cortona3D Viewer</a>.";
var iCaptionEmbedCortonaNotFound = "Cortona3D Viewer cannot be loaded.<br>Please install <a href=\"http://www.cortona3d.com/install/\">Cortona3D Viewer</a>.";
var iWarningCortonaOldVersion = "Outdated version of the Cortona3D Viewer is found on your computer. This cannot render 3D scenes. Update Cortona3D Viewer to the latest version.";
var iMessageCloseButton = "Close";
var TXT_ABOUT_DOCUMENT = "3D Procedure Version:";
var TXT_ABOUT_CORTONA = "Cortona3D Viewer Version:";
var eventsArray = new Array();
var actions_list = [];
var items_actions = [[0, 1, 2, 3, 4],[0, 1, 2, 3],[4],[5],[6, 7],[6],[7],[8, 9, 10]];
var items_scroll = [[],[0, 1, 2, 3],[4],[5],[],[6],[7],[8, 9, 10]];

var globalAnnoName='';
var arrPicNav;
var currSelectImg = 0;
var isStep = false;

function showGBFT(framename,pdfPath) {
	debugger;
    var frame = $("#floatFrame");
    frame.css({visibility:"visible"});
    var maskWidth = $(window).width();
    var freamWidth = maskWidth - 15 + "px";
    frame.css({width:freamWidth,height:"385px"});
    var dialogLeft = (maskWidth/2) - (frame.width()/2);
    var dialogTop = $("#" + framename).offset().top;
    frame.css({top:dialogTop,left:dialogLeft});
    frame.fadeIn();
    PDFObject.embed(pdfPath, "#floatFrame");
}

function showImageModel(imgTag,isGongBu)
{
	isStep = isGongBu;
	var selTag = event.srcElement;
	var frame = $("#floatFrame");
	var image = $("#floatImage");
	var cortonaFloat = $("#floatCortona");
	frame.css({visibility:"visible"});
//	var thistop=$(selTag).offset().top;
    var maskWidth = $(window).width();
    var freamWidth = maskWidth - 15 + "px";
    frame.css({width:freamWidth,height:"385px"});
//    var dialogTop =  thistop;
    var dialogLeft = (maskWidth/2) - (frame.width()/2);
    var dialogTop = $("#baginFloat").offset().top;
    if(isGongBu){
    	dialogTop = $(imgTag).offset().top;
    }

	var imgSrc = selTag.src;
	//注释集处理
	var $tag = $(selTag);
	var annSrc = $tag.attr("annoPic");
	if(annSrc){
		frame.css({top:dialogTop,left:dialogLeft});
		frame.fadeIn();

		globalAnnoName = $tag.attr("annoName");
		cortonaFloat.hide();
		image.css({width:freamWidth,height:"385px"});
		image.show();
		if(!isGongBu){
			initPicNavOption(imgTag)
		}
		myPvApi.LoadModel(annSrc, "");
		myPvApi.OnLoadComplete = loadAnno;

		clearListView();
		myPvApi.OnAddViewState = myAddViewState;
		listViewAction();

		return;
	}
//	escape();编码
//	unescape();解码
	imgSrc = unescape(imgSrc);
	var length = imgSrc.length;
	var index = imgSrc.lastIndexOf("/");
	if(index > -1){
		var name = imgSrc.substring(index+1,length);
		var flag = mapImageSrc2Model.get(name);
		if(flag){
			imgSrc = flag;
		}
	}
	//获取imgSrc 判断是否3d动画 加载不同的组件展示
	var nIndex = imgSrc.lastIndexOf(".");
	var nLength = imgSrc.length;
	var modelName = imgSrc.substring(nIndex + 1,nLength);
	if(modelName == "wrl"){
		image.hide();
		cortonaFloat.css({width:freamWidth,height:"385px"});
		cortonaFloat.css({visibility:"visible",top:dialogTop,left:dialogLeft});
		cortonaFloat.show();
	}else{
		cortonaFloat.hide();
		frame.css({top:dialogTop,left:dialogLeft});
		frame.fadeIn();
		image.css({width:freamWidth,height:"385px"});
		image.show();
		if(!isGongBu){
			initPicNavOption(imgTag)
		}
		myPvApi.LoadModel(imgSrc, "");
		globalAnnoName =  imgSrc;
		myPvApi.OnLoadComplete = loadAnno;
		clearListView();
		myPvApi.OnAddViewState = myAddViewState;
		listViewAction();
	}
}

//注释集回调函数
function loadAnno(){
	if(globalAnnoName){
		myPvApi.LoadAnnotation(globalAnnoName);
	}
	//获取注释集个数
	var numAnno = myPvApi.GetNumOfAnnotations();
	 var numElements = document.getElementById("listSelection").childNodes.length;
     for (i = 0; i < numElements; ++i){
    	 document.getElementById("listSelection").remove(0);
     }
     var oOption = document.createElement("OPTION");
     oOption.text = "-----";
     oOption.value = -1;
     document.getElementById("listSelection").add(oOption);

     if(numAnno == 0){
    	 //按钮置灰
    	 _disableAnnoButOption();
     }else{
    	 for (i = 0; i < numAnno; i++) {
    		 var oOption = document.createElement("OPTION");
    		 oOption.text = myPvApi.GetAnnotationName(i);
    		 oOption.value = i;
    		 document.getElementById("listSelection").add(oOption);
    	 }
    	//按钮还原
    	 _enableAnnoButOption();
     }
     changePicNavOptionEnable(currSelectImg);

     //meixin添加
	 viewStates = myPvApi.ListViewStates();
}

//视图
function myAddViewState(name, type) {
	var oOption = document.createElement("OPTION");
    oOption.text = name;
    oOption.value = name + " " + type;
    if(!(oOption.text == "F" || oOption.text =="C" || oOption.text =="B" || oOption.text =="D")){
    	document.getElementById("listView").add(oOption);
    }
    if(oOption.text == "Default"){
    	document.getElementById("listView").value = oOption.value;
    }
}

function clearListView(){
	var viewElements = document.getElementById("listView").childNodes.length;
    for (i = 0; i < viewElements; ++i){
   	 document.getElementById("listView").remove(0);
    }
}

function listViewAction(){
	var my_array = document.getElementById("listView").value.split(" ");
    var name = my_array[0];
    var type = my_array[1];
    myPvApi.SetViewState(name, type);
}

/* Starts the Animation for selected option */
function startAnimation() {
	var annoNum = document.getElementById("listSelection").value;
	if (annoNum == -1) {
		alert("请选择注释集!");
    }
    myPvApi.StartAnimation(true);
}

/* Stops the Animation for selected option */
function stopAnimation() {
    myPvApi.StopAnimation();
}

function closeCVWindow(){
	$("#floatFrame").hide();
}

function closeC3DWindow(){
	$("#floatCortona").hide();
}

function listSelected(){
	 var annoNum = document.getElementById("listSelection").value;
     var annoName = myPvApi.GetAnnotationName(annoNum);
     if (annoNum != -1) {
         myPvApi.LoadAnnotation(annoName);
     }
}

function _disableAnnoButOption(){
	document.getElementById("listSelection").disabled = true;
	document.getElementById("start_but").disabled = true;
	document.getElementById("stop_but").disabled = true;
}

function _enableAnnoButOption(){
	document.getElementById("listSelection").disabled = false;
	document.getElementById("start_but").disabled = false;
	document.getElementById("stop_but").disabled = false;
}

function initPicNavOption(objTag){
//	var selfSrc = $(objTag).attr("src");
	var selfSrc = objTag.src;
	currSelectImg = 0;
	//清空arr
	arrPicNav = new Array();
	//简图部分 添加导航功能
	//===简图导航功能===
	var p = $(objTag).parentsUntil(".canzhuang");
	var cs = p.children(".showtup-tbody");
	cs.each(function(){
//		var tempSrc = $(this).find("img").attr("src");
		var tempSrc = $(this).find("img")[0].src;
		tempSrc = unescape(tempSrc);
		var annoPic = $(this).find("img").attr("annoPic");

		var tempObj = new Object();
		tempObj.index = arrPicNav.length + 1;
		if(selfSrc == tempSrc){
			currSelectImg = arrPicNav.length;
		}
		tempObj.imgSrc = tempSrc;

		if(annoPic){
			tempObj.targetSrc = annoPic;
			arrPicNav.push(tempObj);
		}else{
			var length = tempSrc.length;
			var index = tempSrc.lastIndexOf("/");
			if(index > -1){
				var name = tempSrc.substring(index+1,length);
				var flag = mapImageSrc2Model.get(name);
				if(flag){
					tempSrc = flag;
				}
			}else{
				var flag = mapImageSrc2Model.get(tempSrc);
				if(flag){
					tempSrc = flag;
				}
			}
			tempObj.targetSrc = tempSrc;

			//过滤corn3D动画
			var nLength = tempSrc.length;
			var nIndex = tempSrc.lastIndexOf(".");
			var suffix = tempSrc.substring(nIndex+1,nLength);
			if(suffix != "wrl"){
				arrPicNav.push(tempObj);
			}
		}
	});
}

function previous(){
	//上一张简图
	currSelectImg--;
	var prevObj = arrPicNav[currSelectImg];
	myPvApi.LoadModel(prevObj.targetSrc, "");
	globalAnnoName =  prevObj.targetSrc;
	myPvApi.OnLoadComplete = loadAnno;
	clearListView();
	myPvApi.OnAddViewState = myAddViewState;
	listViewAction();
}

function next(){
	//下一张
	currSelectImg++;
	var nextObj = arrPicNav[currSelectImg];
	myPvApi.LoadModel(nextObj.targetSrc, "");
	globalAnnoName =  nextObj.targetSrc;
	myPvApi.OnLoadComplete = loadAnno;
	clearListView();
	myPvApi.OnAddViewState = myAddViewState;
	listViewAction();
}

function changePicNavOptionEnable(i){
	if(isStep){
		document.getElementById("prev_but").disabled = true;
		document.getElementById("next_but").disabled = true;
		return;
	}
	var arrL = arrPicNav.length;
	if(arrL <= 1){
		document.getElementById("prev_but").disabled = true;
		document.getElementById("next_but").disabled = true;
	}else{

		document.getElementById("prev_but").disabled = true;
		document.getElementById("next_but").disabled = true;

		if(i+1 >= arrL){
			document.getElementById("prev_but").disabled = false;
			document.getElementById("next_but").disabled = true;
		}

		if(i-1 >= 0 && i+1 < arrL){
			document.getElementById("prev_but").disabled = false;
			document.getElementById("next_but").disabled = false;
		}
		if(i-1 < 0){
			document.getElementById("prev_but").disabled = true;
			document.getElementById("next_but").disabled = false;
		}

	}
}