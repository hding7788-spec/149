<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ page language="java" pageEncoding="UTF-8"%>

<script>
	function openNewWindow(url)
	{	   
		window.open(url, '', 'height=800,width=350,toolbar=no,menubar=no,resizable=yes,scrollbars=yes,location=no,status=no');
	}
	
	function appendUsers(role, uidArray, uidNameArray) {    
			for(var i=0; i<uidArray.length; i++)
			{   
	 			tryAppendUser(role, uidArray[i], uidNameArray[i]);
	 		}
    }
	
	// Remove selected user
    function removeUser(spanId) {
        var userSpan = document.getElementById(spanId);
        if (userSpan != null) {        	
            userSpan.parentNode.removeChild(userSpan);                      
        }
    }
    
    // find html section for defined role
    function findRoleSection(role) {
        var roleSections = document.getElementsByTagName("div");
        alert(roleSections);
        var section = null;
        for (var i = 0; i < roleSections.length; i++) {
        	alert(roleSections.item(i));
        	alert(roleSections.item(i).getAttribute("name"));
            if (role == roleSections.item(i).getAttribute("name")) {
                section = roleSections.item(i);
                break;
            }
        }

        if (section == null)
            alert("Roles not found <" + role + ">!");

        return section;
    }
    
    // if user found, not add again
    function tryAppendUser(role, userOid, fullName) {        
        var section = findRoleSection(role);
        if (section == null)
            return;
            		        
        var eid = "ROLE." + role + "." + userOid + "." + fullName;
        var userSpan = document.getElementById(eid);
        if (userSpan != null)
            return; //user is there

        // create user span
        userSpan = document.createElement("span");
        userSpan.id = eid;

        // remove userpart
        var userLink = document.createElement("a");
        userLink.href = "javascript:removeUser('" + eid + "')";
        userLink.innerHTML = fullName;
        userLink.title = "Remove User : " + fullName;
		
        //user input
        var userField = document.createElement("input");
        userField.type = "hidden";
        userField.name = "ROLE." + role + "." + userOid;
        userField.value = userOid;

        // construct user span
        userSpan.appendChild(document.createTextNode(" "));
        userSpan.appendChild(userLink);
        userSpan.appendChild(userField);
		
        // add user to role section
        section.appendChild(userSpan);
    }
</script>

<jsp:include page="${mvc:getComponentURL('com.glaway.mpm.mvc.builders.processplan.planUniteTaskAllocateBuilder')}" flush="true" />
<input type="button" name="button1" value="完成派工" onclick="window.close();"/>
<%@include file="/netmarkets/jsp/util/end.jspf"%>