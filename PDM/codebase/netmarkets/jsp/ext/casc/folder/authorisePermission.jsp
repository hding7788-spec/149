<%@page language="java"  pageEncoding="GBK"%>
<%@page import="java.util.Map"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<jca:initializeItem operation="${createBean.create}"/>
<jca:wizard title="动态授权" buttonList="DefaultWizardButtonsNoApply">
    <jca:wizardStep type="custom" action="selectNeedAuthoriseUser" label="选择接收者"></jca:wizardStep>
</jca:wizard>

<!--script>
  function user_validate(){
	    
	 if(confirm("are you confirm create?")){
		 return true; 
	 }else{
		 return false;
	 }
  }
  
  setUserSubmitFunction(user_validate);

</script-->

<%@ include file="/netmarkets/jsp/util/end.jspf"%>