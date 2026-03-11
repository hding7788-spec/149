<%@page import="com.glaway.mpm.mpmresource.TypeNameConstants"%>
<%@page import="wt.type.TypedUtility"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="com.glaway.mpm.util.TypeUtil"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@page language="java" pageEncoding="GBK" contentType="text/html; charset=GBK"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc"         prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers"    prefix="w"%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components"
	prefix="jca"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<script type="text/javascript">
PTC.navigation.loadScript("netmarkets/javascript/casc/jquery16.js");
var $j = jQuery.noConflict();
</script>

<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request"/>
<%Boolean flag=TypeUtil.IsYouxiao();
NmCommandBean cb2 = new NmCommandBean();
cb2.setCompContext(nmcontext.getContext().toString());
cb2.setRequest(request);
Boolean leixing=false;
Persistable obj = (Persistable)cb2.getPageOid().getRefObject();
if(obj instanceof WTPart){
    WTPart part =(WTPart)obj;
    String typeName1 = TypedUtility.getTypeIdentifier(part).getTypename();
	if (!typeName1.equals(TypeNameConstants.DMSB) && !typeName1.equals(TypeNameConstants.ZYLB) && !typeName1.equals(TypeNameConstants.CZGW) && !typeName1.equals(TypeNameConstants.WZLB) && !typeName1.equals(TypeNameConstants.DZQY) && !typeName1.equals(TypeNameConstants.CSXM) && !typeName1.equals(TypeNameConstants.CSXMMC)) {
	    leixing=true;
	}
}
 if(flag&&leixing){
     out.println(com.glaway.mpm.constants.TypeNameConstants.errorMessage);
}else{
%>

<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.mpmresource.EditMPMResourceBuilder')}" flush="true" />


<w:checkBox name="idCheckIn" id="idCheckIn" checked="true"/>  是否检入
<%}%>

<script type="text/javascript">


/**
* 选择专业类别：根据专业类别加载工序名称
*
*/
function selectSpecializedType() {
  $j.ajax({
            type:    "POST",
            url:     "netmarkets/jsp/ext/casc/sop/part/getProcedure.jsp",
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
      var str=typeArray[i].split("=");
      obj.options.add(new Option(str[0],str[0])); //这个兼容IE与firefox
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




</script>
