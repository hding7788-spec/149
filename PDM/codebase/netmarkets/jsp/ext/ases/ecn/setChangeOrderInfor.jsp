<%-- <%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="wt.httpgw.URLFactory"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.glaway.mpm.util.IBAHelper"%>
<%@page import="ext.casc.ecn.changeinfo.ModifyChangeinfo"%>--%>
<%@page import="ext.casc.ecn.ecnConstant"
%>
<%-- <%
	URLFactory fac=new URLFactory();
String herf=fac.getBaseHREF();
%>--%>
<head>
<style type="text/css">
.td{
font-size:15px;
text-align:center;
background-color:rgb(192,192,192);
word-break: keep-all;white-space:nowrap;
}


</style>
</head>

<%
    String type = request.getParameter("type");
// NmCommandBean cb = new NmCommandBean();
// cb.setRequest(request);
// NmOid nmOid = cb.getActionOid();
// Object ob = nmOid.getRefObject();
// String Objnumber = "";
// if(ob instanceof WTChangeOrder2){
// 	WTChangeOrder2 order2 = (WTChangeOrder2)ob;
// 	IBAHelper iba = new IBAHelper(order2);
// 	Objnumber = order2.getNumber();
// }

String xuhao = ecnConstant.XUHAO;

String xuanxiangneirong = ecnConstant.XUANXIANGNEIRONG;

String caozuo = ecnConstant.CAOZUO;

String tianjiaxuanxiang = ecnConstant.TIANJIAXUANXIANG;

String deleterow = ecnConstant.DELROW;

String addrow = ecnConstant.ADDROW;

String cause = ecnConstant.CAUSE;

String designnumber = ecnConstant.DESIGNNUMBER;

String beforinfor = ecnConstant.BEFORINFOR;

String afterinfor = ecnConstant.AFTERINFOR;

String cancel = ecnConstant.CANCEL;

String complete = ecnConstant.COMPLETE;

String exception1 = ecnConstant.EXCEPTIONSTR1;

String exception5 = ecnConstant.EXCEPTIONSTR5;

String exception7 = ecnConstant.EXCEPTIONSTR7;

String exception9 = ecnConstant.EXCEPTIONSTR9;

String exception10 = ecnConstant.EXCEPTIONSTR10;

String sjgg = ecnConstant.SJGG;

String gyws = ecnConstant.GYWS;

String[] causes = {ecnConstant.GYWS, ecnConstant.SJGG};
if("doc".equals(type)){
    causes = new String[]{ecnConstant.FAYH, ecnConstant.NRWS, ecnConstant.SJGG};
}
StringBuffer sb = new StringBuffer();
String FIRST_HTML = "<select name=\"CHANGECAUSE";
String SECOND_HTML = "\" id=\"CHANGECAUSE_";
String THRERD_HTML = "\" onchange=changeValue(this) style=\"width:100;height:40;border:0\">";
sb.append("<option value=\"\"></option>");
for (int i = 0; i < causes.length; i++) {
    String tempStr = causes[i];
    sb.append("<option value=\"" + tempStr + "\">");
    sb.append(tempStr);
    sb.append("</option>");
}
String OPTIONS = sb.toString();
String FORTH = "</select>";

%>
<div style="border:1px solid rgb(192,192,192);padding-right:0px;position:relative" >
<div width= "100%"; style="background-color:rgb(230,238,243);border:1px solid rgb(230,238,243);"><h4><%=ecnConstant.title %></h4></div>
<div id="scroll_head" style="display:block; top: 0px; left: 0px; position: relative;">
<table  width= "100%"; id="tableSelect" border="1" style="border-collapse:collapse; ">
    <tr>
        <td width="60px" class="td"><%= xuhao %></td>
        <td width="100px" class="td"><%= cause %></td>
        <td width="200px" class="td"><%= designnumber %></td>
        <td class="td"><%= beforinfor %></td>
        <td class="td"><%= afterinfor %></td>
        <td width="60px" class="td"><%= caozuo %></td>
    </tr>
</table>
</div>

<div style="display:block; top: 50px; left: 0px; position: relative; margin:50px;">
<table id="tableSelect" border="0" cellpadding="4" cellspacing="1">
    <tr>
        <td><input type = "button" value = "<%= addrow %>" onclick="addrow()" style="font-size:12pt;width:80;height:30"/></td>
        <td width="60"></td>
        <td><input type = "button" value = "<%=complete%>" onclick="fncomplete()" style="font-size:12pt;width:80;height:30"/></td>
        <td width="60"></td>
        <td><input type = "button" value = "<%=cancel%>" onclick="cancle()" style="font-size:12pt;width:80;height:30"/></td>
        <td width="60"></td>
    </tr>
</table>
</div>
</div>
<%-- <form id="submitinfo" method="post" action="<%=herf%>app/netmarkets/jsp/ext/ases/ecn/actionChangeinfo.jsp">
<input id="cause" type="hidden" name="cause" value="" >
<input id="designnumber" type="hidden" name="designnumber" value="" >
<input id="beforinfor" type="hidden" name="beforinfor" value="" >
<input id="afterinfor" type="hidden" name="afterinfor" value="" >
<input id="objnum" type="hidden" name="objnum" value="<%= Objnumber%>">
 </form>--%>

