/***
 * 合并参装件信息
 *
 */

function valTdData(){
	var map = new Map();
	var $czjTable = $(".valData");
	$czjTable.each(function(index,element){
		var table = $(element)
		table.find("tr td").each(function(i,e){
			var text = $(e).text()
			if(text != "" && text != undefined){
				var arr = text.split("*");
				if(!map.containsKey(arr[0])){
					map.put(arr[0],arr[1]);
				}else{
					var value = map.get(arr[0]);
					var intValue = parseInt(value) + parseInt(arr[1])
					map.removeByKey(arr[0]);
					map.put(arr[0],intValue);
				}
			}
		});
		if(!map.isEmpty()){
			var html = "";
			for(var i =0; i < map.size();i++){
				var obj = map.element(i);
				html += "<tr><td>"+ obj.key + "*" + obj.value +"</td><tr>"
			}
			table.html(html);
			map.clear();
		}
	});
}

function initPageContent(){
	var frame = $("#floatFrame");
	frame.css({position:"absolute",visibility:"hidden"});
	var shDiv = $("#right");
	shDiv.css({height:"0px"});
	shDiv.hide();
}

function initImgStyle(){
	var frame = $("#floatFrame");
	frame.css({position:"absolute",visibility:"hidden"});
	$("#closeFloat").click( function () { frame.hide(); });
	$("#closeFloatCortona").click( function () { $("#floatCortona").hide();});
}

/***
 * 处理模型图片信息
 */
var mapImageSrc2Model = new Map();
function imgSrcValData(){
	var imgs = $(".imgSrcVal");
	imgs.each(function(){
		var imgSrc = $(this).attr("src");
		var annoPic = $(this).attr("annoPic");
		if(annoPic){
			$(this).attr({src:annoPic});
			$(this).attr({annoPic:imgSrc});
			return;
		}
		var length = imgSrc.length;
		var nIndex = imgSrc.lastIndexOf(".");
		var modelName = imgSrc.substring(0,nIndex);
		var suffix = imgSrc.substring(nIndex+1,length);
		//suffix = "ol,pvz,prt,ed,edz,eda,dwg,dxf,plt,drw -----jpg,gif,png,bmp,
		if(suffix == "pvs" || suffix == "pvz" || suffix == "ol" || suffix == "plt"
			|| suffix == "drw" || suffix == "ed" || suffix == "edz" || suffix == "prt" || suffix == "PRT"
				|| suffix == "eda" || suffix == "dwg" || suffix == "dxf"){
			modelName = unescape(modelName);
			var changeSrc = modelName + "_short.jpg"
			$(this).attr({src:changeSrc});
			var nameIndex = changeSrc.lastIndexOf("/");
			changeSrc = changeSrc.substring(nameIndex+1,changeSrc.length);
			mapImageSrc2Model.put(changeSrc,imgSrc);
		}else if(suffix == "wrl"){
			var changeSrc = "Cortona3D.png";
			$(this).attr({src:changeSrc});
			mapImageSrc2Model.put(changeSrc,imgSrc);
		}
	});
}

/**
 * 简图排布算法--装配工艺
 */
function setProcessImg(){
	var showImg = $(".showtup");
	showImg.each(function(){
		var tBody = $(this).find(".showtup-tbody");
		var size = tBody.size();
		switch(size){
			case 1 :
				var img = tBody[0];
				$(img).css({width:"570px",height:"395px"});
				$(img).find(".showtup-title").css({width:"570px"});
				$(img).find(".showtup-content").css({width:"570px",height:"370px"});
				$(img).find(".showtup-content img").css({width:"570px",height:"370px"});
				break;
			case 2:
				tBody.each(function(){
					$(this).css({width:"575px",height:"195px"});
					$(this).find(".showtup-title").css({width:"575px"});
					$(this).find(".showtup-content").css({width:"575px",height:"170px"});
					$(this).find(".showtup-content img").css({width:"575px",height:"170px"});
				});
				break;
			default:
				tBody.each(function(){
					$(this).css({width:"270px",height:"195px"});
					$(this).find(".showtup-title").css({width:"270px"});
					$(this).find(".showtup-content").css({width:"270px",height:"170px"});
					$(this).find(".showtup-content img").css({width:"270px",height:"170px"});
				});
				break;
		}
	});
}

