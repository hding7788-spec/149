<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<jca:tabToHighlight actionName="propertyPanel" objectType="object"/>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb" %>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>

<%
    String contextPath = request.getContextPath();
    String oid = request.getParameter("oid");
%>


<input type="hidden" name="oidArray" value="${oidArray}">

<div id="SetListProcessPlanBuilderGrid"></div>

<script>
    Ext.onReady(function () {
        var onReadyLoad = false;
        var sm = new Ext.grid.CheckboxSelectionModel({handleMouseDown: Ext.empthFn});
        var cm = new Ext.grid.ColumnModel([
            sm,
            {header: '', dataIndex: 'type_icon', width: 30, sortable: true},
            {header: '部件图号', dataIndex: 'partnumber', autoWidth: true, sortable: true, renderer: renderNumber},
            {header: '部件名称', dataIndex: 'partname', autoWidth: true, sortable: true},
            {header: '工艺文件编号', dataIndex: 'number', autoWidth: true, sortable: true, renderer: renderNumber},
            {header: '名称', dataIndex: 'name', autoWidth: true, sortable: true},
            {header: '版本', dataIndex: 'version', width: 50, sortable: true},
            {header: '状态', dataIndex: 'state', autoWidth: true, sortable: true},
            {header: '工序号', dataIndex: 'stepNumber', width: 50, sortable: true},
            {header: '工序名称', dataIndex: 'stepName', autoWidth: true, sortable: true},
            {header: '有辅', dataIndex: 'hasFu', width: 40, sortable: true},
            {header: '制造单位', dataIndex: 'workShop', autoWidth: true, sortable: true},
            {header: '准结', dataIndex: 'zhunjie', autoWidth: true,editor: new Ext.form.NumberField({
                    allowBlank: true,
                    allowDecimals: true,
                    decimalPrecision: 2,
                    minValue: 0
                }), sortable: true},
            {header: '单件人工', dataIndex: 'danjian', autoWidth: true, editor: new Ext.form.NumberField({
                    allowBlank: true,
                    allowDecimals: true,
                    decimalPrecision: 2,
                    minValue: 0
                }),sortable: true},
            {header: '单件设备', dataIndex: 'danJianSheBeiGS', autoWidth: true,  editor: new Ext.form.NumberField({
                    allowBlank: true,
                    allowDecimals: true,
                    decimalPrecision: 2,
                    minValue: 0
                }), sortable: true},
            {header: '是否设备工时', dataIndex: 'isSheBeiGS', autoWidth: true, sortable: true},
            {header: '工时定额状态', dataIndex: 'gongShiDingEState', autoWidth: true, sortable: true}
        ]);

        var records = new Ext.data.Record.create([{name: 'id'}, {name: 'type_icon'}, {name: 'code'}, {name: 'partnumber'}, {name: 'partname'}, {name: 'number'}, {name: 'name'}, {name: 'version'}, {name: 'state'}, {name: 'stepNumber'}, {name: 'stepName'}, {name: 'workShop'}, {name: 'isSheBeiGS'},{name: 'gongShiDingEState'},{name: 'zhunjie'}, {name: 'danjian'}, {name: 'zhunjie_old'}, {name: 'danjian_old'}, {name: 'danJianSheBeiGS'}, {name: 'danJianSheBeiGS_old'},  {name: 'hasFu'}, {name: 'isZhuRen'}]);
        var store = new Ext.data.Store({
            proxy: new Ext.data.HttpProxy(
                {
                    url: "<%=contextPath%>/netmarkets/jsp/ext/casc/gongshidinge/generateGongShiTable.jsp?oid=<%=oid%>",//获取数据的后台地址
                    method: "POST",
                    timeout: 9000000
                }),
            reader: new Ext.data.JsonReader(
                {
                    root: "data",
                    id: "id",
                    totalProperty: "totalCount"
                }, records)
        });

        store.load();
        store.on('load', function () {
            grid.loadMask.show();
            onReadyLoad = true;
        });

        var grid = new Ext.grid.EditorGridPanel({
            autoHeight: false,  // 修改为false以支持滚动
            height: Ext.getBody().getViewSize().height - 140,        // 设置固定高度
            autoWidth: true,
            autoScroll: true,
            draggable: true,
            clicksToEdit: 2, // 双击进入编辑
            enableColumnHide: true,  // 添加列隐藏功能
            viewConfig: {
                forceFit: true,      // 自动调整列宽
                scrollOffset: 0      // 消除滚动条偏移
            },
            bolder: true,
            id: 'gongshi_list_grid',
            renderTo: 'SetListProcessPlanBuilderGrid',
            store: store,
            title: "工序工时定额列表",
            cm: cm,
            loadMask: true,
            sm: sm,
            tbar: [
                {
                    text: '保存',
                    handler: function () {
                        // edit();
                        save();
                    },
                    cls: "x-btn-text-icon",
                    icon: "<%=contextPath%>/netmarkets/images/save.gif",
                    scope: this
                },{
                    text: '提交',
                    handler: function () {
                        submit();
                    },
                    cls: "x-btn-text-icon",
                    icon: "<%=contextPath%>/netmarkets/images/lwc_saved.gif",
                    scope: this
                },{
                    text: '导出',
                    handler: function () {
                        exportData();
                    },
                    cls: "x-btn-text-icon",
                    icon: "<%=contextPath%>/netmarkets/images/export_to_excel.png",
                    scope: this
                },{
                    text: '历史记录',
                    handler: function () {
                        history();
                    },
                    cls: "x-btn-text-icon",
                    icon: "<%=contextPath%>/netmarkets/images/history.gif",
                    scope: this
                },{
                    text: '全选工艺',
                    handler: function () {
                        selectAll();
                    },
                    cls: "x-btn-text-icon",
                    icon: "<%=contextPath%>/netmarkets/images/select_all.png",
                    scope: this
                },'->',{
                    text: '过滤',
                    handler:function(){
                        var searchValue=Ext.getCmp("searchValue").getValue();
                        var pattern=new RegExp(searchValue);
                        grid.getStore().filterBy(function(record,id){
                            for(var d in record.data){
                                var isFind =  pattern.test(record.data[d]);
                                if(isFind){
                                    return isFind;
                                }
                            }

                        });
                    } ,
                    cls : "x-btn-text-icon",
                    icon : "<%=contextPath%>/netmarkets/images/filter.gif",
                    scope:this
                },{
                    xtype: 'textfield',fieldLabel:'过滤', blankText: '表格任意字段值',labelWidth:40, name:'searchValue', id:'searchValue'
                }
            ]

        });
        grid.loadMask.show();

        grid.on('beforeedit', function(e) {
            var record = e.record;
            if (record.get('hasFu') === '是') {
                alert("该工序有辅工艺，不能编辑！");
                return false;  // 阻止编辑
            }
        });

        // 新增单击事件，批量编辑选中行的当前列
        grid.on('cellclick', function (grid, rowIndex, colIndex, e) {
            var record = grid.getStore().getAt(rowIndex);
            var fieldName = grid.getColumnModel().getDataIndex(colIndex);
            // 只允许编辑 zhunjie, danjian, danJianSheBeiGS 列
            if (fieldName === 'zhunjie' || fieldName === 'danjian' || fieldName === 'danJianSheBeiGS') {
                // 新增全局校验
                var selections = grid.getSelectionModel().getSelections();
                var currentSelected = grid.getSelectionModel().isSelected(rowIndex);
                if (!currentSelected) {
                    selections = [record];
                }

                // 遍历所有选中行进行校验
                var isValid = true;
                Ext.each(selections, function(sel) {
                    var gongShiDingEState = sel.get('gongShiDingEState') || '';
                    var isZhuRen = sel.get('isZhuRen');
                    if(isZhuRen && gongShiDingEState === '进行中') {
                        alert("工艺"+sel.data.code+"工序"+sel.data.stepNumber+"正在执行中，执行经理不能编辑！");
                        isValid = false;
                        return false;
                    }
                    if(!isZhuRen && gongShiDingEState === '保存提交') {
                        alert("工艺"+sel.data.code+"工序"+sel.data.stepNumber+"定额状态【保存提交】，工艺定额员不能编辑！");
                        isValid = false;
                        return false;
                    }
                    if(sel.get('hasFu') === '是') {
                        alert("工艺"+sel.data.code+"工序"+sel.data.stepNumber+"有辅工艺，不能编辑！");
                        isValid = false;
                        return false;
                    }
                });

                if (!isValid || selections.length < 1) {
                    return;
                }

                if (selections.length >= 1) {
                    Ext.MessageBox.prompt('批量编辑', '请输入新的值（数字，保留两位小数）：', function (btn, value) {
                        if (btn === 'ok') {
                            if (!isDouble(value)) {
                                alert("请输入有效的数字（最多两位小数）！");
                                return;
                            }
                            Ext.each(selections, function (sel) {
                                if (sel.get('hasFu') !== '是') { // 仅编辑无辅工艺的记录
                                    sel.set(fieldName, value);
                                }
                            });
                            grid.getStore().commitChanges(); // 提交更改
                        }
                    });
                }
            }
        });
    });

    function renderNumber(value) {
        var splits = value.split("@");
        var num = splits[0];
        var oid = splits[1];
        var url = " app/#ptc1/tcomp/infoPage?oid=" + oid;
        var value = "<a href=\"" + url + "\"  target=_blank>" + num + "</a>";
        return value;
    }
