<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="ext.casc.constants.Constants"%>
<%@page import="ext.casc.workflow.signtrue.zp.HuiQianWorkFlowService"%>
<%@page import="ext.casc.workflow.signtrue.zp.SignatureService"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@page import="ext.casc.part.SignatureHelper"%>
<%@page import="java.util.*"%>
<input type="hidden" name="completeReviewhidden" id="completeReviewhidden" onclick="" />

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<script>
		var oidArray = new Array();
		var oidArray2 = new Array();
</script>
<%
String zpgyzz = Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG;
	String passRadio = "通过";

	String rejectRadio = "驳回";

	String tips = "有未指派的工艺文件,请指派后再完成任务";

	String tips2 = "不能把同组的三维模型和二维图分配给不同的车间！可根据提示编号到PDM中查找相应模型-相关对象-参考页签中的图纸是否与PRT分配同一部门。";

	String tips3 = "无需会签的工艺文件不允许指派！";

	String tips4 = "有指派车间的工艺文件填写的会签意见无效！";

	String tips5 = "非组织会签的任务不允许再指派工艺文件！";

	String weichuli = "无需会签";

	String tongyi = "同意";

	String butongyi = "不同意";

	String setConclusion = "批量设置会签结论:";

	String selectData = "请选择数据!";

	String allWeichuli = "请设置您负责的图纸的会签结论!";

	String noAdvise = "请为结论为不同意的图纸填写意见!";

	String tempSave = "临时保存意见";
	String workflowProcessOid = request.getParameter("oid");
	String contextPath = request.getContextPath();
	String activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(workflowProcessOid);

	commandBean.getRequestData().getParameterMap().put("oid",workflowProcessOid);
	List<String> oidList = SignatureHelper.getReviewOid(workflowProcessOid);
	if(oidList != null && oidList.size() > 0){
		for(int i = 0 ; i < oidList.size() ; i++){
			String tempOid = oidList.get(i);
			//if(tempOid.indexOf("wt.change2.WTChangeOrder2")>-1){
			//	continue;
			//}
%>
<script>
	var tempOid = '<%=tempOid%>';
	oidArray[oidArray.length] = tempOid;

</script>
<%
		}
	}
%>
<input type="hidden" name="oidArray" value="${oidArray}" >
<script>
	var butongyi = '<%=butongyi%>';
	var weichuli = '<%=weichuli%>';
	var tips = '<%=tips%>';
	var tips2 = '<%=tips2%>';
	var tips3 = '<%=tips3%>';
	var tips4 = '<%=tips4%>';
	var tips5 = '<%=tips5%>';
	var workflowProcessOid = '<%=workflowProcessOid%>';
	function showHQRY(oid){
		var win;
		var nav;
	    var loader;
	    var root;
	    var categoryid;
	    if(!loader){
	        loader = new Ext.tree.TreeLoader({
	            url :  'netmarkets/jsp/ext/workflow/loadGYYData.jsp?type=1&workOid='+workflowProcessOid
	        });
	        loader.on('beforeload', function(treeloader, node) {
	            treeloader.baseParams = {
	                id : node.id,
	                text:node.text,
	                method : 'tree'
	            };
	        }, this);
	    }

	    if(!root){
	        root = new Ext.tree.AsyncTreeNode({
	            id : '0',
	            text : '<%=Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG%>',
	            icon: ""
	        });
	    }
	    if(!nav){
	        nav = new Ext.tree.TreePanel({
	            autoScroll:true,
	            animate : true,
	            height:300,
	            border:false,
	            loader : loader,
	            root : root,
	            enableDD : false,
	            rootVisible:false
	        });
	    }

		if(!win){
	    	win = new Ext.Window({
	    		title: '<%=Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG%>',
	        	id:"GYZZ_Window",
	        	width:450,
	       	 	minWidth: 200,
	        	minHeight: 300,
	        	layout: 'fit',
	        	//plain:true,
	        	closeAction:'hide',
	        	bodyStyle:'padding:5px;',
	        	buttonAlign:'center',
	        	items:[nav],
	        	modal:true,
	        	  buttons : [{
	                text:'确定',
	                cls : "x-btn-text-icon",
	            	icon : "netmarkets/images/save.png",
	                handler:function(){
	                	var  arr=nav.getChecked();
	                	var value= "";
	                	var display="";
	                	for(var i=0;i<arr.length;i++){
	                		value = value+arr[i].id+";";
	                		display = display+arr[i].text+";";
	                	}
	                	zpGYZZ(oid,value,display);
	                	var grid1 = Ext.getCmp("ext.casc.workflow.tree.mvc.builder.SetListSignatureBuilder");
	            		var grid2 = Ext.getCmp("set_list_signature2");
	            		var grid3 = Ext.getCmp("set_tree_signature");
	            		if(grid1){
	            			var selModel = grid1.getSelectionModel();
	            			selModel.clearSelections();
	            		}
	            		if(grid2){
	            			var selModel = grid2.getSelectionModel();
	            			selModel.clearSelections();
	            		}
	            		if(grid3){
	            			var selModel = grid3.getSelectionModel();
	            			selModel.clearSelections();
	            		}
	                	win.close();
	                }
	            },{
	                text : '取消',
	                cls : "x-btn-text-icon",
	            	icon : "netmarkets/images/cancel.png",
	                handler:function(){
	                    win.close();
	                }
	            }]

	    	});
	    	}
	    	win.show();
		//window.open("<%=contextPath%>/netmarkets/jsp/ext/workflow/setZPGYZZ.jsp?useJSCA=false&epmoid="+oid+"&oid="+workflowProcessOid,""," top=300, left=600, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no");
}

