<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.pbom.TechnicSignedBrowsePBOMBuilder')}" flush="true" />

<%@include file="/netmarkets/jsp/util/end.jspf"%>
