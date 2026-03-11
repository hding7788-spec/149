<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="comp"
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core"            prefix="c"
%><%@ taglib uri="http://www.ptc.com/windchill/taglib/workItem" prefix="workItem"
%><%@ taglib tagdir="/WEB-INF/tags"                             prefix="tags" 
%><%@ taglib uri="http://www.ptc.com/windchill/taglib/util" prefix="util" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@tag import="wt.httpgw.URLFactory,
               wt.workflow.work.WorkItem,
               java.util.ResourceBundle,
               wt.workflow.worklist.worklistResource,
               wt.workflow.work.WorkItem,
               wt.workflow.engine.WfActivity,
               wt.workflow.engine.WfProcess,
               wt.util.WTProperties,
               wt.taglib.util.PluginTagGenerator,
               wt.util.WTException,
               com.ptc.windchill.enterprise.workitem.WorkItemCommands,
               wt.fc.ReferenceFactory,
               wt.fc.WTReference,
               wt.fc.Persistable,
               com.ptc.netmarkets.model.NmOid,
               com.ptc.netmarkets.util.misc.NetmarketURL,
               com.ptc.netmarkets.model.NmOid,
               javax.servlet.http.HttpServletRequest,
               java.util.Locale,
               java.util.Properties,
               java.io.File"%>

<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean"  scope="request"/>
<jsp:useBean id="urlFactoryBean" class="com.ptc.netmarkets.util.beans.NmURLFactoryBean"  scope="request"/>
<jsp:useBean id="localeBean" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>

<%!
   private static final String RESOURCE = "wt.workflow.worklist.worklistResource";  
%>

<INPUT TYPE="hidden" name="setUpParticipantexpanded" id="setUpParticipantexpanded" value = "false"/>
<%
ResourceBundle worklistRb = ResourceBundle.getBundle(RESOURCE, localeBean.getLocale());

WTProperties properties = WTProperties.getLocalProperties ();
			  
boolean setupParticipantAppletStyle = properties.getProperty ("wt.clients.workflow.SetUpParticipantsAppletDisplay", false);
%>

