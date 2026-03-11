var data = {
		${data}
};
var type = ${type};
var technicsType = ${technicsType};

var items = data.items;
var len = items.length;
var lOid = null;
var mAry = null;
var msg = [];


/**
 * 获得指定元素
 */
function getE(id) {
	return document.getElementById(id);
}

/**
 * 获得指定元素的value
 */
function getValue(id) {
	return getE(id).value;
}

/**
 * 给指定元素填充html
 */
function setHTML(id, html) {
	getE(id).innerHTML = html;
}

/**
 * 设置指定元素显示样式
 */
function setDisplay(id, display){
	var e = getE(id);
	if(e)
		e.style.display = display;
}

/**
 * 检查该值是不是数值
 */
function validate(v) {
	var reg = /(^[1-9]\d*$)|(^\d+\.\d+$)/;
	return reg.test(v);
}

function validateAll(a,b,c) {
	return validate(a) && validate(b) && validate(c);
}

/**
 * 检查该输入值
 */
function check(o) {
	var v = o.value;
	v = v.replace(/\s+/g, "");
	o.value = v;
	
	if(o.name){
		add(lOid + "_zb", lOid + "_zbgs");
		add(lOid + "_dj", lOid + "_djgs");
	}
	
	if(!isEmpty(v)) {
		o.style.border = "thin solid #e0e0e0";
		if(!validate(v)) {
			alert("请输入正确数值！");
//			o.focus();
			o.style.border = "thin solid red";
		}
	}
}

function add(name, id){
	var c = document.getElementsByName(name);
	
	var sum = 0;
	for(var i = 0; i < c.length; i++){
		var v = c[i].value;
		if(v && validate(v)){
			sum += parseInt(v);
		}
	}
	getE(id).value = sum == 0 ? "" : sum;
}

/**
 * 判断是否为空
 */
function isEmpty(v) {
	return v == "";
}



/**
 * 产生左边列表
 */
function generateL() {
	var first = null;
	var isKey = false;
	var s = '';
	var lstMaterials;
	for(var i = 0; i < len; i++) {
		
		var item = items[i];
		if(item.type == "step") {
			var border = "";
			if(i != 0) {
				s += '<div align="center"><img src="../resource/jt.png"/></div>';
			} else {
				border = "border: solid 2px blue;";
				first = item.stepNumber;
				lstMaterials = item.materials
			}
			
			var gxh = "";
			if(item.isKey && item.isKey == "true"){
				isKey = true;
				gxh += "(GX)";
			}
			
			s += '<div>'
					+ '<table id="' + item.stepNumber + '" align="center" cellspacing="0" style="word-break: break-all;width: 80px;cursor: hand;border: solid 2px blue;' + border + '" oid="' + item.oid + '" zbgs="' + item.PrepareWorkHours + '" djgs="' + item.TaktTime + '" mzsl="'+ item.NumberOfGroup +'" onclick="modifyR(this);">'
						+ '<tr><td style="background-color: #c9c9c9;text-align: center;">' + item.workShop + '</td></tr>'
						+ '<tr><td style="text-align: center;">' + item.stepName + gxh + '</td></tr>'
						+ '<tr><td style="background-color: #c9c9c9;text-align: center;">' + item.workType + '</td></tr>'
						+ '</table>'
				+' </div>';
		}
	}
	
	setHTML("tables", s);
	if(isKey)
		setHTML("bt", "(GYGJ)");
	
	if(first){
		modifyR(getE(first));
		//取值
		var Materials = getMaterials(lstMaterials);
		//赋值
		setTdMaterials(Materials);
	}
}

/**
 * 获取工艺的第一个材料
 * @param lstMaterials
 * @returns
 */
function getMaterials(lstMaterials) {
	var s = '';
	if(lstMaterials!=null){
		for(var j = 0; j < lstMaterials.length; j++) {
			s=lstMaterials[0];
		}	
	}
	return s;
}

