//httpOcxUrl是websocket 服务器地址及端口
var httpOcxUrl='http://127.0.0.1:5988';
//var ie_browser=false;

function toAjax(url, senddata, callback) {
	var xhr;
	
	if(window.XMLHttpRequest){
		//IE7+，其他浏览器
		xhr=new XMLHttpRequest();
	}else{
		//IE低版本
		xhr=ActiveXObject('Microsoft.XMLHTTP');
	}
	
	xhr.open('post',url,false);	
	//接收 3
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4) {
			if(xhr.status==200){
				if(callback) {
					var data=$.parseJSON(xhr.responseText)
					//alert(data.value);
					callback(data.value);
				}
			} else if(xhr.status==12029 || xhr.status==12007){
				alert("调用UKeyControl接口失败");
				callback("#"+xhr.status);
			} else{
				//alert("error:"+xhr.status);
				callback("#"+xhr.status);
			}
		}
	}
	
	xhr.send(senddata);
}

function sendAjaxToServer(methodName,param1,param2,param3){
	//console.log("方法名："+methodName+"参数列表{"+param1+"、"+param2+"、"+param3+"}");

	sendMessage={};
	sendMessage.methodName=methodName;
	sendMessage.param1=param1;
	sendMessage.param2=param2;
	sendMessage.param3=param3;
	var jsondata = JSON.stringify(sendMessage);
	jsondata = jsondata.replace(/ /g,"%20");
	
	var senddata="ukeyjsondata="+jsondata;
	
	var ajax_ret;
	function ajaxResultDeal(response) {
		ajax_ret=response;
	}
	
	var cb=ajaxResultDeal;
	toAjax(httpOcxUrl, senddata, cb);
	
	//console.log("服务器返回结果："+ajax_ret);
	//console.log(".....................................................................");
	
	return ajax_ret;	
}

//----------------------------------------begin-----------------------------------------------

function CheckDev(port) {
	return sendAjaxToServer('CheckDev',port,null,null)
}

function EnumDev() {
	return sendAjaxToServer('EnumDev',null,null,null)
}

function VgetnameFirst(ocsp_ip,port) {
	return sendAjaxToServer('VgetnameFirst',ocsp_ip,port,null)
} 

function Vgetname(psw,ocsp_ip,port) {
	return sendAjaxToServer('Vgetname',psw,ocsp_ip,port)
} 

function concat(asc,base1,base2) {
	return sendAjaxToServer('concat',asc,base1,base2)
}

function dataSignCertEx(data, port, ishash) {
	return sendAjaxToServer('dataSignCertEx',data,port,ishash)
}

function readCert(which, port) {
	return sendAjaxToServer('readCert',which,port,null)
}

function ParseCert(cert, parseStr, base64Flag) {
	return sendAjaxToServer('ParseCert',cert,parseStr,base64Flag)
}

//-----------------------------------------end----------------------------------------
