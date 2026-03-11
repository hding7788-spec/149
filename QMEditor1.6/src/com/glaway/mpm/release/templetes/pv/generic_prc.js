/*
 * generic_prc.js 15:51 10.10.2011
 *
 */
var scriptVersion="1.34";

var api = null;
var scene_is_loaded = false;
var is_stop_by_step = false;
var cortona = null;
var isMSIE = true;
var isOpera = false;
var isFF = false;
var isChrome = false;

var warningModelessDialog=null;
var active_events = new Array();
var warningIsOpen = false;
var warningText = "";
var messageTitleText = "";
var isLoaded = false;
var cMenu = null;
var cortona_bttn_down = 0;
var chkcnt = 0;

function on_load() {
	insertCortona();
	set_block(true);
//	hlight_first();
}

function on_unload() {
	on_beforeunload();
}

function insertCortona(){
	isMSIE=(navigator.appName=="Microsoft Internet Explorer");
	if(!isMSIE){
		isOpera=(navigator.appName.toLowerCase()=="opera");
		if(!isOpera){
			isFF = (navigator.userAgent.toLowerCase().lastIndexOf("firefox")>0);
			if(!isFF){
				isChrome = (navigator.userAgent.toLowerCase().lastIndexOf("chrome")>0);
			}
		}
	}
	window.onresize = resize_container;
	resize_container();
	var scene = (propSceneName!=".wrl")? propSceneName : "";
	var muteSound = (propMuteSound)? 1: 2;
	var BackColor = "FFFFFF";
	var loadingText = iLoadingMessage;
	if(isMSIE){
		try{BackColor = propBackground3DColor.substr(5,2)+propBackground3DColor.substr(3,2)+propBackground3DColor.substr(1,2);}catch(e){}
		document.body.style.cursor=resFolder+"hourglas.ani";
		document.getElementById("cortona_holder").innerHTML = '<table id="loading_table" width="100%" height="100%"><tr><td align="center" valign="middle" class="loadingMessage">'+loadingText+'</td></tr></table><span style="visibility: hidden;position: absolute;"><span id="oContextHTML" style="position: absolute;"></span></span><object style="display:none;" classid="CLSID:86A88967-7A20-11D2-8EDA-00600818EDB1" type="application/x-oleobject" width="100%" height="100%" id="cortonaControl"><param name="Scene" value="'+scene+'"><param name="BackColor" value="&h'+BackColor+'"><param name="NavigationBar" value="'+((isShowNavigationBar)?1:0)+'"><param name="Skin" value="'+skinID+'"><param name="ConsoleMode" value="0"><param name="ContextMenu" value="False"><param name="CpuLoading" value="80"><param name="LoadDroppedScene" value="False"><param name="RendererHints" value="5168"><param name="RendererName" value="DirectX Renderer"><param name="RendererOptimization" value="0"><param name="ShowLogo" value="False"><param name="TravelSpeed" value="2"><param name="viewpoint_transition_mode" value="0"><param name="WaitForAllResources" value="True"><param name="MuteSound" value="'+muteSound+'"></object>';
	}else{
		BackColor = propBackground3DColor;
		document.getElementById("cortona_holder").innerHTML = '<embed type="application/x-cortona" title="Cortona3D Viewer" width="100%" height="100%" id="cortonaControl" src="'+scene+'" vrml_background_color="'+BackColor+'" navigationbar="'+((isShowNavigationBar)?1:0)+'" skin="'+skinID+'" consolemode="0" contextmenu="false" cpuloading="80" loaddroppedscene="false" rendererhints="5168" renderername="DirectX Renderer" rendereroptimization="0" vrml_splashscreen="true" travel_speed="2" viewpoint_transition_mode="0" WaitForAllResourses="true" mutesound="'+muteSound+'" />';
	}
	cortona = document.getElementById("cortonaControl");
	setTimeout("on_load2();", 0);
	cMenu = new ContextMenu(document.getElementById("menu_container"));
	window.document.onkeydown=CloseContextMenu;
	window.onblur = CloseContextMenu;
}

