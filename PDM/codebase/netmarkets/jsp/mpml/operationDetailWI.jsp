<%@include file="imports.jspf"%>
<%
IeService ieObj = (IeService) request.getAttribute("ieObjFromCore");
String globalHRef = (String)request.getAttribute("globalHRef");
String pendingChangeToolTip= (String)request.getAttribute("pendingChangeToolTipFromCore");
%>
<TABLE  width="100%" cellpadding=2>
    <!-- OPERATION LONG DESCRIPTION -->
    <!--DOCUMENTS -->
    <TR><TD>
        <TABLE  width="100%" >
                <TR>
                    <!--DOCUMENT NAME-->
                    <TD class="tdOpDetHTableHeader" width="49%"><fmt:message key="mpml.operationDetailWorkInstruction.docName"/></TD>
                    <!--NUMBER-->
                    <TD class="tdOpDetHTableHeader" width="30%"><fmt:message key="mpml.operationDetailWorkInstruction.number"/></TD>
                    <!-- content type icon -->
                    <TD class="tdOpDetHTableHeader" width="3%" align="center"></TD>
                    <!-- info icon -->
                    <TD class="tdOpDetHTableHeader" width="3%" align="center"></TD>
                    <!--ILLUSTRATION-->
                    <TD class="tdOpDetHTableHeader" width="15%"><fmt:message key="mpml.operationDetailWorkInstruction.illustration"/></TD>
                    <%
                      String docObid =  ieObj.getAttributeValue("SORTED_ALL_TYPES_OF_DOCUMENTS", 0, "obid");
                      String obid = WorkInstructionsUtilities.getOid(docObid);
                      String imageLinkDoc = WorkInstructionsUtilities.getPendingStatusIconString(obid);
          			  if(!imageLinkDoc.equals("")){
          			  %>	
                      	<TD class="tdOpDetHTableHeader" COLSPAN=1></TD>
                      <%}
          			  int documentOperationDetailTableFormatIndex = 0;
                    %> 
                </TR>
              <ie:forEach groupIn="SORTED_ALL_TYPES_OF_DOCUMENTS" groupOut="CURRENT_DOCUMENT">
                    <TR>
                        <!--DOCUMENT NAME-->
                        <% 
                           docObid =  ieObj.getAttributeValue("CURRENT_DOCUMENT", 0, "obid");
                           String contentIconUrl = ieObj.getAttributeValue("CURRENT_DOCUMENT", 0, "format.standardIconStr");
                           String containerId = ieObj.getAttributeValue("CURRENT_DOCUMENT", 0, "container.id");
                           obid = WorkInstructionsUtilities.getOid(docObid);
                           WTDocument docObj = (WTDocument)WorkInstructionsUtilities.getObject(obid);
                           String docDownloadUrl = WorkInstructionsUtilities.getDocumentDownloadUrl(docObj);                           
                           String containeroid = containerId.substring(0,containerId.lastIndexOf(':'));
                           String docFullLink = urlFactoryBean.getHREF("app/#ptc1/tcomp/infoPage?"+"ContainerOid="+containeroid+"&oid="+obid+"&u8=1");
                           String tdOperationDetailTableFormat = "tdOPDetHTableNormalOdd";
                           documentOperationDetailTableFormatIndex++;
      					   if(documentOperationDetailTableFormatIndex%2==0)
      					   {
   								tdOperationDetailTableFormat = "tdOPDetHTableNormalEven";
   						   }
   						   else
   						   {
   						   		tdOperationDetailTableFormat = "tdOPDetHTableNormal";
   						   }
                        %>
                        <TD class=<%=tdOperationDetailTableFormat%> width="49%" align="center"><pre-wrap><A HREF="<%=globalHRef%><%=docFullLink%> " target="_blank" ><ie:getValue name="name" groupIn="CURRENT_DOCUMENT"/></A></pre-wrap></TD>
                        <!--NUMBER-->
                        <TD class=<%=tdOperationDetailTableFormat%> width="30%" align="center"><pre-wrap><ie:getValue name="number" groupIn="CURRENT_DOCUMENT"/></pre-wrap></TD>                       
                        <!-- content type -->
                        <TD class=<%=tdOperationDetailTableFormat%> width="3%"  align="center"><pre-wrap><A HREF="<%=docDownloadUrl%>" target="_blank" >
                        				<img src="<%=globalHRef%><%=contentIconUrl%>" alt="Download" border="0" /></A></pre-wrap></TD>
                        <!-- Info -->
                        <TD class=<%=tdOperationDetailTableFormat%> width="3%"  align="center"><pre-wrap><A HREF="<%=globalHRef%><%=docFullLink%> " target="_blank" >
                        				<img src="../../images/details.gif" alt="View Information" border="0" /></A></pre-wrap></TD>                        
                    	<!--ILLUSTRATION-->
                    	 <%  // Set the illustration value according to the class name.                   	 
                            String classN =   ieObj.getAttributeValue("CURRENT_DOCUMENT", 0, "class");
			     			String valueImageDisplayed = null;   
			     			java.util.Enumeration attNames = ieObj.getAttributeNames("CURRENT_DOCUMENT", 0);
			     			while ( attNames.hasMoreElements () ) 
			     			{
			        			String attName = (String)attNames.nextElement ();
								if ( attName.endsWith("illustration"))
								{
					   				valueImageDisplayed =  ieObj.getAttributeValue("CURRENT_DOCUMENT", 0, attName);
					   				break;
						  		}
			     		   }
			     		   if (valueImageDisplayed != null)
			     		   {
			        	   		valueImageDisplayed="mpml.utilityWorkInstruction."+valueImageDisplayed.toUpperCase();  
                           		valueImageDisplayed=WorkInstructionsUtilities.getLocalizedValue( "com.ptc.windchill.mpml.client.clientResource",valueImageDisplayed); 
                         %>
                         	<TD class=<%=tdOperationDetailTableFormat%> width="10%" align="center"><pre-wrap><%=valueImageDisplayed%></pre-wrap></TD>
                         <%} 
			     		   else
			     		   {
			     		   %>
			          			<TD class=<%=tdOperationDetailTableFormat%>width="15%" align="center"><pre-wrap><fmt:message key="mpml.utilityWorkInstruction.FALSE"/></pre-wrap></TD>
                           <%
                           }
                           %>
			     
			     	<% imageLinkDoc = WorkInstructionsUtilities.getPendingStatusIconString(obid);
                       if(!imageLinkDoc.equals(""))
                       {%>	
			     			<TD class="tdPPTableNormal" COLSPAN=1> 
                        		<IMG alt="<%=pendingChangeToolTip%>" SRC="<%=imageLinkDoc%>" />
                       <%}%>
                       	</TD>       
                    </TR>
                </ie:forEach>  
            </TABLE>
        </TD></TR>
        <%--<TR><TD valign="top"> <%@ include file="allocatedCC_ToAnOperation.jspf"%></TD></TR> --%>
    <TR><TD><BR></TD></TR>
</TABLE>                        