function zpGYZZ(oid,userOid ,userName){
	var checkboxElement = document.getElementsByName("pjl_selPJLsa1__1");
	var flag = false;
	for ( var i = 0; i < checkboxElement.length; i++) {
		if (checkboxElement[i].checked) {
			flag = true;
			break;
		}
	}
	if(flag){
		for ( var i = 0; i < checkboxElement.length; i++) {
			if (checkboxElement[i].checked) {
				var tempValue = checkboxElement[i];
				var temp = tempValue.value;
				if (temp.indexOf("EPMDocument") > -1|| temp.indexOf("WTDocument") > -1
						|| temp.indexOf("WTChangeOrder2") > -1|| temp.indexOf("ChangeRequest") > -1
						|| temp.indexOf("MPMProcessPlan") > -1|| temp.indexOf("WTPart") > -1) {
					var n = temp.lastIndexOf("$");
					var selectx = temp.substring(n + 4, temp.length - 2);
					if(selectx.indexOf("^VR:") > -1){
						var ojbs = selectx.split("^VR:");
						selectx = ojbs[ojbs.length-1];
					}
					document.getElementById(selectx+"_sign_person").value=userName;
					document.getElementById(selectx+"_sign_person").title=userName;
					document.getElementById(selectx+"_sign_person_value").value=userOid;
				}
			}
		}
	}else{
		document.getElementById(oid+"_sign_person").value=userName;
		document.getElementById(oid+"_sign_person_value").value=userOid;

	}
}

