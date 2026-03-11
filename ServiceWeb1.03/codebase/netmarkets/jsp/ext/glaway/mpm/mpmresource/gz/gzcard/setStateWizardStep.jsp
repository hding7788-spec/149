<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.io.File"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"
	prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<jsp:include
	page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.mpmresource.gz.gzcard.SetStateBuilder')}"
	flush="true" />
	
<%@include file="/netmarkets/jsp/util/end.jspf"%>