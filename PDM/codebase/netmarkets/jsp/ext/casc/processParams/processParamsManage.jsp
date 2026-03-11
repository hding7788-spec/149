<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />	
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>


<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>

<fmt:setBundle basename="ext.ases.dataSearch.resource.viewHelperResource" />
<fmt:message var="dataTransferTitle" key="DATATRANSFERTITLE" />
<fmt:message var="number" key="NUMBER" />
<fmt:message var="name" key="NAME" />
<fmt:message var="package_num" key="PACKAGE_NUM" />
<fmt:message var="send_date" key="SEND_DATE" />
<fmt:message var="from" key="FROM" />
<fmt:message var="to" key="TO" />
<fmt:message var="product_code" key="PRODUCT_CODE" />
<fmt:message var="send_type" key="SEND_TYPE" />
<fmt:message var="carft_sign" key="CRAFTSIGN" />
<fmt:message var="sendindueform" key="SENDINDUEFORM" />
<fmt:message var="search" key="SEARCH" />
<fmt:message var="search_result" key="SEARCH_RESULT" />
<fmt:message var="obj_version" key="OBJ_VERSION" />

<script language="javascript">
	
	function submitQuery(){

		PTC.jca.table.Utils.reload('ext.casc.common.mvc.builder.ProcessParamsManageBuilder', {}, true);
    }

		
</script>

<fieldset >
	<legend>
	    <font size="3px" style="font-weight: bold;"> ${dataTransferTitle}</font>
	</legend>
</fieldset>

<table border="0" >

 <input type="hidden"  name="null___searchFlag___textbox" id ="searchFlag" value="${searchFlag_default}" >
 <input type="hidden"  name="null___sendStart___textbox" id ="sendStart" value="" >
 <input type="hidden"  name="null___sendEnd___textbox" id ="sendEnd" value="" >

	<tr id="package_search">
	 <td>
	   <label for="gyName"><font style="font-weight: bold;"> 工艺参数名称： </font></label>
       &nbsp;&nbsp;
	  <input type="text" value="${number_default}" name="null___gyName___textbox" id="gyName" maxlength="100" required="">

	   &nbsp;&nbsp;&nbsp;&nbsp;
		 <label for="parameterCategory"><font style="font-weight: bold;">工艺参数类型: </font></label>
		 &nbsp;&nbsp;
		 <select  name="null___parameterCategory___textbox" id="parameterCategory" maxlength="100" required="">
			 <option value=""></option>
			 <option value="基础参数">基础参数</option>
			 <option value="枚举参数">枚举参数</option>
			 <option value="知识参数">知识参数</option>
		 </select>
		 &nbsp;&nbsp;&nbsp;&nbsp;
		 <label for="processCategory"><font style="font-weight: bold;">专业: </font></label>
		 &nbsp;&nbsp;
		 <select name="null___processCategory___textbox" id="processCategory" maxlength="100" required="">
			 <% for(String typeDisplay :ext.casc.processPlan.Constants.TECHNICSTYPES_DISPLAY){ %>
			 <option value="<%=typeDisplay%>"><%=typeDisplay%></option>
			 <% }%>
		 </select>
	 </td>
	  
	</tr>
	

     <tr>
	 <td>



	 </td>	  
	</tr>
	<tr/>
	<tr>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" value="${search}" onclick="submitQuery();"/></td>
	</tr>
</table>
<br/>
<fieldset >
	<legend>
	    <font style="font-weight: bold;"> ${search_result}</font>
	</legend>
</fieldset>

<jsp:include page="${mvc:getComponentURL('ext.casc.common.mvc.builder.ProcessParamsManageBuilder')}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>