<script type="text/javascript">


var tableSelect = document.getElementById("tableSelect");
var hfDelIds = document.getElementById("hfDelIds ");
var deleterow = '<%= deleterow %>';
var FIRST_HTML = '<%= FIRST_HTML %>';
var SECOND_HTML = '<%= SECOND_HTML %>';
var THRERD_HTML = '<%= THRERD_HTML %>';
var OPTIONS = '<%= OPTIONS %>';
var FORTH = '<%= FORTH %>';
function addrow()
{
    var r = tableSelect.insertRow(tableSelect.rows.length);//插入一行tr
    var c = r.insertCell(0);//插入一个单元格td
    var rIndex = tableSelect.rows.length - 1;
    c.innerHTML = rIndex;
    c.setAttribute('align','center');
    c = r.insertCell(1);
    c.innerHTML = FIRST_HTML + SECOND_HTML + rIndex + THRERD_HTML + OPTIONS + FORTH
    c = r.insertCell(2);
    c.innerHTML = "<input type=\"text\" id=\"DESIGNCHANGENUM_" + rIndex + "\" name=\"DESIGNCHANGENUM\" style=\"width:200;height:30;border:none\"/>";
    c = r.insertCell(3);
    c.innerHTML = "<textarea type=\"textarea\" id=\"CHANGEBEFOR_" + rIndex + "\" name=\"CHANGEBEFOR\" style=\"width:100%;height:40;border:none\"/>";
    c = r.insertCell(4);
    c.innerHTML = "<textarea type=\"textarea\" id=\"CHANGEAFTER_" + rIndex + "\" name=\"CHANGEAFTER\" style=\"width:100%;height:40;border:none\"/>";
    c = r.insertCell(5);
    c.innerHTML = "<input type=\"button\" value=\"" + deleterow + "\" onclick=\"delrow(" + rIndex + ")\"/>";

}
function delrow(index)
{

    tableSelect.deleteRow(index);
    for(var i = index; i < tableSelect.rows.length;i++)
    {
        tableSelect.rows[i].cells[0].innerHTML = i;
        var changeCause = tableSelect.rows[i].cells[1].getElementsByTagName("select");
        var designNum = tableSelect.rows[i].cells[2].getElementsByTagName("textarea");
        var cBeforInfor = tableSelect.rows[i].cells[3].getElementsByTagName("textarea");
        var cAfterInfor = tableSelect.rows[i].cells[4].getElementsByTagName("input");
        for(var j = 0; j < changeCause.length; j++)
        {
          changeCause[j].id = "CHANGECAUSE_" + i;
          changeCause[j].name = "CHANGECAUSE";
        }

        for(var j = 0; j < designNum.length; j++)
        {
           designNum[j].id = "DESIGNCHANGENUM_" + i;
           designNum[j].name = "DESIGNCHANGENUM";
        }

        for(var j = 0; j < cBeforInfor.length; j++)
        {
        	cBeforInfor[j].id = "CHANGEBEFOR_" + i;
        	cBeforInfor[j].name = "CHANGEBEFOR";
        }

        for(var j = 0; j < cAfterInfor.length; j++)
        {
        	cAfterInfor[j].id = "CHANGEAFTER_" + i;
        	cAfterInfor[j].name = "CHANGEAFTER";
        }
        tableSelect.rows[i].cells[5].innerHTML = "<input type=\"button\" value=\"" + deleterow + "\" onclick=\"delrow(" + i + ")\"/>";
    }
}


