<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@page language="java" pageEncoding="UTF-8"
        contentType="text/html; charset=UTF-8"%>
<font color="red">需要选择工艺，只有勾选的工艺才会批量创建变更单！</font>
<jsp:include page="${mvc:getComponentURL('ext.casc.common.mvc.builder.CollectProcessDocBuilder')}"  flush="true"/>
