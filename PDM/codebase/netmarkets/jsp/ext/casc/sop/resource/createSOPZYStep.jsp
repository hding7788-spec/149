<%@page language="java" pageEncoding="GBK" contentType="text/html; charset=GBK"%>
<%@page import="com.glaway.mpm.constants.TypeNameConstants"%>
<%@page import="com.glaway.mpm.util.TypeUtil"%>
<%@page import="com.ptc.core.meta.common.TypeIdentifier"%>
<%@page import="wt.type.TypedUtility"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Locale"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>

<jca:initializeItem operation="CREATE" baseTypeName="com.ptc.windchill.mpml.resource.MPMResource" />

<script type="text/javascript">
PTC.navigation.loadScript("netmarkets/javascript/casc/jquery16.js");
var $j = jQuery.noConflict();
</script>

<%
	String path = request.getContextPath();
	String mpmResourceType=request.getParameter("mpmResourceType");
	String topTypeName = TypeNameConstants.getTypeName(mpmResourceType);
	List<TypeIdentifier> list = TypeUtil.getChildTypes(topTypeName);
	Boolean flag=TypeUtil.IsYouxiao();

	if(list!=null&&list.size()!=0){
%>

<div id="topTypeDiv">
	<table class="pp" id="tbl_big">
		<tbody>
			<tr class="separator">
				<td colSpan="2">
					<div class="blank"></div>
				</td>
			</tr>
			<tr>
				<td class="pplabel">
					<span class="requiredfield">*</span>
					<label>
						大类
					</label>
				</td>
				<td class="ppdata" colSpan="1">
					<select id="topType" name="topType" onchange="changeTopType()"
						class="required accessibleChange ">
						<option value="">
							--选择大类--
						</option>
						<%  for(TypeIdentifier identifier : list){%>
						<option value="<%= identifier.getTypename()%>"><%=TypedUtility.getLocalizedTypeName(identifier,Locale.CHINA) %></option>
						<% }%>
					</select>
				</td>
			</tr>
		</tbody>
	</table>
</div>

<div id="smallTypeDiv">
	<table class="pp" id="tbl_small">
		<tbody>
			<tr class="separator">
				<td colSpan="2">
					<div class="blank"></div>
				</td>
			</tr>
			<tr>
				<td class="pplabel">
					<span class="requiredfield">*</span>
					<label>
						小类
					</label>
				</td>
				<td class="ppdata" colSpan="1">
					<select id="smallType" name="smallType"
						onchange="changeSmallType()" class="required accessibleChange ">
						<option value="">
							--选择小类--
						</option>
					</select>
				</td>
			</tr>
		</tbody>
	</table>
</div>

<div id="tableDiv">

</div>
<div id="loading" align="center"
	style="position: absolute; top: 0px; left: 0px; right: 0px; buttom: 0px; width: 100%; height: 100%; z-index: 100; visibility: hidden; background-color: white; filter: alpha(opacity =         80);">
	<img src="netmarkets/images/glaway/mpm/loading.gif"
		style="position: absolute; top: 45%; left: 45%;">

</div>
<%} else{%>
<jsp:include
	page="${mvc:getComponentURL('ext.casc.sop.mvc.builders.CreateSOPZYBuilder')}"
	flush="true" />
<%} %>

<input type="hidden" id="typeName" name="typeName" />