function selectUser2(object, veroid){
	var grid = Ext.getCmp("ext.casc.workflow.tree.mvc.builder.SetListSignatureBuilder");
	if (!grid) {
		grid = Ext.getCmp("set_list_signature2");
	}
	if (!grid) {
		grid = Ext.getCmp("set_tree_signature");
	}

	var selModel = grid.getSelectionModel();
	var selectedRows = selModel.getSelections();
	if (selectedRows.length > 0) {
		for (var i = 0; i < selectedRows.length; i++) {
			//var ss= selectedRows[i].get("partType");
			var temp = selectedRows[i].id;
			if (temp.indexOf("EPMDocument") > -1 || temp.indexOf("WTDocument") > -1 ||
				temp.indexOf("WTChangeOrder2") > -1|| temp.indexOf("ChangeRequest") > -1 ||
				temp.indexOf("MPMProcessPlan") > -1|| temp.indexOf("WTPart") > -1) {

				document.getElementById(temp + "_sign_person_value").value = object.value;
			}
		}
	}

	selModel.clearSelections();
}
function selectUser3(object, veroid){
	var grid = Ext.getCmp("ext.casc.workflow.tree.mvc.builder.SetListSignatureBuilder");
	if (!grid) {
		grid = Ext.getCmp("set_list_signature2");
	}
	if (!grid) {
		grid = Ext.getCmp("set_tree_signature");
	}

	var selModel = grid.getSelectionModel();
	var selectedRows = selModel.getSelections();
	for (var i = 0; i < selectedRows.length; i++) {
		var temp = selectedRows[i].id;
		if (temp.indexOf("EPMDocument") > -1 || temp.indexOf("WTDocument") > -1 ||
		temp.indexOf("WTChangeOrder2") > -1|| temp.indexOf("ChangeRequest") > -1 ||
		temp.indexOf("MPMProcessPlan") > -1|| temp.indexOf("WTPart") > -1) {


			document.getElementById(temp + "_sign_person_value" + (object.id.substring(object.id.indexOf('value')+5))).checked = object.checked;
		}
	}
	selModel.clearSelections();
}


function selectSignResult(object, veroid){
	var grid = Ext.getCmp("ext.casc.workflow.tree.mvc.builder.SetListSignatureBuilder");
	if (!grid) {
		grid = Ext.getCmp("set_list_signature2");
	}
	if (!grid) {
		grid = Ext.getCmp("set_tree_signature");
	}
	var selModel = grid.getSelectionModel();
	var selectedRows = selModel.getSelections();
	if (selectedRows.length > 0) {
		for (var i = 0; i < selectedRows.length; i++) {
			//var ss= selectedRows[i].get("partType");
			var temp = selectedRows[i].id;
			if (temp.indexOf("EPMDocument") > -1 || temp.indexOf("WTDocument") > -1 ||
				temp.indexOf("WTChangeOrder2") > -1 ||
				temp.indexOf("MPMProcessPlan") > -1|| temp.indexOf("WTPart") > -1) {

				document.getElementsByName(temp + "_select")[0].value = object.value;
			}
		}
	}

	//selModel.clearSelections();
}
	</script>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="ext.casc.workflow.tree.resource.asesSignatureResource"/>
<fmt:message var="treeName"    key="PART_TREE_LABEL"/>
<fmt:message var="sign_result"    key="SIGN_RESULT"/>
<fmt:message var="sign_message"    key="SIGN_MESSAGE"/>
<fmt:message var="sign_advise"    key="SIGN_ADVISE"/>
<fmt:message var="neibuhuiqian"    key="NEIBUHUIQIAN"/>
<fmt:message var="waibuhuiqian"    key="WAIBUHUIQIAN"/>
<fmt:message var="neibugongyihuiqian"    key="NEIBUGONGYIHUIQIAN"/>
<fmt:message var="gongyihuiqian"    key="GONGYIHUIQIAN"/>
<fmt:message var="waibugongyihuiqian"    key="WAIBUGONGYIHUIQIAN"/>
<%--<fmt:message var="actionsName" key="ACTIONS_COLUMN_LABEL"/>--%>
   <%-->Build a descriptor and assign it to page variable treeDescriptor<--%>


<div id ="SetListSignatureBuildergrid"></div>

