<%@ page language="java" session="true" pageEncoding="GBK" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<jca:tabToHighlight actionName="propertyPanel" objectType="object"/>
<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
String contextPath = request.getContextPath();

%>
<script type="text/javascript">
function setParamValues(obj,templateId,gyParamNumber,partNumber){

	var url = "<%=contextPath%>/netmarkets/jsp/ext/casc/processPlan/editParams.jsp?";
	var paramValue = obj.value;
	var params = "templateId="+templateId+"&gyParamNumber="+gyParamNumber+"&paramValue="+paramValue+"&partNumber="+partNumber;

	params = encodeURI(params);

	window.open(url+params,"_blank","location=no,resizable=yes,top=100,toolbar=no,height=450,width=400");

}


</script>
<%

%>


<jsp:include page="${mvc:getComponentURL('ext.casc.processPlan.mvc.builder.BatchSetParamsBuilder')}"
             flush="true"></jsp:include>
<script type="text/javascript">


</script>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>