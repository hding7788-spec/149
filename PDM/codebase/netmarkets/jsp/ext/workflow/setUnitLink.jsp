<%@ page import="java.util.*"%>
<%@ page import="com.ptc.netmarkets.util.table.NmHTMLTable,com.ptc.netmarkets.util.table.NmDefaultHTMLTable,com.ptc.netmarkets.util.misc.NmTableRenderer"%>
<%@ page import="ext.casc.util.AssignmentHelper"%>
<%@ page import="ext.casc.constants.Constants"%>
<%@ include file="/netmarkets/jsp/util/beginPopup.jspf"%>

<%

		List<Map<String,String>> releaseConfigList = AssignmentHelper.getElecConfig();
		List<String> unitList =  new ArrayList<String>();
		NmHTMLTable table =  null;
		table = AssignmentHelper.getConfigReleaseDocTable(releaseConfigList, unitList);
		modelBean.setModel(table);
		modelBean.setSingleSelect(false);
		NmTableRenderer.draw( modelBean, objectBean, sessionBean, localeBean, urlFactoryBean,actionBean, stringBean, linkBean, nmcontext, checkBoxBean,
		textBoxBean,radioButtonBean, textAreaBean, comboBoxBean, dateBean, out, request, response);
		modelBean.setModel(null);
      }
    }
    catch (Throwable t) {
		%><tags:renderError throwable="<%=t%>" pageModel="${jcaPageModel}"/><%
    }
    finally {

    }
}%>
<%

String buttonValue = new String("确定");
buttonValue  = new String(buttonValue.getBytes("iso-8859-1"),"UTF-8");

%>
<div >
<table width="80%">
<tr>
<td>
&nbsp;
</td>
<td align="center">
<input type="button" value="<%=buttonValue%>" onclick="selectDepartMent()">
</td>
</table>
</div>
<script>
function selectDepartMent(){

	var departCheckElement = document.getElementsByName("departCheck");
		for(var i = 0 ; i < departCheckElement.length ; i++){
			if(departCheckElement[i].checked){
				var tempDepartmentValue = departCheckElement[i].value;
				opener.document.getElementById("CustActVarselectUnitValueCustActVar").value = tempDepartmentValue;
				if(tempDepartmentValue!=""){
					var waibuhuiqian_div = opener.document.getElementsByName("<%=Constants.ROLE_WAIBUHUIQIANZHE%>");
					if(waibuhuiqian_div[0]){
						while(waibuhuiqian_div[0].lastChild) //
						{
							waibuhuiqian_div[0].removeChild(waibuhuiqian_div[0].lastChild);
						}
					}
				}
			}
		}
		window.close();
}
</script>