/**
 * 给加工工艺的材料赋值
 * @param Materials
 * @returns
 */
function setTdMaterials(Materials) {
	if(Materials!="") {
		setHTML("materialName",Materials.materialName);
		setHTML("materialNumber",Materials.materialNumber);
		setHTML("materialCode",Materials.materialCode);
		setHTML("materialCrision",Materials.materialCrision);
		setHTML("materialState",Materials.materialState);
	}	
}

/**
 * 根据左边选中列表动态产生中间及右边的table和插件
 */
function modifyR(o) {
	
	if(type == 1) {
		if(!operateMsg()) {
			return;
		}
	}

	if(lOid){
		setDisplay(lOid + "_fj", "none");
		setDisplay(o.oid + "_fj", "");
	}
	
	mAry = new Array();
	lOid = o.oid;
	var id = o.id;
	oldId = o.id;
	o.style.border = "solid 2px blue";
	
	var s = '<table cellspacing="0" style="word-break: break-all;width: 100%;cursor: hand;">'
			+ '<tr style="background-color: lightblue;" height="30px">' 
				+ '<td width="9%">工步号</td>'
				+ '<td width="38%">工步内容</td>'
				+ '<td width="10%">配合工种</td>'
				+ '<td width="10%">制造单位</td>';

	if(type == 1) {
		s += '<td width="10%">准备工时(h)</td>'
			+ '<td width="10%">单件工时(h)</td>'
			+ '<td width="11%">每组数量(个)</td>';
	}
	
	s += '</tr>';
	
	var s6 = '<table cellspacing="0" style="word-break: break-all;width: 100%;">'
				+ '<tr style="background-color: lightblue;" height="30px"><td width="9%">编号</td><td width="13%">名称</td><td width="13%">材料</td><td width="13%">涂装</td><td width="10%">备注</td><td width="10%">使用数量</td></tr>';
	
	var s2 = '<table cellspacing="0" style="word-break: break-all;width: 100%;">'
				+ '<tr style="background-color: lightblue;" height="30px"><td width="22%">设备名称</td><td width="26%">设备型号</td><td width="20%">使用数量</td></tr>';
	
	var s3 = '<table cellspacing="0" style="word-break: break-all;width: 100%;">'
		  		+ '<tr style="background-color: lightblue;" height="30px"><td width="22%">工装及工具名称</td><td width="26%">标准号</td><td width="10%">规格</td><td width="10%">使用数量</td></tr>';
	
	var s4 = '<table cellspacing="0" style="word-break: break-all;width: 100%;">'
	   			+'<tr style="background-color: lightblue;" height="30px"><td width="22%">材料名称</td><td width="13%">标准号</td><td width="13%">物料编码</td><td width="10%">物料状态</td><td width="10%">使用数量</td></tr>';

	for(var i = 0; i < len; i++) {
		var item = items[i];
		var children;
		
		if(item.type == "step") {
			
			if(id == item.stepNumber) {
				children = item.children;
				showUL(item.oid);
				if(technicsType == 1)
					actionplay(item);
				s2 += generateEquips(item.equips);
				s3 += generateTools(item.tools);
				s4 += generateMaterials(item.materials);
				s6 += generateParts(item.parts);
				
				var gxh = item.stepNumber;
				if(item.isKey && item.isKey == "true"){
					gxh += "(GX)";
				}
				
				setHTML("gxbh", item.stepNumber);
				setHTML("gxh", gxh);
				setHTML("gxm", item.stepName);
				setHTML("zzdw", item.workShop);
				setHTML("cz", item.workType);
				setHTML("jy", item.workType);
				setHTML("gw", "工位" + item.stepNumber);
				setHTML("gxjs", item.procedureContent);
			} else {
				getE(item.stepNumber).style.border = '';
				children = null;
			}
			
		} else {
			
			if(children) {
				var l = children.length;
				
				for(var j = 0; j < l; j++) {
					
					if(children[j]._reference == item.oid) {
						
						var content = item.procedureContent;
						if(content){
							if(content.indexOf(".JPG") != -1){
								content = '<img src="' + content + '"/>';
							}
						}

						s += '<tr height="30px" id="' + item.oid + '" onclick="showO(this);">'
								+ '<td>' + item.stepNumber + '</td>'
								+ '<td>' + content + '</td>'
								+ '<td>' + item.workType + '</td>'
								+ '<td>' + item.workShop + '</td>';	
															
						if(type == 1) {
							var zbgs = "";
							var djgs = "";
							var mzsl = "";
							var isExist = false;
							for(var k=0; k < msg.length; k++) {
								
								if(msg[k].oid == item.oid) {
									zbgs = msg[k].zbgs;
									djgs = msg[k].djgs;
									mzsl = msg[k].mzsl;
									isExist = true;
									break;
								}
							}
							
							if(!isExist) {
								zbgs = item.PrepareWorkHours;
								djgs = item.TaktTime;
								mzsl = item.NumberOfGroup;
							}
							
							s += '<td><input size="9" name="' + lOid + '_zb" id="' + item.oid + '_zbgs" value="' + zbgs + '" type="text"  onblur="check(this);" /></td>'
								+ '<td><input size="9" name="' + lOid + '_dj" id="' + item.oid + '_djgs" value="' + djgs + '" type="text"  onblur="check(this);" /></td>'
								+ '<td><input size="9" id="' + item.oid + '_mzsl" value="' + mzsl + '" type="text"  onblur="check(this);" /></td>';
						}
						
						s += '</tr>';
						mAry.push(item.oid);
						
					}
				}
			}
		}
	}
	s += '</table>';
	s2 += '</table>';
	s3 += '</table>';
	s4 += '</table>';
	setHTML("one", s);
	setHTML("two", s2);
	setHTML("three", s3);
	setHTML("four", s4);
	setHTML("seven", s6);
	
	if(type == 1){
		var zbgs = "";
		var djgs = "";
		var mzsl = "";
		var isExist = false;
		for(var k = 0; k < msg.length; k++) {
								
			if(msg[k].oid == lOid) {
				zbgs = msg[k].zbgs;
				djgs = msg[k].djgs;
				mzsl = msg[k].mzsl;
				isExist = true;
				break;
			}
		}
		
		if(!isExist) {
			zbgs = o.zbgs;
			djgs = o.djgs;
			mzsl = o.mzsl;
		}
		
		var s5 = '&nbsp;&nbsp;工序准备工时(h)：<input size="7" type="text" id="' + lOid + '_zbgs" value="' + zbgs + '" onblur="check(this);"/>'
		      + '&nbsp;&nbsp;工序单件工时(h)：<input size="7" type="text" id="' + lOid + '_djgs" value="' + djgs + '" onblur="check(this);" />'
		      + '&nbsp;&nbsp;工序每组数量(个)：<input size="7" type="text" id="' + lOid + '_mzsl" value="' + mzsl + '" onblur="check(this);"/>';
		setHTML("six", s5);
	}
	
	
	if(type == 1) {
		setHTML("five", '<input type="button" value="提交" onclick="submit();"/>');
	}
}

