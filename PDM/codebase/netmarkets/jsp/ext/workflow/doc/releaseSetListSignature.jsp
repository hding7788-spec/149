<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@page import="ext.casc.part.SignatureHelper"%>
<%@page import="ext.casc.constants.PageConstants"%>
<%@page import="java.util.*"%>

<input type="hidden" name="completeReviewhidden" id="completeReviewhidden" onclick="" />

<script>
		var oidArray = new Array();
		var activityName = "";
</script>

<%
	String path = request.getContextPath();
	String workflowProcessOid = request.getParameter("oid");
	List<String> oidList = SignatureHelper.getReviewOid3(workflowProcessOid);
	System.out.println(oidList);
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
<%
  Object pbo = SignatureHelper.getPBO(workflowProcessOid);
  if(pbo instanceof wt.change2.WTChangeOrder2 ){
	  %>
	<jsp:include page="${mvc:getComponentURL('ext.casc.workflow.doc.mvc.builder.ShowReadObjBuilder')}" />
	   <%
  }
%>
<jsp:include page="${mvc:getComponentURL('ext.casc.workflow.doc.mvc.builder.ShowReviewObjBuilder')}" />

<script>

	function setReviewData() {
		var bohui =  document.getElementsByName("workitem$taskFormTemplate$<%=workflowProcessOid%>$___WfUserEvent0___radio");
		var isbohui = false;
		if(bohui[1]&&bohui[1].value=="<%=PageConstants.PAGE_BOHUI%>"){
				if(bohui[1].checked){
					isbohui = true;
				}
		}

   		var setValues = "";
   		var signValues = "";
   		var signCommentValues = "";
   		var isSet = true;
   		for ( var i = 0; i < oidArray.length; i++) {
	   		var tempOid = oidArray[i];

	   		var selectElement = document.getElementById(tempOid+"_select");
	   	    var inputAdviseElement = document.getElementById(tempOid+"_advise");
	   		if(selectElement&&inputAdviseElement){
	   			var tempSelect = selectElement[selectElement.selectedIndex].value;

				signCommentValues = signCommentValues + tempOid + ";;;qqq" + tempSelect + ";;;qqq" + "" + ";;;qqq" + inputAdviseElement.value + ";;;qqq" + tempOid;
				if(i<oidArray.length-1){
					signCommentValues += ";;;ppp";
				}
	   		}



	   		var selElementValue = "";
   			var selElement = document.getElementsByName(tempOid + "_selected_department");
	   		//alert("---->>>selValue:"+selElement);
	   		if(!selElement[0]) {
	   			continue;
	   		}
	   		selElementValue = selElement[0].value;
	   		isSet = true;
   			if(isSet && selElementValue!=""){
   				isSet = false;
			} else {
				continue;
			}
   			setValues = setValues + tempOid + "~" + selElementValue + "@";
   		}
       //alert(isSet);
   		for ( var i = 0; i < oidArray.length; i++) {
	   		var tempOid = oidArray[i];
	   		//alert("---->>>tempOid:"+tempOid);
	   		var selElementValue = "";
   			var selElement = document.getElementsByName(tempOid + "_selected_sign_info");
	   		//alert("---->>>selValue:"+selElement);
	   		if(!selElement[0]) {
	   			continue;
	   		}
	   		selElementValue = selElement[0].value;
	   		//alert(selElementValue);
	   		isSet = true;
   			if(isSet && selElementValue!=""){
   				isSet = false;
			} else {
				continue;
			}
   			signValues = signValues + tempOid + "~" + selElementValue + "@";
   		}

   		//alert(signValues);
   		//alert(isSet);
   		//return;

		if(setValues=="" && signValues==""){
			//var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
   			//completehiddenBtn.click();
   			//return;
		}
		setValues = encodeURI(setValues);
		signValues = encodeURI(signValues);
		signCommentValues = encodeURI(signCommentValues);
		//alert("---"+signValues);

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
   		var finalValue = setValues+";;;;"+signValues;
   		if(setValues=="" && signValues==""){
   			finalValue="";
   		}
   		//alert(finalValue);
   		xmlHttpRequest.open("POST", "<%=path%>/netmarkets/jsp/ext/workflow/doc/setPageValue.jsp", true);
   		xmlHttpRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
   		xmlHttpRequest.send("value=" + finalValue + "&oid=" + workflowProcessOid+"&signCommentValues="+signCommentValues+"&isbohui="+isbohui);
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
		var path = "<%=path%>/netmarkets/jsp/ext/glaway/mpm/processplan/selectedDepartment.jsp";
		var ret = window.showModalDialog(path,obj,"dialogWidth=400px;dialogHeight=400px;resizable=yes;");
		if(ret != null) {
			setValue(obj,versionoid,ret);
		}
	}

	 function trim(str){
			return str.replace(/(^\s*)|(\s*$)/g,"");      //使用正则    \s匹配空格、换行符、换页符等。
		}

	function setSignInfo(obj,isAdd,versionoid){
		var win;
		var nav;
		if(!nav){
			var data = [];//['1','2','3']
			var store = new Ext.data.Store({
				 proxy: new Ext.data.MemoryProxy(data),
			        reader: new Ext.data.ArrayReader({}, [
			            {name: 'department'},
			            {name: 'username'},
			            {name: 'description'}
			        ])
			});
			store.load();

			 var sm = new Ext.grid.CheckboxSelectionModel();

			var cm = new Ext.grid.ColumnModel([
			    sm,
			    {
				id : 'department',
				header : "<%=PageConstants.PAGE_KEHUDANWEI%>",
				dataIndex : 'department',
				width : 250,
				editor : new Ext.form.TextField({
					allowBlank : false
				// 不能为空
				})
			}
			, {
				id : 'username',
				header : "<%=PageConstants.PAGE_KEHU%>",
				dataIndex : 'username',
				width : 250,
				editor : new Ext.form.TextField({
					allowBlank : false
				// 不能为空
				})}
			, {
				id : 'description',
				header : "<%=PageConstants.PAGE_MIAOSHU%>",
				dataIndex : 'description',
				width : 250,
				editor : new Ext.form.TextField({
					allowBlank : false
				// 不能为空
				})}

			]);
			 if(isAdd == 'false'){
				 //设置不可编辑
				 cm = new Ext.grid.ColumnModel([
				                			    {
				                				id : 'department',
				                				header : "<%=PageConstants.PAGE_BUMEN%>",
				                				dataIndex : 'department',
				                				width : 250
				                			}
				                			, {
				                				id : 'username',
				                				header : "<%=PageConstants.PAGE_YONGHUMINGI%>",
				                				dataIndex : 'username',
				                				width : 250}
				                			, {
				                				id : 'description',
				                				header : "<%=PageConstants.PAGE_SHUOMING%>",
				                				dataIndex : 'description',
				                				width : 250}

				                			]);

             }

			var User = Ext.data.Record.create([
			                       			{
			                       				name : 'department',
			                       				type : 'string'
			                       			}
			                       			, {
			                       				name : 'username',
			                       				type : 'string'
			                       			}
			                       			, {
			                       				name : 'description',
			                       				type : 'string'
			                       			}
			                       			]);

	        nav = new Ext.grid.EditorGridPanel({
	        	store : store,
	        	cm : cm,
	        	stripeRows: true,
	        	columnLines: true,
	        	width : 800,
	        	height : 300,
	        	autoExpandColumn : 'department',
	        	//title : 'manager',// 标题
	        	frame : true,
	        	clicksToEdit : 1,// 设置点击几次才可编辑
	        	selModel : sm,
	        	tbar: [
	        	{
	        	text : '<%=PageConstants.PAGE_TIANJIA%>',
	        	id : 'grid_add_button',
	        	handler : function() {// 点击按钮执行的操作
					var n = nav.getStore().getCount();// 获得总行数
                           var p = new User({
								department : '',
								username : '',
								description:''
							});
					nav.stopEditing();// 停止编辑
					store.insert(n, p);// 插入到最后一行
					nav.startEditing(n, 0);// 开始编辑1单元格 }
                   }
	        	}
	           ,
	           {
	        	   text :'<%=PageConstants.PAGE_SHANCHU%>',
	        	   id : 'grid_delete_button',
	        	   handler : function() {
	        		  var rows = nav.getSelectionModel().getSelections();// 返回值为Record 数组
				      if (rows.length == 0) {
					      Ext.MessageBox.alert('<%=PageConstants.PAGE_JINGGAO%>',
							'<%=PageConstants.PAGE_TIP1%>');
				     } else {
                          if (rows) {
							for ( var i = 0; i < rows.length; i++) {
								store.remove(rows[i]);
							}
						  }
				       }
	        	   }
	           }
	        ]
	        });
	}
	  var wind_title = '<%=PageConstants.PAGE_SHEZHIWBHQXINXI%>';
	  if(isAdd == 'false'){
		  wind_title = '<%=PageConstants.PAGE_CHAKANWBHQXINXI%>';
	  }
    	if(!win){
        	win = new Ext.Window({
        		title: wind_title,
            	id:"grid_Window",
            	width:800,
           	 	minWidth: 600,
            	minHeight: 300,
            	layout: 'fit',
            	//plain:true,
            	closeAction:'hide',
            	bodyStyle:'padding:5px;',
            	buttonAlign:'center',
            	items:[nav],
            	modal:true,
            	  buttons : [{
                    text:'<%=PageConstants.PAGE_QUEDING%>',
                    id:'grid_ok_button',
                    cls : "x-btn-text-icon",
                	icon : "netmarkets/images/save.png",
                    handler:function(){
                    	if(isAdd == 'false'){
                    		win.close();
                    	}else{
                    	   var myStore = nav.getStore();
                    	   setSignValue(myStore,win,versionoid);
                    	}
                    }
                },{
                    text : '<%=PageConstants.PAGE_QUXIAO%>',
                    id:'grid_cancel_button',
                    cls : "x-btn-text-icon",
                	icon : "netmarkets/images/cancel.png",
                    handler:function(){
                        win.close();
                    }
                }]

        	});
        	var defaultValue = document.getElementById(versionoid+"_selected_sign_info").value;
			if(defaultValue){
					var values = defaultValue.split(";");
					for(var i=0;i<values.length;i++){
						var s = values[i];
						var infos = s.split(":");
						var n = nav.getStore().getCount();// 获得总行数
	                    var p = new User({
								department : infos[0],
								username : infos[1],
								description:infos[2]
							});
	                    store.insert(n, p);
					}
			}


        	win.show();
        	if(isAdd == 'false'){
				document.getElementById('grid_add_button').style.display="none";
				document.getElementById('grid_delete_button').style.display="none";
				//document.getElementById('grid_delete_button').disable=false;
			}
	  }
   }

	function setSignValue(store,win,versionoid){
		var finalValues = '';
		store.each(function (record) {
    		if(finalValues == ''){
    			finalValues = trim(record.get('department'))+":"+trim(record.get('username'))
    			              +":"+trim(record.get('description'));
    		}else{
    			finalValues = finalValues +";"+
    			record.get('department')+":"+record.get('username')+":"+record.get('description');
    		}
    	});
    	//alert(finalValues);
    	document.getElementById(versionoid+"_selected_sign_info").value=finalValues;
    	win.close();
	}

	function setValue(obj,versionoid,ret){
		var checkboxElement = document.getElementsByName("pjl_selPJLsa1__1");
		var flag = false;
		for ( var i = 0; i < checkboxElement.length; i++) {
			if (checkboxElement[i].checked) {
				flag = true;
				break;
			}
		}
		if(flag){
			var size = checkboxElement.length;
			for ( var i = 0; i < checkboxElement.length; i++) {
				if (checkboxElement[i].checked) {
					var tempValue = checkboxElement[i];
					var temp = tempValue.value;
					alert(temp);
					if (temp.indexOf("WTPart") > -1) {
						var n = temp.lastIndexOf("$");
						var selectx = temp.substring(n + 4, temp.length - 2);
						document.getElementById(selectx+"_selected_department").value=ret;
					}
				}
			}
			var grid = Ext.getCmp("com.glaway.mpm.mvc.builders.processplan.ProcessPlanReviewBuilder");
			var selModel = grid.getSelectionModel();
			selModel.clearSelections();
		}else{
			document.getElementById(versionoid+"_selected_department").value=ret;
		}
	}
</script>