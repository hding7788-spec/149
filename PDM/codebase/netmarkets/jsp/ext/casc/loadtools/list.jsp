<%@ include file="/netmarkets/jsp/componentCatalog/beginComponentPage.jspf"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>

<div class="customizationPage">
<h1>LoadTools</h1>
<table>
    <tr>
        <td class="title"><a href="#" onClick="PTC.carambolaTools.reloadErpDate(); return false;">Reload Erp Datas</a></td>
        <td>重新加载ERP数据</td>
    </tr>
</table>
</div>

<script type="text/javascript">
    PTC.carambolaTools = {};
    PTC.carambolaTools.reloadErpDate = function() {
        var url = "netmarkets/jsp/ext/casc/loadtools/ReloadErpData.jsp";
        new ajax.ContentLoader(url, PTC.carambolaTools.openThisPage, null, 'GET', null);
        alert("Reload ErpData Success.")
        return false;
    PTC.carambolaTools.openThisPage = function() {
    	new ajax.ContentLoader('netmarkets/jsp/ext/casc/loadtools/list.jsp', null, null, 'GET', null);
    	  }
    }
</script>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>