function cortonaControl_OnSceneLoaded(bSuccess){
	if(isLoaded && cortona && cortona.Engine){
		on_cortona_scene_loaded(bSuccess);
	}else{
		setTimeout("cortonaControl_OnSceneLoaded("+bSuccess+")", chkcnt);
		chkcnt+=100;
	}
}
//function cortonaControl_OnSceneUnloaded(){on_cortona_scene_unloaded();}
function cortonaControl_MouseDown(Button, Shift, X, Y){on_cortona_mouse_down(Button, Shift, X, Y);}
function cortonaControl_MouseUp(Button, Shift, X, Y){on_cortona_mouse_up(Button, Shift, X, Y);}
//function cortonaControl_MouseMove(Button, Shift, X, Y){if(typeof(on_cortona_mouse_move)=="function")on_cortona_mouse_move(Button, Shift, X, Y);}
//function cortonaControl_OnMouseOut(){if(typeof(on_cortona_mouse_out)=="function")on_cortona_mouse_out();}
//function cortonaControl_OnKeyDown (key, shift){on_cortona_key_down(key, shift);}

function resize_container(){
	if(!isMSIE){
		if(prop3DFramePosition=="Top" || prop3DFramePosition=="Bottom"){
			document.getElementById('divContainer').style.height=Math.floor(document.body.clientHeight*(100-prop3DFrameSize)/100)+"px";
		}else{
			document.getElementById('divContainer').style.height=document.body.clientHeight+"px";
		}
	}
	CloseContextMenu();
	if(warningIsOpen && warningModelessDialog==null){
		try{
			var winH = document.body.clientHeight;
			var winW = document.body.clientWidth;
			var wH = 400;
			var wW = 500;
			var X = (winW-wW)/2;
			var Y = (winH-wH)/2;
			var cnt = document.getElementById("message_container");
			cnt.style.left=X+"px";
			cnt.style.top=Y+"px";
		}catch(e){}
	}
}
function on_cortona_scene_loaded(success){
	var isStopWaiting = false;
	if(success) {
//		alert("RootNodes.Count:" + cortona.Engine.RootNodes.Count);
		if (cortona.Engine.RootNodes.Count > 0){
			api_initialize();
			isStopWaiting = true;
		}
	}else{
		alert(iWarningErrorOnVRMLLoading);
		isStopWaiting = true;
	}
	if(isStopWaiting && isMSIE){
		document.body.style.cursor="";
		document.getElementById('loading_table').style.display = "none";
		cortona.style.display = "";
	}
}

function on_cortona_mouse_down(Button, Shift, X, Y){
	if(warningIsOpen && warningModelessDialog==null)_on_warning_close();
	cortona_bttn_down=Button;
	if(Button==1)SmartCloseContextMenu(X,Y);
	else CloseContextMenu(X,Y);
}
function on_cortona_mouse_up(Button, Shift, X, Y){
	if(cortona_bttn_down==2 && Button==0){
		cortona_bttn_down=0;
		ShowCMenu(X,Y);
	}
}

function on_cortona_mouse_move(Button, Shift, X, Y){}
function on_cortona_mouse_out(){}
function on_cortona_key_down(key, shift){
}

function api_initialize(){
	api = new SimulationAPI(cortona);
	api.on_simulation_load = _on_simulation_loaded;
	api.on_vcr_state = _on_vcr_state_changed;
	api.on_events_out = _on_simulation_events_out;
	api.on_start_new_substep = _on_start_new_substep;
	api.scene_loaded_on_start();
	isPageLoaded=true;
}

function get_basepath() {
  var path = location.href.split('/');
  path.pop();
  path.push('');
  path = path.join('/');
  return path;
}

function on_load2() {
	//手动调用api初始化
	cortonaControl_OnSceneLoaded(true);
	
	if(isLoaded)return;
	document.getElementById("autostopbox").checked=autostopbox_checked;
	document.getElementById("warningbox").checked=warningbox_checked;	
	document.getElementById("speed2").checked = true;
	try{
		cortona.uiAction("revokePropPage:{E5711FD5-464F-11D3-9D7D-00A0247A5F3F}");
		if(!("Version" in cortona)){
	  		var wrnText = (typeof(iWarningCortonaOldVersion)!='undefined')? iWarningCortonaOldVersion: "Outdated version of the Cortona3D Viewer is found on your computer.\nThis cannot render 3D scenes. Update Cortona3D Viewer to the latest version.";
	  	  	alert(wrnText);
	  	}
	}catch(e){
		if(isMSIE)document.body.style.cursor="";
		var installMessage = "";
		if(typeof(iCaptionCortonaNotFound) != 'undefined')installMessage = (isMSIE)? iCaptionCortonaNotFound: iCaptionEmbedCortonaNotFound;
		else{
			installMessage = (isMSIE)? iWarningCortonaNotFound: iWarningEmbedCortonaNotFound;
			installMessage += "<br><br><a href=\"http://www.cortona3d.com/install/\">Cortona3D Viewer</a>"
		}
		document.getElementById('cortona_holder').innerHTML = '<table width="100%" height="100%"><tr><td align="center" valign="middle" class="errorMessage">'+installMessage+'</td></tr></table>';
		document.getElementById("control_bar").style.visibility = "hidden";
		return;
	}
	isLoaded = true;
}

