<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="ext.casc.constants.Constants"%>
<%@page import="ext.casc.process.ProcessConstants"%>
<%@page language="java" session="true" pageEncoding="GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<jca:tabToHighlight actionName="propertyPanel" objectType="object" />
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/standardAttributeConfigs.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/carambola" prefix="cmb"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags"%>

<%@page import="wt.util.WTProperties"%>
<%@page import="java.util.List,ext.casc.process.ProcessTaskItem,com.ptc.netmarkets.model.NmOid"%>

<%
String workflowProcessOid = "";
String msg = ProcessConstants.JSP_ACTIONS_ZHIPAI_FAILED;
List list = commandBean.getSelectedOidForPopup();
//out.println("----------list:"+list);
ReferenceFactory rf = new ReferenceFactory();
boolean flag = false;
String same = "ok";
if (list != null) {
    String taskName = "";
    String taskState = "";
    for (Object object : list) {
        if (object instanceof NmOid) {
			//out.println("----------object:"+object);
            NmOid oid = (NmOid) object;
            ProcessTaskItem taskItem = (ProcessTaskItem) oid.getRefObject();
            workflowProcessOid = rf.getReferenceString(taskItem);
            taskName = taskItem.getTaskItemName();
            taskState = taskItem.getTaskItemState();
            //out.println("----------taskName:"+taskName);
            if(!ProcessConstants.TASK_NAME_ZHIPAIGONGYIYUAN.equals(taskName)
                    &&!ProcessConstants.TASK_NAME_ZHIPAIGONGYIYUAN_JUJUE.equals(taskName)
                    &&!ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWUZHIPAI.equals(taskName)
                    &&!ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWUZHIPAI_JUJUE.equals(taskName)
                    &&!ProcessConstants.TASK_NAME_LINSHIGONGYIRENWUZHIPAI.equals(taskName)
                    &&!ProcessConstants.TASK_NAME_LINSHIGONGYIRENWUZHIPAI_JUJUE.equals(taskName)
                    &&!ProcessConstants.TASK_NAME_LINGBUJIANGONGYIRENWUZHIPAI.equals(taskName)
                    &&!ProcessConstants.TASK_NAME_LINGBUJIANGONGYIRENWUZHIPAI_JUJUE.equals(taskName)){
                flag = true;
				same = "no";
            }
            if(!ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN.equals(taskState)){
                flag = true;
				same = "no";
				msg = ProcessConstants.JSP_ACTIONS_ZHIPAI_FAILED2;
            }
        }
    }
}
if(!flag){
%>
<jsp:include page="${mvc:getComponentURL('ext.casc.process.mvc.builder.ZhiPaiGongYiYuanBuilder')}" />
<%}%>

<input type="hidden" name="same" id="same" value="<%=same%>" >

<script language="javascript">
	function validateSelectedTaskItem(){
		var same = document.getElementById("same").value;
		//alert("same:"+same);
		if(same=="no"){
			alert("<%=msg%>");
			window.close();
		}
	}

	var workflowProcessOid='<%=workflowProcessOid%>';
	function showHQRY(oid){
		var win;
		var nav;
	    var loader;
	    var root;
	    var categoryid;
	    if(!loader){
	        loader = new Ext.tree.TreeLoader({
	            url :  'netmarkets/jsp/ext/workflow/loadGYYData.jsp?type=2&workOid='+workflowProcessOid
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
	            text :'<%=Constants.ACTIVITYNAME_ZPGYY%>',
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
	    		title: '<%=Constants.ACTIVITYNAME_ZPGYY%>',
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
	                text:'<%=Constants.JSP_DISPLAY_OK%>',
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
	                	var grid1 = Ext.getCmp("ext.casc.process.mvc.builder.ZhiPaiGongYiYuanBuilder");
	            		if(grid1){
	            			var selModel = grid1.getSelectionModel();
	            			selModel.clearSelections();
	            		}
	                	win.close();
	                }
	            },{
	                text : '<%=Constants.JSP_DISPLAY_CANCEL%>',
	                cls : "x-btn-text-icon",
	            	icon : "netmarkets/images/cancel.png",
	                handler:function(){
	                    win.close();
	                }
	            }]

	    	});
	    	}
	    	win.show();
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
					if (temp.indexOf("ProcessTaskItem") > -1) {
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
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>