<script type="text/javascript">
    $j("#typeName").val('<%=topTypeName%>');
	function changeTopType() {
	    $j("#typeName").val($j("#topType").val());
	    $j("#loading").css("visibility","visible");
	    $j.ajax({
                  type:    "POST",
                  url:     "netmarkets/jsp/ext/glaway/mpm/mpmresource/getSmallType.jsp",
                  data:    "typeName="+ $j("#topType").val(),
                  success: function(msg){
                               msg=msg.trim();
                               if("{}"==msg){
                                   $j("#smallType").attr("class","");
                                   $j("#smallTypeDiv").hide();
                               	   getAttributePanel($j("#topType").val());
                               }else{
                                   $j("#smallType").attr("class","required accessibleChange ");
                                   $j("#smallTypeDiv").show();
                                   deleteTypeOptions($j("#smallType option"));
                                   addTypeOptions($j("#smallType"),msg);
                                   getAttributePanel($j("#smallType").val());
                               }
                           }
        });
	}

	function changeSmallType(){
	    $j("#typeName").val($j("#smallType").val());
	    $j("#loading").css("visibility","visible");
	    getAttributePanel($j("#smallType").val());
	}

	function getAttributePanel(typeName) {
		$j("#tableDiv").load("netmarkets/jsp/ext/glaway/mpm/mpmresource/getAttributePanel.jsp", {mpmResourceType:'<%=mpmResourceType%>', typeName:  typeName}, function (responseText, textStatus, XMLHttpRequest){
              $j("#loading").css("visibility","hidden");
		});
	}

	function deleteTypeOptions(typeOptions){
	      for(var i = 1; i < typeOptions.length; i++){
              typeOptions.get(i).remove();
          }
	}

	function addTypeOptions(typeSelect,typesString){
	     var typeArray=typesString.substring(1,typesString.length-1).split(",");
	     for(var i = 0; i < typeArray.length; i++){
              var str=typeArray[i].split("=");
              typeSelect.append("<option value='"+str[0]+"'>"+str[1]+"</option>");
         }
	}

	function setSelZhiZaoDanWei(obj){
		var obj = new Object();
		var path = "<%=path%>/netmarkets/jsp/ext/glaway/mpm/mpmresource/selectedZhiZaoDanWei.jsp";
		var ret = window.showModalDialog(path,obj,"dialogWidth=400px;dialogHeight=400px;resizable=yes;");
		if(ret != null) {
			document.getElementById("zhizaodanwei").value=ret;
		}
	}

	  /**
     * 选择专业类别：根据专业类别加载工序名称
     *
     */
    function selectSpecializedType() {
	    $j.ajax({
                  type:    "POST",
                  url:     "netmarkets/jsp/ext/casc/sop/resource/getProceduce.jsp",
                  data:    "specializedType="+ $j("#SpecializedType").val(),
                  success: function(msg){
                               msg=msg.trim();
                               /**清空下拉框值*/
                               deleteTypeOptions('ProceduceName');
                               /**添加新的值*/
                               addTypeOptions(msg);
                           }
		 });



	    $j.ajax({
            type:    "POST",
            url:     "netmarkets/jsp/ext/casc/sop/part/getParametersName.jsp",
            data:    "specializedType="+ $j("#SpecializedType").val(),
            success: function(msg){
                         msg=msg.trim();
                         /**清空下拉框值*/
                         deleteTypeOptions('ParametersName');
                         /**添加新的值*/
                         addTypeOptions2(msg,'ParametersName');
                     }
	 	});

	    $j.ajax({
            type:    "POST",
            url:     "netmarkets/jsp/ext/casc/sop/part/getMaterialCategory.jsp",
            data:    "specializedType="+ $j("#SpecializedType").val(),
            success: function(msg){
                         msg=msg.trim();
                         /**清空下拉框值*/
                         deleteTypeOptions('MaterialCategory');
                         /**添加新的值*/
                         addTypeOptions2(msg,'MaterialCategory');
                     }
	 	});


    }

    function deleteTypeOptions(typeString){
        var obj=document.getElementById(typeString);
        obj.options.length=0;
    }


    function addTypeOptions(typesString){
        var obj=document.getElementById('ProceduceName');
        var typeArray=typesString.substring(1,typesString.length-1).split(",");
        obj.options.add(new Option("",""));
        for(var i = 0; i < typeArray.length; i++){
            // var str=typeArray[i].split("=");
            // obj.options.add(new Option(str[0],str[0])); //这个兼容IE与firefox
            var str=typeArray[i].split("=");
            var keyStr = str[0].trim();
            var valueStr = str[1].trim();
            if(keyStr == "sopSeqNumber"){
                // document.getElementById("shunxuhao").value = valueStr;
            }else{
                obj.options.add(new Option(keyStr,valueStr)); //这个兼容IE与firefox
            }
        }
    }

    function addTypeOptions2(typesString,type){
        var obj=document.getElementById(type);
        var typeArray=typesString.substring(1,typesString.length-1).split(",");
        obj.options.add(new Option("",""));
        for(var i = 0; i < typeArray.length; i++){
            var str=typeArray[i].split("=");
            obj.options.add(new Option(str[0],str[1])); //这个兼容IE与firefox
        }
    }

    function getNextNumber(){
        $j.ajax({
            type:    "POST",
            url:     "netmarkets/jsp/ext/casc/sop/resource/getSopSeqNumber.jsp",
            data:    "mpmresourceType=<%=mpmResourceType%>",
            success: function(msg){
                msg=msg.trim();
                alert(msg);
                document.getElementById("number").value = msg;
            }
        });
    }

</script>


