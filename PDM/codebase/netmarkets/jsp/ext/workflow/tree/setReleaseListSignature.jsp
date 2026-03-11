<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@page import="ext.casc.part.SignatureHelper"%>
<%@page import="java.util.*"%>
<%@page pageEncoding="UTF-8" %>
<input type="hidden" name="completeReviewhidden" id="completeReviewhidden" onclick="" />
<script>
		var oidArray = new Array();
		var activityName = "";
</script>
<%
	String path = request.getContextPath();
	String workflowProcessOid = request.getParameter("oid");
	List<String> oidList = SignatureHelper.getReviewOid(workflowProcessOid);
	if(oidList != null && oidList.size() > 0){
		for(int i = 0 ; i < oidList.size() ; i++){
			String tempOid = oidList.get(i);
%>
<script>
	var workflowProcessOid = '<%=workflowProcessOid%>';
	var tempOid = '<%=tempOid%>';
	oidArray[oidArray.length] = tempOid;
</script>
<%
		}
	}
%>

<jsp:include page="${mvc:getComponentURL('ext.casc.workflow.tree.mvc.builder.SetReleaseListSignatureBuilder')}" />

<script>
	function setReviewData() {
		debugger;
		var isWuXuDaYin1 =  document.getElementById('routingChoice_无需打印');
		var isWuXuDaYin2 =  document.getElementById('routingChoice_无需打印_设计更改单偏离单不允许选无需打印');
		if((isWuXuDaYin1 != null && isWuXuDaYin1.checked) || (isWuXuDaYin2 != null && isWuXuDaYin2.checked)){
			var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
			completehiddenBtn.click();
			return;
		}
		var setValues = "";
		if(oidArray.length==0){
			var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
			completehiddenBtn.click();
			return;
		}
		var a = true;
		for ( var i = 0; i < oidArray.length; i++) {
			var tempOid = oidArray[i];
			var selElementValue = "";
			var selElectronicElementValue = "";
			
			var toGYYElementValue = "";
			var toGYYNameElementValue = "";
			var selElement = document.getElementsByName(tempOid + "_selected_department");
			var toGYYNameElement = document.getElementsByName(tempOid + "_selected_toggy");
			var toGYYElement = document.getElementsByName(tempOid + "_selected_toggy_value");
			var selElectronicElement = document.getElementsByName(tempOid + "_selected_elec_department");
	   		if(!selElement[0]||!selElectronicElement[0]) {
	   			continue;
	   		}
	   		selElementValue = selElement[0].value;
	   		selElectronicElementValue=selElectronicElement[0].value;
   			if(selElementValue!=""){
   				selElementValue=selElementValue+"###"+selElectronicElementValue;
   				setValues = setValues + tempOid + "~" + selElementValue + "~";
   				if(toGYYNameElement[0]){
   		   			toGYYElementValue = toGYYElement[0].value;
   		   			toGYYNameElementValue = toGYYNameElement[0].value;
   		   			if(toGYYElementValue!=""){
   	   					setValues = setValues + toGYYElementValue + "split"+ toGYYNameElementValue +"@";
   					}else{
   						setValues = setValues + "@";
   						if(a){
   							a = false;
   							if(confirm("是否确定不通知工艺员！")){
   	   							continue;
   	   						}else{
   	   							return;
   	   						}
   						}
   					}
   		   		}else{
   		   			setValues = setValues + "@";
   		   		}
			}else{
				alert("请选择分发部门");
				//var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
	   			//completehiddenBtn.click();
	   			return;
			}

   		}
		setValues = encodeURI(setValues);
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

   		xmlHttpRequest.onreadystatechange = function() {
   			if (xmlHttpRequest.readyState == 4) {
   				if (xmlHttpRequest.status == 200) {
   					var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
   					completehiddenBtn.click();
   				} else {

   				}
   			}
   		}
   		xmlHttpRequest.open("POST", "<%=path%>/netmarkets/jsp/ext/workflow/tree/setSelDepValue2.jsp", true);
   		xmlHttpRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
   		xmlHttpRequest.send("value=" + setValues + "&oid=" + workflowProcessOid);
   		
   		
   		
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

	function setSelDepartment(obj,versionoid){
		var obj = new Object();
		var path = "<%=path%>/netmarkets/jsp/ext/workflow/tree/selectedDepartment.jsp?type=paper";
		var ret = window.showModalDialog(path,obj,"dialogWidth=400px;dialogHeight=500px;resizable=yes;");
		if(ret != null) {
			setValue(obj,versionoid,ret,"_selected_department");
		}
	}
	
	function setSelElectricDepartment(obj,versionoid){
		var obj = new Object();
		var path = "<%=path%>/netmarkets/jsp/ext/workflow/tree/selectedDepartment.jsp?type=electric";
		var ret = window.showModalDialog(path,obj,"dialogWidth=400px;dialogHeight=500px;resizable=yes;");
		if(ret != null) {
			setValue(obj,versionoid,ret,"_selected_elec_department");
		}
	}

	function setValue(obj,versionoid,ret,type){
		var grid = Ext.getCmp("ext.casc.workflow.tree.mvc.builder.SetReleaseListSignatureBuilder");
		var selModel = grid.getSelectionModel();
		var selectedRows = selModel.getSelections();
		if (selectedRows.length > 0) {
			for ( var i = 0; i < selectedRows.length; i++) {
				var temp = selectedRows[i].get("oid");
				if (temp.indexOf("EPMDocument") > -1
						|| temp.indexOf("WTDocument") > -1
						|| temp.indexOf("WTChangeOrder2") > -1
						|| temp.indexOf("MPMProcessPlan") > -1
						|| temp.indexOf("ChangePackaged") > -1
						|| temp.indexOf("ChangeRequest") > -1) {
					if(temp.indexOf("OR:")>-1 || temp.indexOf("VR:")>-1){
						temp = temp.substring(3);
					}
					document.getElementById(temp + type).value = ret;
				}
			}
		} else {
			document.getElementById(versionoid).value = ret;
		}
	}

	function showToGGY(oid){
		var win;
		 var MobjectRecord = Ext.data.Record.create([{
	            name: 'fullname',
	            type: 'string'
	        },{
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
	                header: '<%=new String("用户名")%>',
	                dataIndex: 'name',
	                width:300
	            },
	            {
	                header: '<%=new String("姓名")%>',
	                dataIndex: 'fullname',
	                width:300
	            },
	            {
	                header: 'userid',
	                dataIndex: 'userid',
	                width:300,
	                hidden:true
	            }
	        ]);
	        var store = new Ext.data.Store({
	            proxy: new Ext.data.HttpProxy({
	                url: '<%=path%>/netmarkets/jsp/ext/workflow/tree/searchOtherHqUser.jsp'
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
	            title: '<%=new String("查询用户")%>',
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
	                title: '<%=new String("通知工艺员")%>',
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
	                        text: '<%=new String("确定")%>',
	                        cls: "x-btn-text-icon",
	                        icon: "<%=path%>/netmarkets/images/save.gif",
	                        handler: function(){
	                            var grid = Ext.getCmp("gridAllRecord");
	                            var selModel = grid.getSelectionModel();
	                            var selectedRows = selModel.getSelections();
	                            var userValue = "";
	                            var userfullname = "";
	                            var userIid = "";
	                            var usernames = "";
	                            var userids = "";
	                            var olduserid = document.getElementById(oid + "_selected_toggy_value").value;
	                            if (selectedRows.length > 0) {
	                                for (var i = 0; i < selectedRows.length; i++) {
	                                    userValue = selectedRows[i].data.name;
	                                    userfullname = selectedRows[i].data.fullname;
	                                    userIid = selectedRows[i].data.userid;
	                                    if(userids != null && olduserid.indexOf(userIid) > -1){
	                                        continue;
										}
	                                    usernames = usernames + userfullname + "(" + userValue + ");";
	                                    userids = userids + "OR:wt.org.WTUser:" + userIid + ";";
	                                }
	                            }
	                            zpGYY(oid,userids,usernames,"");
	                            win.close();
	                        }
	                    },
	                    {
	                        text: '<%=new String("取消")%>',
	                        cls: "x-btn-text-icon",
	                        icon: "<%=path%>/netmarkets/images/cancel.png",
	                        handler: function(){
	                            win.close();
	                        }
	                    }],
	                tbar: [
	                    {
	                        id: "searchInput",
	                        xtype: "textfield",
	                        emptyText: "<%=new String("请输入关键字")%>"
	                    }, {
	                        xtype: "button",
	                        text: "<%=new String("查询")%>",
	                        iconCls: "x-btn-search",
	                        scope: this,
	                        handler: function () {
	                            var theGrid = Ext.getCmp('gridAllRecord');
	                            var searchValue = Ext.getCmp("searchInput").getValue();
	                            theGrid.getStore().load({
	                                url:"<%=path%>/netmarkets/jsp/ext/workflow/tree/searchOtherHqUser.jsp",
	                                params:{searchValue:searchValue}
	                            });

	                        }
	                    }

	                ]

	            });
	        }
	        win.show();
    }

	function clearToGGY(oid){
		zpGYY(oid,"","","clear");
	}

	function zpGYY(oid, userOid, userName,type) {
		var grid = Ext.getCmp("ext.casc.workflow.tree.mvc.builder.SetReleaseListSignatureBuilder");
		var selModel = grid.getSelectionModel();
		debugger;
		var selectedRows = selModel.getSelections();
		if (selectedRows.length > 0) {
			for ( var i = 0; i < selectedRows.length; i++) {
				var temp = selectedRows[i].get("oid");
				if (temp.indexOf("EPMDocument") > -1
						|| temp.indexOf("WTDocument") > -1
						|| temp.indexOf("WTChangeOrder2") > -1
						|| temp.indexOf("MPMProcessPlan") > -1
						|| temp.indexOf("ChangePackaged") > -1
						|| temp.indexOf("ChangeRequest") > -1) {
					if(temp.indexOf("OR:")>-1 || temp.indexOf("VR:")>-1){
						temp = temp.substring(3);
					}
					if(type == "clear"){
						 document.getElementById(temp + "_selected_toggy").value = "";
		                 document.getElementById(temp + "_selected_toggy_value").value = "";
					}else{
						var oldname = "";
						var oldoid = "";
						oldname = document.getElementById(temp + "_selected_toggy").value;
						oldoid = document.getElementById(temp + "_selected_toggy_value").value;
						if (oldoid.indexOf(userOid) > -1 && oldoid != "") {
							oldname = oldname;
							oldoid = oldoid ;
						}else{
							oldname = oldname + userName;
							oldoid = oldoid + userOid;
						}
						document.getElementById(temp + "_selected_toggy").value = oldname;
						document.getElementById(temp + "_selected_toggy_value").value = oldoid;
					}
				}
			}
		} else {
			 if(type == "clear"){
                 document.getElementById(oid + "_selected_toggy").value = "";
                 document.getElementById(oid + "_selected_toggy_value").value = "";
				}else{
					var oldname = "";
					var oldoid = "";
					oldname = document.getElementById(oid + "_selected_toggy").value;
					oldoid = document.getElementById(oid + "_selected_toggy_value").value;
					if (oldoid.indexOf(userOid) > -1 && oldoid != "") {
						userName = oldname;
						userOid = oldoid ;
					}else{
						userName = oldname + userName;
						userOid = oldoid + userOid;
					}
					document.getElementById(oid + "_selected_toggy").value = userName;
					document.getElementById(oid + "_selected_toggy_value").value = userOid;
				}

		}

	}
</script>