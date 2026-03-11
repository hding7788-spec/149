<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="ext.casc.process.ProcessConstants" %>
<%@ page import="wt.fc.ReferenceFactory" %>
<%@ page import="wt.workflow.work.WorkItem" %>
<%@ page import="wt.workflow.engine.WfActivity" %>
<%@ page import="wt.change2.WTAnalysisActivity" %>
<%@ page import="ext.casc.analysisActivity.helper.AnalysisConstant" %>
<input type="hidden" name="completeReviewhidden" id="completeReviewhidden" onclick=""/>
<%
    String dateinfo = ProcessConstants.JSP_MSG_DATEINFO;
    String type_pbom = AnalysisConstant.TYPE_PBOM;
    String type_technics = AnalysisConstant.TYPE_TECHNICS;

    String add = "添加";
    String delete = "移除";
    String tempSave = "保存意见";

    String workflowProcessOid = request.getParameter("oid");
    String contextPath = request.getContextPath();
    ReferenceFactory rf = new ReferenceFactory();
    WorkItem wi = (WorkItem) rf.getReference(workflowProcessOid).getObject();
    WfActivity activity = (WfActivity) wi.getSource().getObject();
    WTAnalysisActivity pbo = (WTAnalysisActivity) activity.getContext().getValue("primaryBusinessObject");
    String analysisActivityOid = "OR%3A" + WTAnalysisActivity.class.getName() + "%3A" + pbo.getPersistInfo().getObjectIdentifier().getId();
    String analysisNumber = pbo.getNumber();
%>

