<%@ page language="java" session="true" pageEncoding="GBK" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<jca:tabToHighlight actionName="propertyPanel" objectType="object"/>
<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<script type="text/javascript">

<%

%>


<jsp:include page="${mvc:getComponentURL('ext.casc.common.mvc.builder.SetTemplateEnumBuilder')}"
             flush="true"></jsp:include>
<script type="text/javascript">


</script>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>