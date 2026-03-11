<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean" %>
<%@ page import="com.ptc.netmarkets.model.NmOid" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<%@taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%
    NmCommandBean cb = new NmCommandBean();
    /* cb.setCompContext(NmContext.getContext().toString()); */
    cb.setRequest(request);
    NmOid nmOid = cb.getPrimaryOid();


%>
<LINK REL=stylesheet HREF="netmarkets/css/windchill-base.css" TYPE="text/css">
<LINK REL=stylesheet id="theme0" HREF="netmarkets/themes/windchill/xtheme-windchill.css" TYPE="text/css">

<div id="serachCondation">
    <fieldset class=" x-fieldset x-form-label-left" style="width: auto;">
        <input type="hidden" id="work" name="work" value=""/>
        <legend class="x-fieldset-header x-unselectable" style="-moz-user-select: none;"><span
                class="attributePanel-fieldset-title">筛选条件</span></legend>
        <table>
            <tr>
                <td colspan="1" align="left"><w:label value="工艺文件编号"/></td>
                <td colspan="1" align="left"><w:textBox name="number" id="number" size="30" maxlength="100" value=""/>
                </td>
                <td>&nbsp;&nbsp;</td>
                <td colspan="1" align="left"><w:label value="工艺文件名称"/></td>
                <td colspan="1" align="left"><w:textBox name="name" id="name" size="30" maxlength="100" value=""/>
                </td>
            </tr>
            <tr>
                <td align="left" colspan="4">
                    <input type="button" name="search" value="搜索工艺文件" class="x-btn-text" onclick="doSearch();"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <input type="Reset" name="Reset" value="重置搜索条件" class="x-btn-text" onclick="clearPickerData();"/>&nbsp;&nbsp;&nbsp;&nbsp;
                </td>
            </tr>
        </table>

    </fieldset>
</div>
<div id="planList">
    <jsp:include page="${mvc:getComponentURL('ext.casc.doc.mvc.builder.SearchProcessPlanBuilder')}"/>
</div>

<input type="Reset" name="Reset" value="添加关联工艺文件" class="x-btn-text" onclick="doAddDoc();"/>&nbsp;&nbsp;

<script type="text/javascript">
    function clearPickerData() {
        document.getElementById("number").value = "";
        document.getElementById("name").value = "";
    }

    function doSearch() {
        if(document.getElementById("number").value == "" && document.getElementById("name").value == ""){
            alert("工艺编号和名称至少一个不为空！");
            return;
        }
        setworktype("search");
        PTC.jca.table.Utils.reload('ext.casc.doc.mvc.builder.SearchProcessPlanBuilder', {}, true);
    }

    function setworktype(worktype){
        var ele=document.getElementById("work");
        ele.value=worktype;
    }

    function doAddDoc() {
        var table = PTC.jca.table.Utils.getTable('table__ext.casc.doc.mvc.builder.SearchProcessPlanBuilder');
        var allSelection = table.getSelectionModel().getSelections();
        var str = '';
        var ids = '';
        var oldIds = window.opener.document.getElementById('PROCESSDOCNUM').value;
        for (var i = 0; i < allSelection.length; i++) {
            var row = allSelection[i];
            debugger;
            var name = row.data.name;
            var docid = row.data.oid;
            if(oldIds.indexOf(docid)>-1){
                continue;
            }
            str += name + ";";
            ids += docid + ";";
        }
        window.opener.document.getElementById('PROCESSDOCNAME').value += str;
        window.opener.document.getElementById('PROCESSDOCNUM').value += ids;
        window.close();
    }

</script>

<%@ include file="/netmarkets/jsp/util/end.jspf" %>