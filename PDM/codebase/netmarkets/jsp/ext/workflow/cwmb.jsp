<%@page import="ext.casc.workflow.CwbmDBController"%>
<%@page import="ext.casc.constants.Constants"%>
<%@page import="wt.httpgw.URLFactory"%>
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
URLFactory fac=new URLFactory();
String herf=fac.getBaseHREF();
String dizhi= request.getParameter("dizhi");
String name=CwbmDBController.getTemplateNameById(dizhi);

%>

<script type="text/javascript">
function sure(){
	var mingchengValue=document.getElementById("mingcheng").value;
	mingchengValue = mingchengValue.replace(/[ ]/g,"");
	if(mingchengValue.length==0){
		alert("<%=Constants.QINGSHURUZHENGQUEMINGCHENG%>");
		return;
	}

	var xiaoduizhe1="";
	var xiaoduizhe=window.opener.document.getElementsByName("<%=Constants.JUESE_XIAODUIZHE%>");
	if(xiaoduizhe.length>0){
		  xiaoduizhe1=xiaoduizhe[0].value;
		}
	 var shenhezhe1="";
	var shenhezhe=window.opener.document.getElementsByName("<%=Constants.JUESE_SHENHEZHE%>");
	if(shenhezhe.length>0){
	  shenhezhe1=shenhezhe[0].value;
	}
	var neibuhuiqianzhe1="";
	var neibuhuiqianzhe=window.opener.document.getElementsByName("<%=Constants.JUESE_NEIBUHUIQIANZHE%>");
	if(neibuhuiqianzhe.length>0){
		for (var i=0;i<neibuhuiqianzhe.length;i++){
            neibuhuiqianzhe1=neibuhuiqianzhe1+"&&"+neibuhuiqianzhe[i].value;
        }
        neibuhuiqianzhe1 = neibuhuiqianzhe1.substring(2);
    }
    var biaoshenzhe1="";
	var biaoshenzhe=window.opener.document.getElementsByName("<%=Constants.JUESE_BIAOSHENZHE%>");
	if(biaoshenzhe.length>0){
		  biaoshenzhe1=biaoshenzhe[0].value;
		}
	var pizhunzhe1="";
	var pizhunzhe=window.opener.document.getElementsByName("<%=Constants.JUESE_PIZHUNZHE%>");
	if(pizhunzhe.length>0){
		  pizhunzhe1=pizhunzhe[0].value;
		}
	 var dayinzhe1="";
	var dayinzhe=window.opener.document.getElementsByName("<%=Constants.JUESE_DAYINZHE%>");
	if(dayinzhe.length>0){
		  dayinzhe1=dayinzhe[0].value;
		}
	var gongshidiongeyuan1="";
	var gongshidiongeyuan=window.opener.document.getElementsByName("<%=Constants.JUESE_GONGSHIDINGEYUAN%>");
	if(gongshidiongeyuan.length>0){
		  gongshidiongeyuan1=gongshidiongeyuan[0].value;
		}
	var waibuhuiqianzhe1="";
	var waibuhuiqianzhe=window.opener.document.getElementsByName("<%=Constants.JUESE_WAIBUHUIQIANZHE%>");
	if(waibuhuiqianzhe.length>0){
		  waibuhuiqianzhe1=waibuhuiqianzhe[0].value;
		}

	var xiaoduizhe=document.getElementById("xiaoduizhe");
	xiaoduizhe.value=xiaoduizhe1;
	var shenhezhe=document.getElementById("shenhezhe");
	shenhezhe.value=shenhezhe1;
	var neibuhuiqianzhe=document.getElementById("neibuhuiqianzhe");
	neibuhuiqianzhe.value=neibuhuiqianzhe1;
	var biaoshenzhe=document.getElementById("biaoshenzhe");
	biaoshenzhe.value=biaoshenzhe1;
	var pizhunzhe=document.getElementById("pizhunzhe");
	pizhunzhe.value=pizhunzhe1;
	var dayinzhe=document.getElementById("dayinzhe");
	dayinzhe.value=dayinzhe1;
	var gongshidiongeyuan=document.getElementById("gongshidiongeyuan");
	gongshidiongeyuan.value=gongshidiongeyuan1;
	var waibuhuiqianzhe=document.getElementById("waibuhuiqianzhe");
	waibuhuiqianzhe.value=waibuhuiqianzhe1;
	   document.getElementById("mblx1").value="<%=name%>";
	 document.getElementById("form").submit();
}

function can(){
	window.close();
}
</script>
<form id="form"  method="post" action="<%=herf%>app/netmarkets/jsp/ext/workflow/cwmbAction.jsp">
<input id="xiaoduizhe" type="hidden" name="xiaoduizhe" value="" >
<input id="shenhezhe" type="hidden" name="shenhezhe" value="" >
<input id="neibuhuiqianzhe" type="hidden" name="neibuhuiqianzhe" value="" >
<input id="biaoshenzhe" type="hidden" name="biaoshenzhe" value="" >
<input id="pizhunzhe" type="hidden" name="pizhunzhe" value="" >
<input id="dayinzhe" type="hidden" name="dayinzhe" value="" >
<input id="gongshidiongeyuan" type="hidden" name="gongshidiongeyuan" value="" >
<input id="waibuhuiqianzhe" type="hidden" name="waibuhuiqianzhe" value="" >
<input id="mblx1" type="hidden" name="mblx1" value="">
<table style='height:150px;width:400px;text-align:center' >
<tr>
<td><%=Constants.MUBANMINGCHENG %></td>
<td ><input  style="width:250px;" id="mingcheng" type="text" name="mingcheng" value="" required="required" ><font color=red>("<%=Constants.BITIAN%>")</font></td>
</tr>
<tr>
<tr>
</tr>
</table>
<tr>
<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td>
<input id="ok" type="button" name="ok" onclick="sure()"  value="<%=Constants.JSP_DISPLAY_OK%>"></td>
<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td><input id="cancel" type="button" name="cancel" value="<%=Constants.JSP_DISPLAY_CANCEL%>" onclick="can()" ></td>
</tr>
</form>
