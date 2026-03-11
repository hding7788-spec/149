<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ page import="ext.casc.process.ProcessConstants" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<jca:tabToHighlight actionName="propertyPanel" objectType="object"/>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb" %>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>

<%
    session.setAttribute("TaskType", ProcessConstants.TASK_TYPE_GONGYISHEJI);
    String contextPath = request.getContextPath();
    String oid = request.getParameter("oid");
    String dateinfo = ProcessConstants.JSP_MSG_DATEINFO;
%>


<input type="hidden" name="oidArray" value="${oidArray}">

<div id="SetListSignatureBuildergrid"></div>

<script>
    Ext.onReady(function () {
        var onReadyLoad = false;
        var sm = new Ext.grid.CheckboxSelectionModel({handleMouseDown: Ext.empthFn});
        var cm = new Ext.grid.ColumnModel([
            sm,
            {header: '', dataIndex: 'type_icon', width: 20, sortable: true},
            {header: '编号', dataIndex: 'number', autoWidth: true, sortable: true, renderer: renderNumber},
            {header: '名称', dataIndex: 'name', autoWidth: true, sortable: true},
            {header: '版本', dataIndex: 'version', autoWidth: true, sortable: true},
            {header: '图号', dataIndex: 'CINDEX', autoWidth: true, sortable: true},
            {header: '零组件生产类型', dataIndex: 'MTYPE', autoWidth: true, sortable: true},
            {header: '*主制车间', dataIndex: 'zhuzhichejian', autoWidth: true, sortable: true},
            {header: '*计划完成时间', dataIndex: 'jihuawanchengshijian', width: 180, sortable: true},
            {header: '材料定额计划完成时间', dataIndex: 'cldePlanTime', width: 180, sortable: true},
            {header: '任务要求', dataIndex: 'renwuyaoqiu', width: 180, sortable: true},
            {header: '任务依据', dataIndex: 'renwuyiju', autoWidth: true, sortable: true},
            {header: '所属型号', dataIndex: 'MINDEX', autoWidth: true, sortable: true},
            {header: '材料', dataIndex: 'CMAT', autoWidth: true, sortable: true},
            {header: '当前阶段', dataIndex: 'PHASE_CODE', autoWidth: true, sortable: true}
        ]);

        var records = new Ext.data.Record.create([{name: 'id'}, {name: 'type_icon'}, {name: 'number'}, {name: 'name'}, {name: 'version'}, {name: 'CINDEX'}, {name: 'MINDEX'}, {name: 'CMAT'}, {name: 'PHASE_CODE'}, {name: 'MTYPE'}, {name: 'zhuzhichejian'}, {name: 'jihuawanchengshijian'}, {name: 'cldePlanTime'}, {name: 'renwuyaoqiu'}, {name: 'renwuyiju'}]);
        var store = new Ext.data.Store({
            proxy: new Ext.data.HttpProxy(
                {
                    url: "<%=contextPath%>/netmarkets/jsp/ext/workflow/tree/generateProcessTaskTable.jsp?oid=<%=oid%>",//获取数据的后台地址
                    method: "POST"
                }),
            //解析json
            reader: new Ext.data.JsonReader(
                {
                    root: "data",
                    id: "id",
                    totalProperty: "totalCount"          //总的数据条数
                }, records)
        });

        store.load();
        store.on('load', function () {
            SetListSignatureBuilderGrid.loadMask.show();
            onReadyLoad = true;
        });
        var SetListSignatureBuilderGrid = new Ext.grid.GridPanel({
            width:window.screen.width+100,
            //height:document.body.scrollHeight,
            autoHeight:true,
            autoScroll: true,
            draggable: true,
            id: 'PBOM_ASSIGN_TASK',//id与以前Builderi的id一致
            renderTo: 'SetListSignatureBuildergrid',
            store: store,
            title: "对象列表",
            cm: cm,
            loadMask: true,
            sm: sm,
            tbar: [
                {
                    text: '',
                    handler: function () {
                        add();
                    },
                    cls: "x-btn-text-icon",
                    icon: "<%=contextPath%>/netmarkets/images/save.gif",
                    scope: this
                },
                {
                    text: '',
                    handler: function () {
                        add();

                    },
                    cls: "x-btn-text-icon",
                    icon: "<%=contextPath%>/netmarkets/images/add16x16.gif",
                    scope: this
                },
                {
                    text: '',
                    handler: function () {
                        removeParts();
                    },
                    cls: "x-btn-text-icon",
                    icon: "<%=contextPath%>/netmarkets/images/remove16x16.gif",
                    scope: this
                }
            ]

        });
        SetListSignatureBuilderGrid.loadMask.show();
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

    function add() {
        //<jca:action actionType="customProcessTask" actionName="addPart" />
        window.open("<%=contextPath%>/ptc1/customProcessTask/addPartNew?partOid=<%=oid%>&wizardActionClass=ext.casc.process.assign.ProAssignTaskBuilderAddPartProcessor&wizardActionMethod=execute");

    }

    function removeParts() {
        var grid = Ext.getCmp("PBOM_ASSIGN_TASK");
        var selModel = grid.getSelectionModel();
        var selectedRows = selModel.getSelections();
        if (selectedRows.length > 0) {
            for (var i = 0; i < selectedRows.length; i++) {
                grid.store.remove(selectedRows[i]);
            }
        }
    }

    function verify1(object) {
        var selectValue = object.value;
        var selectId = object.id;
        var index = selectId.indexOf("_zhuzhichejian");
        var tempOid = selectId.substring(0, index);

        for (var i = 1; i < 9; i++) {
            var selectElement = document.getElementById(tempOid + "_fuzhichejian" + i);
            if (selectElement) {
                var checkValue = i + "";
                if ((selectElement.checked && selectValue == checkValue)) {
                    alert("辅制车间已经分配了该车间，请重新分配");
                    selectElement.checked = false;
                }
            }

        }
        var selectElementX = document.getElementById(tempOid + "_fuzhichejian项");
        if (selectElementX) {
            if ((selectElementX.checked && selectValue == "项")) {
                alert("辅制车间已经分配了该车间，请重新分配");
                selectElementX.checked = false;
            }
        }

        var grid = Ext.getCmp("PBOM_ASSIGN_TASK");
        var selModel = grid.getSelectionModel();
        var selectedRows = selModel.getSelections();
        if (selectedRows.length > 0) {
            for (var i = 0; i < selectedRows.length; i++) {
                //var ss= selectedRows[i].get("partType");
                var temp = selectedRows[i].id;
                if (temp.indexOf("WTPart") > -1) {
                    var n = temp.lastIndexOf("$");
                    var selectx = temp.substring(n + 4, temp.length - 2);
                    if (selectx.indexOf("^VR:") > -1) {
                        var ojbs = selectx.split("^VR:");
                        selectx = ojbs[ojbs.length - 1];
                    }
                    if (document.getElementById(selectx + "_zhuzhichejian")) {
                        document.getElementById(selectx + "_zhuzhichejian").value = object.value;
                    }

                }
            }
        }
        //selModel.clearSelections();
    }

    function doHandleMonth(month) {
        if (month.toString().length == 1) {
            month = "0" + month;
        }
        return month;
    }

    function getToDay() {
        var now = new Date();
        var nowYear = now.getFullYear();
        var nowMonth = now.getMonth();
        var nowDate = now.getDate();
        newdate = new Date(nowYear, nowMonth, nowDate);
        nowMonth = doHandleMonth(nowMonth + 1);
        nowDate = doHandleMonth(nowDate);
        return nowYear + "/" + nowMonth + "/" + nowDate;
    }

    function validateDate(object) {
        var today = getToDay();
        var selDate = object.value;
        if (Date.parse(today) > Date.parse(selDate)) {
            alert("<%=dateinfo%>");
            object.value = "";
            return;
        }
    }

    function verify3(object) {

        validateDate(object);

        var grid = Ext.getCmp("PBOM_ASSIGN_TASK");
        var selModel = grid.getSelectionModel();
        var selectedRows = selModel.getSelections();
        if (selectedRows.length > 0) {
            for (var i = 0; i < selectedRows.length; i++) {
                var temp = selectedRows[i].id;
                if (temp.indexOf("WTPart") > -1) {
                    var n = temp.lastIndexOf("$");
                    var selectx = temp.substring(n + 4, temp.length - 2);
                    if (selectx.indexOf("^VR:") > -1) {
                        var ojbs = selectx.split("^VR:");
                        selectx = ojbs[ojbs.length - 1];
                    }
                    if (document.getElementById(selectx + "_jihuawanchengshijian")) {
                        document.getElementById(selectx + "_jihuawanchengshijian").value = object.value;
                    }

                }
            }
        }
//	selModel.clearSelections();
    }

    function verify4(object) {

        validateDate(object);

        var grid = Ext.getCmp("PBOM_ASSIGN_TASK");
        var selModel = grid.getSelectionModel();
        var selectedRows = selModel.getSelections();
        if (selectedRows.length > 0) {
            for (var i = 0; i < selectedRows.length; i++) {
                var temp = selectedRows[i].id;
                if (temp.indexOf("WTPart") > -1) {
                    var n = temp.lastIndexOf("$");
                    var selectx = temp.substring(n + 4, temp.length - 2);
                    if (selectx.indexOf("^VR:") > -1) {
                        var ojbs = selectx.split("^VR:");
                        selectx = ojbs[ojbs.length - 1];
                    }
                    if (document.getElementById(selectx + "_cldePlanTime")) {
                        document.getElementById(selectx + "_cldePlanTime").value = object.value;
                    }

                }
            }
        }
//	selModel.clearSelections();
    }

    function verify5(object) {
        var selectValue = object.value;
        var selectId = object.id;
        var index = selectId.indexOf("_renwuyiju");
        var tempOid = selectId.substring(0, index);

        var grid = Ext.getCmp("PBOM_ASSIGN_TASK");
        var selModel = grid.getSelectionModel();
        var selectedRows = selModel.getSelections();
        if (selectedRows.length > 0) {
            for (var i = 0; i < selectedRows.length; i++) {
                //var ss= selectedRows[i].get("partType");
                var temp = selectedRows[i].id;
                if (temp.indexOf("WTPart") > -1) {
                    var n = temp.lastIndexOf("$");
                    var selectx = temp.substring(n + 4, temp.length - 2);
                    if (selectx.indexOf("^VR:") > -1) {
                        var ojbs = selectx.split("^VR:");
                        selectx = ojbs[ojbs.length - 1];
                    }
                    var zzcj = document.getElementById(selectx + "_renwuyiju");

                    zzcj.value = object.value;
                }
            }
        }
        selModel.clearSelections();

    }

</SCRIPT>


<%@ include file="/netmarkets/jsp/util/end.jspf" %>