</script>

<script language="javascript">

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

    function save() {
        var selections = Ext.getCmp("gongshi_list_grid").getSelectionModel().getSelections();
        var param = '';
        var ids = '';
        if (selections.length > 0) {
            var isZhuRen = selections[0].data.isZhuRen;

            // 新增状态校验逻辑
            var isValid = true;
            Ext.each(selections, function(sel) {
                var gongShiDingEState = sel.get('gongShiDingEState') || '';
                // 普通定额员只能操作"进行中"状态
                if(!isZhuRen && gongShiDingEState !== '进行中') {
                    alert("工艺"+sel.data.code+"工序"+sel.data.stepNumber+"状态非【进行中】，工时定额员不能直接保存，请点击提交发起签审！");
                    isValid = false;
                    return false;
                }
            });
            if (!isValid) return;

            var noEdit = false;
            for (var i = 0; i < selections.length; i++) {
                var selection = selections[i];
                var hasFu = selection.data.hasFu;
                var number = selection.data.code;
                var name = selection.data.name;
                var stepNumber = selection.data.stepNumber;
                var isSheBeiGS = selection.data.isSheBeiGS; // 是否设备工时
                var zhunjie = selection.data.zhunjie || selection.data.zhunjie_old || '';
                var danjian = selection.data.danjian || selection.data.danjian_old || '';
                var danJianSheBeiGS = selection.data.danJianSheBeiGS || selection.data.danJianSheBeiGS_old || '';

                var zhunjieOld = selection.data.zhunjie_old;
                var danjianOld = selection.data.danjian_old;
                var danJianSheBeiGSOld = selection.data.danJianSheBeiGS_old;

                if (danjian === '' || zhunjie === '' || danJianSheBeiGS === '') {
                    // alert("工艺" + number + "工序" + stepNumber + "工时定额有空值，不允许提交工时定额");
                    // return;
                }
                if (zhunjie === zhunjieOld && danjian === danjianOld && danJianSheBeiGS === danJianSheBeiGSOld) {
                    noEdit = true;
                }

                // 校验是否设备工时为“是”时，单件设备不能为空
                if (isSheBeiGS === '是' && (danJianSheBeiGS === '' || danJianSheBeiGS == null)) {
                    alert("工艺" + number + "工序" + stepNumber + "是否设备工时为‘是’，单件设备必须填写，才能提交");
                    return;
                }

                param += "id=" + selection.id + "&&&number=" + number + "&&&name=" + name + "&&&stepNumber=" + stepNumber + "&&&zhunjie=" + zhunjie + "&&&danjian=" + danjian + "&&&danJianSheBeiGS=" + danJianSheBeiGS + "@!@";
                ids += selection.id + "~" + number + "~" + stepNumber + "@!@";
            }


            Ext.Ajax.request({
                url: "<%=contextPath%>/netmarkets/jsp/ext/casc/gongshidinge/saveGongshi.jsp",
                params: {param: param, oid: "<%=oid%>"},
                method: "POST",
                success: function (result, req) {
                    var msg = trim(result.responseText);
                    if (msg.length > 0) {
                        alert("保存工时定额成功！");
                        Ext.getCmp("gongshi_list_grid").store.commitChanges();
                    } else {
                        alert("发起修改工时定额失败！请联系管理员");
                    }
                },
                failure: function (result, req) {
                    Ext.MessageBox.alert('Failed', "Failed posted fork: " + result.date);
                }
            });
        }
    }

    //暂时废弃
    function edit() {
        var selections = Ext.getCmp("gongshi_list_grid").getSelectionModel().getSelections();
        if (selections.length > 0) {
            for(var i=0;i<selections.length;i++){
                var hasFu = selections[i].data.hasFu;
                if(hasFu === '是'){
                    alert("工艺" + selections[i].data.code + "工序" + selections[i].data.stepNumber + "有辅工艺，不允许修改工时定额");
                    return;
                }
            }
            var zhunjie = '';
            var danjian = '';
            var danJianSheBeiGS = '';
            if(selections.length === 1){
                zhunjie = selections[0].data.zhunjie;
                danjian = selections[0].data.danjian;
                danJianSheBeiGS = selections[0].data.danJianSheBeiGS;
            }
            var win;
            if (!win) {
                win = new Ext.Window({
                    title: '修改工时定额',
                    id: "EditGongshi_Window",
                    width: 300,
                    height: 200,
                    minWidth: 200,
                    minHeight: 200,
                    bodyStyle: 'padding:5px;',
                    buttonAlign: 'center',
                    layout: 'form',
                    items: [{
                        xtype: 'textfield',
                        id: 'zhunjie',
                        name: 'zhunjie',
                        fieldLabel: '准结',
                        value: zhunjie
                    },{
                        xtype: 'textfield',
                        id: 'danjian',
                        name: 'danjian',
                        fieldLabel: '单件人工',
                        value: danjian
                    },{
                        xtype: 'textfield',
                        id: 'danJianSheBeiGS',
                        name: 'danJianSheBeiGS',
                        fieldLabel: '单件设备',
                        value: danJianSheBeiGS
                    }],
                    buttons: [{
                        text: '保存',
                        cls: "x-btn-text-icon",
                        icon: "netmarkets/images/save.png",
                        handler: function () {
                            var zhunjie = Ext.getDom("zhunjie").value;
                            var danjian = Ext.getDom("danjian").value;
                            var danJianSheBeiGS = Ext.getDom("danJianSheBeiGS").value;
                            if(!isDouble(zhunjie) || !isDouble(danjian)|| !isDouble(danJianSheBeiGS)){
                                alert("工时定额只能填写数字或两位小数！")
                                return;
                            }
                            for(var i=0;i<selections.length;i++){
                                selections[i].set("zhunjie",zhunjie);
                                selections[i].set("danjian",danjian);
                                selections[i].set("danJianSheBeiGS",danJianSheBeiGS);
                            }
                            win.close();
                        }
                    },{
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
        } else {
            alert("请选择需要修改的信息！");
        }
    }

    function submit() {
        var selections = Ext.getCmp("gongshi_list_grid").getSelectionModel().getSelections();
        var param = '';
        var ids = '';
        if (selections.length > 0) {
            var isZhuRen = selections[0].data.isZhuRen;
            var noEdit = false;
            for(var i=0;i<selections.length;i++){
                var selection = selections[i];
                var hasFu = selection.data.hasFu;
                var number = selection.data.code;
                var name = selection.data.name;
                var stepNumber = selection.data.stepNumber;

                var isSheBeiGS = selection.data.isSheBeiGS; // 是否设备工时
                var gongShiDingEState = selection.data.gongShiDingEState; // 工时定额状态

                var zhunjie = selection.data.zhunjie || selection.data.zhunjie_old || '';
                var danjian = selection.data.danjian || selection.data.danjian_old || '';
                var danJianSheBeiGS = selection.data.danJianSheBeiGS || selection.data.danJianSheBeiGS_old || '';

                var zhunjieOld = selection.data.zhunjie_old;
                var danjianOld = selection.data.danjian_old;
                var danJianSheBeiGSOld = selection.data.danJianSheBeiGS_old;

                if(danjian === '' || zhunjie === ''|| danJianSheBeiGS === ''){
                    // alert("工艺" + number + "工序" + stepNumber + "工时定额有空值，不允许提交工时定额");
                    // return;
                }
                if(zhunjie === zhunjieOld && danjian === danjianOld && danJianSheBeiGS === danJianSheBeiGSOld){
                    noEdit = true;
                }

                // 校验是否设备工时为“是”时，单件设备不能为空
                if (isSheBeiGS === '是' && (danJianSheBeiGS === '' || danJianSheBeiGS == null)) {
                    alert("工艺" + number + "工序" + stepNumber + "是否设备工时为‘是’，单件设备必须填写，才能提交");
                    return;
                }

                param += "id=" + selection.id + "&&&number=" + number + "&&&name=" + name + "&&&stepNumber=" + stepNumber + "&&&zhunjie=" + zhunjie + "&&&danjian=" + danjian + "&&&danJianSheBeiGS=" + danJianSheBeiGS + "@!@";
                ids += selection.id + "~" + number + "~" + stepNumber + "@!@";
            }

            if(!isZhuRen){
                if (gongShiDingEState !== '已审核' && gongShiDingEState !== '已导入' && gongShiDingEState !== '') {
                    alert("工艺" + number + "工序" + stepNumber + "工时定额状态不是【已审核】或【已导入】，不允许提交！");
                    return;
                }
                //校验选中工序是否有正在进行的签审流程
                var url = "netmarkets/jsp/ext/casc/gongshidinge/validate.jsp?type=step&oid=" + ids;
                url = encodeURI(url);
                xmlHttpRequest = createXMLHttpRequest();
                xmlHttpRequest.open("GET", url, false);
                xmlHttpRequest.setRequestHeader("Content-Type", "text/html;charset=UTF-8");
                xmlHttpRequest.setRequestHeader("If-Modified-Since", "0");
                xmlHttpRequest.send(null);
                if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200) {
                    var result = xmlHttpRequest.responseText.replace(/(^\s*)|(\s*$)/g, "");
                    if (result !== '') {
                        alert(result);
                        return;
                    }
                }
            }

            var message = "确定提交工时定额！";
            if (noEdit) {
                message = "所选工时中含有未修改条目，确定提交工时定额！";
            }
            var flag = confirm(message);
            if(flag){
                Ext.Ajax.request({
                    url: "<%=contextPath%>/netmarkets/jsp/ext/casc/gongshidinge/submitGongshi.jsp",
                    params: {param: param,oid: "<%=oid%>"},
                    method: "POST",
                    success: function(result, req) {
                        var msg = trim(result.responseText);
                        if (msg.length > 0) {
                            if(msg === 'dingeyuan'){
                                alert("创建工时定额签审流程成功！");
                            }else {
                                alert("修改工时定额成功！");
                            }
                            Ext.getCmp("gongshi_list_grid").store.commitChanges();
                        } else {
                            alert("发起修改工时定额失败！请联系管理员");
                        }
                    },
                    failure: function(result, req) {
                        Ext.MessageBox.alert('Failed', "Failed posted fork: " + result.date);
                    }
                });
            }
        } else {
            alert("请选择需要提交的信息！");
        }
    }

    function exportData() {
        window.open("<%=contextPath%>/ptc1/ext/casc/gongshidinge/exportGongshiData?&oid=<%=oid%>", '导出', 'height=600, width=650, top=150, left=300');
    }

    function history() {
        var win;
        var records = new Ext.data.Record.create([{name: 'type_icon'}, {name: 'technicsnumber'}, {name: 'technicsname'}, {name: 'number'}, {name: 'name'}, {name: 'zhunjie'}, {name: 'danjian'}, {name: 'danJianSheBeiGS'}, {name: 'modifier'}, {name: 'modifyTime'}]);
        var columns = new Ext.grid.ColumnModel([
            {header: '', dataIndex: 'type_icon', width: 30, sortable: true},
            {header: '工艺编号', dataIndex: 'technicsnumber', autoWidth: true, sortable: true},
            {header: '工艺名称', dataIndex: 'technicsname', width: 200, sortable: true},
            {header: '工序号', dataIndex: 'number', width: 60, sortable: true},
            {header: '工序名称', dataIndex: 'name', autoWidth: true, sortable: true},
            {header: '准结', dataIndex: 'zhunjie', width: 60, sortable: true},
            {header: '单件人工', dataIndex: 'danjian', width: 60, sortable: true},
            {header: '单件设备', dataIndex: 'danJianSheBeiGS', width: 60, sortable: true},
            {header: '修改人', dataIndex: 'modifier', autoWidth: true, sortable: true},
            {header: '修改时间', dataIndex: 'modifyTime', autoWidth: true, sortable: true}
        ]);

        var params = '';
        var selections = Ext.getCmp("gongshi_list_grid").getSelectionModel().getSelections();
        if(selections.length > 0){
            for(var i=0;i<selections.length;i++){
                params += selections[i].data.id + "@!@";
            }
        }

        var store = new Ext.data.Store({
            proxy: new Ext.data.HttpProxy({
                url: "<%=contextPath%>/netmarkets/jsp/ext/casc/gongshidinge/loadHistoryData.jsp?oid=" + params,
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
            title: '工时定额历史信息',
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
                title: '查看定额记录',
                id: "AllDealRecord_Window",
                width: 1000,
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

    function selectAll() {
        var grid = Ext.getCmp("gongshi_list_grid");
        var selectionModel = grid.getSelectionModel();
        var store = grid.getStore();
        var text = "已勾选工艺编号: \n";

        debugger;

        const selectedCodes = new Set();
        var currentSelections = selectionModel.getSelections();

        if (currentSelections.length === 0) {
            alert("请先选择至少一行数据");
            return;
        }

        Ext.each(currentSelections, function(selection) {
            if(!selectedCodes.contains(selection.data.code)) {
                text += selection.data.code + "\n";
            }
            selectedCodes.add(selection.data.code);
        });

        store.each(function(record) {
            var code = record.data.code;
            if (code && selectedCodes.contains(code)) {
                var index = store.indexOf(record);
                selectionModel.selectRow(index, true);
            }
        });

        alert(text);
    }

    function isDouble(value) {
        var pattern = /^-?\d+(\.\d{1,2})?$/;
        return pattern.test(value);
    }
</SCRIPT>


<%@ include file="/netmarkets/jsp/util/end.jspf" %>