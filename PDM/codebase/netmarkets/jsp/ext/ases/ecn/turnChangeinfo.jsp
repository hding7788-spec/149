<%@page import="com.glaway.mpm.util.IBAHelper"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="ext.casc.ecn.changeinfo.ModifyChangeinfo"%>
<%@page import="ext.casc.ecn.ecnConstant"%>
<%@page import="wt.change2.WTChangeOrder2"%>
<%@page import="wt.httpgw.URLFactory"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List,java.util.Map"
%>
<%@ page import="com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper" %>
<%
	URLFactory fac=new URLFactory();
String herf=fac.getBaseHREF();
%>
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
	NmCommandBean cb = new NmCommandBean();
cb.setRequest(request);

NmOid nmOid = cb.getActionOid();
Object ob = nmOid.getRefObject();
String changeCause = "";
String designNum = "";
String changeBefore = "";
String changeAfter = "";
String Objnumber = "";
	String sjgg = ecnConstant.SJGG;
	String gyws = ecnConstant.GYWS;
	String[] causes = {ecnConstant.GYWS, ecnConstant.SJGG};
if(ob instanceof WTChangeOrder2){
	WTChangeOrder2 order2 = (WTChangeOrder2)ob;
	IBAHelper iba = new IBAHelper(order2);
	changeCause = iba.getIBADisplayValue("CHANGECAUSE");
	if(changeCause != null){
		changeCause = changeCause.replace("\r\n","");
	}
	designNum = iba.getIBADisplayValue("DESIGNCHANGENUM");
	if(designNum != null) {
		designNum = designNum.replace("\r\n","");
	}
	if("null".equals(designNum)||designNum == null){
		designNum = "";
	}
	changeBefore = iba.getIBADisplayValue("CHANGEBEFOR");
	if(changeBefore == null || "null".equals(changeBefore)){
		changeBefore = "";
	} else {
		changeBefore = changeBefore.replace("\r\n","");
	}
	changeAfter = iba.getIBADisplayValue("CHANGEAFTER");
	if(changeAfter == null || "null".equals(changeAfter)){
		changeAfter = "";
	} else {
		changeAfter = changeAfter.replace("\r\n","");
	}
	Objnumber = order2.getNumber();

	String objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(order2).toString();
	//文档更改单
	if(objectType.indexOf("casc.sast.149.DOCUMENT_ECN") > -1){
		causes = new String[]{ecnConstant.FAYH, ecnConstant.NRWS, ecnConstant.SJGG};
	}
}
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

String str = "";
List<Map<String, Object>> list = ModifyChangeinfo.searchAll(Objnumber);
if(list.size() > 0){
	str = "true";
}else{
	str = "flase";
}

List list1 = new ArrayList();
List list2 = new ArrayList();
List list3 = new ArrayList();
List list4 = new ArrayList();

String cause0 = "";
String designNum0 = "";
String beforInfo0 = "";
String afterInfo0 = "";

String cause1 = "";
String designNum1 = "";
String beforInfo1 ="";
String afterInfo1 ="";

for(int i=0;i<list.size();i++){
	cause0 = (String)list.get(i).get("CHANGEINFO_REASON");
	if(cause0 == null||("null").equals(cause0)){
		cause0 = ""+"~";
	}else{
		cause0 = cause0 + "~";
	}
	designNum0 = (String)list.get(i).get("CHANGEINFO_PLAN");
	if(designNum0 == null||("null").equals(designNum0)){
		designNum0 = ""+"~";
	}else{
		designNum0 = designNum0 + "~";
	}
	beforInfo0 = (String)list.get(i).get("CHANGEINFO_BEFORE");
	if(beforInfo0 == null||("null").equals(beforInfo0)){
		beforInfo0 = ""+"~";
	}else{
		beforInfo0 = beforInfo0 + "~";
	}
	afterInfo0 = (String)list.get(i).get("CHANGEINFO_BEHIND");
	if(afterInfo0 == null||("null").equals(afterInfo0)){
		afterInfo0 = ""+"~";
	}else{
		afterInfo0 = afterInfo0 + "~";
	}
	list1.add(cause0);
	list2.add(designNum0);
	list3.add(beforInfo0);
	list4.add(afterInfo0);
}
for(int i=0;i<list1.size();i++){
	cause1 += list1.get(i);
	designNum1 += list2.get(i);
	beforInfo1 += list3.get(i);
	afterInfo1 += list4.get(i);
}

