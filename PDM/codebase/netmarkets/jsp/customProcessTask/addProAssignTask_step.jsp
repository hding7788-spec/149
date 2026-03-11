<%@page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />	
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<%@page import="wt.util.WTProperties"%>
<%@page import="java.util.List"%>
<%@page import="ext.casc.process.ProcessConstants"%>
<%@page import="ext.casc.process.util.ProcessUtil"%>

<script> 
		var oidArray = new Array();
</script>

<%
	List<String> oidList = (List<String>) session.getAttribute("oidList");
	//out.println("-------oidList:"+oidList);
	String msg = ProcessConstants.JSP_JS_VALIDATE;
	if(oidList != null && oidList.size() > 0){
		for(int i = 0 ; i < oidList.size() ; i++){
			String tempOid = oidList.get(i);
%>
<script>
	var tempOid = '<%=tempOid%>';
	oidArray[oidArray.length] = tempOid;
</script>
<%
		}
	}
%>

<input type="hidden" name="oidArray" value="${oidArray}" >

<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.AddProAssignTaskBuilder')}" />

<script language="javascript">
function verify1(object){
	var selectValue = object.value;
	var selectId = object.id;
	var index = selectId.indexOf("_zhuzhichejian");
	var tempOid = selectId.substring(0,index);
	
	var selectElement1 = document.getElementById(tempOid+"_fuzhichejian1");
	var selectElement2 = document.getElementById(tempOid+"_fuzhichejian2");
	var selectElement3 = document.getElementById(tempOid+"_fuzhichejian3");
	var selectElement4 = document.getElementById(tempOid+"_fuzhichejian4");
	var selectElement5 = document.getElementById(tempOid+"_fuzhichejian5");
	var selectElement6 = document.getElementById(tempOid+"_fuzhichejian6");
	var selectElement7 = document.getElementById(tempOid+"_fuzhichejian7");
	var selectElement8 = document.getElementById(tempOid+"_fuzhichejian8");
	var selectElement9 = document.getElementById(tempOid+"_fuzhichejian9");
	
	if((selectElement1.checked&&selectValue=="1")||
		(selectElement2.checked&&selectValue=="2")||
		(selectElement3.checked&&selectValue=="3")||
		(selectElement4.checked&&selectValue=="4")||
		(selectElement5.checked&&selectValue=="5")||
		(selectElement6.checked&&selectValue=="6")||
		(selectElement7.checked&&selectValue=="7")||
		(selectElement8.checked&&selectValue=="8")||
		(selectElement9.checked&&selectValue=="9")){
		alert("<%=new String("辅制车间已经分配了该车间，请重新分配".getBytes("iso-8859-1"),"GBK")%>");		
	}
	
}
function verify2(object){
	if(object.checked){
		var selectValue = object.value;
		var selectId = object.id;
		var index = selectId.indexOf("_fuzhichejian");
		var tempOid = selectId.substring(0,index);
		var selectElement = document.getElementById(tempOid+"_zhuzhichejian");
		if(selectValue== selectElement.value){
			alert("<%=new String("主制车间已经分配了该车间，请重新分配".getBytes("iso-8859-1"),"GBK")%>");		
		}
	}
}
function validateValue(){
	for(var i = 0 ; i < oidArray.length ; i++){
		var tempOid = oidArray[i];
		var selectElement1 = document.getElementById(tempOid+"_fuzhichejian1");
		var selectElement2 = document.getElementById(tempOid+"_fuzhichejian2");
		var selectElement3 = document.getElementById(tempOid+"_fuzhichejian3");
		var selectElement4 = document.getElementById(tempOid+"_fuzhichejian4");
		var selectElement5 = document.getElementById(tempOid+"_fuzhichejian5");
		var selectElement6 = document.getElementById(tempOid+"_fuzhichejian6");
		var selectElement7 = document.getElementById(tempOid+"_fuzhichejian7");
		var selectElement8 = document.getElementById(tempOid+"_fuzhichejian8");
		if(selectElement1!=null&&selectElement2!=null&&selectElement3!=null&&selectElement4!=null
				&&selectElement5!=null&&selectElement6!=null&&selectElement7!=null&&selectElement8!=null){
			if(!selectElement1.checked&&!selectElement2.checked&&!selectElement3.checked
					&&!selectElement4.checked&&!selectElement5.checked&&!selectElement6.checked
					&&!selectElement7.checked&&!selectElement8.checked){
				alert("<%=msg%>");
				return false;
			}
		}
	}
	return true;
}

</SCRIPT>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>