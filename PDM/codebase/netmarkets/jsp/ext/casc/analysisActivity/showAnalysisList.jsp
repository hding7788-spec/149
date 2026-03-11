<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="wt.fc.ReferenceFactory" %>
<%@ page import="wt.workflow.work.WorkItem" %>
<%@ page import="wt.workflow.engine.WfActivity" %>
<%@ page import="wt.change2.WTAnalysisActivity" %>
<%@ page import="ext.casc.analysisActivity.helper.AnalysisConstant" %>
<input type="hidden" name="completeReviewhidden" id="completeReviewhidden" onclick=""/>
<%
    String type_pbom = AnalysisConstant.TYPE_PBOM;
    String type_technics = AnalysisConstant.TYPE_TECHNICS;
    String type_product = AnalysisConstant.PRODUCT;

    String export = "导出";

    String workflowProcessOid = request.getParameter("oid");
    String contextPath = request.getContextPath();
    ReferenceFactory rf = new ReferenceFactory();
    WorkItem wi = (WorkItem) rf.getReference(workflowProcessOid).getObject();
    WfActivity activity = (WfActivity) wi.getSource().getObject();
    WTAnalysisActivity pbo = (WTAnalysisActivity) activity.getContext().getValue("primaryBusinessObject");
    String analysisActivityOid = "OR%3A" + WTAnalysisActivity.class.getName() + "%3A" + pbo.getPersistInfo().getObjectIdentifier().getId();
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>

<head>
    <style>
        .x-form-textarea:focus {
            background-color: white; /* 将背景色设置为白色，替换掉黄色背景 */
        }
    </style>
</head>

<script>
    var workflowProcessOid = '<%=workflowProcessOid%>';
    var analysisActivityOid = '<%=analysisActivityOid%>';
    var type_pbom = '<%=type_pbom%>';
    var type_technics = '<%=type_technics%>';
    var type_product = '<%=type_product%>';
</script>

<div id="ShowRelatedPbomBuilderGrid"></div>
<br>
<div id="ShowRelatedTechnicsBuilderGrid"></div>
<br>
<div id="ShowRelatedProductsBuilderGrid"></div>
<br>