function on_beforeunload() {
	stopRefresher();
	scene_is_loaded = false;
	is_stop_by_step = false;
	active_events = new Array();
	_block_window();
	document.getElementById("btn_stop").style.fontWeight = 'normal';
	document.getElementById("autostopbox").disabled = true;
	try{
		cortona.style.visibility="hidden";
	}catch(e){}
	if(warningIsOpen && warningModelessDialog!=null)warningModelessDialog.close();
	warningIsOpen = false;
}

function _on_simulation_loaded(flag){
	scene_is_loaded = flag;
	if(flag) {
		api.set_ui_axis(axis_flag);
		api.set_ui_zoom(zoom_flag);
		api.set_ui_vcr(vcr_flag);
		api.set_ui_smoothcontrol(smoothcontrol_flag);
		document.getElementById("btn_stop").style.fontWeight = 'bold';
		document.getElementById("autostopbox").disabled = false;
		document.getElementById("warningbox").disabled = false;
		document.getElementById("speed1").disabled = false;
		document.getElementById("speed2").disabled = false;
		document.getElementById("speed3").disabled = false;
		try{
			if(document.getElementById('vpfreeze')){
				document.getElementById('vpfreeze').disabled=false;
				document.getElementById('vpfreeze').checked=false;
			}
		}catch(err){}
		_checkSkinControl();
		_set_stop_by_step();
		cortona.style.visibility="inherit";		
		_unblock_window();
		if(propIsActiveFocus)setTimeout('try{document.getElementById("btn_play").focus();}catch(err){}',0);
		if(startAfterLoading)play();
	}
	else {
		alert(iWarningUnsupportedFile);
	}	
}

function _checkSkinControl(){
try{
		var foundfields=new Array();
		var skinNode = cortona.Engine.Nodes.Item("_SKIN_INFO");
		var skinInfo = skinNode.Fields.Item('info');
		for(var i=0;i<skinInfo.Count;i++){
			for(var fld in skin_navigation)
				if(skinInfo.GetValue(i).indexOf(fld+"=")>=0){
					 skinInfo.GetValue(i)=fld+"="+skin_navigation[fld];
					 foundfields.push(fld);
					 break;
				}
		}
		for(var fld in skin_navigation){
			var isFound = false;
				for(var str in foundfields) 
					if(str==fld) isFound=true;
			if(!isFound)
				skinInfo.Add(fld+"="+skin_navigation[fld]);
			
		}
}catch(e){}
}

function _block_window(){ 
	document.getElementById("btn_pause").disabled=true;
	document.getElementById("btn_play").disabled=true;
	document.getElementById("btn_stop").disabled=true;
	cortona.InputDevices=0;
	set_block(true);
}

function _unblock_window(){ 
	document.getElementById("btn_pause").disabled=false;
	document.getElementById("btn_play").disabled=false;
	document.getElementById("btn_stop").disabled=false;
	cortona.InputDevices=255;
	set_block(false);
}

function _on_warning_close(){
	setTimeout("_on_warning_next()", 0);
}

function _on_warning_next(){
	if(scene_is_loaded){
		stopRefresher(0);
		warningModelessDialog=null;
		if(!isMSIE || !propIsMessageboxEnabled){
			var cnt = document.getElementById("message_container");
			cnt.innerHTML = "";
			cnt.style.left="0";
			cnt.style.top="0";
			cnt.style.width="0";
			cnt.style.height="0";
		}
		warningIsOpen = false;
		var new_active_events = new Array();
		if(document.getElementById("warningbox").checked)
			for(var i=1; i<active_events.length; i++)
					new_active_events.push(active_events[i]);
		active_events = new_active_events;
		if(active_events.length>0)
			_on_warning_open();
		else{
			_unblock_window();
			api.vcr_play_ext();
		}
	}
}

