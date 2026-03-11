<%@ page import="java.net.URLEncoder" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    String contextPath = request.getContextPath();
    String processName = request.getParameter("processName");
    processName = URLEncoder.encode(processName,"UTF-8");
    System.out.println("contextPath：" + contextPath);
%>

<script type="text/javascript" src="<%=contextPath%>/netmarkets/javascript/ext/adapter/ext/ext-base.js"></script>

<script type="text/javascript" src="<%=contextPath%>/netmarkets/javascript/ext/ext-all.js"></script>

<link rel="stylesheet" type="text/css" href="<%=contextPath%>/netmarkets/javascript/ext/resources/css/ext-all.css">


<div id="userUnitGrid"></div>

<script>
    Ext.onReady(function () {
        var onReadyLoad = false;
        var sm = new Ext.grid.CheckboxSelectionModel({handleMouseDown: Ext.empthFn, singleSelect: true});
        var cm = new Ext.grid.ColumnModel([
            sm,
            {header: '序号', dataIndex: 'index', autoWidth: true, sortable: true},
            {header: 'unitid', dataIndex: 'unitid', autoWidth: true, sortable: true,hidden:true},
            {header: 'unitcode', dataIndex: 'unitcode', autoWidth: true, sortable: true,hidden:true},
            {header: 'useriid', dataIndex: 'useriid', autoWidth: true, sortable: true,hidden:true},
            {header: '协同单位', dataIndex: 'unit', autoWidth: true, sortable: true},
            {header: '协同人', dataIndex: 'user', width: 200, sortable: true},
			{header: "操作", dataIndex: "addUser", width: 50,
				renderer: function (value, cellmeta) {
				return "<input type='button' value='添加'>";

				}
			},{header: "清空", dataIndex: "clearUser", width: 50,
                renderer: function (value, cellmeta) {
                    return "<input type='button' value='清空'>";

                }
            }
				
        ]);

        var records = new Ext.data.Record.create([{name: 'id'}, {name: 'unitid'}, {name: 'unitcode'} ,{name: 'useriid'} ,{name: 'index'}, {name: 'unit'}, {name: 'user'}]);
        var store = new Ext.data.Store({
            proxy: new Ext.data.HttpProxy(
                {
                    url: "<%=contextPath%>/netmarkets/jsp/ext/sast/center/workflow/generateUserList.jsp?type=siteInfo",//获取数据的后台地址
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

        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    var inputValue = window.opener.document.getElementById("userAndUnit").value;
                    var inputUnitValue = inputValue.split(":")[0];
                    var inputUserValue = inputValue.split(":")[1];
                    var store = userUnitGrid.getStore();
                    for (var i = 0; i < store.data.length; i++) {
                        var unitValue = store.getAt(i).data.unit;
                        var userValue = store.getAt(i).data.user;
                        if (inputUnitValue == unitValue && inputUserValue == userValue) {
                            userUnitGrid.getSelectionModel().selectRow(i);
                        }
                    }
                }
            }
        });
        store.on('load', function () {
            userUnitGrid.loadMask.show();
            onReadyLoad = true;
        });
        var userUnitGrid = new Ext.grid.GridPanel({
            width: 600,
            height:document.body.scrollHeight,
            //autoHeight: true,
            autoScroll: true,
            draggable: true,
            id: 'SetUserUnit',
            renderTo: 'userUnitGrid',
            store: store,
            title: "跨域会签参与单位与人员设置",
            cm: cm,
            loadMask: true,
            sm: sm,
            buttons: [
                {
                    text: '确定',
                    cls: "x-btn-text-icon",
                    icon: "<%=contextPath%>/netmarkets/images/save.gif",
                    handler: function(){
                        add();
                    }
                },
                {
                    text: '取消',
                    cls: "x-btn-text-icon",
                    icon: "<%=contextPath%>/netmarkets/images/cancel.png",
                    handler: function(){
                        window.close();
                    }
                }]


        });
        userUnitGrid.loadMask.show();
		userUnitGrid.addListener('cellclick', cellclick);
    });

	
	function cellclick(userUnitGrid, rowIndex, columnIndex, e) {
		userUnitGrid.getSelectionModel().selectRow(rowIndex);

　　    var fieldName = userUnitGrid.getColumnModel().getDataIndex(columnIndex); //Get field name 
　　    if (fieldName == "addUser") {
			addUser(rowIndex);
 　　   }
        else if (fieldName == "clearUser") {
            clearUser(rowIndex);
        }
   }
