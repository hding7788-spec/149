<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="ext.casc.constants.Constants" %>
<%@page import="ext.casc.part.SignatureHelper" %>
<%@page import="ext.casc.workflow.signtrue.zp.HuiQianWorkFlowService" %>
<%@page import="java.util.List" %>
<input type="hidden" name="completeReviewhidden" id="completeReviewhidden" onclick=""/>
<script>
    var oidArray = new Array();
</script>
<%
    String passRadio = new String("通过");

    String tips = new String("有未指派的文件,请指派完全后再完成任务");

    String rejectRadio = new String("驳回");

    String weichuli = new String("无需会签");

    String butongyi = new String("不同意");

    String zpGYY2 = new String("指派工艺员");

    String workflowProcessOid = request.getParameter("oid");
    String activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(workflowProcessOid);
    String contextPath = request.getContextPath();

    List<String> oidList = SignatureHelper.getReviewOid(workflowProcessOid);
    System.out.println(oidList);
    if(oidList != null && oidList.size() > 0) {
        for(int i = 0; i < oidList.size(); i++) {
            String tempOid = oidList.get(i);
%>
<script>
    var tempOid = '<%=tempOid%>';
    oidArray[oidArray.length] = tempOid;
</script>
<%
        }
    }
%>
<script>
    var butongyi = '<%=butongyi%>';
    var weichuli = '<%=weichuli%>';
    var tips = '<%=tips%>';
    var zpGYY = '<%=zpGYY2%>';
    var workflowProcessOid = '<%=workflowProcessOid%>';
    var activityName = '<%=activityName%>';

    function showHQRY(oid) {
        var win;
        var nav;
        var loader;
        var root;
        if (!loader) {
            loader = new Ext.tree.TreeLoader({
                url: 'netmarkets/jsp/ext/workflow/loadGYYData.jsp?type=2&workOid=' + workflowProcessOid
            });
            loader.on('beforeload', function (treeloader, node) {
                treeloader.baseParams = {
                    id: node.id,
                    text: node.text,
                    method: 'tree'
                };
            }, this);
        }

        if (!root) {
            root = new Ext.tree.AsyncTreeNode({
                id: '0',
                text: '<%=Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG%>',
                icon: ""
            });
        }
        if (!nav) {
            nav = new Ext.tree.TreePanel({
                autoScroll: true,
                animate: true,
                height: 300,
                border: false,
                loader: loader,
                root: root,
                enableDD: false,
                rootVisible: false
            });
        }
        nav.expandAll();
        if (!win) {
            win = new Ext.Window({
                title: zpGYY,
                id: "GYHQ_Window",
                width: 450,
                minWidth: 200,
                minHeight: 300,
                layout: 'fit',
                closeAction: 'hide',
                bodyStyle: 'padding:5px;',
                buttonAlign: 'center',
                items: [nav],
                modal: true,
                buttons: [{
                    text: '确定',
                    cls: "x-btn-text-icon",
                    icon: "netmarkets/images/save.png",
                    handler: function () {
                        var arr = nav.getChecked();
                        if (arr.length > 0) {
                            var userOids = "";
                            var userNames = "";

                            var olduserid = document.getElementById(oid + "_sign_person_value").value;

                            for (var i = 0; i < arr.length; i++) {
                                if (userOids != null && olduserid.indexOf(arr[i].id) > -1) {
                                    continue;
                                }
                                userOids = userOids + arr[i].id + ";"
                                userNames = userNames + arr[i].text + ";"

                            }
                            zpGYZZ(oid, userOids, userNames, "");
                            win.close();

                        }
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
        win.show();
    }

    function zpGYZZ(oid, userOid, userName, type) {
        var grid = Ext.getCmp("set_list_preview_zp");
        var selModel = grid.getSelectionModel();
        var selectedRows = selModel.getSelections();
        if (selectedRows.length > 0) {
            for (var i = 0; i < selectedRows.length; i++) {
                var temp = selectedRows[i].id;
                if (temp.indexOf("EPMDocument") > -1|| temp.indexOf("WTDocument") > -1) {
                    if (type == "clear") {
                        document.getElementById(temp + "_sign_person").value = "";
                        document.getElementById(temp + "_sign_person").title = "";
                        document.getElementById(temp + "_sign_person_value").value = "";
                    } else {
                        var oldname = "";
                        var oldoid = "";
                        oldname = document.getElementById(temp + "_sign_person").value;
                        oldoid = document.getElementById(temp + "_sign_person_value").value;
                        if (oldoid.indexOf(userOid) > -1 && oldoid != "") {
                            oldname = oldname;
                            oldoid = oldoid;
                        } else {
                            oldname = oldname + userName;
                            oldoid = oldoid + userOid;
                        }
                        document.getElementById(temp + "_sign_person").value = oldname;
                        document.getElementById(temp + "_sign_person").title = oldname;
                        document.getElementById(temp + "_sign_person_value").value = oldoid;
                    }
                }
            }
        } else {
            if (type == "clear") {
                document.getElementById(oid + "_sign_person").value = "";
                document.getElementById(oid + "_sign_person_value").value = "";
            } else {
                var oldname = "";
                var oldoid = "";
                oldname = document.getElementById(oid + "_sign_person").value;
                oldoid = document.getElementById(oid + "_sign_person_value").value;
                if (oldname != null || !"".equals(oldname)) {
                    userName = oldname + userName;
                    userOid = oldoid + userOid;
                }
                document.getElementById(oid + "_sign_person").value = userName;
                document.getElementById(oid + "_sign_person_value").value = userOid;
            }
        }
    }

    function showOTHERHQRY(oid) {
        var win;
        var MobjectRecord = Ext.data.Record.create([{
            name: 'fullname',
            type: 'string'
        }, {
            name: 'name',
            type: 'string'
        }, {
            name: 'userid',
            type: 'string'
        }]);
        var sm = new Ext.grid.CheckboxSelectionModel({handleMouseDown: Ext.empthFn, singleSelect: true});
        var columns = new Ext.grid.ColumnModel([
            sm,
            {
                header: '用户名',
                dataIndex: 'name',
                width: 300
            },
            {
                header: '姓名',
                dataIndex: 'fullname',
                width: 300
            },
            {
                header: 'userid',
                dataIndex: 'userid',
                width: 300,
                hidden: true
            }
        ]);
        var store = new Ext.data.Store({
            proxy: new Ext.data.HttpProxy({
                url: '<%=contextPath%>/netmarkets/jsp/ext/workflow/tree/searchOtherHqUser.jsp'
            }),
            reader: new Ext.data.JsonReader({
                totalProperty: 'totalCount',
                root: 'data'
            }, MobjectRecord),
            remoteSort: true
        });
        store.load({
            params: {
                start: 0,
                limit: 15
            }
        });
        var grid = new Ext.grid.GridPanel({
            title: '查询用户',
            region: 'center',
            loadMask: true,
            store: store,
            id: "gridAllRecord",
            cm: columns,
            viewConfig: {
                forceFit: true
            }
        });

        if (!win) {
            win = new Ext.Window({
                title: '指派其他人员',
                id: "AllRecord_Window",
                width: 450,
                height: 500,
                minWidth: 200,
                minHeight: 300,
                layout: 'fit',
                bodyStyle: 'padding:5px;',
                buttonAlign: 'center',
                items: [grid],
                modal: true,
                buttons: [
                    {
                        text: '确定',
                        cls: "x-btn-text-icon",
                        icon: "<%=contextPath%>/netmarkets/images/save.gif",
                        handler: function () {
                            var grid = Ext.getCmp("gridAllRecord");
                            var selModel = grid.getSelectionModel();
                            var selectedRows = selModel.getSelections();
                            var userValue = "";
                            var userfullname = "";
                            var userIid = "";
                            var usernames = "";
                            var userids = "";

                            var olduserid = document.getElementById(oid + "_sign_person_value").value;
                            if (selectedRows.length > 0) {
                                for (var i = 0; i < selectedRows.length; i++) {
                                    userValue = selectedRows[i].data.name;
                                    userfullname = selectedRows[i].data.fullname;
                                    userIid = selectedRows[i].data.userid;
                                    if (userids != null && olduserid.indexOf(userIid) > -1) {
                                        continue;
                                    }
                                    usernames = usernames + userfullname + "(" + userValue + ");";
                                    userids = userids + "OR:wt.org.WTUser:" + userIid + ";";
                                }
                            }
                            zpGYZZ(oid, userids, usernames, "");
                            win.close();
                        }
                    },
                    {
                        text: '取消',
                        cls: "x-btn-text-icon",
                        icon: "<%=contextPath%>/netmarkets/images/cancel.png",
                        handler: function () {
                            win.close();
                        }
                    }],
                tbar: [
                    {
                        id: "searchInput",
                        xtype: "textfield",
                        emptyText: "请输入关键字"
                    }, {
                        xtype: "button",
                        text: "查询",
                        iconCls: "x-btn-search",
                        scope: this,
                        handler: function () {
                            var theGrid = Ext.getCmp('gridAllRecord');
                            var searchValue = Ext.getCmp("searchInput").getValue();
                            theGrid.getStore().load({
                                url: "<%=contextPath%>/netmarkets/jsp/ext/workflow/tree/searchOtherHqUser.jsp",
                                params: {searchValue: searchValue}
                            });

                        }
                    }

                ]

            });
        }
        win.show();
    }

    function clearOtherUser(oid) {
        zpGYZZ(oid, "", "", "clear")
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
</script>
<div id="grid"></div>
<script>
    Ext.onReady(function () {
        var sm = new Ext.grid.CheckboxSelectionModel({handleMouseDown: Ext.empthFn});
        var cm = new Ext.grid.ColumnModel([
            sm,
            {header: '', dataIndex: 'icontype', width: 20},
            {header: '编号', dataIndex: 'number', autoWidth: true, renderer: renderNumber},
            {header: '名称', dataIndex: 'name', autoWidth: true},
            {header: '版本', dataIndex: 'version', autoWidth: true},
            {header: '模型成熟度', dataIndex: 'modelMaturity', autoWidth: true},
            {header: '成熟度变化原因', dataIndex: 'maturityReason', autoWidth: true},
            {header: '指派会签人员', dataIndex: 'sign_person', width: 400}
        ]);

        var records = new Ext.data.Record.create([{name: 'id'}, {name: 'icontype'}, {name: 'number'}, {name: 'name'}, {name: 'version'}, {name: 'modelMaturity'}, {name: 'maturityReason'}, {name: 'sign_person'}]);
        var store = new Ext.data.Store({
            proxy: new Ext.data.HttpProxy(
                {
                    url: "<%=contextPath%>/netmarkets/jsp/ext/workflow/tree/generateTable.jsp?oid=<%=workflowProcessOid%>",//获取数据的后台地址
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
        });
        var grid = new Ext.grid.GridPanel({

            autoHeight: true,
            draggable: true,
            id: 'set_list_preview_zp',
            renderTo: 'grid',
            store: store,
            title: "指派工艺员列表",
            cm: cm,
            loadMask: true,
            sm: sm
        });
        grid.loadMask.show();
    });

    function setReviewData() {
        var signValue = "";
        var isBlankZP = true;
        for (var i = 0; i < oidArray.length; i++) {
            var tempOid = oidArray[i];
            var inputPersonsElementVal = "";
            var inputPersonsElement = document.getElementsByName(tempOid + "_sign_person_value");
            var inputPersonsDisElement = document.getElementsByName(tempOid + "_sign_person");
            if (!inputPersonsElement[0]) continue;
            inputPersonsElementVal = inputPersonsElement[0].value;
            if (isBlankZP && inputPersonsElementVal != "") {
                isBlankZP = false;
            }
            var numberElement = getElementByName(tempOid + "_number");
            var number = "";
            if (numberElement) {
                number = numberElement.value;
            }
            number = "(" + number + ")";

            signValue = signValue + tempOid + "~" + inputPersonsElement[0].value
                + "~" + inputPersonsDisElement[0].value + " @";

        }

        if (isBlankZP) {
            alert(tips);
            return;
        }
        signValue = encodeURI(signValue);
        var xmlHttpRequest;
        if (window.XMLHttpRequest) { // Mozilla, Safari,...
            xmlHttpRequest = new XMLHttpRequest();
            if (xmlHttpRequest.overrideMimeType) {
                xmlHttpRequest.overrideMimeType('text/xml');
            }
        } else if (window.ActiveXObject) { // IE
            try {
                xmlHttpRequest = new ActiveXObject("Msxml2.XMLHTTP");
            } catch (e) {
                try {
                    xmlHttpRequest = new ActiveXObject("Microsoft.XMLHTTP");
                } catch (e) {

                }
            }
        }

        xmlHttpRequest.onreadystatechange = function () {
            if (xmlHttpRequest.readyState == 4) {
                if (xmlHttpRequest.status == 200) {
                    var completehiddenBtn = document
                        .getElementsByName("completeReviewhidden")[0];
                    completehiddenBtn.click();
                } else {

                }
            }
        }

        xmlHttpRequest.open("POST", "<%=contextPath%>/netmarkets/jsp/ext/workflow/setSignZP.jsp", true);
        xmlHttpRequest.setRequestHeader("Content-Type",
            "application/x-www-form-urlencoded;charset=UTF-8");
        xmlHttpRequest.send("value=" + signValue + "&oid=" + workflowProcessOid);
    }

    function replaceReviewCompleteButton() {
        var completeBtn = document.getElementsByName("complete")[0];
        var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
        if (completeBtn && completehiddenBtn) {
            completeBtn.oldOnClick = completeBtn.onclick;
            completehiddenBtn.onclick = completeBtn.onclick;
            completeBtn.onclick = setReviewData;
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
                if (labelElements[i].innerHTML == '<%=passRadio%>'
                    || labelElements[i].innerHTML == '<%=rejectRadio%>') {
                    labelElements[i].style.display = "none";
                }
            }
        }
    }

    hiddenRadio();

</script>