var oldO = null;
function showO(o) {
	
	if(oldO) {
		oldO.style.backgroundColor = "white";
	}
	
	o.style.backgroundColor = "#0070f0";
	oldO = o;
	var oid = o.id;
	
	showUL(oid);
	
	var s6 = '<table cellspacing="0" style="word-break: break-all;width: 100%;">'
			+ '<tr style="background-color: lightblue;" height="30px"><td width="9%">编号</td><td width="13%">名称</td><td width="13%">材料</td><td width="13%">涂装</td><td width="10%">备注</td><td width="10%">使用数量</td></tr>';

	var s2 = '<table cellspacing="0" style="word-break: break-all;width: 100%;">'
			+ '<tr style="background-color: lightblue;" height="30px"><td width="22%">设备名称</td><td width="26%">设备型号</td><td width="20%">使用数量</td></tr>';

	var s3 = '<table cellspacing="0" style="word-break: break-all;width: 100%;">'
  			+ '<tr style="background-color: lightblue;" height="30px"><td width="22%">工装及工具名称</td><td width="26%">标准号</td><td width="10%">规格</td><td width="10%">使用数量</td></tr>';

	var s4 = '<table cellspacing="0" style="word-break: break-all;width: 100%;">'
			+ '<tr style="background-color: lightblue;" height="30px"><td width="22%">材料名称</td><td width="13%">标准号</td><td width="13%">物料编码</td><td width="10%">物料状态</td><td width="10%">使用数量</td></tr>';
	
	for(var i = 0; i < len; i++) {
		
		var item = items[i];
		if(item.oid == oid) {
			s2 += generateEquips(item.equips);
			s3 += generateTools(item.tools);
			s4 += generateMaterials(item.materials);
			s6 += generateParts(item.parts);
		}
	}
	
	s2 += "</table>";
	s3 += "</table>";
	s4 += "</table>";
	setHTML("two", s2);
	setHTML("three", s3);
	setHTML("four", s4);
	setHTML("seven", s6);
}