<workItem:RenderSetUpParticipants/>

   <c:if test="${SetUpParticipantsDisplayType == 'Table' || SetUpParticipantsDisplayType == 'Link' }"> 
    
    <%if(request.getParameter("showComponent") == null) { %> 
	    <td align="right" valign="top" nowrap="true">
	        <b>          
	                  <!--<%= worklistRb.getString(worklistResource.PARTICIPANTS) %>-->
	        </b> 
	    </td>
    <%} %>

    <c:choose>
       <c:when test="${SetUpParticipantsDisplayType == 'Table'}">
          <td valign="top">
	  <script> document.getElementById("setUpParticipantexpanded").value="true"; </script>
          <%
                  if(setupParticipantAppletStyle)
		  {
		    
		    //Get the workitem
                    try
		    {
			NmOid pageOid = commandBean.getPageOid();
			if (pageOid != null && pageOid.isA(WorkItem.class))
			{
                  	  	
			  ReferenceFactory referenceFactory = new ReferenceFactory();
			  WTReference wtReference = referenceFactory.getReference(pageOid.getOid().toString());
			  WorkItem workItem = (WorkItem)wtReference.getObject();
			  String woid = new ReferenceFactory ().getReferenceString((Persistable)workItem);
			  
			  //Get the workitem's Activity
			  WfActivity activity = (WfActivity)workItem.getSource().getObject();			  
			  
			  //Get the Codebase
			  String CODEBASE = properties.getProperty ("wt.server.codebase", "");

			  //Get it's process
			  WorkItemCommands command = new WorkItemCommands();
			  WfProcess process = command.getProcess(activity);//.getParentProcess();			  
			  
			  //Get the locale
			  Locale locale = localeBean.getLocale();
			  String oid = new ReferenceFactory ().getReferenceString((Persistable)process);
	                  %>
	
			<util:plugin	code = "wt/clients/workflow/tasks/AugmentWfProcessRoles.class"
					codebase="<%=CODEBASE%>"
					archive="wt/security/security.jar"
					width="750" height="400">
				<util:params>
					<util:param name="cabinets" value="wt/security/security.cab" />
					<util:param name="cache_option" value="Plugin" />
					<util:param name="cache_archive" value="wtWork.jar"/>
					<util:param name="wt.context.locale" value="<%=locale.toString()%>" />
					<util:param name="oid" value="<%=oid%>"/>
					<util:param name="woid" value="<%=woid%>"/>
				</util:params>
			</util:plugin>			
			
	                <%
	
			}
		   }
		   catch(WTException e)
		   {
		   	e.printStackTrace();
		   }

		  }
		  else
		  {
	          %>
			  <c:if test="${workItemOid != null}">
				<jsp:include page="${mvc:getComponentURL('workflow.setupParticipantsComponent')}" flush="true" >
			    	<jsp:param name="workItemOid" value="${workItemOid}"/>
			    </jsp:include>
			 </c:if>
	          <%
		  }
          %>         
         </td>
       </c:when>
       
       <c:when test="${SetUpParticipantsDisplayType == 'Link'}">        
                     
           <td valign="top">
			<%         
			try
			{
				NmOid workItemOid = commandBean.getPageOid();  // get workitem oid
				if (workItemOid != null && workItemOid.isA(WorkItem.class)) {
			%>
                <!-- <Div ID="setupParticipantIcon">		    
                    <A href="#" onClick="displaySetupParticipantTable();">
                      <IMG SRC="netmarkets/images/participants_edit.gif"
                           hspace=0 vspace=0 border=0 alt="<%= worklistRb.getString(worklistResource.PARTICIPANTS_LABEL) %>"> 
                    </A>
                </Div> -->
                 
			 <%
			 // pre-load setup patricipant table and display it when user clicks on icon
			 %>
                 <Div ID="table__workflow.setupParticipantsComponent_TABLE" class="frame_outer" style="display: none;">
			 <%
                 if(setupParticipantAppletStyle) 
                 {
                    //Get the workitem
			        try
				    {
						NmOid pageOid = commandBean.getPageOid();
						if (pageOid != null && pageOid.isA(WorkItem.class))
						{
			                  	  	
						  ReferenceFactory referenceFactory = new ReferenceFactory();
						  WTReference wtReference = referenceFactory.getReference(pageOid.getOid().toString());
						  WorkItem workItem = (WorkItem)wtReference.getObject();
						  String woid = new ReferenceFactory ().getReferenceString((Persistable)workItem);
						  
						  //Get the workitem's Activity
						  WfActivity activity = (WfActivity)workItem.getSource().getObject();			  
						  
						  //Get the Codebase
						  String CODEBASE = properties.getProperty ("wt.server.codebase", "");
			
						  //Get it's process
						  WorkItemCommands command = new WorkItemCommands();
						  WfProcess process = command.getProcess(activity);//.getParentProcess();			  
						  
						  //Get the locale
						  Locale locale = localeBean.getLocale();
						  String oid = new ReferenceFactory ().getReferenceString((Persistable)process);
						  %>
	
							<util:plugin	code = "wt/clients/workflow/tasks/AugmentWfProcessRoles.class"
									codebase="<%=CODEBASE%>"
									archive="wt/security/security.jar"
									width="750" height="400">
								<util:params>
									<util:param name="cabinets" value="wt/security/security.cab" />
									<util:param name="cache_option" value="Plugin" />
									<util:param name="cache_archive" value="wtWork.jar"/>
									<util:param name="wt.context.locale" value="<%=locale.toString()%>" />
									<util:param name="oid" value="<%=oid%>"/>
									<util:param name="woid" value="<%=woid%>"/>
								</util:params>
							</util:plugin>			
			
						  <%
						 }
				   }
				   catch(WTException e)
				   {
					   e.printStackTrace();
				   }
			  }
			  else
			  {
		 		  %>
				  <c:if test="${workItemOid != null}">
					<jsp:include page="${mvc:getComponentURL('workflow.setupParticipantsComponent')}" flush="true" >
				    	<jsp:param name="workItemOid" value="${workItemOid}"/>
				    </jsp:include>
				  </c:if>
		          <%
     		  }
                 %>         
                 </Div>                   
                   

		   <script type="text/javascript" language="javascript">

		    function displaySetupParticipantTable()	{
		    	thisDiv = document.getElementById('table__workflow.setupParticipantsComponent_TABLE');
		    	if (thisDiv)		{
		    		if (thisDiv.style.display == "none")	{
		    			thisDiv.style.display = "block";
		    			setupParticipantIcon = document.getElementById('setupParticipantIcon');
		    			if (setupParticipantIcon)	{
		    				setupParticipantIcon.style.display = "none";
		    			}
		    		}
		    	}
		    	PTC.jca.table.scroll.updateTableSize('workflow.setupParticipantsComponent');
		    }

		   
			var divId = "workitem_comment";
			var groupCache = {};
			function elementsById(){
			 var id = divId;
			  if(!groupCache[id]){
			    groupCache[id] = [];
			  }
			  var nodes = groupCache[id];
			  for(var x=0; x<nodes .length; x++){
			    if(nodes[x].id != ""){
			      nodes.splice(x, 1);
			      x--;
			    }
			  }
			  var tmpNode = document.getElementById(id);
			  while(tmpNode){
			    nodes.push(tmpNode);
			    tmpNode.id = "";
			    tmpNode = document.getElementById(id);
			  }

			  return nodes;
			}
			
			var existingComments = "";
			var newComments = "";
			
			function setComments(){
				var wiComments = elementsById();
				for(var i = 0; i < wiComments.length; i++){
					if(wiComments[i].name!=null && wiComments[i].name.indexOf("___comments___textarea")>0){
						newComments = wiComments[i].value;
					}
				}
			}
			
			function checkComments(){
				var wiComments = document.getElementById("workitem_comment___old");
				if(wiComments!=null){
					existingComments = wiComments.value;
				}
				setComments();
				if(existingComments != newComments) {
					return (!confirm("<%= worklistRb.getString(worklistResource.UNSAVED_CHANGES_WARNING) %>"));
				}
				
				return true;
			}

		</script>
 
             <%
             }
          } 
	  catch (Exception e) 
	  {
             e.printStackTrace(); 
          }
          %>
          
          </td>
       </c:when>
       
       <c:otherwise>
       </c:otherwise>
    
    </c:choose>
</c:if>    