function _on_warning_open(){ 
if(document.getElementById("warningbox").checked){
	if(!warningIsOpen){
		warningIsOpen = true;
		warningText = active_events[0].description;
		messageTitleText = active_events[0].type;
		pause();
		if(isMSIE && propIsMessageboxEnabled){
			warningModelessDialog = showModelessDialog(resFolder+"generic_msg.html",window,"dialogWidth:500px; dialogHeight:400px; resizable:Yes; help: No; scroll:yes; status:no;");
		}else{
			var winH = document.body.clientHeight;
			var winW = document.body.clientWidth
			var wH = 400;
			var wW = 500;
			var X = (winW-wW)/2;
			var Y = (winH-wH)/2;
			var cnt = document.getElementById("message_container");
			cnt.style.left=X+"px";
			cnt.style.top=Y+"px";
			cnt.style.width=wW+"px";
			cnt.style.height=wH+"px";
			if(isChrome){
				cnt.innerHTML = '<IFRAME id="ifrWarn" style="Z-INDEX: 1001; VISIBILITY: visible; WIDTH: '+wW+'px; HEIGHT: '+wH+'px; POSITION: absolute; LEFT: 0; TOP: 0;" src="'+resFolder+'generic_msg.html" frameSpacing="0" frameBorder="no" scrolling="no"></IFRAME><DIV id="innerDiv_warn" style="DISPLAY: block; Z-INDEX: 1002; LEFT: 0px; TOP: 0px; VISIBILITY: visible; OVERFLOW: visible; WIDTH: '+wW+'px; HEIGHT: '+wH+'px; POSITION: absolute; border:0;"><div class="wholder"><div class="wtitle">&nbsp; '+messageTitleText+' &nbsp;</div><div class="wmsg">'+warningText+'</div><input id="wbtn_ok" class="wbtn_ok" type="button" onclick="_on_warning_close();" value="'+iMessageCloseButton+'"></div></DIV>';
				document.getElementById("wbtn_ok").focus();
			}else{
				cnt.innerHTML = '<IFRAME id="ifrWarn" style="Z-INDEX: 1001; VISIBILITY: visible; WIDTH: '+wW+'px; HEIGHT: '+wH+'px; POSITION: absolute; LEFT: 0; TOP: 0;" src="'+resFolder+'generic_msg.html" frameSpacing="0" frameBorder="no" scrolling="no"></IFRAME>';
			}
			startRefresher(0);
		}
	}
}else{
	active_events = new Array();
	_unblock_window();
}
}

function _set_stop_by_step(){
	if(scene_is_loaded) {
		is_stop_by_step = document.getElementById("autostopbox").checked;
		var autostopMode = 0;
		if(is_stop_by_step)autostopMode = (autostopOnSubsteps)?2:1;		
		api.set_autostop_mode(autostopMode);
	}	
}

function _on_simulation_events_out(events){
	if(document.getElementById("warningbox").checked){
		var validEvents = false; 
		for(var i=0; i<events.length; i++){
			if(checkValidEvent(events[i])){
				active_events.push(events[i]);
				validEvents = true;
			}
		}
		if(validEvents){
			_block_window();	
			_on_warning_open();
		}
	}
}

function checkValidEvent(event){
	for(var i=0; i<eventsArray.length; i++){
		if(eventsArray[i].id == event.id){
			event.description = eventsArray[i].description;
			event.type = eventsArray[i].type;
			return true;
		}
	}
	return false;	
}

var currentState = 0;
function _on_vcr_state_changed(state){ 
  var oldState = currentState;
  currentState = state;
	  var _a = new Array(document.getElementById("btn_stop"), document.getElementById("btn_play"), document.getElementById("btn_pause"));
	  for(var i=0; i<_a.length; i++)
	    _a[i].style.fontWeight = (state == i) ? "bold" : "normal";
		
		if((state!=api.PLAY) && warningIsOpen)
			if(api.vcr_get_fraction()==1)api.vcr_set_fraction(1);
	
		if((is_stop_by_step)&&(state==api.PAUSE)){
			if(api.vcr_get_step_fraction()==1)api.vcr_next_step();
			else{
				if(autostopOnSubsteps && (api.vcr_get_fraction()==1))api.vcr_next_substep();
			}
		}
		if(autorepeat && (oldState == api.PLAY) && (state==api.STOP) && (!is_stop_by_step) && (api.vcr_get_fraction()==1)){
			play();
		}
		if(propIsActiveFocus && !CheckContextMenuOpen()){
		try{
			if(state!=api.PLAY) document.getElementById("btn_play").focus();
				else document.getElementById("btn_pause").focus();
		}catch(err){}
	}
}