<script>
    var workflowProcessOid = '<%=workflowProcessOid%>';
    var analysisActivityOid = '<%=analysisActivityOid%>';
    var analysisNumber = '<%=analysisNumber%>';
    var type_pbom = '<%=type_pbom%>';
    var type_technics = '<%=type_technics%>';

    function selectUser(oid, type, title) {
        window.open('netmarkets/jsp/ext/casc/analysisActivity/selectUser.jsp?oid=' + oid + '&type=' + type + '&title=' + title, title, 'height=500, width=500, top=150, left=300');
    }

    function clearUser(oid, type, number) {
        var grid;
        if (type == type_pbom) {
            grid = Ext.getCmp("set_related_pbom");
        } else if (type == type_technics) {
            grid = Ext.getCmp("set_related_technics");
        }
        if (grid) {
            if (document.getElementById(oid + "_responser_" + type)) {
                document.getElementById(oid + "_responser_" + type).value = '';
            }
            if (document.getElementById(oid + "_responser_value_" + type)) {
                document.getElementById(oid + "_responser_value_" + type).value = '';
            }
            var selModel = grid.getSelectionModel();
            var selectedRows = selModel.getSelections();
            if (selectedRows.length > 0) {
                for (var i = 0; i < selectedRows.length; i++) {
                    var temp = selectedRows[i].id;
                    if (document.getElementById(temp + "_responser_" + type)) {
                        document.getElementById(temp + "_responser_" + type).value = '';
                    }
                    if (document.getElementById(temp + "_responser_value_" + type)) {
                        document.getElementById(temp + "_responser_value_" + type).value = '';
                    }
                }
            }
        }
    }

    function setUserValue(userName, userOid, type, title) {
        var grid;
        if (type == type_pbom) {
            grid = Ext.getCmp("set_related_pbom");
        } else if (type == type_technics) {
            grid = Ext.getCmp("set_related_technics");
        }
        if (grid) {
            var selModel = grid.getSelectionModel();
            var selectedRows = selModel.getSelections();
            if (selectedRows.length > 0) {
                for (var i = 0; i < selectedRows.length; i++) {
                    var temp = selectedRows[i].id;
                    if (title == '选择责任人') {
                        if (document.getElementById(temp + "_responser_" + type)) {
                            document.getElementById(temp + "_responser_" + type).value = userName;
                        }
                        if (document.getElementById(temp + "_responser_value_" + type)) {
                            document.getElementById(temp + "_responser_value_" + type).value = userOid;
                        }
                    }
                }
            }
        }
    }

    function selectAffected(object, type) {
        var grid;
        if (type == type_pbom) {
            grid = Ext.getCmp("set_related_pbom");
        } else if (type == type_technics) {
            grid = Ext.getCmp("set_related_technics");
        }
        if (grid) {
            var selModel = grid.getSelectionModel();
            var selectedRows = selModel.getSelections();
            if (selectedRows.length > 0) {
                for (var i = 0; i < selectedRows.length; i++) {
                    var temp = selectedRows[i].id;
                    if (document.getElementById(temp + "_affected_" + type)) {
                        document.getElementById(temp + "_affected_" + type).value = object.value;
                    }
                }
            }
        }
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

    function verifyDate(object, type) {
        validateDate(object);

        var grid;
        if (type == type_pbom) {
            grid = Ext.getCmp("set_related_pbom");
        } else if (type == type_technics) {
            grid = Ext.getCmp("set_related_technics");
        }
        if (grid) {
            var selModel = grid.getSelectionModel();
            var selectedRows = selModel.getSelections();
            if (selectedRows.length > 0) {
                for (var i = 0; i < selectedRows.length; i++) {
                    var temp = selectedRows[i].id;
                    if (document.getElementById(temp + "_completeTime_" + type)) {
                        document.getElementById(temp + "_completeTime_" + type).value = object.value;
                    }
                }
            }
        }
    }

    function changeRequirement(object, type) {
        var grid;
        if (type == type_pbom) {
            grid = Ext.getCmp("set_related_pbom");
        } else if (type == type_technics) {
            grid = Ext.getCmp("set_related_technics");
        }
        if (grid) {
            var selModel = grid.getSelectionModel();
            var selectedRows = selModel.getSelections();
            if (selectedRows.length > 0) {
                for (var i = 0; i < selectedRows.length; i++) {
                    var temp = selectedRows[i].id;
                    if (document.getElementById(temp + "_requirement_" + type)) {
                        document.getElementById(temp + "_requirement_" + type).value = object.value;
                    }
                }
            }
        }
    }

</script>

<div id="SetRelatedPbomBuilderGrid"></div>
<br>
<div id="SetRelatedTechnicsBuilderGrid"></div>
<br>

<script>
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

    /*受影响PBOM列表*/
    var cmpbom = new Ext.grid.ColumnModel([
        smpbom,
        {header: '', dataIndex: 'icontype', width: 20},
        {header: '编号', dataIndex: 'number', autoWidth: true, renderer: renderNumber, sortable: true},
        {header: '名称', dataIndex: 'name', autoWidth: true, sortable: true},
        {header: '版本', dataIndex: 'version', width: 120, sortable: true},
        {header: '状态', dataIndex: 'state', width: 80, sortable: true},
        {header: '修改者', dataIndex: 'modifier', width: 80, sortable: true},
        {header: '修改时间', dataIndex: 'modifyTime', width: 120, sortable: true},
        {header: '有无影响', dataIndex: 'affected', width: 120},
        {header: '责任人', dataIndex: 'responser', width: 260},
        {header: '更改要求', dataIndex: 'requirement', width: 300},
        {header: '要求完成时间', dataIndex: 'completeTime', width: 200}
    ]);

    var recordspbom = new Ext.data.Record.create([{name: 'id'}, {name: 'icontype'}, {name: 'number'}, {name: 'code'}, {name: 'name'}, {name: 'version'}, {name: 'state'}, {name: 'modifier'}, {name: 'modifyTime'}, {name: 'affected'}, {name: 'responser'}, {name: 'requirement'}, {name: 'completeTime'}]);
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

    var SetRelatedPbomBuilderGrid = new Ext.grid.GridPanel({
        autoHeight: true,
        draggable: true,
        id: 'set_related_pbom',
        renderTo: 'SetRelatedPbomBuilderGrid',
        store: storepbom,
        title: "受影响的PBOM",
        cm: cmpbom,
        loadMask: true,
        sm: smpbom,
        tbar: [
            {
                text: '<%=add%>',
                handler: function () {
                    addObj(type_pbom);
                },
                cls: "x-btn-text-icon",
                icon: "<%=contextPath%>/netmarkets/images/add16x16.gif",
                scope: this
            },
            {
                text: '<%=delete%>',
                handler: function () {
                    delObj(type_pbom);
                },
                cls: "x-btn-text-icon",
                icon: "<%=contextPath%>/netmarkets/images/remove16x16.gif",
                scope: this
            },
            {
                text: '<%=tempSave%>',
                handler: function () {
                    saveWriteInfo(type_pbom);
                },
                cls: "x-btn-text-icon",
                icon: "<%=contextPath%>/netmarkets/images/save.png",
                scope: this
            }
        ]
    });
    SetRelatedPbomBuilderGrid.loadMask.show();


    /*受影响工艺文件列表*/
    var cmtechnics = new Ext.grid.ColumnModel([
        smtechnics,
        {header: '', dataIndex: 'icontype', width: 20},
        {header: '编号', dataIndex: 'number', autoWidth: true, renderer: renderNumber, sortable: true},
        {header: '名称', dataIndex: 'name', autoWidth: true, sortable: true},
        {header: '版本', dataIndex: 'version', width: 80, sortable: true},
        {header: '状态', dataIndex: 'state', width: 80, sortable: true},
        {header: '修改者', dataIndex: 'modifier', width: 80, sortable: true},
        {header: '修改时间', dataIndex: 'modifyTime', width: 120, sortable: true},
        {header: '编制部门', dataIndex: 'dept', width: 80, sortable: true},
        {header: '有无影响', dataIndex: 'affected', width: 120},
        {header: '责任人', dataIndex: 'responser', width: 260},
        {header: '更改要求', dataIndex: 'requirement', width: 300},
        {header: '要求完成时间', dataIndex: 'completeTime', width: 200}
    ]);

    var recordstechnics = new Ext.data.Record.create([{name: 'id'}, {name: 'icontype'}, {name: 'number'}, {name: 'code'}, {name: 'name'}, {name: 'version'}, {name: 'state'}, {name: 'modifier'}, {name: 'modifyTime'}, {name: 'dept'}, {name: 'affected'}, {name: 'responser'}, {name: 'requirement'}, {name: 'completeTime'}]);
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

    var SetRelatedTechnicsBuilderGrid = new Ext.grid.GridPanel({
        autoHeight: true,
        draggable: true,
        id: 'set_related_technics',
        renderTo: 'SetRelatedTechnicsBuilderGrid',
        store: storetechnics,
        title: "受影响的工艺文件",
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
                text: '<%=delete%>',
                handler: function () {
                    delObj(type_technics);
                },
                cls: "x-btn-text-icon",
                icon: "<%=contextPath%>/netmarkets/images/remove16x16.gif",
                scope: this
            },
            {
                text: '<%=tempSave%>',
                handler: function () {
                    saveWriteInfo(type_technics);
                },
                cls: "x-btn-text-icon",
                icon: "<%=contextPath%>/netmarkets/images/save.png",
                scope: this
            }
        ]
    });
    SetRelatedTechnicsBuilderGrid.loadMask.show();


    function addObj(type) {
        //保存意见
        saveWriteInfo(type);
        var winObj = window.open("<%=contextPath%>/ptc1/ext/casc/analysisActivity/addRelatedObj?objType=" + type + "&AjaxEnabled=component&wizardActionClass=ext.casc.analysisActivity.process.ExtAddRelatedPbomProcessor&wizardActionMethod=execute&actionName=addRelatedPbom&portlet=poppedup&context=analysisActivity%24relatedObjects%24" + analysisActivityOid + "%24&oid=" + analysisActivityOid + "&type=2", '添加受影响对象', 'height=600, width=650, top=150, left=300');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (type == type_pbom) {
                    var grid = Ext.getCmp("set_related_pbom");
                    if (grid) {
                        grid.store.reload();
                    }
                    var grid2 = Ext.getCmp("set_related_technics");
                    if (grid2) {
                        grid2.store.reload();
                    }
                } else if (type == type_technics) {
                    var grid = Ext.getCmp("set_related_technics");
                    if (grid) {
                        grid.store.reload();
                    }
                }
            }
        }, 3);
    }

    function delObj(type) {
        var flag = confirm("确认移除条目！");
        if (flag) {
            //保存意见
            saveWriteInfo(type);
            var grid;
            if (type == type_pbom) {
                grid = Ext.getCmp("set_related_pbom");
            } else if (type == type_technics) {
                grid = Ext.getCmp("set_related_technics");
            }
            if (grid) {
                var selModel = grid.getSelectionModel();
                var selectedRows = selModel.getSelections();
                if (selectedRows.length > 0) {
                    var temp = '';
                    for (var i = 0; i < selectedRows.length; i++) {
                        temp += selectedRows[i].id.replace(":", "%3A") + "~";
                    }

                    var url = "<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/delRelatedObj.jsp?type=2";
                    url = encodeURI(url);

                    Ext.Ajax.request({
                        url: url,
                        params: {
                            ids: temp,
                            number: analysisNumber
                        },
                        method: "POST",
                        success: function (response) {
                            grid.store.reload();
                        },
                        failure: function (response) {
                            Ext.Msg.alert("警告", "删除失败，请稍后再试！");
                        }
                    });
                }
            }
        }
    }

    //保存意见
    function saveWriteInfo(type) {
        var grid;
        if (type == type_pbom) {
            grid = Ext.getCmp("set_related_pbom");
        } else if (type == type_technics) {
            grid = Ext.getCmp("set_related_technics");
        }
        if (grid) {
            var params = "";
            for (var i = 0; i < grid.store.data.length; i++) {
                var row = grid.store.data.get(i);
                var dataId = row.id;
                    var affected = document.getElementById(dataId + "_affected_" + type).value;
                    var responser = document.getElementById(dataId + "_responser_value_" + type).value;
                    var requirement = document.getElementById(dataId + "_requirement_" + type).value;
                    var completeTime = document.getElementById(dataId + "_completeTime_" + type).value;
                    params += "id=" + dataId + "&";
                    params += "type=" + type + "&";
                    params += "affected=" + affected + "&";
                    params += "responser=" + responser + "&";
                    params += "requirement=" + requirement + "&";
                    params += "completeTime=" + completeTime + "@!@";
            }
            var url = "<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/saveWriteInfo.jsp?workItemOid=<%=workflowProcessOid%>";
            url = encodeURI(url);

            Ext.Ajax.request({
                url: url,
                params: {data: params},
                method: "POST",
                success: function (response) {
                    grid.store.reload();
                },
                failure: function (response) {
                    Ext.Msg.alert("警告", "意见保存失败，请稍后再试！");
                }
            });
        }
    }

    function setAnalysisData() {
        if (onReadyLoad > 0) {
            alert("数据加载中，请稍等！")
            return;
        }

        var gridpbom = Ext.getCmp("set_related_pbom");
        var gridtechnics = Ext.getCmp("set_related_technics");
        var params = "";
        //pbom
        for (var i = 0; i < gridpbom.store.data.length; i++) {
            var row = gridpbom.store.data.get(i);
            var dataId = row.id;
            var number = row.data.code;

            var affected = document.getElementById(dataId + "_affected_" + type_pbom).value;
            var responser_name = document.getElementById(dataId + "_responser_" + type_pbom).value;
            var responser = document.getElementById(dataId + "_responser_value_" + type_pbom).value;
            var requirement = document.getElementById(dataId + "_requirement_" + type_pbom).value;
            var completeTime = document.getElementById(dataId + "_completeTime_" + type_pbom).value;

            params += "id=" + dataId + "&";
            params += "type=" + type_pbom + "&";
            if (affected == '有影响' || affected == '新增') {
                //pbom的负责人不允许选择管理员
                if (responser_name.indexOf("Administrator") > -1) {
                    alert(number + "的责任人不允许为管理员");
                    responser.focus();
                    return;
                }

                if (responser == null || responser == '') {
                    alert(number + "的责任人不允许为空");
                    responser.focus();
                    return;
                }
                if (completeTime == null || completeTime == '') {
                    alert(number + "的要求完成时间不允许为空");
                    completeTime.focus();
                    return;
                }

                params += "affected=" + affected + "&";
                params += "responser=" + responser + "&";
                params += "requirement=" + requirement + "&";
                params += "completeTime=" + completeTime + "@!@";
            } else {
                params += "affected=" + affected + "&";
                params += "requirement=" + requirement + "&";
                params += "completeTime=" + completeTime + "@!@";
            }
        }


        //technics
        for (var i = 0; i < gridtechnics.store.data.length; i++) {
            var row = gridtechnics.store.data.get(i);
            var dataId = row.id;
            var number = row.data.code;

            var affected = document.getElementById(dataId + "_affected_" + type_technics).value;
            var responser = document.getElementById(dataId + "_responser_value_" + type_technics).value;
            var requirement = document.getElementById(dataId + "_requirement_" + type_technics).value;
            var completeTime = document.getElementById(dataId + "_completeTime_" + type_technics).value;

            params += "id=" + dataId + "&";
            params += "type=" + type_technics + "&";
            if (affected == '有影响') {
                if (responser == null || responser == '') {
                    alert(number + "的责任人不允许为空");
                    responser.focus();
                    return;
                }
                if (completeTime == null || completeTime == '') {
                    alert(number + "的要求完成时间不允许为空");
                    completeTime.focus();
                    return;
                }
                params += "affected=" + affected + "&";
                params += "responser=" + responser + "&";
                params += "requirement=" + requirement + "&";
                params += "completeTime=" + completeTime + "@!@";
            } else {
                params += "affected=" + affected + "&";
                params += "requirement=" + requirement + "&";
                params += "completeTime=" + completeTime + "@!@";
            }
        }

        var url = "<%=contextPath%>/netmarkets/jsp/ext/casc/analysisActivity/setAnalysisResult.jsp?workItemOid="+workflowProcessOid + "&type=2";
        url = encodeURI(url);

        Ext.Ajax.request({
            url: url,
            params: {
                data: params
            },
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
            completeBtn.onclick = setAnalysisData;
        } else {
            var gridpbom = Ext.getCmp("set_related_pbom");
            gridpbom.tbar.hide();
            gridpbom.tbar.dom.style.height = '0px';
            var gridtechnics = Ext.getCmp("set_related_technics");
            gridtechnics.tbar.hide();
            gridtechnics.tbar.dom.style.height = '0px';
        }
    }

    replaceReviewCompleteButton();

</script>
