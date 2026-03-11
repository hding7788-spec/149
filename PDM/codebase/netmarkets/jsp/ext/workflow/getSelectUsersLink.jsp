<%@ page contentType="text/html;charset=utf-8"%>
<BODY >
<TABLE border=0 width="100%">
<TBODY>
<TR>
<TD class=tabledatafont noWrap align="center"><BR>查找用户： <INPUT id=searchUserKey name=searchUserKey size=15 type=text> <INPUT onclick=searchUsers() value=&nbsp;查找&nbsp; type=button> </TD></TR>
<TR>
<TD class=tabledatafont align="center">请选择要添加的用户：<BR><SELECT id=users multiple size=21 name=null___Users___combobox><OPTION size=21 value="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp</OPTION></SELECT> </TD></TR>

<TR/>
<TR>
<td align="center">
<input type="button" value="确定" onclick="setReviewUsers()">
</td>
</TR>
</TBODY></TABLE>
</BODY>

<script >
	function init(){
	var userStr = opener.document.getElementById("CustActVarselectUsersValueCustActVar").value;
	var userList = document.getElementById("users");
	if(userStr==''){
	return;
	}
		var userArray = userStr.split(";");
		for( var i = 0;i<userArray.length;i++){
			var option = new Option(userArray[i],userArray[i])
			option.selected=true;
			userList.options.add(option);
		}
}
init();
</script>
<script>


function searchUsers(){
			var reviewUnit = opener.document.getElementById("CustActVarselectUnitValueCustActVar").value;
			if(reviewUnit==''){
				alert("请先选择电子会签单!");
				return;
			}
			var userName = document.getElementById("searchUserKey").value;
			if(userName == ""){
				alert("请输入用户名，如zhangsan");
				return;
			}else{								
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
          					alert("不支持AJAX!");
							return;
          				}
       			}
      		}
       	
				xmlHttpRequest.onreadystatechange=function(){
			  	if(xmlHttpRequest.readyState==4){
				  	if (xmlHttpRequest.status == 200) {
				  		var userNameList = xmlHttpRequest.responseText;
						if(userNameList.indexOf("ptcServiceName")>-1){
							alert("外部服务器不可用!");
							return;
						}else if(userNameList.indexOf("503")>-1){
							alert("外部服务器不可用!");
							return;
						}else if(userNameList.indexOf("refused")>-1){
							alert("外部服务器不可用!");
							return;
						}
				  		var userNameArray = userNameList.split("|");
						if(userNameArray.length<2){
							alert("没有相应的用户!");
							return;
						}						
						processUsers(userNameArray);
				  	}else{
				  		alert("远程调用出错!");
				  	}
				  }
				}
				
				xmlHttpRequest.open("GET","searchUser.jsp?userName="+userName+"&reviewUnit="+reviewUnit,true);
				xmlHttpRequest.setRequestHeader("If-Modified-Since","0");
				xmlHttpRequest.send(null);
			}
		}

	function processUsers(userNameArray){
				var userList = document.getElementById("users");
			var len = userList.options.length;

			var usersOidName="";
			for (var i = 1;i<len ;i++ )
			{
				usersOidName=usersOidName+";"+userList.options[i].value;
			}
				for(var i = 1; i <userNameArray.length;i++){					
					var userStr = userNameArray[i];
					var userArray = userStr.split(";");
					if(usersOidName.indexOf(userArray[0])<1){
						var option = new Option(userArray[0],userArray[0])
						userList.options.add(option);
					}					
				}
	}

	function setReviewUsers(){
			var userList = document.getElementById("users");
			var len = userList.options.length;
			var usersOidName="";
			for (var i = 1;i<len ;i++ )
			{
				if(userList.options[i].selected){
					if(usersOidName==""){
					usersOidName=userList.options[i].innerHTML
					}else{
					usersOidName=usersOidName+";"+userList.options[i].value;
					}					
				}
			}
	opener.document.getElementById("CustActVarselectUsersValueCustActVar").value=usersOidName;
	window.close();
	}
		</script>