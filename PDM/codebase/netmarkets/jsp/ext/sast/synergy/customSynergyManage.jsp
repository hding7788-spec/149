<%@ page import="wt.org.WTUser" %>
<%@ page import="wt.session.SessionHelper" %>
<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%
    WTUser currentUser = (WTUser) SessionHelper.getPrincipal();

    String userName = currentUser.getName();
    System.out.println("userName = "+userName);
%>
<style>
    .box:hover{
        background-color: rgba(253, 205, 146, 0.47);
        cursor:pointer;
    }
</style>

<ul>
    <li id="searchdata"  class="box" onclick="setSelectStyle(this)">
        <a id="searchdata_href" href="netmarkets/jsp/ext/ases/dataSearch/searchdata.jsp">数据发放管理</a>
    </li>
    <li id="collaborativeSiteManagement"  class="box" onclick="setSelectStyle(this)">
        <a id="collaborativeSiteManagement_href" href="netmarkets/jsp/ext/sast/center/collaborativeSiteManagement.jsp">协同站点管理</a>
    </li>
    <li id="syncProductMapping"  class="box" onclick="setSelectStyle(this)">
        <a id="syncProductMapping_href" href="netmarkets/jsp/ext/sast/center/productModel/syncProductMapping.jsp">协同型号映射</a>
    </li>
    <li id="syncModelTypeMapping"  class="box" onclick="setSelectStyle(this)">
        <a id="syncModelTypeMapping_href" href="netmarkets/jsp/ext/sast/center/productModel/syncModelTypeMapping.jsp">协同类型映射
        </a>
    </li>
    <li id="centerWorkflowInfo"  class="box" onclick="setSelectStyle(this)">
        <a id="centerWorkflowInfo_href" href="netmarkets/jsp/ext/sast/center/workflow/centerWorkflowInfoSearch.jsp">协同流程查看</a>
    </li>
    <li id="searchSynergyRecord"  class="box" onclick="setSelectStyle(this)">
        <a id="searchSynergyRecord_href" href="netmarkets/jsp/ext/sast/synergy/searchSynergyRecord.jsp">跨域协同记录管理</a>
    </li>
</ul>
<script>
    function setSelectStyle(obj) {
        document.getElementById(obj.id+"_href").click();
    }
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>