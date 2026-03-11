<%@page language="java" session="true" pageEncoding="GBK"%>
 <%@page import="java.util.Iterator"%>
<%@page import="ext.casc.util.WCUtil,java.util.Set,ext.casc.constants.Constants"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />

<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<%@page import="wt.util.WTProperties"%>
<%@page import="java.util.List"%>
<%@page import="ext.casc.process.ProcessConstants"%>
<%@page import="ext.casc.process.util.ProcessUtil"%>
<%
Set<String> set = WCUtil.getRoleListBySelectProduct(commandBean);
Iterator<String> iterator = set.iterator();
%>

<script language="javascript">
	Ext.override(Ext.ux.grid.BufferView,{cacheSize:1000});
	function changeRole(){
		var role = document.getElementById("role").value;
		var ele = document.getElementById("selectRole");
      	ele.value = role;

      	PTC.jca.table.Utils.reload('RoleListBuilder', {}, true);
	}

	/**
	*author:陈鸣
	*时间：2015年8-26
	*修改所选择产品的角色参与者
	*
	*/
	function selectAll(object){
		 var a1=object.id.split("_OR")[0];
		 var a2=object.id.split("wt.org.WTUser:")[1];
		var grid = Ext.getCmp("RoleListBuilder");
		var selModel = grid.getSelectionModel();
		var selectedRows = selModel.getSelections();
		if (selectedRows.length > 0) {
			for (var i = 0; i < selectedRows.length; i++) {
				var temp = selectedRows[i].id;//custom$updateRoleUsers_step$OR:wt.inf.container.ExchangeContainer:5$OR:wt.pdmlink.PDMLinkProduct:1177901!*
				var a3=temp.split(".PDMLinkProduct:")[1];
				var a4=a3.split("!")[0];
				var proid ="_OR:wt.pdmlink.PDMLinkProduct:";//OR:wt.pdmlink.PDMLinkProduct:1177901
				var inputid = a1+proid+a4+"_OR:wt.org.WTUser:"+a2;
				if(document.getElementById(inputid)){
					document.getElementById(inputid).checked = object.checked;
				}

			}
		}
		selModel.clearSelections();
	}

</script>

<input type="hidden" name="selectRole" id="selectRole" value=""/>

<table>
  <tr>
    <td><%=Constants.PRODUCT_SELECT_ROLE %></td>
    <td>
		<select id="role" name="role" onchange="changeRole();">
			<option value=""></option>
			<% while(iterator.hasNext()){
				    String roleName = iterator.next();
			%>
			<option value="<%=roleName %>"><%=roleName %></option>
			<%
			   }
			%>
		</select>
	</td>
  </tr>
</table>

<jsp:include page="${mvc:getComponentURL('ext.casc.product.mvc.builder.RoleListBuilder')}" />




<%@ include file="/netmarkets/jsp/util/end.jspf"%>