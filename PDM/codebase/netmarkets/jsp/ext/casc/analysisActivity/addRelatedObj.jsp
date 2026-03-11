<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%response.setContentType("text/html; charset=UTF-8");%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<%@ page import="ext.casc.ui.actionsRB,
                 com.ptc.windchill.enterprise.util.PartManagementHelper,
                 com.ptc.netmarkets.util.beans.NmCommandBean "%>
<%@ page import="wt.part.WTPart" %>
<%@ page import="ext.casc.analysisActivity.helper.AnalysisConstant" %>
<%@ page import="wt.change2.WTAnalysisActivity" %>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>

<fmt:setBundle basename="ext.casc.ui.actionsRB" />
<fmt:message var="pbom" key="<%= actionsRB.ANALYSISACTIVITY_ADDRELATEDPBOM_TITLE%>" />
<fmt:message var="technics" key="<%= actionsRB.ANALYSISACTIVITY_ADDRELATEDTECHNICS_TITLE%>" />
<fmt:message var="changeOrder" key="<%= actionsRB.ANALYSISACTIVITY_ADDRELATEDCHANGEORDER_TITLE%>" />
<fmt:message var="tempTechnics" key="<%= actionsRB.ANALYSISACTIVITY_RELATEDTECHNICS_TITLE%>" />
<fmt:message var="analysis" key="<%= actionsRB.ANALYSISACTIVITY_REPLACEANALYSIS_TITLE%>" />


<%
    NmCommandBean cb = new NmCommandBean();
    cb.setCompContext(nmcontext.getContext().toString());
    cb.setRequest(request);

    String containerReferenceString =  PartManagementHelper.getContainersToSearch(cb.getContainerRef());

    String objType = request.getParameter("objType");
    String tableHeader = "";
    boolean multiSelect = true;
    if(AnalysisConstant.TYPE_PBOM.equals(objType)){
        tableHeader = (String) pageContext.findAttribute("pbom");
        objType = WTPart.class.getName();
    } else if(AnalysisConstant.TYPE_TECHNICS.equals(objType)) {
        tableHeader = (String) pageContext.findAttribute("technics");
        objType = "wt.doc.WTDocument";
    } else if("change".equals(objType)) {
        tableHeader = (String) pageContext.findAttribute("changeOrder");
        objType = "WCTYPE|wt.change2.WTChangeOrder2|casc.sast.149.PROCESS_ECN";
    } else if("docChange".equals(objType)) {
        tableHeader = (String) pageContext.findAttribute("changeOrder");
        objType = "WCTYPE|wt.change2.WTChangeOrder2|casc.sast.149.DOCUMENT_ECN";
    } else if("tempTechnics".equals(objType)) {
        tableHeader = (String) pageContext.findAttribute("tempTechnics");
        objType = "WCTYPE|wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN";
    } else if("analysis".equals(objType)) {
        tableHeader = (String) pageContext.findAttribute("analysis");
        objType = WTAnalysisActivity.class.getName();
        multiSelect = false;
    } else {
        objType = WTPart.class.getName();
    }

    request.setAttribute("objectType",objType);
	request.setAttribute("tableLabel",tableHeader);
	request.setAttribute("multiSelect",multiSelect);

%>
<span style="font-size: larger; color: red; font-weight: bold;">根据工艺文件编号查询工艺文件时需要通过名称模糊搜索！ 提示：模糊搜索需要以*开头结尾！</span>
<wctags:genericPicker id="addRelatedObj" inline="true" pickerCallback="doNothing"
                      pickerTitle="${tableLabel}" multiSelect="${multiSelect}"
                      componentId="RelatedObjectAddAssociation"
                      containerRef="<%=containerReferenceString%>"
                      defaultVersionValue="LATEST"
                      baseWhereClause="(latestIteration='1')"
                      objectType="${objectType}" />
<%@ include file="/netmarkets/jsp/util/end.jspf"%>