/**
 * 简图排布算法--零件工艺
 */
function setPartProcessImg(){
	var showImg = $(".showtup_part");
	showImg.each(function(){
		var tBody = $(this).find(".showtup-tbody");
		var size = tBody.size();
		switch(size){
			case 1 :
				var img = tBody[0];
				$(img).css({width:"730px",height:"395px"});
				$(img).find(".showtup-title").css({width:"730px"});
				$(img).find(".showtup-content").css({width:"730px",height:"370px"});
				$(img).find(".showtup-content img").css({width:"730px",height:"370px"});
				break;
			case 2:
				tBody.each(function(){
					$(this).css({width:"730px",height:"195px"});
					$(this).find(".showtup-title").css({width:"730px"});
					$(this).find(".showtup-content").css({width:"730px",height:"170px"});
					$(this).find(".showtup-content img").css({width:"730px",height:"170px"});
				});
				break;
			default:
				tBody.each(function(){
					$(this).css({width:"350px",height:"195px"});
					$(this).find(".showtup-title").css({width:"350px"});
					$(this).find(".showtup-content").css({width:"350px",height:"170px"});
					$(this).find(".showtup-content img").css({width:"350px",height:"170px"});
				});
				break;
		}
	});
}

/**
 * 自适应高度算法
 */
function setTreesHeight(){
	var defaultTreeHeight = "215px";
	var defaultParentHeight = "675px";
	var winHeight = $(window).height() - 5;
	var parentHeight = $("#left").height();

	if(winHeight > parentHeight){
		$("#left").css({"height":winHeight});
		var tempHeight = winHeight - parentHeight + 215 + "px";
		$(".trees").css({"height":tempHeight});
	}else{
		$("#left").css({"height":defaultParentHeight});
		$(".trees").css({"height":defaultTreeHeight});
	}
}

function setTitleFloat(){
	$(window).scroll(function() {
		 var top = $(window).scrollTop();
		var left= $(window).scrollLeft();

		$(".title").css({left:left,top:top});
	 });
}

//---------------------fream js调用----------------------------
function initATagClick(){

	$("#top_00").click(function(){
        debugger;
		var name = $(this).attr("href");
		var nIndex = name.lastIndexOf("#");
		var nLength = name.length;
		var modelName = name.substring(nIndex + 1,nLength);
		window.parent.frames["frameDivId"].showContentByDivName(modelName);
	});

	$(".nav_anchor").each(function(){
		$(this).click(function(){
            debugger;
			var name = $(this).attr("href");
			var cortonaID = $(this).attr("cortonaID");
			var nIndex = name.lastIndexOf("#");
			var nLength = name.length;
			var modelName = name.substring(nIndex + 1,nLength);
			window.top.frames["frameDivId"].showContentByDivName(modelName,cortonaID);

		});
	});
}

function showContentByDivName(param,id){
	$("#floatFrame").hide();;
	$("#floatCortona").hide();
	var shDiv = $(".sh_div");
	var newContent = $("#newContent");
	shDiv.each(function(){
		var name = $(this).attr("name");
		//alert("name="+name + " param="+param +" id="+param);
		if(name == param){
            debugger;
			var h = $(this).html();
            newContent.html(h);
			if(name != "top_00"){
				//获取title
				var title = $("#share").html();
				var maoTarget = "<a name='" + name + "'></a>";
				title = maoTarget + "<div class='share'>" + title + "</div><br/>";
				h = title + h;

                var pdfPathEle = document.getElementById(name + "pdfpath");
                if(pdfPathEle != null){
                    var framename = name + "pdfFrame";
                    var pdfPath = pdfPathEle.value;

                    var frame = $("#" + framename);
                    frame.css({visibility:"visible",overflow:"hidden"});
                    var maskWidth = $(window).width();
                    var freamWidth = maskWidth - 45 + "px";
                    frame.css({width:freamWidth,height:"385px"});
                    var dialogLeft = (maskWidth/2) - (frame.width()/2);
                    var dialogTop = $("#" + framename).offset().top;
                    frame.css({top:dialogTop,left:dialogLeft});
                    frame.fadeIn();
                    // alert(pdfPath);
                    PDFObject.embed(pdfPath, frame);
				}
			}

		}else{
			$(this).css({height:"0px"});
			$(this).hide();
		}
	});
	if(id){
		playaction(id);
	}
}

