<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://www.ptc.com/windchill/taglib/fmt" %>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components" %>

<%@ page import="com.ptc.windchill.enterprise.doc.documentResource" %>
<%@ page import="com.ptc.windchill.enterprise.attachments.attachmentsResource" %>
<%@ page import="com.ptc.windchill.enterprise.object.dataUtilities.dataUtilitiesResource" %>
<%@ page import="com.ptc.windchill.enterprise.baseline.baselineResource" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf" %>

<%-- Get resource bundles and localized labels. --%>
<fmt:setLocale value="${localeBean.locale}" />
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />
<fmt:message var="enabledVersion" key="<%= documentResource.ENABLED_VERSION %>" />
<fmt:message var="template"       key="<%= documentResource.TEMPLATE %>" />
<fmt:message var="locationLabel"  key="<%= documentResource.LOCATION %>" />

<fmt:setBundle basename="com.ptc.windchill.enterprise.attachments.attachmentsResource" />
<fmt:message var="primaryLabel" key="<%= attachmentsResource.PRIMARY_CONTENT %>" />
<fmt:message var="shortcut"     key="<%= attachmentsResource.ITERATION_LATEST_LABEL %>" />
<fmt:message var="format"       key="<%= attachmentsResource.FORMAT %>" />

<fmt:setBundle basename="com.ptc.windchill.enterprise.object.dataUtilities.dataUtilitiesResource"/>
<fmt:message var="pdmCheckedOutVersion" key="<%= dataUtilitiesResource.PDM_CHECKED_OUT_VERSION %>" />
<fmt:message var="pdmCheckedOutStatus" key="<%= dataUtilitiesResource.PDM_CHECKED_OUT_STATUS_TABLE_HEADER %>" />
<fmt:message var="modifiedBy_Label" key="<%= dataUtilitiesResource.MODIFIEDBY_LABEL %>" />
<fmt:message var="resultingPDMVersion" key="<%= dataUtilitiesResource.RESULTING_PDM_VERSION %>" />
	
<fmt:setBundle basename="com.ptc.windchill.enterprise.baseline.baselineResource" />
<fmt:message var="primaryLabel" key="<%= baselineResource.BASELINE_PRIMARY_LABEL %>" />

<%-- Get the composite path to the object for use by setComponentProperty, below. --%>
<%@ include file="/netmarkets/jsp/folder/getCompositePath.jspf" %>
<%-- Define the contents of the document property panel. --%>
<jca:describePropertyPanel var="propertyPanelDescriptor">
  <%-- <jca:describeProperty id="name" />--%>
   <jca:describeProperty id="number" />
   <jca:describeProperty id="topObject" dataUtilityId="topObject" label="${primaryLabel}"/>
   <jca:describeProperty id="lifeCycleState" />
   	<jca:describeProperty id="creatorFullName" />
   <jca:describeProperty id="pdmCheckoutStatus" need="containerName" isInfoPageLink="true" label="${pdmCheckedOutStatus}"/>
   <jca:describeProperty id="pdmCheckoutVersion" need="version" label="${pdmCheckedOutVersion}"/>
   <jca:describeProperty id="resultingPDMVersion" need="version" label="${resultingPDMVersion}"/>
   <jca:describeProperty id="compositePath" label="${locationLabel}" >
      <jca:setComponentProperty key="COMPOSITE_PATH"  value="${compositePath}"/>
   </jca:describeProperty>
   <jca:describeProperty id="templated" label="${template}" need="template.templated"/>
   <jca:describeProperty id="enabledVersion" label="${enabledVersion}"/>
</jca:describePropertyPanel>

<c:choose>
   <c:when test='${param.miniInfoPage == "true"}'>
      <c:set var="nav_bar_name" value="third_level_nav_dti" />
   </c:when>
   <c:otherwise>
      <c:set var="nav_bar_name" value="third_level_nav_processEnvelope" />
   </c:otherwise>
</c:choose>

<jca:describeInfoPage showVisualization="false" navBarName="${nav_bar_name}" 
                      propertyPanel="${propertyPanelDescriptor}" helpContext="baseline.infoHelp">
   <jca:describeStatusGlyph id="statusFamily_Share" />
   <jca:describeStatusGlyph id="statusFamily_General" />
   <jca:describeStatusGlyph id="statusFamily_Security" />
</jca:describeInfoPage>


<%@ include file="/netmarkets/jsp/components/infoPage.jspf" %>
<%@ include file="/netmarkets/jsp/util/end.jspf" %>