<script>
var onReadyLoad = false;
//Ext.onReady(function(){
function renderNumber(value){
	var splits = value.split("@");
	var num = splits[0];
	var oid = splits[1];
	var url =" app/#ptc1/tcomp/infoPage?oid="+oid

	var value = "<a href=\""+url+"\"  target=_blank>"+num+"</a>";
	return value;
}
 var sm = new Ext.grid.CheckboxSelectionModel({handleMouseDown: Ext.empthFn});

           var cm = new Ext.grid.ColumnModel([
           	sm,
				 	{header:'', dataIndex:'icontype',width:20},
          	{header:'编号', dataIndex:'number', autoWidth:true,renderer:renderNumber},
          	{header:'名称', dataIndex:'name', autoWidth:true},

          	{header:'版本', dataIndex:'version', autoWidth:true},
          	{header:'状态', dataIndex:'state', autoWidth:true},
          	{header:'更新状态', dataIndex:'updatestate', autoWidth:true},
          	{header:'落实意见', dataIndex:'implementadvise', autoWidth:true},
			     {header:'会签结论', dataIndex:'sign_result', autoWidth:true},
				  {header:'会签意见', dataIndex:'sign_advise', autoWidth:true},
				  {header:'工艺检查报告', dataIndex:'check_report', autoWidth:true},
				 <%if (activityName.equals(zpgyzz)) {%>
				  {header:'*主制车间', dataIndex:'zhuzhichejian', autoWidth:true},
				  {header:'辅制车间', dataIndex:'fuzhichejian',width:300},
				  <%}%>

               {header:'设计者', dataIndex:'DESIGNER', autoWidth:true}
          ]);



        var records=new Ext.data.Record.create([ {name:'id'},{name:'icontype'},{name:'number'},{name:'name'},{name:'version'},{name:'state'},{name:'sign_result'},{name:'sign_advise'},{name:'check_report'},{name:'zhuzhichejian'},{name:'fuzhichejian'},{name:'CMAT'}, {name:'CMATUP'},{name:'CMATDOWN'},{name:'CSIZE'},{name:'count'},{name:'DESIGNER'},{name:'PTC_MATERIAL_NAME'}]);
          var store = new Ext.data.Store({
          	 proxy:new Ext.data.HttpProxy(
			   {
					 url:"<%=contextPath%>/netmarkets/jsp/ext/workflow/tree/generateTable.jsp?oid=<%=workflowProcessOid%>",//获取数据的后台地址
					 method:"POST",
				   	 timeout:9000000
			   }),
         //解析json
			   reader:new Ext.data.JsonReader(
			   {
					root:"data",
					id:"id",
					totalProperty:"totalCount"          //总的数据条数
			   },records)
          });

            store.load();
			  store.on('load',function(){
					 SetListSignatureBuilderGrid.loadMask.show();
					 onReadyLoad=true;
			  });
          var SetListSignatureBuilderGrid = new Ext.grid.GridPanel({

				autoHeight:true,
					draggable:true,
				id:'set_list_signature2',//id与以前Builderi的id一致
          	renderTo:'SetListSignatureBuildergrid',
          	store: store,
				title:"签审列表",
          	cm: cm,
			    loadMask :true,
          	sm: sm,
          	tbar:[{
                  text: '<%=tempSave%>',
                  handler:function(){
                      saveWriteInfo();
                  } ,
                  cls : "x-btn-text-icon",
                  icon : "<%=contextPath%>/netmarkets/images/save.png",
                  scope:this
              }]
          });
			SetListSignatureBuilderGrid.loadMask.show();
			function saveWriteInfo() {
				var grid = Ext.getCmp("set_list_signature2");
				if (grid) {
					var params = "";
					for (var i = 0; i < grid.store.data.length; i++) {
						var row = grid.store.data.get(i);
						var dataId = row.id;
						var advise = document.getElementsByName(dataId + "_advise")[0].value;
						var select = document.getElementsByName(dataId + "_select")[0].value;
						params += "id=" + dataId + "&";
						params += "advise=" + advise + "&";
						params += "select=" + select + "@!@";
					}
					var url = "<%=contextPath%>/netmarkets/jsp/ext/workflow/tree/saveActivityRecord.jsp?workItemOid=<%=workflowProcessOid%>";
					url = encodeURI(url);

					Ext.Ajax.request({
						url: url,
						params: {data: params},
						method: "POST",
						success: function (response) {
							Ext.Msg.alert("信息", "数据更新成功！", function() { store.reload(); });
							grid.store.reload();
						},
						failure: function (response) {
							Ext.Msg.alert("警告", "意见保存失败，请稍后再试！", function() { store.reload(); });
						}
					});
				}
		    }
