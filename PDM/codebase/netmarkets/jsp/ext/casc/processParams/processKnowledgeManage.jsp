<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@page import="wt.util.WTProperties"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />	
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<%@ page import="java.util.Map,java.util.Locale"%> 
<%@ page import="wt.org.WTUser, wt.session.SessionHelper"%> 
<%@ page import="java.util.Calendar,java.text.SimpleDateFormat,java.util.GregorianCalendar"%>
<%@ page import="java.util.*"%>
<%@ page import="ext.casc.constants.Constants"%>
<%@ page import="java.net.*"%>
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
	


		
</script>



<br/>
<fieldset >
	<legend>
	    <font style="font-weight: bold;"> 工艺知识管理</font>
	</legend>
</fieldset>

<jsp:include page="${mvc:getComponentURL('ext.casc.common.mvc.builder.ProcessKnowledgeManageBuilder')}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>