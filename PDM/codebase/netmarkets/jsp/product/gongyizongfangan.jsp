<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<%@ page import="com.ptc.windchill.pdmlink.product.server.processors.productResource" %>
<%@ page import="com.ptc.windchill.enterprise.history.historyResource" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<jsp:include page="${mvc:getComponentURL('ext.casc.product.mvc.builder.GongyizongfanganBuilder')}" />
<%@ include file="/netmarkets/jsp/util/end.jspf"%>