function gongbuTRClickAction(element){
	var cortId = $(element).attr("cortonaid");
	$(".gongbuTrCss").each(function(index,elem){
		if(elem === element){
			$(elem).addClass("changeColorBySelect");
		}else{
			$(elem).removeClass("changeColorBySelect");
		}
	});
	if(cortId){
		playaction(cortId);
	}
}

//控制动画步骤
function playaction(id) {
	if(!api){
		return;
	}
	if (api.control_script.Fields.Item('vcr_play').Value != 0) {
		api.control_script.Fields.Item('vcr_play').Value = 0;
	}
	api.control_script.Fields.Item('vcr_play').Value = -1;
	api.vcr_set_position(id);
}


/**
 * Map
 */
function Map() {
    this.elements = new Array();

    this.size = function() {
        return this.elements.length;
    };

    this.isEmpty = function() {
        return (this.elements.length < 1);
    };

    this.clear = function() {
        this.elements = new Array();
    };

    this.put = function(_key, _value) {
        this.elements.push( {
            key : _key,
            value : _value
        });
    };

    this.removeByKey = function(_key) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].key == _key) {
                    this.elements.splice(i, 1);
                    return true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    this.removeByValue = function(_value) {//removeByValueAndKey
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value) {
                    this.elements.splice(i, 1);
                    return true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    this.removeByValueAndKey = function(_key,_value) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value && this.elements[i].key == _key) {
                    this.elements.splice(i, 1);
                    return true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    this.get = function(_key) {
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].key == _key) {
                    return this.elements[i].value;
                }
            }
        } catch (e) {
            return false;
        }
        return false;
    };

    this.element = function(_index) {
        if (_index < 0 || _index >= this.elements.length) {
            return null;
        }
        return this.elements[_index];
    };

    this.containsKey = function(_key) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].key == _key) {
                    bln = true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    this.containsValue = function(_value) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value) {
                    bln = true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    this.containsObj = function(_key,_value) {
        var bln = false;
        try {
            for (i = 0; i < this.elements.length; i++) {
                if (this.elements[i].value == _value && this.elements[i].key == _key) {
                    bln = true;
                }
            }
        } catch (e) {
            bln = false;
        }
        return bln;
    };

    this.values = function() {
        var arr = new Array();
        for (i = 0; i < this.elements.length; i++) {
            arr.push(this.elements[i].value);
        }
        return arr;
    };

    this.valuesByKey = function(_key) {
        var arr = new Array();
        for (i = 0; i < this.elements.length; i++) {
            if (this.elements[i].key == _key) {
                arr.push(this.elements[i].value);
            }
        }
        return arr;
    };

    this.keys = function() {
        var arr = new Array();
        for (i = 0; i < this.elements.length; i++) {
            arr.push(this.elements[i].key);
        }
        return arr;
    };

    this.keysByValue = function(_value) {
        var arr = new Array();
        for (i = 0; i < this.elements.length; i++) {
            if(_value == this.elements[i].value){
                arr.push(this.elements[i].key);
            }
        }
        return arr;
    };

    this.keysRemoveDuplicate = function() {
        var arr = new Array();
        for (i = 0; i < this.elements.length; i++) {
            var flag = true;
            for(var j=0;j<arr.length;j++){
                if(arr[j] == this.elements[i].key){
                    flag = false;
                    break;
                }
            }
            if(flag){
                arr.push(this.elements[i].key);
            }
        }
        return arr;
    };
}

function replaseProcessContent(){
	$(".procedureContent").each(function(){
		var html = $(this).html();
		while(html.indexOf("&lt;") >= 0){
			html = html.replace("&lt;","<");
		}
		while(html.indexOf("&gt;") >= 0){
			html = html.replace("&gt;",">");
		}
		while(html.indexOf("&amp;") >= 0){
			html = html.replace("&amp;","&");
		}
		$(this).html(html);
	});
}