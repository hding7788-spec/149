<%@ page import="ext.casc.processPlan.Constants" %>
<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean" %>
<%@ page import="com.ptc.netmarkets.model.NmOid" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<%@taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags" %>
<%@include file="/netmarkets/jsp/util/begin.jspf" %>
<%--<%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %>--%>
<fmt:setLocale value="${localeBean.locale}"/>

<LINK REL=stylesheet HREF="netmarkets/css/windchill-base.css" TYPE="text/css">
<LINK REL=stylesheet HREF="netmarkets/javascript/ext/resources/css/ext-sandbox.css" TYPE="text/css">
<LINK REL=stylesheet id="theme0" HREF="netmarkets/themes/windchill/xtheme-windchill.css" TYPE="text/css">

<%
    NmCommandBean cb = new NmCommandBean();
    cb.setRequest(request);
    NmOid nmOid = cb.getPrimaryOid();

	String partNumber = request.getParameter("partNumber");
	partNumber = java.net.URLDecoder.decode(partNumber,"UTF-8");


    request.setAttribute("dept", Constants.DEPTS);
    request.setAttribute("productType", Constants.PRODUCT_TYPE);
    request.setAttribute("speciality", Constants.SPECIALITYS);

%>

<div id="serachCondation">
    <fieldset class=" x-fieldset x-form-label-left" style="width: auto;">
        <%--        <input type="hidden" name="planNumber" id="planNumber" value=""/>--%>
        <input type="hidden" id="work" name="work" value=""/>
        <legend class="x-fieldset-header x-unselectable" style="-moz-user-select: none;"><span
                class="attributePanel-fieldset-title">筛选条件</span></legend>
        <table>
            <tr>
                <td colspan="1" style="text-align: left;"><w:label value="编号"/></td>
                <td colspan="1" style="text-align: left;"><w:textBox name="number" id="number" size="30" maxlength="100"
                                                                     value=""/>

                </td>
                <td colspan="1" style="text-align: left;"><wctags:userPicker id="creator" label="创建者"
                                                                             readOnlyPickerTextBox="false"
                                                                             editable="true"
                                                                             showSuggestion="true" suggestMinChars="1"/>
                </td>
            </tr>
            <tr>
                <td colspan="1" style="text-align: left;"><w:label value="名称"/></td>
                <td colspan="1" style="text-align: left;">
                    <w:textBox name="name" id="name" size="30" maxlength="100" value=""/>
                <td colspan="1" style="text-align: left;"><w:label value="部门"/></td>
                <td colspan="1" style="text-align: left;"><w:comboBox propertyLabel="部门" id="dept" name="dept"
                                                                      internalValues="${dept}"
                                                                      displayValues="${dept}"
                                                                      onchange=""/>
                </td>

                <td colspan="2" style="text-align: left;"><w:label value="创建时间启自:"/></td>
                <td colspan="1" style="text-align: left;"><w:dateInputComponent propertyLabel="创建时间"
                                                                                id="createTime_FROM"
                                                                                name="createTime_FROM" required="false"
                                                                                dateValueType="DATE_ONLY"/>
                </td>
            </tr>
            <tr>
                <td colspan="1" style="text-align: left;"><w:label value="专业"/></td>
                <td colspan="1" style="text-align: left;"><w:comboBox propertyLabel="专业" id="speciality"
                                                                      name="speciality"
                                                                      internalValues="${speciality}"
                                                                      displayValues="${speciality}"
                                                                      onchange=""/>

                <td colspan="1" style="text-align: left;"><w:label value="产品类型"/></td>
                <td colspan="1" style="text-align: left;"><w:comboBox propertyLabel="产品类型" id="productType"
                                                                      name="productType"
                                                                      internalValues="${productType}"
                                                                      displayValues="${productType}"
                                                                      onchange=""/>

                </td>
                <td colspan="2" style="text-align: left;"><w:label value="创建时间终于:"/></td>
                <td colspan="1" style="text-align: left;"><w:dateInputComponent propertyLabel="创建时间"
                                                                                id="createTime_To"
                                                                                name="createTime_To" required="false"
                                                                                dateValueType="DATE_ONLY"/>
                </td>
            </tr>
            <tr>

            </tr>
            <tr>
                <td align="left" colspan="4">
                    <input type="button" name="search" value="搜索" class="x-btn-text" onclick="doSearch();"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <input type="Reset" name="Reset" value="重置" class="x-btn-text" onclick="clearPickerData();"/>&nbsp;&nbsp;&nbsp;&nbsp;
                </td>
            </tr>
        </table>

    </fieldset>
</div>
<div id="planList">
    <jsp:include page="${mvc:getComponentURL('ext.casc.processPlan.mvc.builder.ProcessTemplateBuilder')}"/>
</div>

<input type="button" name="add" value="添加模板" class="x-btn-text" onclick="doAddDoc();"/>&nbsp;&nbsp;

<script type="text/javascript">
    function clearDate() {
        document.getElementById("createTime_FROM").value = "";
        document.getElementById("createTime_To").value = "";
    }
    clearDate();
    function clearPickerData() {
        document.getElementById("number").value = "";
        document.getElementById("name").value = "";

    }

    function doSearch() {
        debugger;
        setworktype("search");
        PTC.jca.table.Utils.reload('ext.casc.processPlan.mvc.builder.ProcessTemplateBuilder', {
            work: document.getElementById("work").value,
            number: document.getElementById("number").value,
            name: document.getElementById("name").value,
            dept: document.getElementById("dept").value,
            speciality: document.getElementById("speciality").value,
            creator: document.getElementById("creator").value,

            productType: document.getElementById("productType").value,
            createTime_FROM: document.getElementById("createTime_FROM").value,
            createTime_To: document.getElementById("createTime_To").value
        }, true);
    }

    function setworktype(worktype) {
        var ele = document.getElementById("work");
        ele.value = worktype;

    }

    function doAddDoc() {
        debugger;
        var table = PTC.jca.table.Utils.getTable('table__ext.casc.processPlan.mvc.builder.ProcessTemplateBuilder');
        var allSelection = table.getSelectionModel().getSelections();
        if (allSelection.length == 0) {
            alert("请选择模板");
            return;
        }
        var grid = window.opener.Ext.getCmp("ext.casc.processPlan.mvc.builder.BatchCreateMPMProcessPlanBuilder");
        var selModel = grid.getSelectionModel();
        var selectedRows = selModel.getSelections();
        debugger;
        if (selectedRows.length > 0) {
        	 for(var i=0;i<selectedRows.length;i++){
                 var partNumber = selectedRows[i].data.number;
                 window.opener.document.getElementById("processTemplate_"+ partNumber).value = allSelection[0].data.name;
                 window.opener.document.getElementById("processTemplate_"+ partNumber+"_oid").value = allSelection[0].data.oid;
             }
        }else{
        	window.opener.document.getElementById("processTemplate_<%=partNumber%>").value = allSelection[0].data.name;
            window.opener.document.getElementById("processTemplate_<%=partNumber%>_oid").value = allSelection[0].data.oid;

        }



        window.close();
    }

</script>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>