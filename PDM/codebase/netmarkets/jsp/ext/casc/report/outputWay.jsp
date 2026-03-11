<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%
	String actionName = request.getParameter("actionName");
	System.out.println(actionName);
%>

	<SCRIPT LANGUAGE="JavaScript">
		/* window.onload = function(){
			var buttom = document.getElementById("ext-gen36");
			buttom.onclick = function(){
				window.close();
			}
		} */

		/* document.getElementById("ext-gen36").onclick=function(){
			window.close();
		} */

	</SCRIPT>
	<jca:wizard title="选择批次号">
		<jca:wizardStep action="reportWizardStep" type="customReport" label="选择批次号"/>
	</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>