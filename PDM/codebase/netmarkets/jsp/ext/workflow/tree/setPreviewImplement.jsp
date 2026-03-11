<%@page import="ext.casc.constants.Constants" %>
<%@page import="ext.casc.workflow.signtrue.zp.HuiQianWorkFlowService" %>
<%@page import="ext.casc.part.SignatureHelper" %>
<%@page import="java.util.*" %>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<input type="hidden" name="completeReviewhidden" id="completeReviewhidden" onclick=""/>
<script>
    var oidArray = new Array();
</script>
<%
    String zpgyzz = Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG;
    String passRadio = new String("通过");

    String rejectRadio = new String("驳回");

    String tips1 = new String("无需会签的工艺文件不允许指派！");

    String tips2 = new String("非组织会签的任务不允许再指派工艺文件！");

    String weichuli = new String("无需会签");

    String butongyi = new String("不同意");

    String allWeichuli = new String("请设置您负责的图纸的会签结论!");

    String tempSave = new String("临时保存意见");

    String workflowProcessOid = request.getParameter("oid");
    String contextPath = request.getContextPath();
    String activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(workflowProcessOid);

    List<String> oidList = SignatureHelper.getReviewOid(workflowProcessOid);
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

<input type="hidden" name="oidArray" value="${oidArray}">
<script>
    var butongyi = '<%=butongyi%>';
    var weichuli = '<%=weichuli%>';
    var tips1 = '<%=tips1%>';
    var tips2 = '<%=tips2%>';
    var workflowProcessOid = '<%=workflowProcessOid%>';

    function selectSignResult(object, veroid) {
        var grid = Ext.getCmp("set_list_preview");
        if (grid) {
            var selModel = grid.getSelectionModel();
            var selectedRows = selModel.getSelections();
            if (selectedRows.length > 0) {
                for (var i = 0; i < selectedRows.length; i++) {
                    var temp = selectedRows[i].id;
                    if (temp.indexOf("EPMDocument") > -1 || temp.indexOf("WTDocument") > -1 ||
                        temp.indexOf("WTChangeOrder2") > -1 || temp.indexOf("WTPart") > -1 ||
                        temp.indexOf("MPMProcessPlan") > -1) {
                        document.getElementsByName(temp + "_select")[0].value = object.value;
                    }
                }
            }
        }
    }

    function selectUser2(object, veroid) {
        var grid = Ext.getCmp("set_list_preview");
        if (grid) {
            var selModel = grid.getSelectionModel();
            var selectedRows = selModel.getSelections();
            if (selectedRows.length > 0) {
                for (var i = 0; i < selectedRows.length; i++) {
                    var temp = selectedRows[i].id;
                    if (temp.indexOf("EPMDocument") > -1 || temp.indexOf("WTDocument") > -1 ||
                        temp.indexOf("WTChangeOrder2") > -1 || temp.indexOf("WTPart") > -1 ||
                        temp.indexOf("MPMProcessPlan") > -1) {
                        document.getElementById(temp + "_sign_person_value").value = object.value;
                    }
                }
            }
            selModel.clearSelections();
        }
    }

    function selectUser3(object, veroid) {
        var grid = Ext.getCmp("set_list_preview");
        if (grid) {
            var selModel = grid.getSelectionModel();
            var selectedRows = selModel.getSelections();
            for (var i = 0; i < selectedRows.length; i++) {
                var temp = selectedRows[i].id;
                if (temp.indexOf("EPMDocument") > -1 || temp.indexOf("WTDocument") > -1 ||
                    temp.indexOf("WTChangeOrder2") > -1 || temp.indexOf("WTPart") > -1 ||
                    temp.indexOf("MPMProcessPlan") > -1) {
                    document.getElementById(temp + "_sign_person_value" + (object.id.substring(object.id.indexOf('value') + 5))).checked = object.checked;
                }
            }
            selModel.clearSelections();
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
</script>

<style type="text/css">
    .fuzhichejian-column {
        white-space: nowrap;
        overflow-x: scroll;
        overflow-y: auto;
        max-width: 500px;
        height: 38px;
    }

    /* 设置特定表格的行高 */
    .custom-grid .x-grid3-row {
        height: 40px !important;
    }

    .custom-grid .x-grid3-cell-inner {
        line-height: 40px;
    }

    .fuzhichejian-column::-webkit-scrollbar {
        height: 6px;
    }

    .fuzhichejian-column::-webkit-scrollbar-thumb {
        border-radius: 10px;
        -webkit-box-shadow: inset 0 0 5px rgb(0 0 0 / 20%);
        background-color: #494f55 !important;
    }

</style>

<div id="SetListPreviewBuilderGrid"></div>

<script>
    var onReadyLoad = false;

    var sm = new Ext.grid.CheckboxSelectionModel({handleMouseDown: Ext.empthFn});
    var cm = new Ext.grid.ColumnModel([
        sm,
        {header: '', dataIndex: 'icontype', width: 20},
        {header: '编号', dataIndex: 'number', autoWidth: true, renderer: renderNumber},
        {header: '名称', dataIndex: 'name', autoWidth: true},
        {header: '版本', dataIndex: 'version', autoWidth: true},
        {header: '模型成熟度', dataIndex: 'modelMaturity', autoWidth: true},
        {header: '成熟度变化原因', dataIndex: 'maturityReason', autoWidth: true},
        {header: '会签结论', dataIndex: 'sign_result', autoWidth: true},
        {header: '会签意见', dataIndex: 'sign_advise', autoWidth: true}
        <%if (activityName.equals(zpgyzz)) {%>,
        {header: '*主制车间', dataIndex: 'zhuzhichejian', autoWidth: true},
        {
            header: '辅制车间',
            dataIndex: 'fuzhichejian',
            width: 500,   // 设置固定宽度
            renderer: function(value) {
                return '<div class="fuzhichejian-column">' + value + '</div>';
            }
        }
        <%}%>
    ]);

    var records = new Ext.data.Record.create([{name: 'id'}, {name: 'icontype'}, {name: 'number'}, {name: 'name'}, {name: 'version'}, {name: 'modelMaturity'}, {name: 'maturityReason'}, {name: 'sign_result'}, {name: 'sign_advise'}, {name: 'zhuzhichejian'}, {name: 'fuzhichejian'}]);
    var store = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy(
            {
                url: "<%=contextPath%>/netmarkets/jsp/ext/workflow/tree/generateTable.jsp?oid=<%=workflowProcessOid%>",//获取数据的后台地址
                method: "POST",
                timeout: 9000000
            }),
        //解析json
        reader: new Ext.data.JsonReader(
            {
                root: "data",
                id: "id",
                totalProperty: "totalCount"
            }, records)
    });

    store.load();
    store.on('load', function () {
        SetListPreviewBuilderGrid.loadMask.show();
        onReadyLoad = true;
    });
    var SetListPreviewBuilderGrid = new Ext.grid.GridPanel({
        cls: 'custom-grid',
        autoHeight: true,
        draggable: true,
        id: 'set_list_preview',
        renderTo: 'SetListPreviewBuilderGrid',
        store: store,
        title: "预审列表",
        cm: cm,
        loadMask: true,
        sm: sm,
        tbar: [{
            text: '<%=tempSave%>',
            handler: function () {
                saveWriteInfo();
            },
            cls: "x-btn-text-icon",
            icon: "<%=contextPath%>/netmarkets/images/save.png",
            scope: this
        }]
    });
    SetListPreviewBuilderGrid.loadMask.show();

    function saveWriteInfo() {
        var params = "";
        if (oidArray.length > 0) {
            var allinputs = document.getElementsByTagName('input');
            for (var i = 0; i < allinputs.length; i++) {
                var oneinput = allinputs[i];
                if (oneinput.name.indexOf("_advise") > -1) {
                    params = params + oneinput.name + "=" + oneinput.value + "&"
                }
            }
            var allselects = document.getElementsByTagName('select');
            for (var i = 0; i < allselects.length; i++) {
                var oneinput = allselects[i];
                if (oneinput.name.indexOf("_select") > -1) {
                    params = params + oneinput.name + "=" + oneinput.value + "&"
                }
            }
            var url = "<%=contextPath%>/netmarkets/jsp/ext/workflow/tree/saveWriteInfo.jsp?workItemOid=<%=workflowProcessOid%>";

            url = encodeURI(url);

            Ext.Ajax.request({
                url: url,
                params: {data: params},
                method: "POST",
                success: function (response) {
                    Ext.Msg.alert("信息", "数据更新成功！", function () {
                        store.reload();
                    });
                },
                failure: function (response) {
                    Ext.Msg.alert("警告", "数据更新失败，请稍后再试！");
                }
            });
        } else {
            Ext.Msg.alert("警告", "没有任何需要更新的数据！");
        }
    }

    function find2(name) {
        if (document.mainform.elements != null) {
            for (var i = 0; i < document.mainform.elements.length; i++) {
                var e = document.mainform.elements[i];
                if (e.name.indexOf(name) >= 0 && e.name.indexOf("old") < 0)
                    return e;
            }
        }
        return null;
    }

    function setReviewData() {
        if (!onReadyLoad) {
            alert("数据加载中，请稍等！")
            return;
        }
        debugger;
        var zpGYZZ = "0";//如果是0代表是工艺会签，1代表指派工艺组组长
        var flag = true;
        var flag1 = true;
        var signValue = "";
        var isHQElement = document.getElementsByName("workitem$taskFormTemplate$<%=workflowProcessOid%>$___CustActVarisHuiqianCustActVar___");
        var isHQ = false;
        if (isHQElement[0]) {
            isHQ = isHQElement[0].checked;
        }
        for (var i = 0; i < oidArray.length; i++) {
            var tempOid = oidArray[i];
            var selectElement = document.getElementById(tempOid + "_select");
            if (selectElement == null) continue;
            var tempSelect = selectElement[selectElement.selectedIndex].value;
            if (tempSelect == butongyi) {
                flag = false;
            }
            if (tempSelect != weichuli) {
                flag1 = false;
            }
            var inputAdviseElement = document.getElementById(tempOid + "_advise");

            var zhuzhichejianElement = document.getElementsByName(tempOid + "_sign_person_valueA");
            var fuzhichejianElement = document.getElementsByName(tempOid + "_sign_person_valueB");

            var inputPersonsElementVal = "";
            var zhuzhichejian = "";
            var fuzhichejian = "";
            if (zhuzhichejianElement[0]) {
                zpGYZZ = "1";
            }
            for (var j = 0; j < zhuzhichejianElement.length; j++) {
                if (zhuzhichejianElement[j].value != "") {
                    zhuzhichejian = zhuzhichejianElement[j].value + ";";
                }
            }
            for (var j = 0; j < fuzhichejianElement.length; j++) {
                if (fuzhichejianElement[j].checked) {
                    fuzhichejian = fuzhichejian + fuzhichejianElement[j].value + ";";
                }
            }
            inputPersonsElementVal = zhuzhichejian + fuzhichejian;

            var numberElement = getElementByName(tempOid + "_number");
            var number = "";
            if (numberElement) {
                number = numberElement.value;
            }
            number = "(" + number + ")";
            if (isHQ) {//组织会签的校验

                if (tempSelect != weichuli && zhuzhichejian == "") {
                    alert(number + "的主制车间不允许为空");
                    inputAdviseElement.focus();
                    return;
                }

                //无需会签 不允许指派
                if (tempSelect == weichuli && (zhuzhichejian != "" || fuzhichejian != "")) {
                    alert(number + tips1);
                    inputAdviseElement.focus();
                    return;
                }

                //无需会签 不允许填写会签意见
                if (tempSelect == weichuli && inputAdviseElement.value != "") {
                    alert(number + "无需会签，不允许填写会签意见");
                    inputAdviseElement.focus();
                    return;
                }

                //如果辅制车间中包含了主制车间
                if (fuzhichejian != "" && zhuzhichejian != "" && fuzhichejian.indexOf(zhuzhichejian) > -1) {
                    alert(number + "主制车间和辅制车间不能选择同一车间");
                    inputAdviseElement.focus();
                    return;
                }
            } else {
                //非组织会签校验
                if (inputPersonsElementVal != "") {
                    alert(number + tips2);//非组织会签不能够再指派给别人
                    inputAdviseElement.focus();
                    return;
                }
            }
            if (tempSelect != weichuli) {
                signValue = signValue + tempOid + ";;;qqq" + tempSelect + ";;;qqq" + inputAdviseElement.value + ";;;qqq"
                    + inputPersonsElementVal + ";;;qqq" + zhuzhichejian + ";;;qqq" + fuzhichejian
            }
            if (i < oidArray.length - 1) {
                if (tempSelect != weichuli) {
                    signValue += ";;;ppp";
                }
            }
        }

        if (flag1) {
            var allWeichuli = '<%=allWeichuli%>';
            var zzhq = find2("isHuiqian");
            if (zzhq != null && (zzhq && zzhq.checked == false)) {
                alert(allWeichuli);
                return;
            }
        }

        if (!flag) {
            var passRadio = document.getElementById('<%=passRadio%>');
            var rejectRadio = document.getElementById('<%=rejectRadio%>');
            if (passRadio && rejectRadio) {
                rejectRadio.checked = true;
            }
        } else {
            var passRadio = document.getElementById('<%=passRadio%>');
            var rejectRadio = document.getElementById('<%=rejectRadio%>');
            if (passRadio && rejectRadio) {
                passRadio.checked = true;
            }
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
                    var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
                    completehiddenBtn.click();
                } else {
                }
            }
        }

        xmlHttpRequest.open("POST", "<%=contextPath%>/netmarkets/jsp/ext/workflow/tree/setPreviewImplementValue.jsp", true);
        xmlHttpRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        xmlHttpRequest.send("value=" + signValue + "&oid=" + workflowProcessOid + "&zpGYZZ=" + zpGYZZ);
    }

    function replaceReviewCompleteButton() {
        var completeBtn = document.getElementsByName("complete")[0];
        var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
        if (completeBtn && completehiddenBtn) {
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
                if (labelElements[i].innerHTML == '<%=passRadio%>' || labelElements[i].innerHTML == '<%=rejectRadio%>') {
                    labelElements[i].style.display = "none";
                }
            }
        }
    }

    hiddenRadio();
</script>