//     });
	 function find2(name) {
		 //alert("find2 name:"+name);
		 //alert("mainform.elements:"+document.mainform.elements);
		 if(document.mainform.elements!=null){
			 for(var i=0;i<document.mainform.elements.length;i++){
			var e = document.mainform.elements[i];
			//alert("e.name:"+e.name);
			if (e.name.indexOf(name)>=0 && e.name.indexOf("old")<0)
				return e;
			}
		 }

		return null;
	}
	 function setReviewData(){
		 if(!onReadyLoad) {
			 alert("数据加载中，请稍等！")
			 return;

		 }
	 	var bhToGYZZEl = document.getElementById("routingChoice_驳回到工艺组长");
		if (bhToGYZZEl && bhToGYZZEl.checked) {
			var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
			completehiddenBtn.click();
			return ;
		}
		var zpGYZZ = "0";//如果是0代表是工艺会签，1代表指派工艺组组长
	 	var flag = true;
	 	var flag1 = true;
   			var signValue = "";
   			var signValue2 = "";
   			var isHQElement = document.getElementsByName("workitem$taskFormTemplate$<%=workflowProcessOid%>$___CustActVarisHuiqianCustActVar___");
   			var isHQ = false;
   			if(isHQElement[0]){
   				isHQ = isHQElement[0].checked;
   			}
   			var isBlankZP = true;
   			for(var i = 0 ; i < oidArray.length ; i++){
   				var tempOid = oidArray[i];
   				var selectElement = document.getElementById(tempOid+"_select");
				if(selectElement==null)  continue;
				//alert(selectElement);
   				var tempSelect = selectElement[selectElement.selectedIndex].value;
   				//alert("tempSelect is: " + tempSelect);
   				if(tempSelect==butongyi){
   					flag = false;
   				}
   				if(tempSelect!=weichuli){
   					flag1 = false;
   				}
   				var inputAdviseElement = document.getElementById(tempOid+"_advise");
				var inputImplementAdviseElement = document.getElementsByName(tempOid+"_implementadvise");
				var inputStateElement = document.getElementsByName(tempOid+"_updatestate");

				//add by hding 2012/11/21
				var zhuzhichejianElement = document.getElementsByName(tempOid
					+ "_sign_person_valueA");
				var fuzhichejianElement = document.getElementsByName(tempOid
					+ "_sign_person_valueB");

	   			var inputPersonsElementVal = "";
				var zhuzhichejian = "";
				var fuzhichejian = "";
				if (zhuzhichejianElement[0]) {
						zpGYZZ = "1";
				}
				for(var j=0;j<zhuzhichejianElement.length;j++){
					if (zhuzhichejianElement[j].value != "") {
						zhuzhichejian = zhuzhichejianElement[j].value+";";
					}
				}
				for(var j=0;j<fuzhichejianElement.length;j++){
					if (fuzhichejianElement[j].checked) {
						fuzhichejian = fuzhichejian + fuzhichejianElement[j].value + ";";
					}
				}
				inputPersonsElementVal = zhuzhichejian+fuzhichejian;

   				var related2DDRWElement = document.getElementsByName(tempOid+ "_related2DDRW");
   				var numberElement =getElementByName(tempOid+ "_number");
				var number = "";
				if(numberElement){
					number = numberElement.value;
				}
				number = "("+number+")";
   				if(isHQ){//组织会签的校验

	   				if(tempSelect!=weichuli&&zhuzhichejian==""){
	   					alert(number+"的主制车间不允许为空");
						inputAdviseElement.focus();
	   					return;
	   				}

					//无需会签 不允许指派
	   				if(tempSelect==weichuli&&(zhuzhichejian!=""||fuzhichejian!="")){
	   					alert(number+tips3);
						inputAdviseElement.focus();
	   					return;
	   				}

					//无需会签 不允许填写会签意见
	   				if(tempSelect==weichuli&&inputAdviseElement.value!=""){
	   					alert(number+"无需会签，不允许填写会签意见");
						inputAdviseElement.focus();
	   					return;
	   				}
					//指派人的不能填写会签意见
   					/*if(inputAdviseElement.value!=""&&inputPersonsElementVal!=""){
	   					alert(number+tips4);
						inputAdviseElement.focus();
	   					return;
	   				}*/
					//如果副制车间中包含了主制车间
					if(fuzhichejian!=""&&zhuzhichejian!=""&&fuzhichejian.indexOf(zhuzhichejian)>-1){
						alert(number+"主制车间和辅制车间不能选择同一车间");
						inputAdviseElement.focus();
						return;
					}
   				}else{//非组织会签校验
   					if(inputPersonsElementVal!=""){
	   					alert(number+tips5);//非组织会签不能够再指派给别人
	   					inputAdviseElement.focus();
	   					return;
	   				}
   				}
   				if (tempSelect != weichuli) {
   					var inputImplementAdviseString = "";
   					if(inputImplementAdviseElement[0]){

   						inputImplementAdviseString = inputImplementAdviseElement[0].value;
   					}
   					var inputStateElementString = "";
   					if(inputStateElement[0]){

   						inputStateElementString = inputStateElement[0].value;
   					}
					signValue = signValue + tempOid + ";;;qqq" + tempSelect + ";;;qqq" + inputAdviseElement.value + ";;;qqq" + inputImplementAdviseString + ";;;qqq" + inputPersonsElementVal + ";;;qqq" +
					inputPersonsElementVal +
					";;;qqq" +
					zhuzhichejian +
					";;;qqq" +
					fuzhichejian +
					";;;qqq" +
					tempOid +
					";;;qqq" +
					inputStateElementString;
				}
   				if(i<oidArray.length-1){
					if (tempSelect != weichuli) {
						signValue += ";;;ppp";
					}
   				}
   			}


   			if(flag1){
   					var allWeichuli = '<%=allWeichuli%>';
					//alert("allWeichuli is: " + allWeichuli);
					var zzhq = find2("isHuiqian");

   					//alert("zzhq is: " + zzhq);
   					if(zzhq!=null && (zzhq && zzhq.checked == false)){
						alert(allWeichuli);
   						return;
   					}
   			}

   			if(!flag){
   				var passRadio = document.getElementById('<%=passRadio%>');
				var rejectRadio = document.getElementById('<%=rejectRadio%>');
				if(passRadio&&rejectRadio){
					rejectRadio.checked=true;
				}
   			}else{
	   			var passRadio = document.getElementById('<%=passRadio%>');
				var rejectRadio = document.getElementById('<%=rejectRadio%>');
				if(passRadio&&rejectRadio){
					passRadio.checked=true;
				}
   			}

   			signValue=encodeURI(signValue);
			signValue=encodeURI(signValue);
			signValue2=encodeURI(signValue2);

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

	xmlHttpRequest.onreadystatechange=function(){
		if(xmlHttpRequest.readyState==4){
			if (xmlHttpRequest.status == 200) {
				var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
				completehiddenBtn.click();
			}else{
				}
		}
	}

			//xmlHttpRequest.open("GET","<%=contextPath%>/netmarkets/jsp/ext/workflow/tree/setSignValue.jsp?value="+signValue+"&oid="+workflowProcessOid,true);
			//xmlHttpRequest.setRequestHeader("If-Modified-Since","0");
			//xmlHttpRequest.send(null);
			xmlHttpRequest.open("POST","<%=contextPath%>/netmarkets/jsp/ext/workflow/tree/setSignImplementValue.jsp",true);
			xmlHttpRequest.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
			xmlHttpRequest.send("value="+signValue+"&oid="+workflowProcessOid+"&zpGYZZ="+zpGYZZ);
   	}

	function replaceReviewCompleteButton() {
			var completeBtn = document.getElementsByName("complete")[0];
    	var completehiddenBtn = document.getElementsByName("completeReviewhidden")[0];
    	if (completeBtn&&completehiddenBtn) {
        //completeBtn.oldOnClick = completeBtn.onclick;
        completehiddenBtn.onclick = completeBtn.onclick;
    		completeBtn.onclick = setReviewData;
    	}
	}
	replaceReviewCompleteButton();
		function hiddenRadio(){
			var passRadio = document.getElementById('<%=passRadio%>');
			var rejectRadio = document.getElementById('<%=rejectRadio%>');
			if(passRadio&&rejectRadio){
				passRadio.style.display="none";
				rejectRadio.style.display="none";
				var labelElements = document.getElementsByTagName("label");
				for(var i = 0 ; i < labelElements.length ; i++){
					if(labelElements[i].innerHTML == '<%=passRadio%>' || labelElements[i].innerHTML == '<%=rejectRadio%>'){
						labelElements[i].style.display = "none";
					}
				}
			}
	}
	hiddenRadio();
		function setConclusion(){
		var checkboxElement = document.getElementsByName("pjl_selPJLsa1__1");
		var conclusion = document.getElementById('editablecolumns');
		var flag = false;
		for(var i = 0 ; i < checkboxElement.length ; i++){
			if(checkboxElement[i].checked){
				flag = true;
				break;
			}
		}
		if(!flag){
			var selectData = '<%=selectData%>';
			alert(selectData);
			return;
		}
		var size = checkboxElement.length;
		//alert(checkboxElement.length);
		for(var i = 0 ; i < checkboxElement.length ; i++){
			//var tempValue = checkboxElement[i].checked;
			//alert(checkboxElement[i].value);
			//alert(checkboxElement[i].checked);
			if(checkboxElement[i].checked){
				var tempValue = checkboxElement[i];
				var temp = tempValue.value;
				if(temp.indexOf("EPMDocument")>-1||temp.indexOf("WTDocument")>-1||temp.indexOf("WTChangeOrder2")>-1||temp.indexOf("MPMProcessPlan")>-1||temp.indexOf("ChangePackaged")>-1||temp.indexOf("ChangeRequest")>-1|| temp.indexOf("WTPart") > -1)
				{
					var n = temp.lastIndexOf("$");
					//alert(n);
					var selectx = temp.substring(n+4,temp.length-2);
					if(selectx.indexOf("^VR:") > -1){
						var ojbs = selectx.split("^VR:");
						selectx = ojbs[ojbs.length-1];
					}
					//alert("selectx is:" + selectx);

					if(selectx.indexOf("ChangePackaged")>-1){
					}else if(selectx.indexOf("ChangeRequest")>-1){
					}else{
						selectx = selectx.replace(":",">");
					}
					//alert("selectx is:" + selectx);
					var selecta = selectx + "_select";
					//alert("selecta is:" + selecta);
					//var selectb = window.opener.document.getElementById(selecta);
					var selectb = document.getElementsByName(selecta);
					//alert("selectb is: " + selectb.length);
					for(var j=0;j<selectb.length;j++){
						var selectc = selectb[j];
						selectc.selectedIndex=conclusion.selectedIndex;
						//alert("selectc.value is:" + selectc.value);
					}
			  }
			}
		}
	}
		var isZhuZhiHuiQianElement = document.getElementsByName("workitem$taskFormTemplate$<%=workflowProcessOid%>$___CustActVarisHuiqianCustActVar___");
		var isXingHaoJieSuanElement = document.getElementsByName("workitem$taskFormTemplate$<%=workflowProcessOid%>$___CustActVarisXingHaoJieSuanHuiQianCustActVar___");
		if(isXingHaoJieSuanElement[0]&&isZhuZhiHuiQianElement[0]){
			isZhuZhiHuiQianElement[0].onclick= function(){
				if(isZhuZhiHuiQianElement[0].checked==true){
						isXingHaoJieSuanElement[0].checked=false;
				}
			}
			isXingHaoJieSuanElement[0].onclick= function(){
				if(isXingHaoJieSuanElement[0].checked==true){
						isZhuZhiHuiQianElement[0].checked=false;
				}
			}
		}
   	</script>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>