var currentIDs = null;
function _on_start_new_substep(procid, stepid, substepid){ 
	set_active(substepid);
	currentIDs = new Array(procid, stepid, substepid);
}

function play(){
	api.vcr_play();
}

function stop(){
	active_events = new Array();
	api.vcr_stop();
}

function pause(){
	api.vcr_pause();
}

function setFreezeVP(bFlag){
	api.set_freeze_viewpoint(bFlag);
}

//========================
// Procedure List
//========================
function hilightTable(action, bFlag) {
		var aN = get_action_number(action);
		if(aN>=0){
			for(var i=0; i<items_scroll.length; i++){
				var isScrolled = false;
				var a = items_scroll[i];
				for(var j=0; j<a.length; j++){
					if(aN==a[j]){
						if(!is_t_clicked && bFlag){
							document.getElementById((propHideActions)? ("s_"+i) : ("a_"+aN)).scrollIntoView(false);
						}
						
						if(!propHihlightParents && propHideActions){
							hilightCell("s_"+i, bFlag);
						}
						isScrolled=true;
						break;
					}
				}
				if(isScrolled)break;
			}
			if(propHihlightParents){
				for(var i=0; i<items_actions.length; i++){
					var a = items_actions[i];
					for(var j=0; j<a.length; j++){
						if(aN==a[j]){
							hilightCell("s_"+i, bFlag);
							if(propAutoNumbering && propHihlightNumbers)hilightCell("s_"+i+"_n", bFlag);
							break;
						}
					}
				}
			}
			if(!propHideActions){
				hilightCell("a_"+aN, bFlag);
				if(propAutoNumbering && propHihlightNumbers)hilightCell("a_"+aN+"_n", bFlag);
			}
		}
}
function hilightCell(id, bFlag) {
	try{
		var em = document.getElementById(id);
		if(em){			
			em.style.color = (bFlag)? propTableSelectedColor : propTableColor;
			em.style.backgroundColor = (bFlag)? propTableSelectedBackgroundColor: propTableBackgroundColor;
		}
	}catch(e){}
}
function set_block(flag) {
	isBlocked = flag;
	var cursorStyle=(isBlocked)?"":"pointer";
	if(flag)CloseContextMenu();
	if(isMSIE){
		for ( i = 0; i < document.styleSheets[0].rules.length; i++ ){
			if(document.styleSheets[0].rules[i].selectorText==".cellnormal")document.styleSheets[0].rules[i].style.cursor=cursorStyle;
		}
	}else{
		for(var z = 0; z < document.styleSheets.length; z++){
			if(document.styleSheets[z].cssRules){
				for ( i = 0; i < document.styleSheets[z].cssRules.length; i++ ){
					if(document.styleSheets[z].cssRules[i].selectorText==".cellnormal")document.styleSheets[z].cssRules[i].style.cursor=cursorStyle;
				}
			}
		}
	}
}

function hlight_first() {
	if(actions_list.length>0){
		set_active(actions_list[0]);
	}
}

var prevactive = "";
function set_active(substep_id) {
if(prevactive != "")hilightTable(prevactive, false);
prevactive = substep_id;
hilightTable(substep_id, true);
is_t_clicked = false;
}

var is_t_clicked = false;
function on_tab_click(n) {
if(isBlocked)return;
try{
	if(items_actions[n].length>0){		
		is_t_clicked = true;
		api.vcr_set_position(actions_list[items_actions[n][0]]);
	}
	//window.event.cancelBubble = true;
	//window.event.returnValue = false;
}
catch(e) {}	
}

function on_atab_click(n) {
if(isBlocked)return;
try{
	is_t_clicked = true;
	api.vcr_set_position(actions_list[n]);
	//window.event.cancelBubble = true;
	//window.event.returnValue = false;
}
catch(e) {}	
}

