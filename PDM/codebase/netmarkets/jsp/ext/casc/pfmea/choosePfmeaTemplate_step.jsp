<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<input type="hidden" name="templateId" id="templateId" onclick="" />
<input type="hidden" name="templateName" id="templateName" onclick="" />
<script type="text/javascript">
    function preSubmit() {
        debugger;
        var table = PTC.jca.table.Utils.getTable('ext.casc.integrate.pfmea.mvc.builder.ChoosePfmeaTemplateTableBuilder');
        var allSelection = table.getSelectionModel().getSelections();
        var row = allSelection[0];
        var templateName = row.data.templateName;
        var templateId = row.data.templateId;
        document.getElementById("templateId").value = templateId;
        document.getElementById("templateName").value = templateName;
        return true;
    }


</script>
<jsp:include page="${mvc:getComponentURL('ext.casc.integrate.pfmea.mvc.builder.ChoosePfmeaTemplateTableBuilder')}" />

<%@include file="/netmarkets/jsp/util/end.jspf" %>