/**
 * 生成parts的tr
 */
function generateParts(parts) {
	var s = '';
	if(parts)
		for(var j = 0; j < parts.length; j++)
			s += '<tr height="30px"><td>' + parts[j].partNumber + '</td><td>' + parts[j].partName + '</td><td>' + parts[j].material + '</td><td>' + parts[j].dutu + '</td><td>' + parts[j].remark + '</td><td>' + parts[j].useCount + '</td></tr>';
	return s;
}

/**
 * 生成equips的tr
 */
function generateEquips(equips) {
	var s = '';
	
	if(equips)
		for(var j = 0; j < equips.length; j++)
			s += '<tr height="30px"><td>' + equips[j].eqName + '</td><td>' + equips[j].eqModel + '</td><td>' + equips[j].useCount + '</td></tr>';
	
	return s;
}

/**
 * 生成tools的tr
 */
function generateTools(tools) {
	var s = '';
	
	if(tools)
		for(var j = 0; j < tools.length; j++)
			s += '<tr height="30px"><td>' + tools[j].toolName + '</td><td>' + tools[j].toolStdNum + '</td><td>' + tools[j].toolSpec + '</td><td>' + tools[j].useCount + '</td></tr>';
	
	return s;
}

/**
 * 生成materials的tr
 */
function generateMaterials(materials) {
	var s = '';
	
	if(materials)
		for(var j = 0; j < materials.length; j++)
			s += '<tr height="30px"><td>' + materials[j].materialName + '</td><td>' + materials[j].materialCrision + '</td><td>' + materials[j].materialCode + '</td><td>' + materials[j].materialState + '</td><td>' + materials[j].useCount + '</td></tr>';
	
	return s;
}

/**
 * 操作msg
 */
function operateMsg() {
	if(lOid) {
		
		for(var i=0;i<mAry.length;i++) {
			
			var oid = mAry[i];
			var zbgs = getValue(oid + "_zbgs");
			var djgs = getValue(oid + "_djgs");
			var mzsl = getValue(oid + "_mzsl");		
				
			if(!modifyMsg(oid, zbgs, djgs, mzsl)) {
				return false;
			}
		}
		
		var zbgs = getValue(lOid + "_zbgs");
		var djgs = getValue(lOid + "_djgs");
		var mzsl = getValue(lOid + "_mzsl");
		
		if(!modifyMsg(lOid, zbgs, djgs, mzsl)) {
			return false;
		}
		
	}	
	return true;
}

/**
 * 检查msg中是否存在oid。若存在，修改属性，并返回true；否之直接返回false
 */