if(!("").equals(cause1)){
cause1 = cause1.substring(0,cause1.length()-1);
designNum1 =designNum1.substring(0,designNum1.length()-1);
beforInfo1 = beforInfo1.substring(0,beforInfo1.length()-1);
afterInfo1 = afterInfo1.substring(0,afterInfo1.length()-1);
}
	if(cause1 != null){
		cause1 = cause1.replace("\r\n","");
	}
	if(designNum1 != null){
		designNum1 = designNum1.replace("\r\n","");
	}
	if(beforInfo1 != null){
		beforInfo1 = beforInfo1.replace("\r\n","");
	}
	if(afterInfo1 != null) {
		afterInfo1 = afterInfo1.replace("\r\n", "");
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
<form id="submitinfo" method="post" action="<%=herf%>app/netmarkets/jsp/ext/ases/ecn/actionChangeinfo.jsp">
<input id="cause" type="hidden" name="cause" value="" >
<input id="designnumber" type="hidden" name="designnumber" value="" >
<input id="beforinfor" type="hidden" name="beforinfor" value="" >
<input id="afterinfor" type="hidden" name="afterinfor" value="" >
<input id="objnum" type="hidden" name="objnum" value="<%= Objnumber%>">
</form>

<script type="text/javascript">


var tableSelect = document.getElementById("tableSelect");
var hfDelIds = document.getElementById("hfDelIds ");
var deleterow = '<%= deleterow %>';
var FIRST_HTML = '<%= FIRST_HTML %>';
var SECOND_HTML = '<%= SECOND_HTML %>';
var THRERD_HTML = '<%= THRERD_HTML %>';
var OPTIONS = '<%= OPTIONS %>';
var FORTH = '<%= FORTH %>';
var objnum = '<%= Objnumber%>';
var str = '<%= str%>';
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
    var sjgg = '<%=sjgg%>';
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
				if((changeBeboreInfo == "" || changeBeboreInfo == "null") || (changeAfterInfo == "" || changeAfterInfo == "null")){
					alert(exception7);
					return;
				}
			}
		}
		for ( var i = 1; i < tableSelect.rows.length; i++) {

			causes += tableSelect.rows[i].cells[1]
					.getElementsByTagName("select")[0].value;

			if(tableSelect.rows[i].cells[2].getElementsByTagName("input")[0].value == ""){
				designNums = designNums + "null"
			}else{
				designNums += tableSelect.rows[i].cells[2]
				.getElementsByTagName("input")[0].value;
			}

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
			var cause=document.getElementById("cause");
			cause.value = causes;
			var designnumber = document.getElementById("designnumber");
			designnumber.value = designNums;
			var beforinfor =document.getElementById("beforinfor");
			beforinfor.value = beforInfors;
			var afterinfor = document.getElementById("afterinfor");
			afterinfor.value = afterInfors;
		document.getElementById("submitinfo").submit();
// 		window.close();
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
	if(str=="true"){


		var changeOrderCause = '<%=cause1 %>'.split("~");
		var designChangeNum = '<%=designNum1 %>'.split("~");
		var changeOrderBefor = '<%=beforInfo1 %>'.split("~");
		var changeOrderAfter = '<%=afterInfo1 %>'.split("~");
	}else{
		var changeOrderCause = '<%= changeCause %>'.split("~");
		var designChangeNum = '<%= designNum %>'.split("~");
		var changeOrderBefor = '<%= changeBefore %>'.split("~");
		var changeOrderAfter = '<%= changeAfter %>'.split("~");
	}
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
