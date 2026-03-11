<%@page import="java.util.Map"%>
<%@page import="ext.casc.fileprint.FilePrintUtil2"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="java.util.List"%>
<%@page pageEncoding="GBK" %>
<%@page import="java.util.ArrayList"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core"   prefix="c" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"	prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@include file="/netmarkets/jsp/util/begin.jspf"%>

<jsp:include page="${mvc:getComponentURL('ext.casc.consCheckRecords.ProConfigurationBuilder')}" flush="true"></jsp:include>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>