<script>
    var onReadyLoad = 3;

    var xmlHttpRequest;
    function createXMLHttpRequest(){
        if (window.XMLHttpRequest) { // Mozilla, Safari,...
            var xmlHttpRequest = new XMLHttpRequest();
            if (xmlHttpRequest.overrideMimeType) {
                xmlHttpRequest.overrideMimeType('text/xml');
            }
            return xmlHttpRequest;
        } else if (window.ActiveXObject) { // IE
            try {
                var xmlHttpRequest = new ActiveXObject("Msxml2.XMLHTTP");
                return xmlHttpRequest;
            } catch (e) {
                try {
                    var xmlHttpRequest = new ActiveXObject("Microsoft.XMLHTTP");
                    return xmlHttpRequest;
                } catch (e) {
                    alert("不支持AJAX!");
                    return;
                }
            }
        }
    }

    function renderNumber(value) {
        if(value != null && value.length > 0){
            var splits = value.split("@");
            var num = splits[0];
            var oid = splits[1];
            var url = " app/#ptc1/tcomp/infoPage?oid=" + oid
            value = "<a href=\"" + url + "\"  target=_blank>" + num + "</a>";
        }
        return value;
    }

    /*受影响PBOM列表*/
    var cmpbom = new Ext.grid.ColumnModel([
        {header: '', dataIndex: 'icontype', width: 20},
        {header: '编号', dataIndex: 'number', autoWidth: true, renderer: renderNumber, sortable: true},
        {header: '名称', dataIndex: 'name', width: 160, sortable: true},
        {header: '版本', dataIndex: 'version', width: 80, sortable: true},
        {header: '状态', dataIndex: 'state', width: 60, sortable: true},
        {header: '修改者', dataIndex: 'modifier', width: 60, sortable: true},
        {header: '修改时间', dataIndex: 'modifyTime', width: 80, sortable: true},
        {header: '主任师意见', dataIndex: 'affected', width: 80},
        {header: '工艺员意见', dataIndex: 'affectedGyy', width: 80},
        {header: '责任人', dataIndex: 'responser', width: 160},
        {header: '更改要求', dataIndex: 'requirement', width: 300},
        {header: '要求完成时间', dataIndex: 'completeTime', width: 100},
        {header: '完成情况', dataIndex: 'dealStatus', autoWidth: true}
    ]);

    var recordspbom = new Ext.data.Record.create([{name: 'id'}, {name: 'icontype'}, {name: 'number'}, {name: 'code'}, {name: 'name'}, {name: 'version'}, {name: 'state'}, {name: 'modifier'}, {name: 'modifyTime'}, {name: 'affected'}, {name: 'affectedGyy'}, {name: 'responser'}, {name: 'requirement'}, {name: 'completeTime'}, {name: 'dealStatus'}]);
    var storepbom = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy(
            {
                url: "<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/generateRelatedTable.jsp?oid=<%=workflowProcessOid%>&type=" + type_pbom,//获取数据的后台地址
                method: "POST",
                timeout: 9000000
            }),
        //解析json
        reader: new Ext.data.JsonReader(
            {
                root: "data",
                id: "id",
                totalProperty: "totalCount"//总的数据条数
            }, recordspbom)
    });

    storepbom.load();
    storepbom.on('load', function () {
        onReadyLoad--;
    });

    var ShowRelatedPbomBuilderGrid = new Ext.grid.GridPanel({
        height: 300,
        autoScroll: true,
        draggable: true,
        id: 'show_related_pbom',
        renderTo: 'ShowRelatedPbomBuilderGrid',
        store: storepbom,
        title: "受影响的PBOM",
        cm: cmpbom,
        loadMask: true,
        tbar: [
            {
                text: '<%=export%>',
                handler: function () {
                    exportData(type_pbom);
                },
                cls: "x-btn-text-icon",
                icon: "<%=contextPath%>/netmarkets/images/export_to_excel.png",
                scope: this
            }
        ]
    });
    ShowRelatedPbomBuilderGrid.loadMask.show();


    /*受影响工艺文件列表*/
    var cmtechnics = new Ext.grid.ColumnModel([
        {header: '', dataIndex: 'icontype', width: 20},
        {header: '编号', dataIndex: 'number', autoWidth: true, renderer: renderNumber, sortable: true},
        {header: '名称', dataIndex: 'name', width: 160, sortable: true},
        {header: '版本', dataIndex: 'version', width: 80, sortable: true},
        {header: '状态', dataIndex: 'state', width: 60, sortable: true},
        {header: '修改者', dataIndex: 'modifier', width: 60, sortable: true},
        {header: '修改时间', dataIndex: 'modifyTime', width: 80, sortable: true},
        {header: '编制部门', dataIndex: 'dept', width: 60, sortable: true},
        {header: '主任师意见', dataIndex: 'affected', width: 80},
        {header: '工艺员意见', dataIndex: 'affectedGyy', width: 80},
        {header: '工艺员备注', dataIndex: 'remarkGyy', width: 80},
        {header: '责任人', dataIndex: 'responser', width: 160},
        {header: '更改要求', dataIndex: 'requirement', width: 300},
        {header: '要求完成时间', dataIndex: 'completeTime', width: 100},
        {header: '关联更改单/工艺', dataIndex: 'relatedOrder', width: 120},
        {header: '完成情况', dataIndex: 'dealStatus', autoWidth: true}
    ]);

    var recordstechnics = new Ext.data.Record.create([{name: 'id'}, {name: 'icontype'}, {name: 'number'}, {name: 'code'}, {name: 'name'}, {name: 'version'}, {name: 'state'}, {name: 'modifier'}, {name: 'modifyTime'}, {name: 'dept'}, {name: 'affected'}, {name: 'affectedGyy'}, {name: 'remarkGyy'}, {name: 'responser'}, {name: 'requirement'}, {name: 'completeTime'}, {name: 'relatedOrder'}, {name: 'dealStatus'}]);
    var storetechnics = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy(
            {
                url: "<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/generateRelatedTable.jsp?oid=<%=workflowProcessOid%>&type=" + type_technics,//获取数据的后台地址
                method: "POST",
                timeout: 9000000
            }),
        //解析json
        reader: new Ext.data.JsonReader(
            {
                root: "data",
                id: "id",
                totalProperty: "totalCount"//总的数据条数
            }, recordstechnics)
    });

    storetechnics.load();
    storetechnics.on('load', function () {
        onReadyLoad--;
    });

    var ShowRelatedTechnicsBuilderGrid = new Ext.grid.GridPanel({
        height: 300,
        autoScroll: true,
        draggable: true,
        id: 'show_related_technics',
        renderTo: 'ShowRelatedTechnicsBuilderGrid',
        store: storetechnics,
        title: "受影响的工艺文件",
        cm: cmtechnics,
        loadMask: true,
        tbar: [
            {
                text: '<%=export%>',
                handler: function () {
                    exportData(type_technics);
                },
                cls: "x-btn-text-icon",
                icon: "<%=contextPath%>/netmarkets/images/export_to_excel.png",
                scope: this
            }
        ]
    });
    ShowRelatedTechnicsBuilderGrid.loadMask.show();


    /*受影响制品*/
    var cmproduct = new Ext.grid.ColumnModel([
        {header: '', dataIndex: 'icontype', width: 20},
        {header: '编号', dataIndex: 'number', autoWidth: true, renderer: renderNumber, sortable: true},
        {header: '名称', dataIndex: 'name', width: 160, sortable: true},
        {header: '在制品', dataIndex: 'zaizhipin', width: 100},
        {header: '在制品数量', dataIndex: 'zcount', width: 80},
        {header: '在制品实际返修数量', dataIndex: 'zrepaircount', width: 120},
        {header: '在制品整件外协数量', dataIndex: 'zjwxcount', width: 120},
        {header: '在制品整件外协负责人', dataIndex: 'zjwxfzr', width: 120},
        {header: '在制品关联返修工艺', dataIndex: 'zrepairtec', width: 120, renderer: renderNumber},
        {header: '工艺员', dataIndex: 'responser', width: 120},
        {header: '已制品', dataIndex: 'yizhipin', width: 100},
        {header: '已制品数量', dataIndex: 'ycount', width: 80},
        {header: '已制品实际返修数量', dataIndex: 'yrepaircount', width: 120},
        {header: '已制品关联返修工艺', dataIndex: 'yrepairtec', width: 120, renderer: renderNumber},
        {header: '计调员', dataIndex: 'jidiaoyuan', width: 120},
        {header: '更改要求', dataIndex: 'requirement', width: 300},
        {header: '要求完成时间', dataIndex: 'completeTime', width: 120}
    ]);

    var recordsproduct = new Ext.data.Record.create([{name: 'id'}, {name: 'icontype'}, {name: 'number'}, {name: 'name'}, {name: 'code'}, {name: 'zaizhipin'}, {name: 'zcount'}, {name: 'zrepaircount'}, {name: 'zjwxcount'}, {name: 'zjwxfzr'}, {name: 'zrepairtec'}, {name: 'responser'}, {name: 'yizhipin'}, {name: 'ycount'}, {name: 'yrepaircount'}, {name: 'yrepairtec'}, {name: 'jidiaoyuan'}, {name: 'requirement'}, {name: 'completeTime'}]);
    var storeproduct = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy(
            {
                url: "<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/generateRelatedTable.jsp?oid=<%=workflowProcessOid%>&type=" + type_product,//获取数据的后台地址
                method: "POST",
                timeout: 9000000
            }),
        //解析json
        reader: new Ext.data.JsonReader(
            {
                root: "data",
                id: "id",
                totalProperty: "totalCount"//总的数据条数
            }, recordsproduct)
    });

    storeproduct.load();
    storeproduct.on('load', function () {
        onReadyLoad--;
    });

    var ShowRelatedProductsBuilderGrid = new Ext.grid.GridPanel({
        height: 300,
        autoScroll: true,
        draggable: true,
        id: 'show_related_product',
        renderTo: 'ShowRelatedProductsBuilderGrid',
        store: storeproduct,
        title: "受影响的制品",
        cm: cmproduct,
        loadMask: true,
        tbar: [
            {
                text: '<%=export%>',
                handler: function () {
                    exportData(type_product)
                },
                cls: "x-btn-text-icon",
                icon: "<%=contextPath%>/netmarkets/images/export_to_excel.png",
                scope: this
            }
        ]
    });
    ShowRelatedProductsBuilderGrid.loadMask.show();

    function showAllDealRecord(veroid, number, type, partNumber) {
        var win;
        var records = new Ext.data.Record.create([{name: 'number'}, {name: 'dealtype'}, {name: 'count'}, {name: 'repaircount'}, {name: 'code'}, {name: 'responser'}, {name: 'responsibleunit'}, {name: 'status'}, {name: 'finishtime'}, {name: 'card'}, {name: 'comments'}]);
        var columns = new Ext.grid.ColumnModel([
            {header: '图号', dataIndex: 'number', width: 200, sortable: true},
            {header: '处理类型', dataIndex: 'dealtype', autoWidth: true, sortable: true},
            {header: '数量', dataIndex: 'count', autoWidth: true, sortable: true},
            {header: '实际返修数量', dataIndex: 'repaircount', autoWidth: true, sortable: true},
            {header: '库存批次号', dataIndex: 'card', autoWidth: true, sortable: true},
            {header: '不合格品单号/返修计划号', dataIndex: 'code', autoWidth: true, sortable: true},
            {header: '责任人', dataIndex: 'responser', autoWidth: true, sortable: true},
            {header: '责任单位', dataIndex: 'responsibleunit', autoWidth: true, sortable: true},
            {header: '完工状态', dataIndex: 'status', autoWidth: true, sortable: true},
            {header: '实际完成时间', dataIndex: 'finishtime', autoWidth: true, sortable: true},
            {header: '处理意见', dataIndex: 'comments', autoWidth: true, sortable: true}
        ]);
        if (type == 'zaizhipin') {
            columns = new Ext.grid.ColumnModel([
                {header: '图号', dataIndex: 'number', width: 200, sortable: true},
                {header: '处理类型', dataIndex: 'dealtype', autoWidth: true, sortable: true},
                {header: '数量', dataIndex: 'count', autoWidth: true, sortable: true},
                {header: '实际返修数量', dataIndex: 'repaircount', autoWidth: true, sortable: true},
                {header: 'MES路卡号/ERP离散订单号', dataIndex: 'card', autoWidth: true, sortable: true},
                {header: '不合格品单号', dataIndex: 'code', autoWidth: true, sortable: true},
                {header: '责任人', dataIndex: 'responser', autoWidth: true, sortable: true},
                {header: '责任单位', dataIndex: 'responsibleunit', autoWidth: true, sortable: true},
                {header: '完工状态', dataIndex: 'status', autoWidth: true, sortable: true},
                {header: '实际完成时间', dataIndex: 'finishtime', autoWidth: true, sortable: true}
            ]);
        }
        var store = new Ext.data.Store({
            proxy: new Ext.data.HttpProxy({
                url: "<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/loadAllDealRecordData.jsp?oid=" + veroid + "&number=" + number + "&type=" + type + "&partnumber=" + partNumber,
                method: "POST",
                timeout: 9000000
            }),
            reader: new Ext.data.JsonReader({
                totalProperty: 'totalCount',
                root: 'data'
            }, records),
            remoteSort: true
        });
        store.load({
            params: {
                start: 0,
                limit: 15
            }
        });
        var grid = new Ext.grid.GridPanel({
            title: '制品处理记录',
            region: 'center',
            loadMask: true,
            store: store,
            id: "allDealRecord",
            cm: columns,
            viewConfig: {
                forceFit: true
            }
        });

        if (!win) {
            win = new Ext.Window({
                title: '查看所有处理记录',
                id: "AllDealRecord_Window",
                width: 900,
                height: 400,
                minWidth: 200,
                minHeight: 200,
                layout: 'fit',
                bodyStyle: 'padding:5px;',
                buttonAlign: 'center',
                items: [grid],
                modal: true,
                buttons: [{
                    text: '取消',
                    cls: "x-btn-text-icon",
                    icon: "netmarkets/images/cancel.png",
                    handler: function () {
                        win.close();
                    }
                }]

            });
        }
        win.show();
    }

    function exportData(type) {
        //工艺处理导出剩一个关联更改单/工艺没导出 制品的两个关联返修工艺没导出
        window.open("<%=contextPath%>/ptc1/ext/casc/analysisActivity/exportAnalysisData?type=" + type + "&oid=" + analysisActivityOid, '导出', 'height=600, width=650, top=150, left=300');
    }

    function completeData() {
        var url = "<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/dealAnalysisResult.jsp?workItemOid=<%=workflowProcessOid%>";
        url = encodeURI(url);

        Ext.Ajax.request({
            url: url,
            params: {},
            method: "POST",
            success: function (response) {
                var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
                completehiddenBtn.click();
            }
        });
    }

    function replaceReviewCompleteButton() {
        var completeBtn = document.getElementsByName("complete")[0];
        var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
        if (completeBtn && completehiddenBtn) {
            completehiddenBtn.onclick = completeBtn.onclick;
            completeBtn.onclick = completeData;
        }
    }

    replaceReviewCompleteButton();
    
    function showRemark(remark, dataId, isEditable) {
        var win;
        var grid = new Ext.form.TextArea({
            name: "gyyremark",
            value: remark,
            width: 300,
            height: 150,
            readOnly: true
        });

        if (!win) {
            win = new Ext.Window({
                title: '工艺员备注',
                id: "Gyyremark_Window",
                width: 1200,
                height: 400,
                minWidth: 200,
                minHeight: 200,
                layout: 'fit',
                bodyStyle: 'padding:5px;',
                buttonAlign: 'center',
                items: [grid],
                modal: true,
                buttons: [{
                    text: '取消',
                    cls: "x-btn-text-icon",
                    icon: "netmarkets/images/cancel.png",
                    handler: function () {
                        win.close();
                    }
                }]
            });
        }

        win.show();
    }

</script>
