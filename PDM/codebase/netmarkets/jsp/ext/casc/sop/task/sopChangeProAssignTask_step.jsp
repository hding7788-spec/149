<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc" %>
<jca:tabToHighlight actionName="propertyPanel" objectType="object"/>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb" %>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@ page import="ext.casc.sop.constants.SopConstants" %>
<%@ page import="ext.casc.sop.util.StringUtil" %>
<%@ page import="java.util.ArrayList" %>
<%
    session.setAttribute("TaskType", SopConstants.SOP_TASK_TASKTYPE_CHANGE);
    String contextPath = request.getContextPath();
    NmCommandBean nmCommandBean = new NmCommandBean();
    nmCommandBean.setCompContext(nmcontext.getContext().toString());
    nmCommandBean.setRequest(request);
    ArrayList selected2 = nmCommandBean.getSelectedOidForPopup();
    String oid = StringUtil.listToString(selected2);
    if(oid.isEmpty()){
        oid = request.getParameter("oid");
    }
    System.out.println("oid===================" + oid);
    String dateinfo = SopConstants.SOP_MSG_DATAINFO;
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
            {header: '*指定工艺员', dataIndex: 'gongyiyuan', autoWidth: true, sortable: true},
            {header: '*计划完成时间', dataIndex: 'jihuawanchengshijian', width: 180, sortable: true},
            {header: '任务要求', dataIndex: 'renwuyaoqiu', width: 180, sortable: true},
            {header: '所属型号', dataIndex: 'MINDEX', autoWidth: true, sortable: true},
            {header: '当前阶段', dataIndex: 'PHASE_CODE', autoWidth: true, sortable: true}
        ]);

        var records = new Ext.data.Record.create([{name: 'id'}, {name: 'type_icon'}, {name: 'number'}, {name: 'name'}, {name: 'version'}, {name: 'gongyiyuan'}, {name: 'PHASE_CODE'}, {name: 'jihuawanchengshijian'}, {name: 'renwuyaoqiu'}]);
        var store = new Ext.data.Store({
            proxy: new Ext.data.HttpProxy(
                {
                    url: "<%=contextPath%>/netmarkets/jsp/ext/casc/sop/task/generateSopTaskTable.jsp?oid=<%=oid%>",//获取数据的后台地址
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
        var index = selectId.indexOf("_gongyiyuan");
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
                    if (document.getElementById(selectx + "_gongyiyuan")) {
                        document.getElementById(selectx + "_gongyiyuan").value = object.value;
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