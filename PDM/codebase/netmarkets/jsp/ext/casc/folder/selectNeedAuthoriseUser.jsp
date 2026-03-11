<%@page language="java" session="true" pageEncoding="GBK"%>
<%@page import="wt.pdmlink.PDMLinkProduct" %>
<%@page import="wt.fc.Persistable"%>
<%@page import="java.util.List"%>
<%@page import="ext.casc.folder.AuthoriserPermissionUtil"%>
<%@page import="java.util.Set"%>
<%@page import="com.ptc.netmarkets.model.NmOid"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.util.WTContext"%>
<%@page import="java.util.Locale"%>


<%@ page import="com.ptc.netmarkets.util.beans.NmCommandBean,

                 com.ptc.netmarkets.util.misc.NmAction,

                 com.ptc.netmarkets.util.misc.NmHTMLActionModel,

                 com.ptc.netmarkets.util.misc.NmActionServiceHelper,

                 com.ptc.netmarkets.util.misc.NmContext,

                 com.ptc.netmarkets.util.misc.NmContextItem,

                 com.ptc.netmarkets.user.NmUser,

                 com.ptc.netmarkets.work.NmWorkItemCommands,

                 com.ptc.netmarkets.work.workResource,

                 java.util.HashMap,

                 java.util.Iterator,

                 java.util.List,

				 java.util.ArrayList,

                 java.util.ResourceBundle,

                 com.ptc.netmarkets.model.NmOid,

                 java.util.Enumeration,

                 wt.workflow.work.WorkItem,

                 wt.fc.WTObject,

                 wt.inf.container.WTContainer,

                 wt.inf.container.WTContained,

				 wt.fc.Persistable"

%>


<%@include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags" %>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>

<%

    Locale locale = WTContext.getContext().getLocale();
    String contextPath = request.getContextPath();
    WTContainer product = null;
    NmOid nmoid = commandBean.getActionOid();
    if(nmoid!=null){
    	product =  nmoid.getContainerObject();
    }

    ArrayList<NmOid> oids = commandBean.getActionOidsWithWizard();
    NmOid workItemOid=oids.get(0);
    List<String> selectedOids = new ArrayList<String>();
    for(int i=0;i<oids.size();i++){
    	selectedOids.add(oids.get(i).getReferenceString());
    }
    //List<Persistable> ps = AuthoriserPermissionUtil.getALLSelectedObjs(selectedOids);
    //Set<String> usersStr = AuthoriserPermissionUtil.getAllSelectedTeamMembers(product,ps);
    Set<String> usersStr = new java.util.HashSet<String>();

%>
<c:set var="usersStr" value="<%=usersStr%>"></c:set>
<c:set var="oids" value="<%=oids%>"></c:set>
<script>

</script>

 <table>
    <tr>
       <td >* 选择接收者</td>
       <td>
          <input type="hidden" name="principalObjId" value="" id="principalObjId"/>
         <!--wctags:userPicker id="pickUser" label="" pickerTitle="Picker User"  pickerCallback="clientPickerCallback" pickerType="picker" readOnlyPickerTextBox="true"/-->
        <select name="userName" class="required"  id="userName">
	     <option value=""></option>
	     <c:forEach var="userStr" items="${usersStr}">
	         <option value="${userStr}">${userStr}</option>
	     </c:forEach>
	    </select>
	    <!--  add by machongqi 2015-7-27-->
	<%

					 // The search button is only available if all selected items are WorkItems.

					 /*spr:1516427: When attempting to reassign more than 6 tasks, the find button doesn't appear to allow searching for the new 	assignee*/

					 	NmHTMLActionModel am = NmActionServiceHelper.service.getActionModel("reassign search action", workItemOid);

						List actions = am.getActions();

						NmAction reassignSearchUserAction = null;

						for (Iterator i = actions.iterator(); i.hasNext();) {

							NmAction next = (NmAction)i.next();

							if ( next.getAction().equals("reassignSearchUser") ) {

								reassignSearchUserAction = next;

								break;

							}

						}

						reassignSearchUserAction.setButton(true);

						reassignSearchUserAction.setEnabled(true);

						objectBean.setObject(reassignSearchUserAction);

						%>

						<jsp:include page="/netmarkets/jsp/util/object.jsp" flush="true"/>

						<%

						objectBean.setObject(null);

				  %>




			<!--  add by machongqi end-->

	 </td>

    </tr>
    <tr>
       <td>接收提示</td>
       <td ><textarea rows="5" cols="30" name="authorisComment"></textarea><td>
    </tr>
 </table>

 <script type="text/javascript">
 function reassignUserCallback(objects) {

		// The search picker used for the reassign task wizard is

		// single select so expect a single JSON object.

		var oid = objects.pickedObject[0].oid;

		var userName = objects.pickedObject[0].name;

		var displayName = objects.pickedObject[0].fullName;

		var principalObjectOid = objects.pickedObject[0].principalObjId;



		//alert("username: " + userName);

		//alert("displayName: " + displayName.length);

		// This is here because the user picker is currently not sending back the fullName.

		// If the fullName length is 0 then we'll fall back to displaying the username.

		if ( displayName.length == 0 ) {

			selectUser(principalObjectOid, userName);

		}else {

			selectUser(principalObjectOid, displayName);

		}

	}



	function selectUser(userName, displayName) {

		var userList = document.forms.mainform.userName;

		var newOption = new Option(displayName,userName, false, true);

		var size = userList.length;

		var newList = new Array(size);

		var existingOption;



		// Save the existing options in an array.

		for (var i = 0; i < size; i++) {

			existingOption = userList.options[i];

			if ( userList.options[i] ) {

				if ( userList.options[i].value == userName ) {

					// If the user is already in the list, select the user and just return

					userList.options[i].selected = true;

					return;

				}else {

					//alert (userList.options[i].text + " " + userList.options[i].value + " " + userList.options[i].defaultSelected + " " + userList.options[i].selected);

					newList[i] = new Option(userList.options[i].text, userList.options[i].value, false, false);

				}

			}

		}



		// Empty the select list

		userList.options.length=0;



		// Add the new option to the top of the select element.

		userList.options[0]=newOption;



		// Slide the options up an index and add them to the select element

		for (var j = 0; j < newList.length; j++) {

			//alert (newList[j].text + " " + newList[j].value + " " + newList[j].defaultSelected + " " + newList[j].selected);

			userList.options[j+1]=newList[j];

		}



	}


</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>