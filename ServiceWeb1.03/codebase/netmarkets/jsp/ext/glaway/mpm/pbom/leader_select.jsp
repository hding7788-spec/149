<%@ page import="com.glaway.mpm.pbom.ui.PBOMResource" %>
<%@ page import="java.util.*"%>
<%@ page import="com.glaway.mpm.util.Util" %>
<%@ page import="wt.part.WTPart" %>
<%@ page import="com.ptc.netmarkets.model.NmOid" %>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.glaway.mpm.util.ReferenceFactory"%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<jsp:useBean id="commandbean" scope="request" class="com.ptc.netmarkets.util.beans.NmCommandBean" />

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.glaway.mpm.pbom.ui.PBOMResource"/>
<fmt:message var="technic_group" key="<%=PBOMResource.TECHNICGROUP %>"/>
<fmt:message var="responser" key="<%=PBOMResource.RESPONSER %>"/>
<fmt:message var="selectnode" key="<%=PBOMResource.SELECTEDNODE %>"/>
<body>
<script type="text/javascript">
	var xmlHttpRequest;
	
	function changeResponsor(){
		var group = document.getElementById("group");
		var hiddenOid = document.getElementById("oid");
		search(group.value, hiddenOid.value);
	}
	
	function createXmlHttpRequest(){
    	if(window.ActiveXObject){
            return new ActiveXObject("Microsoft.XMLHTTP");
        }
        else if( window.XMLHttpRequest){
            return new XMLHttpRequest();
        }
    }
    
    function search(para, oid){
       var url="netmarkets/jsp/glaway/mpm/pbom/searchResponsor.jsp?para=" + para + "&oid=" + oid;
        xmlHttpRequest=createXmlHttpRequest();
        xmlHttpRequest.onreadystatechange=callbackRole;   //将回调函数注册给状态改变事件
        xmlHttpRequest.open("GET",url,true);
        xmlHttpRequest.setRequestHeader("If-Modified-Since","0");
        xmlHttpRequest.send(null);
    }
    
    function callbackRole(){     
         var user = document.getElementById("respon");
         if(xmlHttpRequest.readyState==4&&xmlHttpRequest.status==200){
         	var result=xmlHttpRequest.responseText;
         	if(result.trim() == ""){
         		alert("此组下无任何人员，请联系管理员!");
         		user.length = 0;
         		user.options.add("","");
         	}else{
	         	var everyUserObj= result.split("|");
	         	user.length = 0;
	         	var count = everyUserObj.length;
	         	for(var i = 0; i < count; i++){
	         		var everUser = everyUserObj[i];
	         		user.options.add(new Option(everUser.split("#")[1],everUser.split("#")[0]));
	         	}
         	}
         }
    }
</script>

<%
	commandbean.setRequest(request);
	String oid = request.getParameter("oid");

	WTPart partTemp = (WTPart)ReferenceFactory.getObjectbyOid(oid);
	
	ArrayList<ArrayList<String>> groupList = Util.getCurrentUsersGroupName(partTemp);
	
	ArrayList<String> listGroupValue = new ArrayList<String>();//显示组外部名称
	ArrayList<String> listGroupKey = new ArrayList<String>();//显示组内部名称
	
	listGroupValue = groupList.get(0);
	request.setAttribute("listGroupValue", listGroupValue);
	listGroupKey = groupList.get(1);
	request.setAttribute("listGroupKey", listGroupKey);
	
	ArrayList<String> listResponsorKey = new ArrayList<String>();//显示人员内部名称
	ArrayList<String> listResponsorValue = new ArrayList<String>();//显示人员外部名称
	ArrayList<String> listUser = new ArrayList<String>();//显示指定组员
	ArrayList<String> listUserTemp = new ArrayList<String>();//显示指定组员
	

	ArrayList<ArrayList<String>> allList = Util.getCurrentContainerTeamGroupUsers(partTemp, listGroupKey.get(0));
	for(int i = 0; i < allList.size(); i++){
		ArrayList<String> list = allList.get(i);
		System.out.println("listResponsorKey:" + list.get(0));
		System.out.println("listResponsorValue:" + list.get(1));
		listResponsorKey.add(list.get(0));
		listResponsorValue.add(list.get(1));
	}
	request.setAttribute("listResponsorKey", listResponsorKey);
	request.setAttribute("listResponsorValue", listResponsorValue);
	System.out.println("listGroupKey.get(0):" + listGroupKey.get(0));
	
	listUserTemp = Util.getCurrentContainerTeamGroupUser(partTemp, listGroupKey.get(0));
	listUser.add(listUserTemp.get(0));
	request.setAttribute("listUser", listUser);
	String selectParts = "";
	ArrayList oidList = commandbean.getSelectedOidForPopup();
	
	for(int i = 0; i < oidList.size(); i++){
		NmOid nmoid = (NmOid)oidList.get(i);
		WTPart part = (WTPart)nmoid.getRef();
		selectParts += part.getName() + ",";
	}
	selectParts = selectParts.substring(0, selectParts.length() -1);
%>

<input type="hidden" id="oid" name="oid" value="<%=oid %>" />

<table>
	<tr>
		<td><w:label value="${selectnode}:" id="selectNode" name="selectNode"/></td>
		<td><%=selectParts %></td>
	</tr>
	<tr>
	</tr>
	<tr>
	</tr>
</table>
	<jca:renderPropertyPanel>
	<w:comboBox propertyLabel="${technic_group}" id="group" name="group" required="true" internalValues="${listGroupKey}" displayValues="${listGroupValue}" onchange="changeResponsor()"></w:comboBox>
	<w:comboBox propertyLabel="${responser}" id="respon" name="respon" required="true" internalValues="${listResponsorKey}" displayValues="${listResponsorValue}" selectedValues="${listUser}"></w:comboBox>
	</jca:renderPropertyPanel>




<%@ include file="/netmarkets/jsp/util/end.jspf"%>

</body>