function checkEIsExists(oid, zbgs, djgs, mzsl) {
	for(var j=0;j<msg.length;j++) {
		
		var e = msg[j];
		if(e.oid == oid) {
			e.zbgs = zbgs;
			e.djgs = djgs;
			e.mzsl = mzsl;
			return true;
		}
	}
	return false;
}

/**
 * 修改msg元素
 */
function modifyMsg(oid, zbgs, djgs, mzsl) {
	
	if(!isEmpty(zbgs) && !isEmpty(djgs) && !isEmpty(mzsl)) {
		
		if(validateAll(zbgs, djgs, mzsl)) {
			
			if(!checkEIsExists(oid, zbgs, djgs, mzsl)) {
				msg[msg.length] = {oid:oid,zbgs:zbgs,djgs:djgs,mzsl:mzsl};					
			}
			
		} else {
			alert("请输入正确数值！");
			return false;
		}
		
	} else if(isEmpty(zbgs) && isEmpty(djgs) && isEmpty(mzsl)) {
		
		for(var j=0;j<msg.length;j++) {
			
			if(msg[j].oid == oid) {
				msg.splice(j, 1);
				break;
			}
		}
		
	} else {
		alert("信息没填完整");
		return false;
	}
	return true;
}

/**
 * json转换成string
 */
function json2Str() {
	var s = "[";
	for(var i=0;i<msg.length;i++) {
		
		if(i != 0) {
			s += ",";
		}
		
		s += "{";
		var j = 0;
		
		for(key in msg[i]) {
			
			if(j++ != 0) {
				s += ",";
			}
			
			s += key+ ":" + msg[i][key];
		}
		s += "}";
	}
	s += "]";
	return s;
}

/**
 * 提交
 */
function submit() {
	
	if(!operateMsg()) {
		return;
	}
	
	if(msg.length == 0) {
		alert("没填写信息");
		return;
	}
	
	var url = "http://pds.nriet.com/Windchill/netmarkets/jsp/glaway/mpm/saveDingE.jsp";
	location.href = url + "?msg=" + json2Str();
}


function actionplay(item){
    if(item){
        playaction(item.cortonaID);
    }
}



/**
 * Cortona
 */
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

var resFolder = "../resource/res_GENERIC_PRC/";
var helpFile = "en/help.html";
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
var skin_navigation = {
    spin: true,
    pan: true,
    zoom: true,
    fit: false,
    setcenter: true,
    skipsensors: true
};
var exporterVersion = "6.1.3.417 (64-bit)";

var iLoadingMessage = "";

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

var actions_list = ["r63abb15a-057d-47e7-a695-0022b7f76a26", "re8f23dd2-cac2-40e1-b821-4f0b18d56414", "rfdff7a04-17ea-4858-a521-1783c6f5f3e3", "rcb85e7fc-b766-4c5c-a9fd-2a2a3783bdfe", "rd1ed826c-2033-48a2-b28c-d2224fe3de33", "r912e0185-6b06-4a4c-a9af-27756368599a", "r777ebed0-aadc-41ee-a6fd-77befc90f9e7", "rd32cbe0d-4956-43bb-8eef-49824f598475", "rec5c532d-035d-45c8-a550-cb9d179f93de", "r286c7a85-e066-4665-a285-1acea1b9d708", "r00a44def-88c2-4f3b-a8e2-2ec1ffd2c03b"];

var items_actions = [[0, 1, 2, 3, 4], [0, 1, 2, 3], [4], [5], [6, 7], [6], [7], [8, 9, 10]];
var items_scroll = [[], [0, 1, 2, 3], [4], [5], [], [6], [7], [8, 9, 10]];

var isPageLoaded = false;
var isBlocked = true;

function on_t(n){
    if (!isBlocked) 
        on_tab_click(n);
}

function on_at(id){
    if (!isBlocked) 
        on_atab_click(id);
}