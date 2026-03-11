<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Collections,
				java.util.List,
                 java.util.ArrayList,
                 java.util.Map,
                 java.util.TreeMap,
                 java.util.HashMap,
                 java.util.Iterator,
                 java.util.Locale,
                 wt.util.WTContext,
                 wt.util.WTMessage"%>
                 
<%@page import="wt.httpgw.GatewayServletHelper"%>
<%@page import="wt.httpgw.URLFactory"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.org.WTGroup"%>
<%@page import="wt.project.Role,wt.fc.PersistenceHelper,wt.fc.Persistable"%>
<%@page import="wt.org.WTPrincipalReference"%>
<%@page import="wt.change2.WTChangeIssue"%>
<%@page import="ext.casc.workflow.setparticipant.PrincipalHelper"%>
<%@page import="ext.casc.workflow.setparticipant.WorkflowUtil"%>
<%@page import="ext.casc.workflow.TaskConfigrationHelper"%>
<%@page import="ext.casc.workflow.util.WorkflowConfigBean"%>
<%@page import="ext.casc.workflow.util.ChineseComparator"%>
<%@page import="ext.casc.workflow.util.ChineseCompatator2"%>

<style type="text/css">
	.SpaceStyle{
		text-decoration:none;
	}
</style>

<%
	//Locale locale = WTContext.getContext().getLocale();
	//String oid = request.getParameter("oid");
	String bohui = "驳回设计";
	String bohui2 = "驳回";
	String tongguo = "通过";
	String xinghaoxuanzecuowu = "型号选择错误";
	String huiqian149 = "149厂工艺会签";
	String gongyishi149 = "149厂工艺师";
	String shujuchakanzhe = "数据查看者";
	String reciever = "电子文件接收人";
	String waibuhuiqian = "外部工艺";
	String neibugy = "内部工艺";
	String neibuhuiqian = "内部工艺";
	String huiqian = "内部会签";
	String waibu = "外部";
	String qx = "取消";
	String zzhq = "组织会签";
	String zzlc = "终止流程";

	String GET_CONFIGPATH_ERROR_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","GET_CONFIGPATH_ERROR",null,locale);
	String PRINCIPAL_TITLE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","PRINCIPAL_TITLE",null,locale);
	String ROLE_LIST = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","ROLE_LIST",null,locale);
	String SELECT_USER = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","SELECT_USER",null,locale);
	String ADD_BUTTON = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","ADD",null,locale);
	String NOT_SUPORT_AJAX_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","NOT_SUPORT_AJAX",null,locale);
	String AJAX_ERROR_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","AJAX_ERROR",null,locale);
	String SELECT_USER_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","SELECT_USER",null,locale);
	String USER_EXIST_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","USER_EXIST",null,locale);
	String NEED_USER_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","NEED_USER",null,locale);
	String INPUT_USER_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","INPUT_USER",null,locale);
	String SEARCH_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","SEARCH",null,locale);
	String USER_NAME_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","USER_NAME",null,locale);
	
	String SINGATURE_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","SINGATURE",null,locale);
	String NEED_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","NEED",null,locale);
	String SELECTABLE_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","SELECTABLE",null,locale);
	String OR_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","OR",null,locale);	
	String TEAM_MESSAGE = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","TEAM",null,locale);	
	String SELECT = WTMessage.getLocalizedMessage("ext.ases.workflow.resource.WorkflowRB","SELECT",null,locale);	
	String isNeedSearchButton = WorkflowUtil.isNeedSearchUserButton();
	
	String selectedPath = (String)TaskConfigrationHelper.getProcessVariableValue(oid,"selectPathIndex");
	Boolean booleanHuiqian149 = (Boolean)TaskConfigrationHelper.getProcessVariableValue(oid,"huiqian149");
	String selected149 = "";
	boolean display149 = false;
	
	if(booleanHuiqian149 != null){
		selected149 = booleanHuiqian149.toString();
		if((selected149 != null) && (selected149.equals("true"))){
			selected149 = "checked";
		}else{
			selected149 = "";
		}
	}
	
	boolean isNeedConfigPath = (Boolean)TaskConfigrationHelper.getActivityVariableByVarName(oid,"isNeedConfigPath");
	//boolean isNeedConfigPath = true;
	//boolean isDisplayPath = TaskConfigrationHelper.getActivityFlagByVarName(oid,"isDisplayPath");
	String contextPath = request.getContextPath();
	ChineseComparator cc = new ChineseComparator();
	Map containerRoleUser = new TreeMap(cc);
	Map tempContainerMap = PrincipalHelper.service.getRoleAndUserByContainer(oid);
	containerRoleUser.putAll(tempContainerMap);		
	List<String> roleList = new ArrayList<String>();
	List<String> identifyList = WorkflowUtil.getIdentifyList(oid);
	System.out.println("identifyList is "+identifyList);
	WorkflowConfigBean wcb = null;
		
	if(isNeedConfigPath){
		wcb = WorkflowUtil.getConfigPath(identifyList);
	}else{
		wcb = WorkflowUtil.getDefaultPath(oid);
	}
	if(wcb != null ){	
		roleList = wcb.getRoleList(oid);
		List<String> titleList = wcb.getTitleList();
		Map<String,Object> contentMap = wcb.getContentMap();
		
		for(int i = 2 ; i < titleList.size() ; i++){
			Object tempKey = titleList.get(i);
			Object tempValue = contentMap.get(tempKey);
			if(tempValue.equals("")){
					contentMap.remove(tempKey);
					titleList.remove(i--);
			}
		}
		if(roleList != null){
			Map workflowRoleUser = null;
			workflowRoleUser = PrincipalHelper.service.getUserByRole(oid,roleList);	
			Map<Role,List<WTUser>> otherRoleUser = PrincipalHelper.service.getRoleAndUserByContainer(oid);
			%>
			
<script>
	var oid = '<%=oid%>';
	var locale = '<%=locale%>';
	var isNeedConfigPath = '<%=isNeedConfigPath%>';
	var containerRoleList = new Array();
	var addPrincipalMap = new Array();
	var buttonFlag = true;
	var buttonFlag2 = true;
	var selectPath = '<%=selectedPath%>';
	var saveFlag = true;
	var waibugongyi = 0;

	function find(name)
	{
		for(var i=0;i<mainform.elements.length;i++){
			var e = mainform.elements[i];
			if (e.name.indexOf(name)>=0 && e.name.indexOf("old")<0)
				return e;
		}
		return null;
	}
	function validata(){
		var flag = true;
		var bohuiRadio = document.getElementById('<%=bohui%>');
		var bohuiRadio2 = document.getElementById('<%=bohui2%>');
		var tongguo = document.getElementById('<%=tongguo%>');
		var xinghaoxuanzecuowu = document.getElementById('<%=xinghaoxuanzecuowu%>');
		var qx = document.getElementById('<%=qx%>');
		var zzlc = document.getElementById('<%=zzlc%>');
		var zzhq = find("isHuiqian");

		if(bohuiRadio && bohuiRadio.checked || bohuiRadio2 && bohuiRadio2.checked || tongguo && tongguo.checked
		||xinghaoxuanzecuowu && xinghaoxuanzecuowu.checked
		||qx && qx.checked ||zzlc && zzlc.checked ||zzhq && zzhq.checked == false){
				var completehiddenBtn = document.getElementsByName("completehidden")[0];
						if (completehiddenBtn) {     	
							completehiddenBtn.onclick();
						}
						return;
		}
		var final_path = "";
		var path_check = document.getElementsByName("path_check");
		for(var i = 0 ; i < addPrincipalMap.length ; i++){
			var titleDiv = document.getElementById("signature_path"+i);
			//alert(titleDiv);
			if(titleDiv!=null){
				//alert("i="+i + "  titleDiv=" + titleDiv.value);
				if(addPrincipalMap[i].userArray != 0){
					var huiqiana = document.getElementById("chedckhuiqian149");
					if(titleDiv.value=='<%=waibuhuiqian%>'){
						if(path_check[i].checked==true&&titleDiv){
							final_path = final_path+titleDiv.value+",";
							//alert(final_path+"aa");
						}else if(huiqiana.checked==true){
							final_path = final_path+titleDiv.value+",";
							//alert(final_path+"bb");
						}
					}else if(path_check[i].checked==true&&titleDiv){
						final_path = final_path+titleDiv.value+",";
						//alert(final_path);
					}
				}
			}
		}
		for(var i = 0 ; i < addPrincipalMap.length ; i++){
			var titleDiv = document.getElementById("signature_path"+i);
			if (addPrincipalMap[i].changePath == "1" || addPrincipalMap[i].value == "1"){		
				if(addPrincipalMap[i].userArray == 0){
					if (addPrincipalMap[i].roleDisName =='<%=shujuchakanzhe%>' ||
					addPrincipalMap[i].roleDisName =='<%=reciever%>'||
					((zzhq && zzhq.checked != true)&&addPrincipalMap[i].roleDisName =='<%=gongyishi149%>')){
					//不用检查数据查看者、电子文件接收人 是否加了用户
					}else{
					flag = false;
					alert(addPrincipalMap[i].roleDisName+" "+'<%=NEED_USER_MESSAGE%>');
					break;
					}							
				}
			}else if (addPrincipalMap[i].value == "3"){
				var path_check = document.getElementsByName("path_check");
				if  (addPrincipalMap[i+1].value == "3"){
					var titleDiv = document.getElementById("signature_path"+i);
					if (path_check[i].checked == false ){
						if(path_check[i+1].checked == false){
							var huiqian149 = document.getElementById("chedckhuiqian149");
							if (titleDiv!=null && (titleDiv.value.indexOf("<%=neibuhuiqian%>")>=0 && huiqian149.checked == true)){
							}else{
								flag = false;
								alert(addPrincipalMap[i].roleDisName+'<%=OR_MESSAGE%>'+addPrincipalMap[i+1].roleDisName+" "+'<%=SELECT%>');
								break;
							}
						}else{
							if (addPrincipalMap[i+1].userArray == 0){
								flag = false;
								if (titleDiv!=null && (titleDiv.value.indexOf("<%=neibuhuiqian%>") >=0 || titleDiv.value.indexOf("<%=huiqian%>")>=0))
									alert(addPrincipalMap[i+1].roleDisName+'<%=TEAM_MESSAGE%>');
								else
									alert(addPrincipalMap[i+1].roleDisName+" "+'<%=NEED_USER_MESSAGE%>');
								break;
							}	
						}
					}else{
						if (addPrincipalMap[i].userArray == 0){
							flag = false;
							alert(addPrincipalMap[i].roleDisName+" "+'<%=NEED_USER_MESSAGE%>');
							break;
						}
					}
					i++;
				}
			}else if (addPrincipalMap[i].value == "4"){
				if(addPrincipalMap[i].userArray == 0){
					if(addPrincipalMap[i+1].value == "4"){
						if(addPrincipalMap[i+1].userArray == 0){
							flag = false;
							alert(addPrincipalMap[i].roleDisName+'<%=OR_MESSAGE%>'+addPrincipalMap[i+1].roleDisName+" "+'<%=NEED_USER_MESSAGE%>');
							break;
						}
					}else if(addPrincipalMap[i-1].value == "4"){
						if(addPrincipalMap[i-1].userArray == 0){
							flag = false;
							alert(addPrincipalMap[i].roleDisName+'<%=OR_MESSAGE%>'+addPrincipalMap[i-1].roleDisName+" "+'<%=NEED_USER_MESSAGE%>');
							break;
						}
					}
				}	
			}
		}
		if(flag){
			saveSignPath(final_path);
			//saveUser();
		}
	}
	
		function searchUser(){
			var userName = document.getElementById("userName").value;
			if(userName == ""){
				alert("<%=INPUT_USER_MESSAGE%>");
			}else{
				var selectElement = document.getElementById("containerRoleList");
				selectElement.selectedIndex = -1;
				userName=encodeURI(userName );
				
				var xmlHttpRequest;
				if (window.XMLHttpRequest) { // Mozilla, Safari,...
        	xmlHttpRequest = new XMLHttpRequest();
        	if (xmlHttpRequest.overrideMimeType) {
        		xmlHttpRequest.overrideMimeType('text/xml');
        	}
      	} else if (window.ActiveXObject) { // IE
      		try {
        		xmlHttpRequest = new ActiveXObject("Msxml2.XMLHTTP");
        	} catch (e) {
        		try {
          		xmlHttpRequest = new ActiveXObject("Microsoft.XMLHTTP");
          	} catch (e) {
          		alert("<%=NOT_SUPORT_AJAX_MESSAGE%>");
          	}
       		}
      	}
       	
				xmlHttpRequest.onreadystatechange=function(){
			  	if(xmlHttpRequest.readyState==4){
				  	if (xmlHttpRequest.status == 200) {
				  		var userNameList = xmlHttpRequest.responseText;
				  		var userNameArray = userNameList.split("`");
							updateSearchUser(userNameArray);
				  	}else{
				  		alert("<%=AJAX_ERROR_MESSAGE%>");
				  	}
				  }
				}
				
				xmlHttpRequest.open("GET","<%=contextPath%>/netmarkets/jsp/ext/ases/workflow/setparticipant/searchUser.jsp?userName="+userName+"&locale="+locale+"&oid="+oid,true);
				xmlHttpRequest.setRequestHeader("If-Modified-Since","0");
				xmlHttpRequest.send(null);
			}
		}
		
		function updateSearchUser(userArray){
			var userList = document.getElementById("userList");
			userList.length=0;
			for(var i = 1 ; i <userArray.length-1;i++){
				var str = userArray[i];
				userList.options.add(new Option(str,str));
			}
			userList.selectedIndex = 0;
		}
		
		function saveSignPath(sign_path){
			if(isNeedConfigPath=='true'){
				var xmlHttpRequest;
			if (window.XMLHttpRequest) { // Mozilla, Safari,...
       		xmlHttpRequest = new XMLHttpRequest();
        	if (xmlHttpRequest.overrideMimeType) {
        		xmlHttpRequest.overrideMimeType('text/xml');
        	}
      	} else if (window.ActiveXObject) { // IE
      		try {
        		xmlHttpRequest = new ActiveXObject("Msxml2.XMLHTTP");
        	} catch (e) {
        		try {
          			xmlHttpRequest = new ActiveXObject("Microsoft.XMLHTTP");
          		} catch (e) {
          			alert("<%=NOT_SUPORT_AJAX_MESSAGE%>");
          		}
       		}
      	}

			xmlHttpRequest.onreadystatechange=function(){
				if(xmlHttpRequest.readyState==4){
					if (xmlHttpRequest.status == 200) {
						var completehiddenBtn = document.getElementsByName("completehidden")[0];
						if (completehiddenBtn) {     	
							completehiddenBtn.onclick();
						}
					}else{
						alert("<%=AJAX_ERROR_MESSAGE%>");
					}
				}
	 		}

			sign_path=encodeURI(sign_path);
			sign_path=encodeURI(sign_path);

			xmlHttpRequest.open("GET","<%=contextPath%>/netmarkets/jsp/ext/ases/workflow/setparticipant/saveSignPath.jsp?sign_path="+sign_path+"&oid="+oid,true);
			xmlHttpRequest.setRequestHeader("If-Modified-Since","0");
			xmlHttpRequest.send(null);
			}else{

				var completehiddenBtn = document.getElementsByName("completehidden")[0];
						if (completehiddenBtn) {     	
							completehiddenBtn.onclick();
						}
			}	
		}	
	
	function saveUser(){
		var value = "";
		var path_check = document.getElementsByName("path_check");
		for(var i = 0 ; i < addPrincipalMap.length; i ++){
			value = value+addPrincipalMap[i].roleName;
			var titleDiv = document.getElementById("signature_path"+i);
			if (titleDiv!=null && (titleDiv.value.indexOf("<%=neibugy%>") >=0 || titleDiv.value.indexOf("<%=waibuhuiqian%>")>=0)){
				if (path_check[i].checked == true){
					for(var j = 0 ; j < addPrincipalMap[i].userArray.length; j ++){
						value = value + "`"+addPrincipalMap[i].userArray[j];
					}
				}
			}else{
				for(var j = 0 ; j < addPrincipalMap[i].userArray.length; j ++){
					value = value + "`"+addPrincipalMap[i].userArray[j];
				}
			}
			if( i < addPrincipalMap.length-1){
				value = value + ":";
			}
		}
	
		var xmlHttpRequest;
		if (window.XMLHttpRequest) { // Mozilla, Safari,...
       		xmlHttpRequest = new XMLHttpRequest();
        	if (xmlHttpRequest.overrideMimeType) {
        		xmlHttpRequest.overrideMimeType('text/xml');
        	}
      	} else if (window.ActiveXObject) { // IE
      		try {
        		xmlHttpRequest = new ActiveXObject("Msxml2.XMLHTTP");
        	} catch (e) {
        		try {
          			xmlHttpRequest = new ActiveXObject("Microsoft.XMLHTTP");
          		} catch (e) {
          			alert("<%=NOT_SUPORT_AJAX_MESSAGE%>");
          		}
       		}
      	}

		xmlHttpRequest.onreadystatechange=function(){
			if(xmlHttpRequest.readyState==4){
				if (xmlHttpRequest.status == 200) {
				buttonFlag = true;
					//alert(message[2]);
					//var completehiddenBtn = document.getElementsByName("completehidden")[0];
					//if (completehiddenBtn) {          	
						//completehiddenBtn.onclick();
					//}
				}else{
					alert("<%=AJAX_ERROR_MESSAGE%>");
					buttonFlag = true;
				}
			}
	 	}

		value=encodeURI(value);
		value=encodeURI(value);

		xmlHttpRequest.open("GET","<%=contextPath%>/netmarkets/jsp/ext/ases/workflow/setparticipant/handle.jsp?value="+value+"&oid="+oid,true);
		xmlHttpRequest.setRequestHeader("If-Modified-Since","0");
		xmlHttpRequest.send(null);
	}
	
	function showUser(){
		var containerSelect = document.getElementById("containerRoleList");
		var userListSelect = document.getElementById("userList");
		userListSelect.length = 0 ;
		var tempUserList = containerRoleList[containerSelect.selectedIndex].userArray;
		for(var i = 0 ; i < tempUserList.length ; i++){
			userListSelect.options.add(new Option(tempUserList[i].userName,tempUserList[i].userName));
		}
	}
	
	function isExist(index,value){
		var flag = false;
		for(var i = 0 ; i < addPrincipalMap[index].userArray.length ; i++){
			if(addPrincipalMap[index].userArray[i] == value){
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	function addUser(index){
		if(buttonFlag != true){
			return ;
		}
		buttonFlag = false;
		var userListSelect = document.getElementById("userList");
		if(userListSelect.selectedIndex == -1){
			alert("<%=SELECT_USER_MESSAGE%>");
			buttonFlag = true;
			return;
		}
				
		if(!isExist(index,userListSelect[userListSelect.selectedIndex].text)){			
			addPrincipalMap[index].userArray[addPrincipalMap[index].userArray.length] = userListSelect[userListSelect.selectedIndex].text;
			saveUser();
			var dE = document.getElementById("role"+index);
			var tempCount = addPrincipalMap[index].userArray.length-1;
			var temp = index+","+tempCount;
			var tempID = "role"+index+"a"+tempCount;
			dE.innerHTML=dE.innerHTML+"<a href='javascript:removeUser("+temp+")' id='"+tempID+"'>"+userListSelect[userListSelect.selectedIndex].text+";<span class='SpaceStyle'>&nbsp;&nbsp;&nbsp;&nbsp</span></a>";
		}else{
			alert("<%=USER_EXIST_MESSAGE%>");
			buttonFlag = true;
		}
		//buttonFlag = true;
	}
	
	function removeUser(roleIndex,userIndex){
		if(buttonFlag != true){
			return ;
		}
		buttonFlag = false;
		addPrincipalMap[roleIndex].userArray.splice(userIndex,1);
		saveUser();
		var divE = document.getElementById("role"+roleIndex);
		var aE = document.getElementById("role"+roleIndex+"a"+userIndex);
		divE.removeChild(aE);
		var childrens = divE.children;
		var count = userIndex;
		for(var i = userIndex ; i < childrens.length ; i++){
			if(childrens[i].href != undefined && childrens[i].href != ""){
				childrens[i].href = "javascript:removeUser("+roleIndex+","+count+")";
				childrens[i].id = "role"+roleIndex+"a"+count;
				count++;
			}
		}
		//buttonFlag = true;
	}
	
	function changePath(index){
		var path_check = document.getElementsByName("path_check");
		var buttonRole = document.getElementById("buttonRole"+index);
		var titleDiv = document.getElementById("signature_path"+index);
		var huiqian149 = document.getElementById("chedckhuiqian149");
		
		if(path_check[index].checked == true){			
			titleDiv.style.display = "";
			buttonRole.disabled = false;
			if(addPrincipalMap[index].value == 2 ||addPrincipalMap[index].value == ""){
				addPrincipalMap[index].changePath = "1";
			}
			//Add saveUser(); by leon 2010.9.10
			saveUser();
			saveSelectPathIndex(index);
		}else{							
			titleDiv.style.display = "none";
			if(titleDiv.value.indexOf("<%=waibu%>")<0){
			buttonRole.disabled = true;
			addPrincipalMap[index].userArray.length = 0;
			addPrincipalMap[index].changePath = "";
			var divE = document.getElementById("role"+index);
			divE.innerHTML = "";
			saveUser();
			}
			removeSelectPathIndex(index);
			
			if(titleDiv.value == "<%=waibuhuiqian%>"){
				huiqian149.checked = false;
			}					
		}
	}
	
	function select149huiqian(){
		var titleDiv = document.getElementById("signature_path"+waibugongyi);
		var huiqian149 = document.getElementById("chedckhuiqian149");
		var addOrRemove = false;
		titleDiv.style.display = "none";
		if(huiqian149.checked == true){
			titleDiv.style.display = "";
			addOrRemove = true;
		}
		
		var xmlHttpRequest;
		if (window.XMLHttpRequest) { // Mozilla, Safari,...
       		xmlHttpRequest = new XMLHttpRequest();
        	if (xmlHttpRequest.overrideMimeType) {
        		xmlHttpRequest.overrideMimeType('text/xml');
        	}
      	} else if (window.ActiveXObject) { // IE
      		try {
        		xmlHttpRequest = new ActiveXObject("Msxml2.XMLHTTP");
        	} catch (e) {
        		try {
          			xmlHttpRequest = new ActiveXObject("Microsoft.XMLHTTP");
          		} catch (e) {
          			alert("<%=NOT_SUPORT_AJAX_MESSAGE%>");
          		}
       		}
      	}

		xmlHttpRequest.onreadystatechange=function(){
			if(xmlHttpRequest.readyState==4){
				if (xmlHttpRequest.status == 200) {
				}else{
					alert("<%=AJAX_ERROR_MESSAGE%>");
				}
			}
	 	}

		xmlHttpRequest.open("GET","<%=contextPath%>/netmarkets/jsp/ext/ases/workflow/setparticipant/add149huiqian.jsp?addOrRemove="+addOrRemove+"&oid="+oid,true);
		xmlHttpRequest.setRequestHeader("If-Modified-Since","0");
		xmlHttpRequest.send(null);
	}
	
	function removeSelectPathIndex(index){
		var xmlHttpRequest;
		if (window.XMLHttpRequest) { // Mozilla, Safari,...
       		xmlHttpRequest = new XMLHttpRequest();
        	if (xmlHttpRequest.overrideMimeType) {
        		xmlHttpRequest.overrideMimeType('text/xml');
        	}
      	} else if (window.ActiveXObject) { // IE
      		try {
        		xmlHttpRequest = new ActiveXObject("Msxml2.XMLHTTP");
        	} catch (e) {
        		try {
          			xmlHttpRequest = new ActiveXObject("Microsoft.XMLHTTP");
          		} catch (e) {
          			alert("<%=NOT_SUPORT_AJAX_MESSAGE%>");
          		}
       		}
      	}

		xmlHttpRequest.onreadystatechange=function(){
			if(xmlHttpRequest.readyState==4){
				if (xmlHttpRequest.status == 200) {
				}else{
					alert("<%=AJAX_ERROR_MESSAGE%>");
				}
			}
	 	}

		xmlHttpRequest.open("GET","<%=contextPath%>/netmarkets/jsp/ext/ases/workflow/setparticipant/removeSelectPathIndex.jsp?index="+index+"&oid="+oid,true);
		xmlHttpRequest.setRequestHeader("If-Modified-Since","0");
		xmlHttpRequest.send(null);
	}
	
	function saveSelectPathIndex(index){
		var xmlHttpRequest;
		if (window.XMLHttpRequest) { // Mozilla, Safari,...
       		xmlHttpRequest = new XMLHttpRequest();
        	if (xmlHttpRequest.overrideMimeType) {
        		xmlHttpRequest.overrideMimeType('text/xml');
        	}
      	} else if (window.ActiveXObject) { // IE
      		try {
        		xmlHttpRequest = new ActiveXObject("Msxml2.XMLHTTP");
        	} catch (e) {
        		try {
          			xmlHttpRequest = new ActiveXObject("Microsoft.XMLHTTP");
          		} catch (e) {
          			alert("<%=NOT_SUPORT_AJAX_MESSAGE%>");
          		}
       		}
      	}

		xmlHttpRequest.onreadystatechange=function(){
			if(xmlHttpRequest.readyState==4){
				if (xmlHttpRequest.status == 200) {
				}else{
					alert("<%=AJAX_ERROR_MESSAGE%>");
				}
			}
	 	}

		xmlHttpRequest.open("GET","<%=contextPath%>/netmarkets/jsp/ext/ases/workflow/setparticipant/saveSelectPathIndex.jsp?index="+index+"&oid="+oid,true);
		xmlHttpRequest.setRequestHeader("If-Modified-Since","0");
		xmlHttpRequest.send(null);
	}
	
	function initalChangePath(index){
		var path_check = document.getElementsByName("path_check");
		var buttonRole = document.getElementById("buttonRole"+index);
		var titleDiv = document.getElementById("signature_path"+index);
		if(path_check[index].checked == true){
			titleDiv.style.display = "";
			buttonRole.disabled = false;
			if(addPrincipalMap[index].value == 2 ||addPrincipalMap[index].value == ""){
				addPrincipalMap[index].changePath = "1";
			}
		}else{
			titleDiv.style.display = "none";
			buttonRole.disabled = true;
			if(titleDiv.value.indexOf("<%=waibu%>")<0){
			addPrincipalMap[index].userArray.length = 0;
			addPrincipalMap[index].changePath = "";
			var divE = document.getElementById("role"+index);
			divE.innerHTML = "";
			}	
			//Don't need,remove by leon 2010.7.10
			//var huiqian149 = document.getElementById("chedckhuiqian149");
			//if(titleDiv.value == "<%=waibuhuiqian%>"){
			//	huiqian149.checked = false;
			//}				
		}			
	}
	
	function initalPath(){
		var path_check = document.getElementsByName("path_check");
		
		if(selectPath != undefined && selectPath != null){
			var selectPathArray = selectPath.split(",");
			for(var i = 0 ; i < selectPathArray.length ; i++){
					var tempIndex = selectPathArray[i];
					if(path_check[tempIndex]){
						path_check[tempIndex].checked = true;
					}
			}	
		}
		
		for(var i = 0 ; i < path_check.length ; i++){
			initalChangePath(i);
		}
		saveUser();
	}
</script>
	<B><%=PRINCIPAL_TITLE %></B>
	<table>
	<%
		if(isDispalyReviewPath){
	%>
		<tr>
			<td><B><%=SINGATURE_MESSAGE%>:</B></td>
			<td><table><tr>
		<%
		List<String> selectableList = new ArrayList<String>();
		
			if(isNeedConfigPath){
			String reviewPath = "";
				for(int i = 2 ; i < titleList.size(); i++){
					String tempTitle = titleList.get(i);
					reviewPath = reviewPath+tempTitle;
					if(i < titleList.size()-1){
						reviewPath = reviewPath + ",";
						System.out.println("reviewPath is" + reviewPath);
					}
					String tempContent = (String)contentMap.get(tempTitle);
					
					if(tempContent != null && !(tempContent.equals("2") || tempContent.equals("3")) && !tempContent.equals("")){
						selectableList.add("");
		%>
						<td><div id="signature_path<%=i-2%>" value="<%=tempTitle%>"><%=tempTitle%></div></td>
		<%
					}else if(tempContent != null && (tempContent.equals("2") || tempContent.equals("3"))){
						selectableList.add(tempTitle);
						if(tempTitle.equals(waibuhuiqian)){
							display149=true;
						}
				
		%>
						<td><div id="signature_path<%=i-2%>" style="{display:none}" value="<%=tempTitle%>"><%=tempTitle%>(<%=SELECTABLE_MESSAGE%>)</div></td>
		<%			
					}
				}
			}else{
				String reviewPath = (String)TaskConfigrationHelper.getProcessVariableValue(oid,"reviewPath");
				if(reviewPath != null){
					String pathArray[] = reviewPath.split(",");
					for(int i = 0 ; i < pathArray.length ; i++){
						String path = pathArray[i];
		%>
					<td><%=path%></td>
		<%
					}
				}
			}
		%>
			</td></table></td>
		</tr>
		<%
			if(isNeedConfigPath){
				for(int i = 0 ; i < selectableList.size() ; i++){
					String tempSelcetable = selectableList.get(i);
					if(tempSelcetable.equals("")){
		%>
				<tr style="{display:none}"><td><%=tempSelcetable%></td><td><input type="checkbox" name="path_check" onclick="changePath('<%=i%>')" checked="true"></td></tr>	
		<%
					}else{
		%>
			<tr><td><%=tempSelcetable%></td><td><input type="checkbox" name="path_check" onclick="changePath('<%=i%>')"></td></tr>
		<%
					}
				}
				
				if((booleanHuiqian149 != null) && (display149)){
%>
					<tr><td><%=huiqian149%></td><td><input type="checkbox" id="chedckhuiqian149" name="chedckhuiqian149" onclick="select149huiqian()" <%=selected149%>></td></tr>
<%	
				}
			}
		}	
	%>
	

		
	</table>
	
<table  width="100%" border="0">
	<tr>
		<td align="right">
		</td>
		<td>
		</td>
		<td></td>
	</tr>
		<%
		if(isNeedSearchButton.equals("true")){
	%>
		<tr>
			<td  width="200"><B><%=USER_NAME_MESSAGE%>:</B><input type="text" name="userName" value="" size="10"></td>
			<td  width="200"><input type="button" value="<%=SEARCH_MESSAGE%>" onclick="searchUser()"></td>
		</tr>
	<%
		}
	%>
	<!--<tr>
		<td width="15%"><B><label><%=ROLE_LIST %></label></B></td>
		<td width="15%"><B><label><%=SELECT_USER %></label></B></td>
		<td></td>
	</tr>-->

	<tr>
		<td valign="top" height="100%">
			<select name="containerRoleList" id="containerRoleList" multiple="multiple" style="{width:170;height:300}" onChange="showUser()" >
			<%
				Iterator it1 = containerRoleUser.keySet().iterator();
			while(it1.hasNext()){
					Role role = (Role)it1.next();
					String roleCHName = role.getDisplay();
					String roleENName = role.toString();
			%>
				<option value="<%=roleENName%>"><%=roleCHName%></option>
				<script>
					var tempContainerObj = new Object();
					tempContainerObj.roleName='<%=roleENName%>';
					tempContainerObj.roleDisName='<%=roleCHName%>';
					var tempContainerObjArray = new Array();
				</script>
				<%
					List userList = (List)containerRoleUser.get(role);
					ChineseCompatator2 cc2 = new ChineseCompatator2();
					Collections.sort(userList, cc2);
					for(int i = 0 ; i < userList.size();i++){
						WTUser user = (WTUser)userList.get(i);
						String userName = user.getFullName()+"("+user.getName()+")";
				%>
				<script>
					var obj = new Object();
					obj.userName = '<%=userName%>';
					tempContainerObjArray[tempContainerObjArray.length] = obj;
				</script>
				<%
					}
				%>
				<script>
					tempContainerObj.userArray=tempContainerObjArray;
					containerRoleList[containerRoleList.length]=tempContainerObj;
				</script>
			<%
				}
			%>
			</select>
		</td>
		<td valign="top">
			<div id="userListDiv" height="100%">
				<select name="userList" id="userList" multiple="multiple" style="{width:160;height:300;}">
				</select>
			</div>
		</td>
		<td width="100%">
			<table border = "0">
			<%
				for(int i = 0 ;roleList != null && i < roleList.size(); i++){
					String tempTitle = null;
					if(isNeedConfigPath){
					 	tempTitle = titleList.get(i+2);
					}else{
						tempTitle = titleList.get(i);
					}
					String tempContent = (String)contentMap.get(tempTitle);
					Role tmpRole = Role.toRole(roleList.get(i));
					String tmpDis = tmpRole.getLocalizedMessage(locale);
					String roleName = (String)roleList.get(i);
					%>
						<!--<option value='<%=tmpRole.toString()%>'><%=tmpDis%></option>-->
				<script>
					var tempRoleAndUserObj = new Object();
					var tempRoleAndUserObj = new Object();
					tempRoleAndUserObj.roleName='<%=roleName%>';
					tempRoleAndUserObj.roleDisName='<%=tmpDis%>';
					tempRoleAndUserObj.path = '<%=tempTitle%>';
					tempRoleAndUserObj.value = '<%=tempContent%>';
					tempRoleAndUserObj.changePath = '';
					var tempRoleAndUserObjArray = new Array();
				</script>
				<tr>
					<td width="300">
						<% if (tmpDis.indexOf(waibu)<0){%>
						<div id="<%=tmpDis%>" >
							<%}else{%>

							<div id="<%=tmpDis%>" style="{display:none}" >
								<%}%>
							<input type="button" id="buttonRole<%=i%>" value="<%=ADD_BUTTON%>" onclick="addUser('<%=i%>')">
								&nbsp;&nbsp;&nbsp;&nbsp;<%=tmpDis%>:
						</div>
					</td>
					<td >
						<% 
						
						List roleUserList = null;
						if (tmpDis.indexOf(waibu)<0){%>
						<div width="100%" id="role<%=i%>">
						<%
							roleUserList = (List)workflowRoleUser.get(tmpRole);
							}else{
							if (tmpDis.indexOf(waibuhuiqian)>=0){
								%><script>waibugongyi = <%=i%> ;</script>
								<%	}%>
						<div width="100%" id="role<%=i%>" style="{display:none}" >
					<%	
							roleUserList = (List)otherRoleUser.get(tmpRole);
					}
							for(int j = 0 ; roleUserList != null && j < roleUserList.size(); j++){
								WTUser user = (WTUser)roleUserList.get(j);
								String userName = user.getFullName()+"("+user.getName()+")";
						%>
							<a href="javascript:removeUser(<%=i%>,<%=j%>);" id="role<%=i%>a<%=j%>" name="<%=userName%>"><%=userName%>;</a>
							<script>
								tempRoleAndUserObjArray[tempRoleAndUserObjArray.length] = '<%=userName%>';
							</script>
						<%		
							}
						%>
						</div>
					</td>
				</tr>
				<script>
					tempRoleAndUserObj.userArray = tempRoleAndUserObjArray;
					addPrincipalMap[addPrincipalMap.length]=tempRoleAndUserObj;
				</script>
			<%		
				}
			%>
			</table>
		</td>
	</tr>
</table>

<script>
initalPath();
</script>
<%
}
	}
%>
