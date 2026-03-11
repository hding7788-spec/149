<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ page import="wt.fc.ReferenceFactory" %>
<%@ page import="wt.workflow.work.WorkItem" %>
<%@ page import="wt.workflow.engine.WfActivity" %>
<%@ page import="wt.change2.WTAnalysisActivity" %>
<%@ page import="ext.casc.analysisActivity.helper.AnalysisConstant" %>
<input type="hidden" name="completeReviewhidden" id="completeReviewhidden" onclick=""/>
<%
    String type_pbom = AnalysisConstant.TYPE_PBOM;
    String type_technics = AnalysisConstant.TYPE_TECHNICS;

    String passRadio = "完成更改";
    String rejectRadio = "驳回";
    String finish = "完成";
    String add = "添加";
    String createChangeOrder = "创建工艺更改单";
    String relatedChangeOrder = "关联更改单/工艺";

    String workflowProcessOid = request.getParameter("oid");
    String contextPath = request.getContextPath();
    ReferenceFactory rf = new ReferenceFactory();
    WorkItem wi = (WorkItem) rf.getReference(workflowProcessOid).getObject();
    WfActivity activity = (WfActivity) wi.getSource().getObject();
    WTAnalysisActivity pbo = (WTAnalysisActivity) activity.getContext().getValue("primaryBusinessObject");
    String analysisActivityOid = "OR%3A" + WTAnalysisActivity.class.getName() + "%3A" + pbo.getPersistInfo().getObjectIdentifier().getId();
    String wiOid = workflowProcessOid.replaceAll(":", "%3A");
    String analysisNumber = pbo.getNumber();
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
    var wiOid = '<%=wiOid%>';
    var analysisActivityOid = '<%=analysisActivityOid%>';
    var analysisNumber = '<%=analysisNumber%>';
    var type_pbom = '<%=type_pbom%>';
    var type_technics = '<%=type_technics%>';

    function selectAffected(object, type) {
        var grid;
        if (type == type_pbom) {
            grid = Ext.getCmp("deal_related_pbom");
        } else if (type == type_technics) {
            grid = Ext.getCmp("deal_related_technics");
        }
        if (grid) {
            var selModel = grid.getSelectionModel();
            var selectedRows = selModel.getSelections();
            if (selectedRows.length > 0) {
                for (var i = 0; i < selectedRows.length; i++) {
                    var temp = selectedRows[i].id;
                    if (document.getElementById(temp + "_affectedGyy_" + type)) {
                        document.getElementById(temp + "_affectedGyy_" + type).value = object.value;
                    }
                }
            }
        }
    }

    function showRemark(remark, dataId, isEditable) {
        var win;
        var grid = new Ext.form.TextArea({
            name: "gyyremark",
            value: remark,
            width: 300,
            height: 150
        });

        if (!win) {
            win = new Ext.Window({
                title: '工艺员备注',
                id: "Gyyremark_Window",
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
                    text: '确定',
                    cls: "x-btn-text-icon",
                    icon: "netmarkets/images/save.png",
                    handler: function () {
                        var remarkGyy = grid.getValue();
                        var params = dataId + "@!@" + remarkGyy;
                        var url = "<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/saveGyyRemark.jsp?workItemOid=<%=workflowProcessOid%>";
                        url = encodeURI(url);
                        Ext.Ajax.request({
                            url: url,
                            params: {data: params},
                            method: "POST",
                            success: function (response) {
                                var grid = Ext.getCmp("deal_related_technics");
                                if (grid) {
                                    grid.store.reload();
                                }                            },
                            failure: function (response) {
                                Ext.Msg.alert("警告", "意见保存失败，请稍后再试！");
                            }
                        });
                        win.close();
                    }
                }, {
                    text: '取消',
                    cls: "x-btn-text-icon",
                    icon: "netmarkets/images/cancel.png",
                    handler: function () {
                        win.close();
                    }
                }]
            });
        }

        if(isEditable == 'false') {
            win.buttons[0].hide();
            grid.readOnly = true;
        }

        win.show();
    }