function get_action_number(id){
	for(var i=0; i<actions_list.length; i++){
		if(actions_list[i]==id){
			return i;
		}
	}
	return -1;
}
var helpWindow = null;
function showHelp(){
	try{
		if(helpWindow!=null)helpWindow.close();
	}catch(e){}
	if(isMSIE){
		helpWindow = window.showModelessDialog(resFolder+helpFile,"","dialogWidth:750px; dialogHeight:600px; resizable:Yes; help: No; scroll:yes; status:no;");
	}else{
		helpWindow = window.open(resFolder+helpFile,"","statusbar=no,resizable=yes,scrollbars=yes,width=750,height=600");
	}
}

function ShowCMenu(X,Y){
	if(cMenu!=null){
		pause();
		cMenu.show(X, Y, cortona);
	}
}

function showCortonaProperties(){
	this.cortona.uiAction("preferences");
}

function showAbout(){
	var docVersion = exporterVersion;
	docVersion += 	"-"+scriptVersion;	
	alert(TXT_ABOUT_DOCUMENT+" "+docVersion+"\n"+TXT_ABOUT_CORTONA+" "+cortona.Version)
}

var isWaitMenu = false;
var menuTimeout = null;
function CloseContextMenu(){
	if(cMenu!=null)cMenu.close();
}
function SmartCloseContextMenu(X, Y){
	if(cMenu!=null){
		if(!cMenu.checkPoint(X, Y)){
			cMenu.close();
		}
	}
}
function OnContextMenuBlur(){
	if(menuTimeout!=null)clearTimeout(menuTimeout);
	if(!isWaitMenu)menuTimeout = setTimeout("CloseContextMenu()", 0);
}
function ContextMenuNoBlur(isWait){
	isWaitMenu = isWait;
	if(menuTimeout!=null)clearTimeout(menuTimeout);
	document.getElementById('cmenu_anchor').focus();
	return false;
}
function CheckContextMenuOpen(){
	return (cMenu!=null && cMenu.checkIsOpen());
}

//==============================================================================
//  ContextMenu
//==============================================================================
function ContextMenu(contextSpan){
this.oContextHTML = contextSpan;
var cncl = new Function('return false;');
this.oContextHTML.onselectstart = cncl;
this.oContextHTML.oncontextmenu = cncl;
this.oContextHTML.style.MozUserSelect="none";
this.isOpen=false;
this.coords = [0, 0, 0, 0];
}

ContextMenu.prototype.show = ContextMenu_show;
ContextMenu.prototype.close = ContextMenu_close;
ContextMenu.prototype.checkIsOpen = ContextMenu_checkIsOpen;
ContextMenu.prototype.checkPoint = ContextMenu_checkPoint;