function fncomplete(){
	var causes = "";
	var designNums = "";
	var beforInfors = "";
	var afterInfors = "";
	var exception1 = '<%=exception1%>';
	var exception5 = '<%=exception5%>';
	var exception7 = '<%=exception7%>';
	var exception9 = '<%=exception9%>';
	var exception10 = '<%=exception10%>';
    var sjgg = '<%=sjgg%>';
    var causeslen = 0;
    var designNumslen = 0;
    var beforInforslen = 0;
    var afterInforslen = 0;

	for ( var i = 1; i < tableSelect.rows.length; i++) {
			var str = tableSelect.rows[i].cells[1].getElementsByTagName("select")[0].value;
			var changeBeboreInfo = tableSelect.rows[i].cells[3].getElementsByTagName("textarea")[0].value;
			var changeAfterInfo = tableSelect.rows[i].cells[4].getElementsByTagName("textarea")[0].value;
			if (str == "" || str == "null") {
				alert(exception5);
				return;
			} else{
				if(str == sjgg){
					var num = tableSelect.rows[i].cells[2].getElementsByTagName("input")[0].value;
					if (num == "" || num == "null") {
					alert(exception1);
					return;
					}
				}
                changeBeboreInfo   =   changeBeboreInfo.replace(/^\s+|\s+$/g,"");
                changeAfterInfo   =   changeAfterInfo.replace(/^\s+|\s+$/g,"");
				if((changeBeboreInfo == null || changeBeboreInfo == "" || changeBeboreInfo == "null") || (changeAfterInfo == null || changeAfterInfo == "" || changeAfterInfo == "null")){
					alert(exception7);
					return;
				}
			}
		}

		for ( var i = 1; i < tableSelect.rows.length; i++) {

			causes += tableSelect.rows[i].cells[1]
					.getElementsByTagName("select")[0].value;

			designNums += tableSelect.rows[i].cells[2]
					.getElementsByTagName("input")[0].value;

			beforInfors += tableSelect.rows[i].cells[3]
					.getElementsByTagName("textarea")[0].value;

			afterInfors += tableSelect.rows[i].cells[4]
					.getElementsByTagName("textarea")[0].value;
			if(tableSelect.rows[i].cells[1]
			.getElementsByTagName("select")[0].value.indexOf("~")!=-1||tableSelect.rows[i].cells[2]
			.getElementsByTagName("input")[0].value.indexOf("~")!=-1||tableSelect.rows[i].cells[3]
			.getElementsByTagName("textarea")[0].value.indexOf("~")!=-1||tableSelect.rows[i].cells[4]
			.getElementsByTagName("textarea")[0].value.indexOf("~")!=-1){
				alert(exception9);
				return;
			}
	 		if(i+1<tableSelect.rows.length){
				causes += "~";
				designNums += "~";
				beforInfors += "~";
				afterInfors += "~";

			}
		}
		for(var i=0;i<causes.length;i++){
			if(causes.charCodeAt(i)>255){
				causeslen = causeslen + 1;
			}else{
				causeslen = causeslen + 0.5;
			}
		}
		for(var i=0;i<designNums.length;i++){
			if(designNums.charCodeAt(i)>255){
				designNumslen = designNumslen + 1;
			}else{
				designNumslen = designNumslen + 0.5;
			}
		}
		for(var i=0;i<beforInfors.length;i++){
			if(beforInfors.charCodeAt(i)>255){
				beforInforslen = beforInforslen + 1;
			}else{
				beforInforslen = beforInforslen + 0.5;
			}
		}
		for(var i=0;i<afterInfors.length;i++){
			if(afterInfors.charCodeAt(i)>255){
				afterInforslen = afterInforslen + 1;
			}else{
				afterInforslen = afterInforslen + 0.5;
			}
		}
		if(causeslen > 500||designNumslen>500||beforInforslen>500||afterInforslen>500){
// 			var cause=document.getElementById("cause");
// 			cause.value = causes;
// 			var designnumber = document.getElementById("designnumber");
// 			designnumber.value = designNums;
// 			var beforinfor =document.getElementById("beforinfor");
// 			beforinfor.value = beforInfors;
// 			var afterinfor = document.getElementById("afterinfor");
// 			afterinfor.value = afterInfors;
// 			document.getElementById("submitinfo").submit();
// 			window.close();
			alert(exception10);
		}else{
			window.opener.document.getElementById('CHANGECAUSE').value = causes;
			window.opener.document.getElementById('DESIGNCHANGENUM').value = designNums;
			window.opener.document.getElementById('CHANGEBEFOR').value = beforInfors;
			window.opener.document.getElementById('CHANGEAFTER').value = afterInfors;
			window.close();
		}
	}

	function cancle() {
		window.close();
	}

	window.onload = function(){
		for ( var i = 1; i < tableSelect.rows.length; i++) {
			var str = tableSelect.rows[i].cells[1].getElementsByTagName("select")[0].value;
			if(str == '<%= gyws%>'){
				document.getElementById("DESIGNCHANGENUM_" + i).readOnly = true;
			}
		}
	}
    function changeValue(obj){
    	var sjgg = '<%=sjgg%>';
    	var arry = obj.id.split("_");
   	    var tarId = "DESIGNCHANGENUM_" + arry[1];
         if(obj.value!=sjgg){
        	 document.getElementById(tarId).value = "";
        	 document.getElementById(tarId).readOnly = true;
         }else{
        	 document.getElementById(tarId).readOnly = false;
         }
    }


	<!-- 初始化数据 -->
	var changeOrderCause = window.opener.document.getElementById('CHANGECAUSE').value
			.split("~");
	var designChangeNum = window.opener.document
			.getElementById('DESIGNCHANGENUM').value.split("~");
	var changeOrderBefor = window.opener.document.getElementById('CHANGEBEFOR').value
			.split("~");
	var changeOrderAfter = window.opener.document.getElementById('CHANGEAFTER').value
			.split("~");

	if (changeOrderCause != null && changeOrderCause.length > 0) {
		for ( var i = 0; i < changeOrderCause.length; i++) {
			addrow();
		}

		for ( var k = 1; k < tableSelect.rows.length; k++) {
			tableSelect.rows[k].cells[1].getElementsByTagName("select")[0].value = changeOrderCause[k - 1];

			tableSelect.rows[k].cells[2].getElementsByTagName("input")[0].value = designChangeNum[k - 1];

			tableSelect.rows[k].cells[3].getElementsByTagName("textarea")[0].value = changeOrderBefor[k - 1];

			tableSelect.rows[k].cells[4].getElementsByTagName("textarea")[0].value = changeOrderAfter[k - 1];

		}
	}

</script>
