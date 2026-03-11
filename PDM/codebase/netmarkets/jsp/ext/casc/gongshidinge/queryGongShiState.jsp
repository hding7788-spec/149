<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="ext.casc.sop.constants.SopConstants" %>
<%@page import="com.ptc.netmarkets.model.NmOid" %>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<jca:tabToHighlight actionName="propertyPanel" objectType="object"/>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb" %>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>

<%@ page import="ext.casc.process.ProcessConstants" %>
<%
    NmCommandBean cb = new NmCommandBean();
    cb.setRequest(request);
    NmOid nmOid = cb.getPrimaryOid();
%>
<script language="javascript" src="netmarkets/javascript/util/calendar.js"></script>

<script language="javascript">

    function preSubmit() {
        setworktype("search");
		PTC.jca.table.Utils.reload('gongshi_querystate', {}, true);
    }

	function setworktype(worktype) {
		var ele = document.getElementById("work");
		ele.value = worktype;
	}
</script>
<input type="hidden" id="work" name="work" value=""/>
<div style="color: #f00; font-weight: bold; margin-bottom: 10px;">提示：已完成的工时定额不在查询范围之内</div>
<table>
    <tr>
        <td>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </td>
    </tr>
    <tr>
        <td><h4>工艺文件流水号：</h4></td>
        <td><input id="technicsNumber" name="technicsNumber" type="text" style="width:200px"></td>
    </tr>
    <tr>
        <td><h4>工艺文件名称：</h4></td>
        <td><input id="technicsName" name="technicsName" type="text" style="width:200px"></td>
    </tr>
    <tr>
        <td>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </td>
    </tr>
    <tr>
        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
        <td align=center><h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" value="<%=ProcessConstants.JSP_SEARCH_SEARCHBUTTON%>" onclick="preSubmit();"/></h4>
        </td>

        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input
                type="reset" value="<%=ProcessConstants.JSP_SEARCH_RESET%>"/></td>
    </tr>
</table>
<jsp:include page="${mvc:getComponentURL('gongshi_querystate')}"/>
<%@ include file="/netmarkets/jsp/util/end.jspf" %>