function ContextMenu_show(ax, ay, coordElement){
	function getOption(txt, onclick, enabled){
		if(enabled){
			return "<tr><td colspan='2' style='background-color: #c0c0c0; color: #000000; font-family:tahoma, sans-serif; font-size:8pt; padding: 2px 17px 2px 17px; cursor: default; white-space : nowrap; text-align : left;' onmouseover='this.style.color=\"#FFFFFF\"; this.style.backgroundColor=\"#000080\";' onmouseout='this.style.color=\"#000000\"; this.style.backgroundColor=\"#c0c0c0\";' onmousedown='ContextMenuNoBlur(true);' onmouseup='ContextMenuNoBlur(false);' onclick='"+onclick+" cMenu.close();'>" +txt+ "</td></tr>";
		}else{
			return "<tr><td colspan='2' disabled style='background-color: #c0c0c0; color: #000000; font-family:tahoma, sans-serif; font-size:8pt; padding: 2px 17px 2px 17px; cursor: default; white-space : nowrap; text-align : left;'>" +txt+ "</td></tr>";
		}
	}
	function getPosition(element){var left = element.offsetLeft; var top = element.offsetTop; for (var parent = element.offsetParent; parent; parent = parent.offsetParent){left += parent.offsetLeft; top += parent.offsetTop;}return {left: left, top: top};}
	
	var hrOption = "<tr><td colspan='2' style='background-color: #c0c0c0; padding: 0px 2px 0px 2px; cursor: default;'><hr></td></tr>";
	var tableSyntax = "<a id='cmenu_anchor' style='text-decoration: none;cursor: default;' onblur='OnContextMenuBlur();' onmousedown='ContextMenuNoBlur(true);' onmouseup='ContextMenuNoBlur(false);' onclick='return false;' href='#'><div style='border: 1px solid; border-color: #c0c0c0 #000000 #000000 #c0c0c0;'><div style='border: 1px solid; border-color: #FFFFFF #808080 #808080 #FFFFFF; background-color: #c0c0c0; padding: 2px 2px 2px 2px;'><table style='border-collapse:collapse;'>";
	var cmTitle = " ";
	tableSyntax += getOption(iContextMenuCortonaProperties, "showCortonaProperties();", true);
	tableSyntax += hrOption;
	tableSyntax += getOption(iContextMenuAbout, "setTimeout(\"showAbout();\",0);", true);	
	tableSyntax += "</table></div></div></a>";
	this.oContextHTML.style.width="";
	this.oContextHTML.style.height="";
	this.oContextHTML.innerHTML=tableSyntax;
	var wW=this.oContextHTML.clientWidth;
	var wH=this.oContextHTML.clientHeight;
	var crtW = coordElement.offsetWidth;
	var crtH = coordElement.offsetHeight;
	var pos = getPosition(coordElement);
	var crtL = pos.left;
	var crtT = pos.top;
	var x = (ax+wW<crtW)? ax+crtL+2: ax+crtL-2-wW;
	var y = (ay+wH<crtH)? ay+crtT+2: ay+crtT-2-wH;
	this.oContextHTML.style.left=x+"px";
	this.oContextHTML.style.top=y+"px";
	this.coords = [x, y, x+wW, y+wH];
	this.oContextHTML.innerHTML = '<IFRAME id="ifr_cmenu" style="Z-INDEX: 1001; VISIBILITY: visible; WIDTH: '+wW+'px; HEIGHT: '+wH+'px; POSITION: absolute; LEFT: 0; TOP: 0;" src="about:blank" frameSpacing="0" frameBorder="no" scrolling="no"></IFRAME><DIV id="innerDiv_cmenu" style="DISPLAY: block; Z-INDEX: 1002; LEFT: 0px; TOP: 0px; VISIBILITY: visible; OVERFLOW: visible; WIDTH: '+wW+'px; HEIGHT: '+wH+'px; POSITION: absolute; border:0;">'+this.oContextHTML.innerHTML+'</DIV>';
	startRefresher(1);
	document.getElementById('cmenu_anchor').hideFocus = true;
	document.getElementById('cmenu_anchor').focus();
	this.isOpen = true;
}
function ContextMenu_close(){
	if(this.isOpen){
		this.isOpen = false;
		stopRefresher(1);
		this.oContextHTML.innerHTML = "";
		this.oContextHTML.style.left="0";
		this.oContextHTML.style.top="0";
		this.oContextHTML.style.width="0";
		this.oContextHTML.style.height="0";
	}
}
function ContextMenu_checkIsOpen(){
	return this.isOpen;
}

function ContextMenu_checkPoint(X, Y){
	return(this.isOpen && this.coords[0]>X && this.coords[2]<X && this.coords[1]>Y && this.coords[3]<Y);
}

//-----------------------
var iZ = 1001;
var warning_interval = null;
var isKeys = [false, false];
var isR2 = false;
function refreshZ(){
	iZ++;
	if(isKeys[0]){
		document.getElementById('ifrWarn').style.zIndex = iZ+5;
		if(isChrome)document.getElementById('innerDiv_warn').style.zIndex = iZ+8;
	}
	if(isKeys[1]){
		document.getElementById('ifr_cmenu').style.zIndex = iZ+10;
		document.getElementById('innerDiv_cmenu').style.zIndex = iZ+20;
	}
	if(iZ>900000)iZ=1001;
}
function startRefresher(n){
	isKeys[n]=true;
	if(!isMSIE && warning_interval==null)warning_interval = window.setInterval("try{refreshZ();}catch(e){}",10);
}
function stopRefresher(n){
	if(arguments.length > 0) isKeys[n]=false;
	else isKeys = [false, false];
	if(!isKeys[0] && !isKeys[1] && warning_interval!=null){
		window.clearInterval(warning_interval);
		warning_interval = null;		
	}
}