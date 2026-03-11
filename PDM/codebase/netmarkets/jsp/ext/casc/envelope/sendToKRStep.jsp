
<%@page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />
<%-- <%@ include file="/netmarkets/jsp/util/begin.jspf"%> --%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>


<table style="positin:absolute;left:50%">
<br>
<br>
<br>
<br>
<br>
	<tr >
		<td><h4><%=ext.sast.center.synch.MQConstants.FAWANGDANWEI_LABEL%></h4></td>
		<td>
		<select style="width:200px"  id="<%=ext.sast.center.synch.MQConstants.FAWANGDANWEI%>" name="<%=ext.sast.center.synch.MQConstants.FAWANGDANWEI%>">
			<option value="<%=ext.sast.center.synch.MQConstants.FAWANGDANWEI_VALUE1%>"><%=ext.sast.center.synch.MQConstants.FAWANGDANWEI_VALUE1%></option>
			<option value="<%=ext.sast.center.synch.MQConstants.FAWANGDANWEI_VALUE2%>"><%=ext.sast.center.synch.MQConstants.FAWANGDANWEI_VALUE2%></option>
		</select>
		</td>
	</tr>
	</table>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>