</script>

<script language="javascript">

    function add() {

        var grid = Ext.getCmp("SetUserUnit");
        var selModel = grid.getSelectionModel();
        var selectedRows = selModel.getSelections();
        var unitid = "";
        var unitValue = "";
        var unitcode = "";
        var userValue = "";
        var useriid = "";
        if (selectedRows.length > 0) {
            for (var i = 0; i < selectedRows.length; i++) {
                unitid = selectedRows[i].data.unitid;
                unitValue = selectedRows[i].data.unit;
                unitcode = selectedRows[i].data.unitcode;
                userValue = selectedRows[i].data.user;
                useriid = selectedRows[i].data.useriid;
            }
        }else{
            alert("未作任何选择");
            return;
        }
        /**
         * 具体人员执行，还是角色执行：
         *            预审流程                  会签流程                    发放流程
         *  -------------------------------------------------------------------------------------
         * 八部        执行人                    执行人                      ----
         * 805        执行人                    执行人                      ----
         * 509        执行人                    执行人                      ----
         * 800        执行角色（型号工艺师）       执行角色（型号工艺师）         执行角色（型号工艺师）
         * 812        执行角色（主任工艺师）       执行角色（主任工艺师）         执行角色（调度/档案/主任工艺师）
         * 149        执行人                    执行角色（主任工艺师）         执行角色（主任工艺师）
         * 802        执行人                    执行人                      执行角色（资料员）
         * 803        执行人                    执行人                      执行角色（资料员）
         * 804        执行人                    执行人                      执行角色（资料员）
         * 806        执行人                    执行人                      执行角色（资料员）
         * 811        执行人                    执行人                      执行角色(资料员)
         */
        //处理逻辑:通过各单位唯一id区别个单位的处理方式；
        //PDM-TODO:单位ID不确定，需确认各单位id具体值
        /**
         * 八部:
         * 805:805
         * 509:
         * 812:
         * 149:
         * 802:
         * 803:
         * 804:
         * 806:
         * 811:
         */

        if(unitValue.indexOf("805") != -1 || unitValue.indexOf("八部") != -1 || unitValue.indexOf("509") != -1){
            if (userValue == null || userValue == "") {
                alert("请指定会签人员");
                return;
            }

        }
        window.opener.document.getElementById("userAndUnit").value = unitValue + ":" + userValue;
        window.opener.document.getElementById("CustActVarselectUnitValueCustActVar").value = unitValue + "(" + unitcode + ")";
        window.opener.document.getElementById("CustActVarselectUsersValueCustActVar").value = userValue;
        window.opener.document.getElementById("CustActVarsiteAndUserIIdCustActVar").value = unitid + "-" + useriid;
        window.close();
    }

        var searchValue = "";

    function clearUser(rowIndex){
        var grid2 = Ext.getCmp("SetUserUnit");
        grid2.getStore().getAt(rowIndex).set("user","");
        grid2.getStore().getAt(rowIndex).set("useriid","");
    }
    function addUser(rowIndex){
        var grid2 = Ext.getCmp("SetUserUnit");
        var unitId = grid2.getStore().getAt(rowIndex).get("unitid");
        var unitInner = grid2.getStore().getAt(rowIndex).get("id");
        var processName = '<%=processName %>';
        var win;
        var MobjectRecord = Ext.data.Record.create([{
            name: 'iid',
            type: 'string'
        },{
            name: 'id',
            type: 'string'
        }, {
            name: 'name',
            type: 'string'
        }, {
            name: 'div_name',
            type: 'string'
        }]);
        var sm = new Ext.grid.CheckboxSelectionModel({handleMouseDown: Ext.empthFn, singleSelect: true});
        var columns = new Ext.grid.ColumnModel([
            sm,
            {
                header: 'IID',
                dataIndex: 'iid',
                width:300,
                hidden:true
            },
            {
                header: '姓名',
                dataIndex: 'id',
                width:300
            },
            {
                header: '登录名称',
                dataIndex: 'name',
                width:300
            },
            {
                header: '所属部门',
                dataIndex: 'div_name',
                width:300
            }
        ]);
        var store = new Ext.data.Store({
            proxy: new Ext.data.HttpProxy({
                url: '<%=contextPath%>/netmarkets/jsp/ext/sast/center/workflow/generateUserList.jsp?type=userInfo&unitiid='+unitId+'&unitInner='+unitInner+'&processName='+processName+''
            }),
            reader: new Ext.data.JsonReader({
                totalProperty: 'totalCount',
                root: 'data'
            },MobjectRecord),
            remoteSort: true
        });
        store.load({
            params:{
                start:0,
                limit:15
            }
        });
        var grid = new Ext.grid.GridPanel({
            title: '查询用户',
            region: 'center',
            loadMask: true,
            store: store,
            id:"gridAllRecord",
            cm: columns,
            viewConfig: {
                forceFit: true
            }
        });

        if (!win) {
            win = new Ext.Window({
                title: '查看所有记录',
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
                        handler: function(){
                            var grid = Ext.getCmp("gridAllRecord");
                            var selModel = grid.getSelectionModel();
                            var selectedRows = selModel.getSelections();
                            var userValue = "";
                            var userId = "";
                            var userIid = "";
                            if (selectedRows.length > 0) {
                                for (var i = 0; i < selectedRows.length; i++) {
                                    userValue = selectedRows[i].data.name;
                                    userId = selectedRows[i].data.id;
                                    userIid = selectedRows[i].data.iid;
                                }
                            }
                            var grid2 = Ext.getCmp("SetUserUnit");
                            var oldUser = grid2.getStore().getAt(rowIndex).get("user");
                            var oldUseriid = grid2.getStore().getAt(rowIndex).get("useriid");
                            if(oldUser==""){
                                grid2.getStore().getAt(rowIndex).set("user", userId + "(" + userValue + ")");
                                grid2.getStore().getAt(rowIndex).set("useriid", userIid);
                            }else{
                                grid2.getStore().getAt(rowIndex).set("user",oldUser+";"+ userId + "(" + userValue + ")");
                                grid2.getStore().getAt(rowIndex).set("useriid",oldUseriid+";"+ userIid);
                            }

                            win.close();
                        }
                    },
                    {
                    text: '取消',
                    cls: "x-btn-text-icon",
                    icon: "<%=contextPath%>/netmarkets/images/cancel.png",
                    handler: function(){
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
                                url:"<%=contextPath%>/netmarkets/jsp/ext/sast/center/workflow/generateUserList.jsp?type=userInfo&unitiid='+unitId+'&unitInner='+unitInner+'&processName='+processName+'",
                                params:{searchValue:searchValue}
                            });


                            // theGrid.getStore().filterBy(function (rec) {
                            //     if (searchValue == null || searchValue == "") {
                            //         return true;
                            //     }
                            //     if (rec.get('id').indexOf(searchValue) != -1 || rec.get('name').indexOf(searchValue) != -1) {
                            //         return true
                            //     } else {
                            //         return false;
                            //     }
                            // });
                        }
                    }

                ]

            });
        }
        win.show();
    }

</script>
