<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@page import="wt.util.WTProperties"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />	
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<%@ page import="com.ptc.netmarkets.model.NmOid" %>

<jsp:include page="${mvc:getComponentURL('ext.casc.purge.mvc.builder.DeleteInvalidPartDataBuilder')}" />

<jsp:include page="${mvc:getComponentURL('ext.casc.purge.mvc.builder.DeleteInvalidDocumentDataBuilder')}" />

<jsp:include page="${mvc:getComponentURL('ext.casc.purge.mvc.builder.DeleteInvalidCADDataBuilder')}" />

<jsp:include page="${mvc:getComponentURL('ext.casc.purge.mvc.builder.DeleteInvalidEPMWorkspaceDataBuilder')}" />


<%@ include file="/netmarkets/jsp/util/end.jspf"%>