<%@page import="wt.util.WTProperties"%>
<%@page import="java.util.List"%>
<%@page pageEncoding="GBK" %>
<%@page import="java.util.ArrayList"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core"   prefix="c" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"	prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<style type="text/css">
button{
	width: 34px;
	height: 19px;

	text-align: left;

	padding-left:2px;
	font-size: 10px;
	font-style: normal;
	font-weight: normal;
	color: #464646;


	display: inline-block;
}
</style>

<script language="javascript" type="text/javascript">
  function openTree(evnt,url)
  {
	  var properties = "width=200,height=400,fullscreen=0,scrollbars=yes,titlebar=0, status=0,resizable=yes,location=0";
	  properties += ",channelmode=0,directories=0, menubar=0,location=0,left="+(evnt.screenX + 20)+",top="+ (evnt.screenY + 1);
//	  var url="common/treeWin.jsp";
      if (isWf())
      {
         myTreeWindow=wfWindowOpen(url,'tree',properties);
      }
      else {
         myTreeWindow=wfWindowOpen(url,'tree',properties);
         myTreeWindow.location.href = url;
         if (myTreeWindow.opener == null) myTreeWindow.opener = self;
      }
  }


</script>
<%

String basePath = WTProperties.getLocalProperties().getProperty(
		"wt.server.codebase", null);
%>


<div id="report-3" class="wizardPanel-body wizardPanel-body-noheader">
<div id="report-4" class=" x-panel stepHeader x-panel-noborder">
<div id="report-5" class="stepPanel">
<div id="report-6" class="x-header-strip-wrap" style="left: 0px;">

</div>
</div>
</div>
</div>

<jsp:include page="${mvc:getComponentURL('ext.casc.integrate.mes.mvc.builders.PbomDesignFilesBuilder')}" flush="true"></jsp:include>
<%--<%} %> --%>
</div>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>