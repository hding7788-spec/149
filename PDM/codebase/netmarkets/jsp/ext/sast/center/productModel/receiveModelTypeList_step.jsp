<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/fmt"  prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<div>
	<jsp:include page="${mvc:getComponentURL('ext.sast.center.productModel.mvc.builder.ReceiveModelTypeListBuilder')}" />
</div>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>