</script>

<div id="DealRelatedPbomBuilderGrid"><br></div>
<div id="ShowChangeAfterPbomBuilderGrid"><br></div>
<div id="DealRelatedTechnicsBuilderGrid"><br></div>
<div id="ShowChangeAfterTechnicsBuilderGrid"><br></div>

<script>
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

    var onReadyLoad = 2;

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

    var smpbom = new Ext.grid.CheckboxSelectionModel({handleMouseDown: Ext.empthFn});
    var smtechnics = new Ext.grid.CheckboxSelectionModel({handleMouseDown: Ext.empthFn});

    //受影响PBOM列表
    var cmpbom = new Ext.grid.ColumnModel([
        smpbom,
        {header: '', dataIndex: 'icontype', width: 20},
        {header: '编号', dataIndex: 'number', autoWidth: true, renderer: renderNumber, sortable: true},
        {header: '名称', dataIndex: 'name', autoWidth: true, sortable: true},
        {header: '版本', dataIndex: 'version', width: 80, sortable: true},
        {header: '状态', dataIndex: 'state', width: 80, sortable: true},
        {header: '修改者', dataIndex: 'modifier', width: 80, sortable: true},
        {header: '修改时间', dataIndex: 'modifyTime', width: 100, sortable: true},
        {header: '主任师意见', dataIndex: 'affected', width: 100},
        {header: '有无影响', dataIndex: 'affectedGyy', width: 100},
        {header: '更改要求', dataIndex: 'requirement', width: 300},
        {header: '要求完成时间', dataIndex: 'completeTime', autoWidth: true},
        {header: '完成情况', dataIndex: 'dealStatus', autoWidth: true}
    ]);

    var recordspbom = new Ext.data.Record.create([{name: 'id'}, {name: 'icontype'}, {name: 'number'}, {name: 'code'}, {name: 'name'}, {name: 'version'}, {name: 'state'}, {name: 'modifier'}, {name: 'modifyTime'}, {name: 'affected'}, {name: 'affectedGyy'}, {name: 'responser'}, {name: 'requirement'}, {name: 'completeTime'}, {name: 'dealStatus'}]);
    var storepbom = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy(
            {
                url: "<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/generateRelatedTable.jsp?oid=<%=workflowProcessOid%>&deal=1&type=" + type_pbom,//获取数据的后台地址
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

    var DealRelatedPbomBuilderGrid = new Ext.grid.GridPanel({
        autoHeight: true,
        draggable: true,
        id: 'deal_related_pbom',
        renderTo: 'DealRelatedPbomBuilderGrid',
        store: storepbom,
        title: "处理受影响的PBOM",
        cm: cmpbom,
        loadMask: true,
        sm: smpbom,
        tbar: [
            {
                text: '<%=finish%>',
                handler: function () {
                    finishDeal(type_pbom);
                },
                cls: "x-btn-text-icon",
                icon: "<%=contextPath%>/netmarkets/images/save.png",
                scope: this
            }
        ]
    });
    DealRelatedPbomBuilderGrid.loadMask.show();

    //受影响PBOM产生的对象列表
    var cmafterpbom = new Ext.grid.ColumnModel([
        {header: '', dataIndex: 'icontype', width: 20},
        {header: '编号', dataIndex: 'number', autoWidth: true, renderer: renderNumber},
        {header: '名称', dataIndex: 'name', autoWidth: true},
        {header: '版本', dataIndex: 'version', autoWidth: true},
        {header: '状态', dataIndex: 'state', autoWidth: true},
        {header: '修改者', dataIndex: 'modifier', autoWidth: true},
        {header: '修改时间', dataIndex: 'modifyTime', autoWidth: true}
    ]);

    var recordsafterpbom = new Ext.data.Record.create([{name: 'id'}, {name: 'icontype'}, {name: 'number'}, {name: 'name'}, {name: 'version'}, {name: 'state'}, {name: 'modifier'}, {name: 'modifyTime'}]);
    var storeafterpbom = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy(
            {
                url: "<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/generateChangeAfter.jsp?oid=<%=workflowProcessOid%>&type=" + type_pbom,//获取数据的后台地址
                method: "POST",
                timeout: 9000000
            }),
        //解析json
        reader: new Ext.data.JsonReader(
            {
                root: "data",
                id: "id",
                totalProperty: "totalCount"//总的数据条数
            }, recordsafterpbom)
    });

    //加载受影响PBOM列表 如果受影响PBOM列表没有值 则隐藏受影响PBOM和产生的PBOM对象表格
    //如果受影响PBOM列表有值 则受影响PBOM和产生的PBOM对象表格都展示
    storepbom.load({
        callback: function (store, options, success) {
            if (store.length == 0) {
                var gridpbom = Ext.getCmp("deal_related_pbom");
                gridpbom.hide();
                var gridafterpbom = Ext.getCmp("show_change_after_pbom");
                gridafterpbom.hide();
            } else {
                storeafterpbom.load();
            }
            onReadyLoad--;
        }
    });

    var ShowChangeAfterPbomBuilderGrid = new Ext.grid.GridPanel({
        autoHeight: true,
        draggable: true,
        id: 'show_change_after_pbom',
        renderTo: 'ShowChangeAfterPbomBuilderGrid',
        store: storeafterpbom,
        title: "产生的PBOM",
        cm: cmafterpbom,
        loadMask: true
    });
    ShowChangeAfterPbomBuilderGrid.loadMask.show();


    //受影响工艺列表
    var cmtechnics = new Ext.grid.ColumnModel([
        smtechnics,
        {header: '', dataIndex: 'icontype', width: 20},
        {header: '编号', dataIndex: 'number', autoWidth: true, renderer: renderNumber, sortable: true},
        {header: '名称', dataIndex: 'name', autoWidth: true, sortable: true},
        {header: '版本', dataIndex: 'version', width: 80, sortable: true},
        {header: '状态', dataIndex: 'state', width: 80, sortable: true},
        {header: '修改者', dataIndex: 'modifier', width: 80, sortable: true},
        {header: '修改时间', dataIndex: 'modifyTime', width: 100, sortable: true},
        {header: '编制部门', dataIndex: 'dept', width: 80, sortable: true},
        {header: '主任师意见', dataIndex: 'affected', width: 100},
        {header: '有无影响', dataIndex: 'affectedGyy', width: 100},
        {header: '工艺员备注', dataIndex: 'remarkGyy', width: 100},
        {header: '更改要求', dataIndex: 'requirement', width: 300},
        {header: '要求完成时间', dataIndex: 'completeTime', autoWidth: true},
        {header: '关联更改单/工艺', dataIndex: 'relatedOrder', autoWidth: true},
        {header: '完成情况', dataIndex: 'dealStatus', autoWidth: true}
    ]);

    var recordstechnics = new Ext.data.Record.create([{name: 'id'}, {name: 'icontype'}, {name: 'number'}, {name: 'code'}, {name: 'name'}, {name: 'version'}, {name: 'state'}, {name: 'modifier'}, {name: 'modifyTime'}, {name: 'dept'}, {name: 'affected'}, {name: 'affectedGyy'}, {name: 'remarkGyy'}, {name: 'responser'}, {name: 'requirement'}, {name: 'completeTime'}, {name: 'relatedOrder'}, {name: 'relatedOrderState'}, {name: 'dealStatus'}, {name: 'pplantype'}]);
    var storetechnics = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy(
            {
                url: "<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/generateRelatedTable.jsp?oid=<%=workflowProcessOid%>&deal=1&type=" + type_technics,//获取数据的后台地址
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

    var DealRelatedTechnicsBuilderGrid = new Ext.grid.GridPanel({
        autoHeight: true,
        draggable: true,
        id: 'deal_related_technics',
        renderTo: 'DealRelatedTechnicsBuilderGrid',
        store: storetechnics,
        title: "处理受影响的工艺",
        cm: cmtechnics,
        loadMask: true,
        sm: smtechnics,
        tbar: [
            {
                text: '<%=add%>',
                handler: function () {
                    addObj(type_technics);
                },
                cls: "x-btn-text-icon",
                icon: "<%=contextPath%>/netmarkets/images/add16x16.gif",
                scope: this
            },
            {
                text: '<%=createChangeOrder%>',
                handler: function () {
                    createChangeOrder();
                },
                cls: "x-btn-text-icon",
                icon: "<%=contextPath%>/netmarkets/images/chgnotice_create.gif",
                scope: this
            },
             {
                text: '<%=relatedChangeOrder%>',
                handler: function () {
                    relatedChangeOrder();
                },
                cls: "x-btn-text-icon",
                icon: "<%=contextPath%>/netmarkets/images/related_objects_search.gif",
                scope: this
            },
            {
                text: '<%=finish%>',
                handler: function () {
                    finishDeal(type_technics);
                },
                cls: "x-btn-text-icon",
                icon: "<%=contextPath%>/netmarkets/images/save.png",
                scope: this
            }
        ]
    });
    DealRelatedTechnicsBuilderGrid.loadMask.show();

    //受影响工艺产生的对象列表
    var cmaftertechnics = new Ext.grid.ColumnModel([
        {header: '', dataIndex: 'icontype', width: 20},
        {header: '编号', dataIndex: 'number', autoWidth: true, renderer: renderNumber},
        {header: '名称', dataIndex: 'name', autoWidth: true},
        {header: '版本', dataIndex: 'version', autoWidth: true},
        {header: '状态', dataIndex: 'state', autoWidth: true},
        {header: '修改者', dataIndex: 'modifier', autoWidth: true},
        {header: '修改时间', dataIndex: 'modifyTime', autoWidth: true},
        {header: '编制部门', dataIndex: 'dept', width: 80, sortable: true}
    ]);

    var recordsaftertechnics = new Ext.data.Record.create([{name: 'id'}, {name: 'icontype'}, {name: 'number'}, {name: 'name'}, {name: 'version'}, {name: 'state'}, {name: 'modifier'}, {name: 'modifyTime'}, {name: 'dept'}]);
    var storeaftertechnics = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy(
            {
                url: "<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/generateChangeAfter.jsp?oid=<%=workflowProcessOid%>&type=" + type_technics,//获取数据的后台地址
                method: "POST",
                timeout: 9000000
            }),
        //解析json
        reader: new Ext.data.JsonReader(
            {
                root: "data",
                id: "id",
                totalProperty: "totalCount"//总的数据条数
            }, recordsaftertechnics)
    });

    //加载受影响工艺列表 如果受影响工艺列表没有值 则隐藏受影响工艺和产生的工艺对象表格
    //如果受影响工艺列表有值 则受影响工艺和产生的工艺对象表格都展示
    storetechnics.load({
        callback: function (store, options, success) {
            if (store.length == 0) {
                var gridtechnics = Ext.getCmp("deal_related_technics");
                gridtechnics.hide();
                var gridaftertechnics = Ext.getCmp("show_change_after_technics");
                gridaftertechnics.hide();
            } else {
                storeaftertechnics.load();
            }
            onReadyLoad--;
        }
    });

    var ShowChangeAfterTechnicsBuilderGrid = new Ext.grid.GridPanel({
        autoHeight: true,
        draggable: true,
        id: 'show_change_after_technics',
        renderTo: 'ShowChangeAfterTechnicsBuilderGrid',
        store: storeaftertechnics,
        title: "产生的工艺",
        cm: cmaftertechnics,
        loadMask: true
    });
    ShowChangeAfterTechnicsBuilderGrid.loadMask.show();

    function addObj(type) {
        var winObj = window.open("<%=contextPath%>/ptc1/ext/casc/analysisActivity/addRelatedObj?objType=" + type + "&AjaxEnabled=component&wizardActionClass=ext.casc.analysisActivity.process.ExtAddRelatedPbomProcessor&wizardActionMethod=execute&actionName=addRelatedPbom&portlet=poppedup&context=analysisActivity%24relatedObjects%24" + analysisActivityOid + "%24&oid=" + analysisActivityOid + "&type=2", '添加受影响对象', 'height=600, width=650, top=150, left=300');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (type == type_technics) {
                    var grid = Ext.getCmp("deal_related_technics");
                    if (grid) {
                        grid.store.reload();
                    }
                }
            }
        }, 3);
    }

    function createChangeOrder() {
        var grid = Ext.getCmp("deal_related_technics");
        if (grid) {
            var selModel = grid.getSelectionModel();
            var selectedRows = selModel.getSelections();
            if (selectedRows.length > 0) {
                if (selectedRows.length > 1) {
                    alert("请只选择一条数据创建！");
                    return;
                }
                var temp = selectedRows[0].id.replace(":", "%3A");
                var pplantype = selectedRows[0].data.pplantype;

                var url = "netmarkets/jsp/ext/casc/analysisActivity/validate.jsp?type=eco&oid=" + temp;
                xmlHttpRequest = createXMLHttpRequest();
                xmlHttpRequest.onreadystatechange = function () {
                    if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200) {
                        var result = xmlHttpRequest.responseText.replace(/(^\s*)|(\s*$)/g, "");
                        if (result == 'true') {
                            var url = "<%=contextPath%>/ptc1/ext/casc/changeNotice/create?context=workitem%24taskFormTemplate%24"+wiOid+"%24|customtemplates%24completeButton%24"+wiOid+"%24VR%3A"+temp+"!*&AjaxEnabled=component&wizardActionClass=ext.casc.change.process.ExtCreateChangeOrderProcessor&wizardActionMethod=execute&actionName=createCustomChangeNotice&portlet=poppedup&oid=VR%3A"+temp+"&u8=1&&docId=" + temp + "&analysisNumber=" + analysisNumber;
                            if (pplantype == 'DOC') {
                                url = "<%=contextPath%>/ptc1/ext/casc/changeNotice/changeNotice/create?context=workitem%24taskFormTemplate%24"+wiOid+"%24|customtemplates%24completeButton%24"+wiOid+"%24VR%3A"+temp+"!*&AjaxEnabled=component&wizardActionClass=ext.casc.change.process.ExtCreateChangeOrderProcessor&wizardActionMethod=execute&actionName=createDocCustomChangeNotice&portlet=poppedup&oid=VR%3A"+temp+"&u8=1&&docId=" + temp + "&analysisNumber=" + analysisNumber;
                            }
                            var winObj = window.open(url, '创建更改单', 'height=600, width=650, top=150, left=300');
                            var loop = setInterval(function () {
                                if (winObj.closed) {
                                    clearInterval(loop);
                                    var grid = Ext.getCmp("deal_related_technics");
                                    if (grid) {
                                        grid.store.reload();
                                    }
                                    var grid2 = Ext.getCmp("show_change_after_technics");
                                    if (grid2) {
                                        grid2.store.reload();
                                    }
                                }
                            }, 3);
                        } else {
                            alert("不符合创建更改单条件！");
                            return;
                        }
                    }
                };
                xmlHttpRequest.open("GET", url, true);
                xmlHttpRequest.setRequestHeader("Content-Type", "text/html;charset=UTF-8");
                xmlHttpRequest.setRequestHeader("If-Modified-Since", "0");
                xmlHttpRequest.send(null);
            }
        }
    }

    function relatedChangeOrder() {
        var grid = Ext.getCmp("deal_related_technics");
        if (grid) {
            var selModel = grid.getSelectionModel();
            var selectedRows = selModel.getSelections();
            if (selectedRows.length > 0) {
                if (selectedRows.length > 1) {
                    alert("请只选择一条数据关联！");
                    return;
                }
                var pplantype = selectedRows[0].data.pplantype;
                var docId = selectedRows[0].id.replace(":", "%3A");
                if (pplantype == '临时工艺文件') {
                    var winObj = window.open("<%=contextPath%>/ptc1/ext/casc/analysisActivity/addRelatedObj?objType=tempTechnics&AjaxEnabled=component&wizardActionClass=ext.casc.analysisActivity.process.ExtRelatedChangeOrderProcessor&wizardActionMethod=execute&portlet=poppedup&context=analysisActivity%24relatedObjects%24" + analysisActivityOid + "%24&oid=" + analysisActivityOid + "&docId=" + docId, '关联工艺文件', 'height=600, width=650, top=150, left=300');
                } else if (pplantype == 'DOC') {
                    var winObj = window.open("<%=contextPath%>/ptc1/ext/casc/analysisActivity/addRelatedObj?objType=docChange&AjaxEnabled=component&wizardActionClass=ext.casc.analysisActivity.process.ExtRelatedChangeOrderProcessor&wizardActionMethod=execute&portlet=poppedup&context=analysisActivity%24relatedObjects%24" + analysisActivityOid + "%24&oid=" + analysisActivityOid + "&docId=" + docId, '关联文档更改单', 'height=600, width=650, top=150, left=300');
                } else {
                    var winObj = window.open("<%=contextPath%>/ptc1/ext/casc/analysisActivity/addRelatedObj?objType=change&AjaxEnabled=component&wizardActionClass=ext.casc.analysisActivity.process.ExtRelatedChangeOrderProcessor&wizardActionMethod=execute&portlet=poppedup&context=analysisActivity%24relatedObjects%24" + analysisActivityOid + "%24&oid=" + analysisActivityOid + "&docId=" + docId, '关联工艺更改单', 'height=600, width=650, top=150, left=300');
                }
                var loop = setInterval(function () {
                    if (winObj.closed) {
                        clearInterval(loop);
                        var grid = Ext.getCmp("deal_related_technics");
                        if(grid){
                            grid.store.reload();
                        }
                    }
                }, 3);
            }
        }
    }

    //完成处理
    function finishDeal(type) {
        var grid;
        if (type == type_pbom) {
            grid = Ext.getCmp("deal_related_pbom");
        } else if (type == type_technics) {
            grid = Ext.getCmp("deal_related_technics");
        }
        var temp = '';
        if (grid) {
            var selModel = grid.getSelectionModel();
            var selectedRows = selModel.getSelections();
            if (selectedRows.length > 0) {
                for (var i = 0; i < selectedRows.length; i++) {
                    var data = selectedRows[i];
                    var dataId = data.id;
                    var number = data.data.code;
                    var dealStatus = data.data.dealStatus;
                    if(dealStatus == '已完成'){
                        //continue;
                    }
                    var affectedGyy = document.getElementById(dataId + "_affectedGyy_" + type).value;
                    if (type == type_pbom) {
                        if (affectedGyy == '新增') {
                            //校验部件下是否有工艺
                            var url = "netmarkets/jsp/ext/casc/analysisActivity/validate.jsp?type=newpbom&oid=" + dataId;
                            xmlHttpRequest = createXMLHttpRequest();
                            xmlHttpRequest.open("GET", url, false);
                            xmlHttpRequest.setRequestHeader("Content-Type", "text/html;charset=UTF-8");
                            xmlHttpRequest.setRequestHeader("If-Modified-Since", "0");
                            xmlHttpRequest.send(null);
                            if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200) {
                                var result = xmlHttpRequest.responseText.replace(/(^\s*)|(\s*$)/g, "");
                                if (result == 'false') {
                                    alert("部件"+number+"没有创建PBOM，创建后方可完成!");
                                    return;
                                }
                            }
                        }
                    } else if (type == type_technics) {
                        if (affectedGyy == '有影响') {
                            var relatedOrder = data.data.relatedOrder;
                            if(relatedOrder == null || relatedOrder == ''){
                                alert(number + "未关联更改单或工艺，不允许完成！");
                                return;
                            }else {
                                var relatedOrderState = data.data.relatedOrderState;
                                if(relatedOrderState != '已批准'){
                                    alert("工艺关联的更改单" + relatedOrder + "还未批准，不允许完成！");
                                    return;
                                }
                            }
                        }
                    }
                    temp += dataId + "@" + affectedGyy + "~";
                }
            }else {
                return;
            }
        }

        if(temp.replace(/(^\s*)|(\s*$)/g, "").length > 0){
            var flag = confirm("确定完成处理！");
            if (flag) {
                var url = "<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/finishDeal.jsp?analysisNumber=" + analysisNumber + "&type=" + type;
                url = encodeURI(url);
                Ext.Ajax.request({
                    url: url,
                    params: {
                        ids: temp
                    },
                    method: "POST",
                    success: function (response) {
                        grid.store.reload();
                    },
                    failure: function (response) {
                        Ext.Msg.alert("警告", "完成处理失败，请稍后再试！");
                    }
                });
            }
        }
    }

    function dealAnalysisData() {
        if (onReadyLoad > 0) {
            alert("数据加载中，请稍等！")
            return;
        }
        var gridpbom = Ext.getCmp("deal_related_pbom");
        var gridtechnics = Ext.getCmp("deal_related_technics");
        var finishAnalysis = document.getElementById("routingChoice_完成更改");

        for (var i = 0; i < gridpbom.store.data.length; i++) {
            var row = gridpbom.store.data.get(i);
            var number = row.data.code;
            var dealStatus = row.data.dealStatus;
            if (dealStatus != '已完成' && finishAnalysis && finishAnalysis.checked) {
                alert("部件" + number + "未完成更改，不允许完成任务！");
                return;
            }
        }

        for (var i = 0; i < gridtechnics.store.data.length; i++) {
            var row = gridtechnics.store.data.get(i);
            var number = row.data.code;
            var dealStatus = row.data.dealStatus;
            if (dealStatus != '已完成' && finishAnalysis && finishAnalysis.checked) {
                alert("工艺" + number + "未完成更改，不允许完成任务！");
                return;
            }
        }

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
            completeBtn.onclick = dealAnalysisData;
        } else {
            var gridpbom = Ext.getCmp("deal_related_pbom");
            gridpbom.tbar.hide();
            gridpbom.tbar.dom.style.height = '0px';
            var gridtechnics = Ext.getCmp("deal_related_technics");
            gridtechnics.tbar.hide();
            gridtechnics.tbar.dom.style.height = '0px';
        }
    }

    replaceReviewCompleteButton();

    function hiddenRadio() {
        var passRadio = document.getElementById('<%=passRadio%>');
        var rejectRadio = document.getElementById('<%=rejectRadio%>');
        if (passRadio && rejectRadio) {
            passRadio.style.display = "none";
            rejectRadio.style.display = "none";
            var labelElements = document.getElementsByTagName("label");
            for (var i = 0; i < labelElements.length; i++) {
                if (labelElements[i].innerHTML == '<%=passRadio%>' || labelElements[i].innerHTML == '<%=rejectRadio%>') {
                    labelElements[i].style.display = "none";
                }
            }
        }
    }

    hiddenRadio();

</script>
