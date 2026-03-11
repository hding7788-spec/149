<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<%@taglib uri="http://www.ptc.com/windchill/taglib/baseline" prefix="baseline" %>

<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>

<%@include file="/netmarkets/jsp/util/begin.jspf"%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.ases.envelope.envelopeResource" />
<fmt:message var="tableLabel" key="ENVELOPE_CONTENTS_TEXT" />
<fmt:message var="nameLabel" key="ENVELOPE_NAME_LABEL" />
<fmt:message var="numberLabel" key="ENVELOPE_NUMBER_LABEL" />
<fmt:message var="updateLabel" key="ENVELOPE_UPDATESTAMP_LABEL" />
<fmt:message var="stateLabel" key="ENVELOPE_STATE_LABEL" />
<fmt:message var="versionLabel" key="ENVELOPE_VERSION_LABEL" />
<c:set var="tableId" value="envelopeMembersTable"/>
<%-->Build a descriptor and assign it to page variable treeDescriptor<--%>
   <jca:describeTableTree  id="envelopeMembersTable2"
                           var="treeDescriptor"
                           type="wt.enterprise.RevisionControlled"
                           label="${tableLabel}"
                           nodeColumn="number"
                           configurable="true"
                           expansion="full"
                           helpContext="DEFAULT_HELP_PAGE"
                           disableAction="false"
                           summary="test">

			    <jca:setComponentProperty key="selectable"     value="true"/>
			    <jca:setComponentProperty key="actionModel"    value="Envelope_tree_toolbar"/>
			    <jca:describeColumn id="type_icon"/>
			    <jca:describeColumn id="number" />
			    <jca:describeColumn id="name" />
				  <jca:describeColumn id="version" />
				  <jca:describeColumn id="containerName"/>
				  <jca:describeColumn id="state" label="${stateLabel}" need="lifeCycleState"/>
		 </jca:describeTableTree>

 <%-->Get a component model for our tree<--%>
<jca:getModel var="treeModel"
                 descriptor="${treeDescriptor}"
                 treeHandler="envelopeTreeHandler"/>

<%-->Render the table<--%>
<jca:renderTableTree model="${treeModel}" pageLimit="0" showCount="true" showPagingLinks="true" helpContext="baseline.objTableHelp"/>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>